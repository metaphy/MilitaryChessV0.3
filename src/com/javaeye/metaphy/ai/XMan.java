/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 2, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.ai;

import com.javaeye.metaphy.model.Coordinate;
import com.javaeye.metaphy.model.Location;

public class XMan {
	public static final int PIECE_TYPE_NUMBER = 12;
	public static final int PIECE_NUMBER_EACHONE = 25;
	// initial coordinate
	private int id;
	// current coordinate. -1 means it's not visiable
	private Coordinate coordinate;
	private byte piece;
	private int[] probability = new int[PIECE_NUMBER_EACHONE];

	public XMan() {
	}

	/**
	 * is the xman dead
	 * 
	 * @return
	 */
	public boolean isDead() {
		return coordinate.value <= 0;
	}

	/**
	 * get the location from id
	 * 
	 * @return Location
	 */
	public Location getLocation() {
		if (id <= 0) {
			return null;
		} else if (id / 100 > 10) {
			return Location.SOUTH;
		} else if (id / 100 < 6) {
			return Location.NORTH;
		} else if (id % 100 < 6) {
			return Location.WEST;
		} else if (id % 100 > 10) {
			return Location.EAST;
		} else {
			return null;
		}
	}

	/**
	 * 12 -> 25
	 * 
	 * @param probability12
	 */
	public void setProbability12(int[] p12) {
		int indexp = 0;
		for (int n = 0; n < p12.length; n++) {
			if (n == 0) {
				probability[indexp++] = p12[n];
			} else if (n == 1) {
				probability[indexp++] = p12[n] / 3;
				probability[indexp++] = (p12[n] - p12[n] / 3) / 2;
				probability[indexp++] = p12[n] - p12[n] / 3
						- (p12[n] - p12[n] / 3) / 2;
			} else if (n == 2) {
				probability[indexp++] = p12[n] / 2;
				probability[indexp++] = p12[n] - p12[n] / 2;
			} else if (n <= 4) {
				probability[indexp++] = p12[n];
			} else if (n <= 8) {
				probability[indexp++] = p12[n] / 2;
				probability[indexp++] = p12[n] - p12[n] / 2;
			} else {
				probability[indexp++] = p12[n] / 3;
				probability[indexp++] = (p12[n] - p12[n] / 3) / 2;
				probability[indexp++] = p12[n] - p12[n] / 3
						- (p12[n] - p12[n] / 3) / 2;
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte getPiece() {
		return piece;
	}

	public void setPiece(byte piece) {
		this.piece = piece;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public int[] getProbability() {
		return probability;
	}

	public void setProbability(int[] probability) {
		this.probability = probability;
	}

}
