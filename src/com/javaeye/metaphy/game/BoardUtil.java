/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 24, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.game;

import com.javaeye.metaphy.model.Location;

public class BoardUtil {

	/**
	 * is enemy
	 * 
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static boolean isEnemy(byte b1, byte b2) {
		Location p1 = getLocatedByByte(b1);
		Location p2 = getLocatedByByte(b2);
		return isEnemy(p1, p2);
	}
	
	public static boolean isEnemy(byte b1, Location p2) {
		Location p1 = getLocatedByByte(b1);
		return isEnemy(p1, p2);
	}
	
	/**
	 * is enemy
	 */
	public static boolean isEnemy(Location p1, Location p2) {
		if ((p1 == Location.SOUTH || p1 == Location.NORTH)
				&& (p2 == Location.WEST || p2 == Location.EAST))
			return true;
		if ((p1 == Location.WEST || p1 == Location.EAST)
				&& (p2 == Location.SOUTH || p2 == Location.NORTH))
			return true;

		return false;
	}

	/**
	 * @param loc
	 * @return
	 */
	public static int locationToOrder(Location loc) {
		int index = 0;
		if (loc == Location.SOUTH) {
			index = 0;
		} else if (loc == Location.WEST) {
			index = 1;
		} else if (loc == Location.NORTH) {
			index = 2;
		} else if (loc == Location.EAST) {
			index = 3;
		}
		return index;
	}

	public static Location orderToLocation(int index) {
		Location loc = null;
		if (index == 0) {
			loc = Location.SOUTH;
		} else if (index == 1) {
			loc = Location.WEST;
		} else if (index == 2) {
			loc = Location.NORTH;
		} else if (index == 3) {
			loc = Location.EAST;
		}

		return loc;
	}

	/**
	 * Convert the 0x3a, 0x2a, 0x1a to 0x0a
	 * 
	 * @param p
	 * @return
	 */
	public static byte pureType(byte p) {
		if (p > 0x40) {
			return INVALID;
		} else if (p > 0x30) {
			return (byte) (p - 0x30);
		} else if (p > 0x20) {
			return (byte) (p - 0x20);
		} else if (p > 0x10) {
			return (byte) (p - 0x10);
		} else {
			return p;
		}
	}

	/**
	 * Piece1 and Piece2 are the same location
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static boolean sameLocation(byte p1, byte p2) {
		if (BoardUtil.getLocatedByByte(p1) == null
				|| BoardUtil.getLocatedByByte(p2) == null) {
			return false;
		}
		return BoardUtil.getLocatedByByte(p1) == BoardUtil.getLocatedByByte(p2);
	}

	/**
	 * Get the located information from this byte
	 * 
	 * @param p
	 * @return
	 */
	public static Location getLocatedByByte(byte p) {
		if (p == INVALID) {
			return null;
		} else if (p < 0x10) {
			return Location.SOUTH;
		} else if (p < 0x20) {
			return Location.NORTH;
		} else if (p < 0x30) {
			return Location.WEST;
		} else if (p < 0x40) {
			return Location.EAST;
		} else {
			return null;
		}
	}

	/**
	 * @param piece
	 * @return
	 */
	public static int indexFromPiece12(byte piece) {
		int index = -1;
		switch (piece) {
		case FLAG_S:
			index = 0;
			break;
		case MINE_S:
			index = 1;
			break;
		case BOMB_S:
			index = 2;
			break;
		case SILING_S:
			index = 3;
			break;
		case JUNZHANG_S:
			index = 4;
			break;
		case SHIZHANG_S:
			index = 5;
			break;
		case LVZHANG_S:
			index = 6;
			break;
		case TUANZHANG_S:
			index = 7;
			break;
		case YINGZHANG_S:
			index = 8;
			break;
		case LIANZHANG_S:
			index = 9;
			break;
		case PAIZHANG_S:
			index = 10;
			break;
		case GONGBING_S:
			index = 11;
			break;

		}
		return index;
	}

	/**
	 * 
	 * @param piece
	 * @return
	 */
	public static int indexFromPiece(byte piece) {
		int index = -1;
		switch (piece) {
		case FLAG_S:
			index = 0;
			break;
		case MINE_S:
			index = 1;
			break;
		case BOMB_S:
			index = 4;
			break;
		case SILING_S:
			index = 6;
			break;
		case JUNZHANG_S:
			index = 7;
			break;
		case SHIZHANG_S:
			index = 8;
			break;
		case LVZHANG_S:
			index = 10;
			break;
		case TUANZHANG_S:
			index = 12;
			break;
		case YINGZHANG_S:
			index = 14;
			break;
		case LIANZHANG_S:
			index = 16;
			break;
		case PAIZHANG_S:
			index = 19;
			break;
		case GONGBING_S:
			index = 22;
			break;
		}
		return index;
	}

