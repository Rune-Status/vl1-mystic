package org.mystic.game.model.entity;

/**
 * Represents a single animation
 * 
 * @author Michael Sasse
 * 
 */
public final class Animation {

	/**
	 * The id of the animation
	 */
	private int id;

	/**
	 * The delay to perform the animation
	 */
	private int delay;

	/**
	 * Constructs a new animation with an id and no delay
	 * 
	 * @param id
	 */
	public Animation(int id) {
		this.id = ((short) id);
		this.delay = 0;
	}

	/**
	 * Constructs a new animation with an id and a delay
	 * 
	 * @param id
	 *            The id of the animation
	 * @param delay
	 *            The delay of the animation
	 */
	public Animation(int id, int delay) {
		this.id = ((short) id);
		this.delay = ((byte) delay);
	}

	/**
	 * Gets the delay of the animation
	 * 
	 * @return
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Gets the id of the animation
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the delay of the animation
	 * 
	 * @param delay
	 *            The delay of the animation
	 */
	public void setDelay(int delay) {
		this.delay = ((byte) delay);
	}

	/**
	 * Sets the id of the animation
	 * 
	 * @param id
	 *            The id of the animation
	 */
	public void setId(int id) {
		this.id = ((short) id);
	}

}