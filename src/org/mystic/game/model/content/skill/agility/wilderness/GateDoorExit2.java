package org.mystic.game.model.content.skill.agility.wilderness;

import org.mystic.game.model.entity.Location;
import org.mystic.game.model.entity.player.Player;
import org.mystic.game.model.entity.player.controllers.Controller;
import org.mystic.game.model.entity.player.controllers.ControllerManager;
import org.mystic.game.task.Task;

public class GateDoorExit2 extends Task {

	private final Player player;

	// private final Door door;

	private final int xMod, yMod;

	private byte stage = 0;

	private final Controller start;

	public GateDoorExit2(Player player, int x, int y, int z, Location dest) {
		super(player, 1, true);
		this.player = player;
		// this.door = Region.getDoor(x, y, z);
		start = player.getController();
		player.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		int xDiff = player.getLocation().getX() - dest.getX();
		int yDiff = player.getLocation().getY() - dest.getY();
		if (xDiff != 0)
			xMod = (xDiff < 0 ? 1 : -1);
		else
			xMod = 0;
		if (yDiff != 0)
			yMod = (yDiff < 0 ? 1 : -1);
		else
			yMod = 0;
		// if (xDiff != 0 && yDiff != 0 || door == null) {
		// player.setController(start);
		// stop();
		// }
		player.setTakeDamage(false);
	}

	@Override
	public void execute() {
		// if (stage == 0) {
		// player.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
		// ObjectManager.remove(new GameObject(ObjectManager.BLANK_OBJECT_ID,
		// door.getX(), door.getY(), door.getZ(),
		// door.getType(), door.getCurrentFace()));
		// door.append();
		// ObjectManager.send(new GameObject(door.getCurrentId(), door.getX(),
		// door.getY(), door.getZ(),
		// door.getType(), door.getCurrentFace()));
		// } else if (stage == 1) {
		// player.getMovementHandler().walkTo(xMod, yMod);
		// } else if (stage == 2) {
		// ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX(),
		// door.getY(), door.getZ(),
		// door.getType(), door.getCurrentFace()));
		// door.append();
		// ObjectManager.send(new GameObject(door.getCurrentId(), door.getX(),
		// door.getY(), door.getZ(),
		// door.getType(), door.getCurrentFace()));
		// player.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
		//
		// stop();
		// }
		// stage++;
	}

	@Override
	public void onStop() {
		if (player.inWilderness()) {
			player.setController(ControllerManager.WILDERNESS_CONTROLLER);
		} else {
			player.setController(ControllerManager.DEFAULT_CONTROLLER);
		}
		player.setTakeDamage(true);
	}

}