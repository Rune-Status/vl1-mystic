package org.mystic.game.model.content.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mystic.game.model.entity.Entity;

public class DamageMap {

	@SuppressWarnings("unused")
	private final Entity entity;

	private Map<Entity, Integer> dmg = new HashMap<Entity, Integer>();

	private long lastDamage = 0L;

	public DamageMap(Entity e) {
		this.entity = e;
	}

	public void addDamage(Entity attacker, int damage) {
		lastDamage = System.currentTimeMillis() + 60000;
		if (damage == 0) {
			return;
		}
		if (dmg.get(attacker) == null) {
			dmg.put(attacker, Integer.valueOf(damage));
		} else {
			int total = dmg.get(attacker).intValue();
			dmg.remove(attacker);
			dmg.put(attacker, Integer.valueOf(total + damage));
		}
	}

	public Entity getKiller() {
		int highDmg = 0;
		Entity highEn = null;
		for (Entry<Entity, Integer> i : dmg.entrySet()) {
			if (i != null && i.getValue() > highDmg) {
				highDmg = i.getValue();
				highEn = i.getKey();
			}
		}
		return highEn;
	}

	public void clear() {
		dmg.clear();
		lastDamage = 0L;
	}

	public boolean isClearHistory() {
		return lastDamage != 0 && dmg.size() > 0 && lastDamage <= System.currentTimeMillis();
	}
}