	/**
	 * get the piece by index
	 * 
	 * @param k
	 * @return
	 */
	public static byte pieceFromIndex(Location loc, int k) {
		byte p = 0x00;
		switch (k) {
		case 0:
			p = FLAG_S;
			break;
		case 1:
		case 2:
		case 3:
			p = MINE_S;
			break;
		case 4:
		case 5:
			p = BOMB_S;
			break;
		case 6:
			p = SILING_S;
			break;
		case 7:
			p = JUNZHANG_S;
			break;
		case 8:
		case 9:
			p = SHIZHANG_S;
			break;
		case 10:
		case 11:
			p = LVZHANG_S;
			break;
		case 12:
		case 13:
			p = TUANZHANG_S;
			break;
		case 14:
		case 15:
			p = YINGZHANG_S;
			break;
		case 16:
		case 17:
		case 18:
			p = LIANZHANG_S;
			break;
		case 19:
		case 20:
		case 21:
			p = PAIZHANG_S;
			break;
		case 22:
		case 23:
		case 24:
			p = GONGBING_S;
			break;
		}

		switch (loc) {
		case NORTH:
			p = (byte) (p + 0x10);
			break;
		case WEST:
			p = (byte) (p + 0x20);
			break;
		case EAST:
			p = (byte) (p + 0x30);
			break;
		}

		return p;
	}

	/* Size of x/y Coordinate */
	public static final int BOARD_ARRAY_SIZE = 17;
	/* SoldierStation type */
	public static final byte INVALID = 0x00;
	public static final byte HEADQUARTER = 0x71;
	public static final byte CAMP = 0x72;
	public static final byte STATION_ROAD = 0x73;
	public static final byte STATION_RAILWAY = 0x74;
	/*
	 * Soldier type 对于棋子而言 － 无棋子: 0x00 ; South的棋子0x0-, North 0x1-, West 0x2-,
	 * East 0x3-
	 */
	public static final byte FLAG_S = 0x02;
	public static final byte MINE_S = 0x03;
	public static final byte BOMB_S = 0x04;
	public static final byte SILING_S = 0x05;
	public static final byte JUNZHANG_S = 0x06;
	public static final byte SHIZHANG_S = 0x07;
	public static final byte LVZHANG_S = 0x08;
	public static final byte TUANZHANG_S = 0x09;
	public static final byte YINGZHANG_S = 0x0A;
	public static final byte LIANZHANG_S = 0x0B;
	public static final byte PAIZHANG_S = 0x0C;
	public static final byte GONGBING_S = 0x0D;
	public static final byte FLAG_N = 0x12;
	public static final byte MINE_N = 0x13;
	public static final byte BOMB_N = 0x14;
	public static final byte SILING_N = 0x15;
	public static final byte JUNZHANG_N = 0x16;
	public static final byte SHIZHANG_N = 0x17;
	public static final byte LVZHANG_N = 0x18;
	public static final byte TUANZHANG_N = 0x19;
	public static final byte YINGZHANG_N = 0x1A;
	public static final byte LIANZHANG_N = 0x1B;
	public static final byte PAIZHANG_N = 0x1C;
	public static final byte GONGBING_N = 0x1D;
	public static final byte FLAG_W = 0x22;
	public static final byte MINE_W = 0x23;
	public static final byte BOMB_W = 0x24;
	public static final byte SILING_W = 0x25;
	public static final byte JUNZHANG_W = 0x26;
	public static final byte SHIZHANG_W = 0x27;
	public static final byte LVZHANG_W = 0x28;
	public static final byte TUANZHANG_W = 0x29;
	public static final byte YINGZHANG_W = 0x2A;
	public static final byte LIANZHANG_W = 0x2B;
	public static final byte PAIZHANG_W = 0x2C;
	public static final byte GONGBING_W = 0x2D;
	public static final byte FLAG_E = 0x32;
	public static final byte MINE_E = 0x33;
	public static final byte BOMB_E = 0x34;
	public static final byte SILING_E = 0x35;
	public static final byte JUNZHANG_E = 0x36;
	public static final byte SHIZHANG_E = 0x37;
	public static final byte LVZHANG_E = 0x38;
	public static final byte TUANZHANG_E = 0x39;
	public static final byte YINGZHANG_E = 0x3A;
	public static final byte LIANZHANG_E = 0x3B;
	public static final byte PAIZHANG_E = 0x3C;
	public static final byte GONGBING_E = 0x3D;

	// Usage:
	// import static com.javaeye.metaphy.game.BoardUtil.INVALID;
	// import static com.javaeye.metaphy.game.BoardUtil.HEADQUARTER ;
	// import static com.javaeye.metaphy.game.BoardUtil.CAMP ;
	// import static com.javaeye.metaphy.game.BoardUtil.STATION_ROAD ;
	// import static com.javaeye.metaphy.game.BoardUtil.STATION_RAILWAY ;
	// import static com.javaeye.metaphy.game.BoardUtil.FLAG_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.MINE_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.BOMB_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.SILING_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.GONGBING_S ;
	// import static com.javaeye.metaphy.game.BoardUtil.FLAG_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.MINE_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.BOMB_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.SILING_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.GONGBING_N ;
	// import static com.javaeye.metaphy.game.BoardUtil.FLAG_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.MINE_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.BOMB_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.SILING_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.GONGBING_W ;
	// import static com.javaeye.metaphy.game.BoardUtil.FLAG_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.MINE_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.BOMB_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.SILING_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.GONGBING_E ;
	// import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;
}
