package org.mystic.game.task.impl;

import org.mystic.game.model.content.dialogue.DialogueManager;
import org.mystic.game.model.entity.Animation;
import org.mystic.game.model.entity.Graphic;
import org.mystic.game.model.entity.item.Item;
import org.mystic.game.model.entity.player.Player;
import org.mystic.game.model.networking.outgoing.SendMessage;
import org.mystic.game.task.Task;
import org.mystic.game.task.TaskIdentifier;

/**
 * <p>
 * A producing action is an action where on item is transformed into another,
 * typically this is in skills such as smithing and crafting.
 * </p>
 * 
 * <p>
 * This class implements code related to all production-type skills, such as
 * dealing with the action itself, replacing the items and checking levels.
 * </p>
 * 
 * <p>
 * The individual crafting, smithing, and other skills implement functionality
 * specific to them such as random events.
 * </p>
 * 
 * @author Graham Edgecombe
 * @author Michael <Scu11>
 */
public abstract class ProductionTask extends Task {

	/**
	 * Player instance because this is for a player
	 */
	protected Player player;

	/**
	 * This starts the actions animation and requirement checks, but prevents the
	 * production from immediately executing.
	 */
	protected boolean started = false;

	/**
	 * The cycle count.
	 */
	protected int cycleCount = 0;

	/**
	 * The amount of items to produce.
	 */
	private int productionCount = 0;

	public ProductionTask(Player player, int delay) {
		this(player, delay, false, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	}

	public ProductionTask(Player player, int delay, boolean immediate, StackType stackType, BreakType breakType,
			TaskIdentifier taskId) {
		super(player, delay, immediate, stackType, breakType, taskId);
		this.player = player;
	}

	/**
	 * Performs extra checks that a specific production event independently uses,
	 * e.g. checking for ingredients in herblore.
	 */
	public abstract boolean canProduce();

	@Override
	public void execute() {
		if (player.getSkill().getLevels()[getSkill()] < getRequiredLevel()) {
			DialogueManager.sendStatement(player, getInsufficentLevelMessage());
			player.getUpdateFlags().sendAnimation(new Animation(-1));
			this.stop();
			return;
		}
		for (Item productionItem : getConsumedItems()) {
			if (productionItem != null) {
				if (player.getInventory().getItemAmount(productionItem.getId()) < productionItem.getAmount()) {
					if (noIngredients(productionItem) != null)
						player.getClient().queueOutgoingPacket(new SendMessage(noIngredients(productionItem)));
					player.getUpdateFlags().sendAnimation(new Animation(-1));
					this.stop();
					return;
				}
			}
		}
		if (!canProduce()) {
			this.stop();
			return;
		}
		if (!started) {
			started = true;
			if (getAnimation() != null) {
				player.getUpdateFlags().sendAnimation(getAnimation());
			}
			if (getGraphic() != null) {
				player.getUpdateFlags().sendGraphic(getGraphic());
			}
			productionCount = getProductionCount();
			cycleCount = getCycleCount();
			return;
		}
		if (cycleCount > 1) {
			cycleCount--;
			return;
		}
		cycleCount = getCycleCount();
		productionCount--;
		if (productionCount != 0) {
			if (getAnimation() != null && getAnimation().getId() > 0) {
				player.getUpdateFlags().sendAnimation(getAnimation());
			}
			if (getGraphic() != null && getGraphic().getId() > 0) {
				player.getUpdateFlags().sendGraphic(getGraphic());
			}
		}
		for (Item item : getConsumedItems()) {
			player.getInventory().remove(item);
		}
		for (Item item : getRewards()) {
			player.getInventory().add(item);
		}
		player.getClient().queueOutgoingPacket(new SendMessage(getSuccessfulProductionMessage()));
		player.getSkill().addExperience(getSkill(), getExperience());
		if (productionCount < 1) {
			player.getUpdateFlags().sendAnimation(new Animation(-1));
			this.stop();
			return;
		}
		for (Item item : getConsumedItems()) {
			if (player.getInventory().getItemAmount(item.getId()) < item.getAmount()) {
				player.getUpdateFlags().sendAnimation(new Animation(-1));
				this.stop();
				return;
			}
		}
	}

	/**
	 * Gets the animation played whilst producing the item.
	 * 
	 * @return The animation played whilst producing the item.
	 */
	public abstract Animation getAnimation();

	/**
	 * Gets the consumed item in the production of this item.
	 * 
	 * @return The consumed item in the production of this item.
	 */
	public abstract Item[] getConsumedItems();

	/**
	 * Gets the amount of cycles before the item is produced.
	 * 
	 * @return The amount of cycles before the item is produced.
	 */
	public abstract int getCycleCount();

	/**
	 * Gets the experience granted for each item that is successfully produced.
	 * 
	 * @return The experience granted for each item that is successfully produced.
	 */
	public abstract double getExperience();

	/**
	 * Gets the graphic played whilst producing the item.
	 * 
	 * @return The graphic played whilst producing the item.
	 */
	public abstract Graphic getGraphic();

	/**
	 * Gets the message sent when the Entity's level is too low to produce this
	 * item.
	 * 
	 * @return The message sent when the Entity's level is too low to produce this
	 *         item.
	 */
	public abstract String getInsufficentLevelMessage();

	/**
	 * Gets the amount of times an item is produced.
	 * 
	 * @return The amount of times an item is produced.
	 */
	public abstract int getProductionCount();

	/**
	 * Gets the required level to produce this item.
	 * 
	 * @return The required level to produce this item.
	 */
	public abstract int getRequiredLevel();

	/**
	 * Gets the rewarded items from production.
	 * 
	 * @return The rewarded items from production.
	 */
	public abstract Item[] getRewards();

	/**
	 * Gets the skill we are using to produce.
	 * 
	 * @return The skill we are using to produce.
	 */
	public abstract int getSkill();

	/**
	 * Gets the message sent when the Entity successfully produces an item.
	 * 
	 * @return The message sent when the Entity successfully produce an item.
	 */
	public abstract String getSuccessfulProductionMessage();

	public boolean isStarted() {
		return started;
	}

	public abstract String noIngredients(Item item);

	public void setCycleCount(int cycleCount) {
		this.cycleCount = cycleCount;
	}

	public void setProductionCount(int productionCount) {
		this.productionCount = productionCount;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
}
