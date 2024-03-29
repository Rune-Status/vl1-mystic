package org.mystic.game.model.content.skill.crafting;

import java.util.HashMap;
import java.util.Map;

import org.mystic.game.model.entity.item.Item;

public enum Jewelry {

	/* Rings */
	GOLD_RING(1635, 5, 15, 1592, 2357),

	SAPPHIRE_RING(1637, 20, 40, 1592, 2357, 1607),

	EMERALD_RING(1639, 27, 55, 1592, 2357, 1605),

	RUBY_RING(1641, 34, 70, 1592, 2357, 1603),

	DIAMOND_RING(1643, 43, 85, 1592, 2357, 1601),

	DRAGONSTONE_RING(1645, 99, 100, 1592, 2357, 1615),

	ONYX_RING(6575, 67, 85, 1592, 2357, 6573),

	/* Necklace */
	GOLD_NECKLACE(1654, 6, 20, 1597, 2357),

	SAPPHIRE_NECKLACE(1656, 22, 55, 1597, 2357, 1607),

	EMERALD_NECKLACE(1658, 29, 60, 1597, 2357, 1605),

	RUBY_NECKLACE(1660, 40, 75, 1597, 2357, 1603),

	DIAMOND_NECKLACE(1662, 56, 90, 1597, 2357, 1601),

	DRAGONSTONE_NECKLACE(1664, 72, 105, 1597, 2357, 1615),

	ONYX_NECKLACE(6577, 82, 120, 1597, 2357, 6573),

	/* Amulet */
	GOLD_AMULET(1673, 8, 30, 1595, 2357),

	SAPPHIRE_AMULET(1675, 24, 55, 1595, 2357, 1607),

	EMERALD_AMULET(1677, 31, 65, 1595, 2357, 1605),

	RUBY_AMULET(1679, 50, 85, 1595, 2357, 1603),

	DIAMOND_AMULET(1681, 70, 100, 1595, 2357, 1601),

	DRAGONSTONE_AMULET(1683, 85, 125, 1595, 2357, 1615),

	ONYX_AMULET(6579, 90, 1250, 1595, 2357, 6573);

	private final Item reward;

	private final short levelRequired;

	private final double experienceGain;

	private final int[] materialsRequired;

	private final static Map<Integer, Jewelry> jewelry = new HashMap<Integer, Jewelry>();

	static {
		for (Jewelry jewel : values()) {
			jewelry.put(Integer.valueOf(jewel.getReward().getId()), jewel);
		}
	}

	public static Jewelry forReward(int id) {
		return jewelry.get(Integer.valueOf(id));
	}

	private Jewelry(final int rewardId, final int levelRequired, final double experienceGain,
			final int... materialsRequired) {
		this.reward = new Item(rewardId);
		this.levelRequired = ((short) levelRequired);
		this.experienceGain = experienceGain;
		this.materialsRequired = materialsRequired;
	}

	public final double getExperience() {
		return experienceGain;
	}

	public final int[] getMaterialsRequired() {
		return materialsRequired;
	}

	public final short getRequiredLevel() {
		return levelRequired;
	}

	public final Item getReward() {
		return reward;
	}

}