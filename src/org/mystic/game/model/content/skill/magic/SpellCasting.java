package org.mystic.game.model.content.skill.magic;

import org.mystic.game.model.content.combat.Combat.CombatTypes;
import org.mystic.game.model.content.combat.impl.Attack;
import org.mystic.game.model.definition.CombatSpellDefinition;
import org.mystic.game.model.entity.Entity;
import org.mystic.game.model.entity.following.Following;
import org.mystic.game.model.entity.item.Item;
import org.mystic.game.model.entity.npc.Npc;
import org.mystic.game.model.entity.player.Player;
import org.mystic.game.model.networking.outgoing.SendMessage;
import org.mystic.utility.GameDefinitionLoader;

public class SpellCasting {

	public static final Attack MAGIC_ATTACK = new Attack(4, 5);

	private final Player player;

	private int castId = -1;

	private int autocastId = -1;

	public SpellCasting(Player player) {
		this.player = player;
	}

	public void appendMultiSpell(Player p) {
		if ((player.getCombat().getMagic().isMulti()) && (p.inMultiArea()) && (p.getController().allowMultiSpells())) {
			byte affected = 0;
			Entity a = p.getCombat().getAttacking();
			if (a == null) {
				return;
			}
			int x = a.getLocation().getX();
			int y = a.getLocation().getY();
			if (p.getController().allowPvPCombat()) {
				for (Player i : p.getPlayers()) {
					if ((p.getController().canAttackPlayer(p, i)) && (i.inMultiArea()) && (!i.equals(a))) {
						if ((Math.abs(x - i.getLocation().getX()) <= 1)
								&& (Math.abs(y - i.getLocation().getY()) <= 1)) {
							p.getCombat().getMagic().finish(i);
							affected = (byte) (affected + 1);

							if (affected == 9) {
								return;
							}
						}
					}
				}
			}
			if (p.getController().canAttackNPC()) {
				for (Npc i : player.getClient().getNpcs()) {
					if ((i.inMultiArea()) && (!i.equals(a))) {
						if ((Math.abs(x - i.getLocation().getX()) <= 1)
								&& (Math.abs(y - i.getLocation().getY()) <= 1)) {
							if (p.getSummoning().hasFamiliar() && p.getSummoning().getFamiliar() == i) {
								return;
							}
							p.getCombat().getMagic().finish(i);
							affected = (byte) (affected + 1);
							if (affected == 9) {
								return;
							}
						}
					}
				}
			}
		}
	}

	public boolean canCast() {
		if (castId != -1) {
			return canCastSpell(castId);
		}
		if (isAutocasting()) {
			return canCastSpell(autocastId);
		}
		return player.getMagic().isDFireShieldEffect();
	}

	private boolean canCastSpell(int id) {
		CombatSpellDefinition definition = getDefinition(id);
		if (definition == null) {
			return false;
		}
		if ((id <= 12445) && (player.getMagic().getSpellBookType() != MagicSkill.SpellBookTypes.MODERN)) {
			return false;
		}
		if ((id > 12445) && (player.getMagic().getSpellBookType() != MagicSkill.SpellBookTypes.ANCIENT)) {
			return false;
		}
		if (player.getMaxLevels()[6] < definition.getLevel()) {
			player.getClient().queueOutgoingPacket(new SendMessage(
					"You need a Magic level of " + definition.getLevel() + " to cast " + definition.getName() + "."));
			return false;
		} else if (player.getSkill().getLevels()[6] < definition.getLevel()) {
			player.getClient().queueOutgoingPacket(new SendMessage(
					"You need a Magic level of " + definition.getLevel() + " to cast " + definition.getName() + "."));
			return false;
		}
		int runeCheck = player.getMagic().hasRunes(createArray(definition.getRunes()));
		if (runeCheck != -1) {
			String name = Item.getDefinition(runeCheck).getName();
			player.getClient().queueOutgoingPacket(
					new SendMessage("You do not have enough " + name + "s to cast " + definition.getName() + "."));
			return false;
		}
		int[] wep = definition.getWeapons();
		boolean found = false;
		if ((wep != null) && (wep.length > 0)) {
			Item weapon = player.getEquipment().getItems()[3];
			if (weapon == null) {
				player.getClient().queueOutgoingPacket(new SendMessage("You need a "
						+ Item.getDefinition(wep[0]).getName() + " to cast " + definition.getName() + "."));
				return false;
			}
			for (int i : wep) {
				if (weapon.getId() == i) {
					found = true;
				}
			}
			if (!found) {
				player.getClient().queueOutgoingPacket(new SendMessage("You need a "
						+ Item.getDefinition(wep[0]).getName() + " to cast " + definition.getName() + "."));
				return false;
			}
		}
		return true;
	}

