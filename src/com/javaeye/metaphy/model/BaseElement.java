/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 3, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.model;

import com.javaeye.metaphy.game.Game;

public class BaseElement {
	/* The coordinate */
	protected int x;
	protected int y;

	/* The type */
	protected byte type;

	public BaseElement() {
		super();
	}

	/**
	 * @param x
	 * @param y
	 */
	public BaseElement(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * @param type
	 */
	public BaseElement(byte type) {
		super();
		this.type = type;
	}

	/**
	 * @param x
	 * @param y
	 * @param type
	 */
	public BaseElement(int x, int y, byte type) {
		super();
		this.x = x;
		this.y = y;
		this.type = type;
	}

	/* Get the point x */
	public int getPointX() {
		return (x + 1) * Game.GRID_UNIT_LENGTH;
	}

	/* Get the point y */
	public int getPointY() {
		return (y + 1) * Game.GRID_UNIT_LENGTH;
	}

	/* getter/ setter */
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

}
