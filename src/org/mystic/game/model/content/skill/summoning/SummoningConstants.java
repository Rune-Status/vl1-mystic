package org.mystic.game.model.content.skill.summoning;

import java.util.HashMap;
import java.util.Map;

import org.mystic.game.model.content.skill.summoning.impl.Minotaur;
import org.mystic.game.model.content.skill.summoning.impl.SpiritJelly;
import org.mystic.game.model.content.skill.summoning.impl.SpiritSpider;
import org.mystic.game.model.content.skill.summoning.impl.SpiritTerrorBird;
import org.mystic.game.model.content.skill.summoning.impl.SpiritWolf;
import org.mystic.game.model.content.skill.summoning.impl.UnicornStallion;
import org.mystic.game.model.content.skill.summoning.impl.Wolpertinger;
import org.mystic.game.model.definition.ItemDefinition;
import org.mystic.game.model.entity.item.Item;

public class SummoningConstants {

	private static final Map<Integer, Familiar> itemsForFamiliar = new HashMap<Integer, Familiar>();

	private static final Map<Familiar, Integer> scrollsForFamiliar = new HashMap<Familiar, Integer>();

	private static final Map<Familiar, FamiliarSpecial> specials = new HashMap<Familiar, FamiliarSpecial>();

	static {
		specials.put(Familiar.a, new SpiritWolf());
		specials.put(Familiar.cc, new SpiritTerrorBird());
		specials.put(Familiar.c, new SpiritSpider());
		specials.put(Familiar.ee, new SpiritJelly());
		specials.put(Familiar.u, new Minotaur());
		specials.put(Familiar.y, new Minotaur());
		specials.put(Familiar.u, new Minotaur());
		specials.put(Familiar.ff, new Minotaur());
		specials.put(Familiar.pp, new Minotaur());
		specials.put(Familiar.aaa, new Minotaur());
		specials.put(Familiar.lll, new Minotaur());
		specials.put(Familiar.mmm, new UnicornStallion());
		specials.put(Familiar.ooo, new Wolpertinger());
		for (Pouch i : Pouch.values()) {
			ItemDefinition def = Item.getDefinition(i.pouchId);
			if (def != null) {
				def.setUntradable();
			}
		}
		for (Scroll i : Scroll.values()) {
			ItemDefinition def = Item.getDefinition(i.itemId);
			if (def != null) {
				def.setUntradable();
			}
		}
		for (Familiar i : Familiar.values()) {
			int pouch = i.pouch;
			if (pouch >= 0) {
				for (Scroll k : Scroll.values()) {
					if ((k.pouch != null) && (k.pouch.getId() == pouch)) {
						scrollsForFamiliar.put(i, Integer.valueOf(k.itemId));
						continue;
					}
				}
			}
		}
		for (Familiar f : Familiar.values()) {
			itemsForFamiliar.put(Integer.valueOf(f.pouch), f);
		}
	}

	public static Familiar getFamiliarForPouch(int id) {
		return itemsForFamiliar.get(Integer.valueOf(id));
	}

	public static int getScrollForFamiliar(FamiliarMob mob) {
		return !scrollsForFamiliar.containsKey(mob.getData()) ? -1 : scrollsForFamiliar.get(mob.getData()).intValue();
	}

	public static FamiliarSpecial getSpecial(Familiar f) {
		return specials.get(f);
	}

	public static boolean isAllowed(Familiar f) {
		if (f.attack <= 0) {
			return false;
		}
		return true;
	}

}