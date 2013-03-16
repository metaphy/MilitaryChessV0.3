/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 25, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.model;


public class Movement implements Comparable<Movement> {
	private Coordinate start;
	private Coordinate end;
	private int value; // The evaluation value of this movement

	public Movement() {
	}

	/**
	 * @param start
	 * @param end
	 */
	public Movement(Coordinate start, Coordinate end) {
		this.start = start;
		this.end = end;
	}

	public Movement(int x0, int y0, int x, int y) {
		this.start = new Coordinate(x0, y0);
		this.end = new Coordinate(x, y);
	}

	/**
	 * @return the start
	 */
	public Coordinate getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(Coordinate start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public Coordinate getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(Coordinate end) {
		this.end = end;
	}

	public int startx() {
		return start.x;
	}

	public int starty() {
		return start.y;
	}

	public int endx() {
		return end.x;
	}

	public int endy() {
		return end.y;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Movement o) {
		Movement m = (Movement) o;
		return m.getValue() - this.value;
	}

	public String toString() {
		return "start: " + start.value + "\tend: " + end.value +"\tvalue=" + value;
	}
}