	public void cast(Spell spell) {
		int runeId = player.getMagic().hasRunes(spell.getRunes());
		if (!player.getController().canUseCombatType(player, CombatTypes.MAGIC)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot use magic right now."));
			return;
		}
		if (player.getMaxLevels()[6] < spell.getLevel()) {
			player.getClient().queueOutgoingPacket(new SendMessage(
					"You need a Magic level of " + spell.getLevel() + " to cast " + spell.getName() + "."));
			return;
		}
		if (runeId != -1) {
			String name = Item.getDefinition(runeId).getName();
			player.getClient().queueOutgoingPacket(
					new SendMessage("You do not have enough " + name + "s to cast " + spell.getName() + "."));
			return;
		}
		if (spell.execute(player)) {
			player.getAchievements().incr(player, "Cast 1,000 spells");
			player.getAchievements().incr(player, "Cast 10,000 spells");
			player.getMagic().removeRunes(spell.getRunes());
			player.getSkill().addExperience(6, spell.getExperience());
		}
	}

	public void castCombatSpell(int castId, Entity other) {
		if (castId == 12445 && other.isTeleblocked()) {
			player.getClient().queueOutgoingPacket(new SendMessage("This player is already affected by this spell."));
			return;
		}
		if (other.isFrozen()) {
			if (castId == 1572 || castId == 1582 || castId == 1592) {
				player.getClient()
						.queueOutgoingPacket(new SendMessage("This player is already affected by this spell."));
				return;
			}
		}
		if (canCastSpell(castId)) {
			this.castId = castId;
			updateMagicAttack();
			player.updateCombatType();
			player.getFollowing().setFollow(other, Following.FollowType.COMBAT);
			player.getCombat().setAttacking(other);
		} else {
			player.getCombat().reset();
		}
	}

	public Item[] createArray(Item[] items) {
		Item[] array = new Item[items.length];
		for (int i = 0; i < array.length; i++) {
			if (items[i] != null) {
				array[i] = new Item(items[i].getId(), items[i].getAmount());
			}
		}
		return array;
	}

	public void disableAutocast() {
		autocastId = -1;
		disableClickCast();
		player.getMagic().setDFireShieldEffect(false);
		Autocast.resetAutoCastInterface(player);
		player.getCombat().setCombatType(CombatTypes.MELEE);
	}

	public void disableClickCast() {
		if (castId != -1) {
			castId = -1;
			player.updateCombatType();
		}
	}

	public void enableAutocast(int autocastId) {
		if (canCastSpell(autocastId)) {
			this.autocastId = autocastId;
			updateMagicAttack();
			player.updateCombatType();
		}
	}

	public double getBaseExperience() {
		if (getCurrentSpellId() != -1) {
			return GameDefinitionLoader.getCombatSpellDefinition(getCurrentSpellId()).getBaseExperience();
		}
		return 2.0;
	}

	public int getCurrentSpellId() {
		if (isClickcasting())
			return castId;
		if (isAutocasting()) {
			return autocastId;
		}
		return -1;
	}

	public CombatSpellDefinition getDefinition(int id) {
		return GameDefinitionLoader.getCombatSpellDefinition(id);
	}

	public boolean isAutocasting() {
		return autocastId != -1;
	}

	public boolean isCastingSpell() {
		return (isAutocasting()) || (isClickcasting()) || (player.getMagic().isDFireShieldEffect());
	}

	public boolean isClickcasting() {
		return castId != -1;
	}

	public void removeRunesForAttack() {
		if (castId != -1) {
			player.getMagic().removeRunes(createArray(getDefinition(castId).getRunes()));
		} else if (autocastId != -1) {
			player.getMagic().removeRunes(createArray(getDefinition(autocastId).getRunes()));
		}
	}

	public void resetOnAttack() {
		if ((isClickcasting()) && (isAutocasting())) {
			castId = -1;
			player.getCombat().reset();
			updateMagicAttack();
			player.updateCombatType();
		} else if (isClickcasting()) {
			castId = -1;
			player.getCombat().reset();
			player.updateCombatType();
		} else if (player.getMagic().isDFireShieldEffect()) {
			player.getMagic().reset();
			player.updateCombatType();
		}
	}

	public void updateMagicAttack() {
		CombatSpellDefinition def = getDefinition(getCurrentSpellId());

		if (def == null) {
			player.getMagic().reset();
			return;
		}

		player.getCombat().getMagic().setAttack(MAGIC_ATTACK, def.getAnimation(), def.getStart(), def.getEnd(),
				def.getProjectile());

		player.getCombat().getMagic().setMulti(def.getName().contains("barrage"));
	}
}
