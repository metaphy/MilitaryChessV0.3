/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 11, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.ai;

import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;
import static com.javaeye.metaphy.game.BoardUtil.CAMP;
import static com.javaeye.metaphy.game.BoardUtil.GONGBING_S;
import static com.javaeye.metaphy.game.BoardUtil.HEADQUARTER;
import static com.javaeye.metaphy.game.BoardUtil.MINE_S;
import static com.javaeye.metaphy.game.BoardUtil.STATION_RAILWAY;
import static com.javaeye.metaphy.game.BoardUtil.STATION_ROAD;

import java.util.ArrayList;
import java.util.Vector;

import com.javaeye.metaphy.game.Board;
import com.javaeye.metaphy.game.BoardUtil;
import com.javaeye.metaphy.model.Coordinate;

public class PathFinding {
	private Board boardClass;
	private byte[][] stations = new byte[BOARD_ARRAY_SIZE][BOARD_ARRAY_SIZE];
	private byte[][] board = new byte[BOARD_ARRAY_SIZE][BOARD_ARRAY_SIZE];
	/* it's for A* path finding */
	private ArrayList<Coordinate> openList = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> closedList = new ArrayList<Coordinate>();

	public PathFinding() {
	}

	public PathFinding(Board boardClass) {
		this.boardClass = boardClass;
		this.stations = this.boardClass.getStations();
		this.board = this.boardClass.getBoard();
	}

	/**
	 * The path from beginning -> end Coordinate
	 */
	public Vector<Coordinate> pathFinding(int x0, int y0, int x, int y) {
		long timeBegin = System.currentTimeMillis();
		// Validation
		if (stations[x0][y0] == BoardUtil.INVALID
				|| stations[x][y] == BoardUtil.INVALID) {
			return null;
		}
		// The pieces in HEADQUARTER and the mines can NOT move
		if (stations[x0][y0] == HEADQUARTER
				|| boardClass.getPureType(board[x0][y0]) == MINE_S) {
			return null;
		}
		// Can not move to a camp which was occupied
		if (stations[x][y] == CAMP && board[x][y] != BoardUtil.INVALID) {
			return null;
		}
		// Piece -> Piece which are belong to the same player
		if (BoardUtil.sameLocation(board[x0][y0], board[x][y])) {
			return null;
		}

		Coordinate beginning = new Coordinate(x0, y0);
		Coordinate end = new Coordinate(x, y);
		Vector<Coordinate> path = new Vector<Coordinate>();
		// Road station or Camp ( | - ) , move only 1 step
		if (stations[x0][y0] == STATION_ROAD || stations[x][y] == STATION_ROAD
				|| stations[x0][y0] == CAMP || stations[x][y] == CAMP
				|| stations[x][y] == HEADQUARTER) {
			if (roadAdjacent(x0, y0, x, y)) {
				path.add(new Coordinate(x, y));
				return path;
			}
		}

		// Camp, move only 1 step (/ \)
		if (stations[x0][y0] == CAMP || stations[x][y] == CAMP) {
			if (campAdjacent(x0, y0, x, y)) {
				path.add(new Coordinate(x, y));
				return path;
			}
		}

		// Railway, A* path finding
		openList = new ArrayList<Coordinate>();
		closedList = new ArrayList<Coordinate>();
		if (stations[x0][y0] == STATION_RAILWAY
				&& stations[x][y] == STATION_RAILWAY
				&& (validRailwayRoad(x0, y0, x, y) || boardClass
						.getPureType(board[x0][y0]) == GONGBING_S)) {
			ArrayList<Coordinate> adjacent = new ArrayList<Coordinate>();
			boolean engineer = boardClass.getPureType(board[x0][y0]) == GONGBING_S;
			Coordinate current = null;
			openList.add(beginning);
			do {
				// Find the minimum F value Coordinate from the openList
				current = lookForMinF(end);
				openList.remove(current);
				closedList.add(current);
				// Get all adjacent XYs of current
				adjacent = allAdjacents(beginning, current, end, engineer);
				// logger.debug("All adjacents of current(" + current.value +
				// ") = " + adjacent.size());

				for (Coordinate adj : adjacent) { // Traverse all adjacents of
					// current Coordinate
					if (!closedListContains(adj)
							&& (board[adj.x][adj.y] == BoardUtil.INVALID || adj
									.equals(end))) {
						if (!openListContains(adj)) {
							adj.parent = current;
							openList.add(adj);
						} else {
							if (getCostG(current.parent, current)
									+ getCostG(current, adj) < getCostG(
									current.parent, adj)) {
								adj.parent = current;
							}
						}
					}
				}
			} while (!openListContains(end) && openList.size() > 0);
			end.parent = current;

			if (openListContains(end)) { // Find the path
				Coordinate t = end;
				while (t != beginning) {
					path.add(t);
					t = t.parent;
				}
			}
		}
		// Convert the path array
		for (int i = path.size() - 1; i > (path.size() - 1) / 2; i--) {
			Coordinate tmp = path.get(i);
			path.set(i, path.get(path.size() - 1 - i));
			path.set(path.size() - 1 - i, tmp);
		}
		long timeDone = System.currentTimeMillis();
		if (timeBegin != timeDone) {
			System.out.println("Path finding time: " + (timeDone - timeBegin)
					+ " ms");
		}
		return path;
	}

	/**
	 * Get the adjacent points of current
	 */
	private ArrayList<Coordinate> allAdjacents(Coordinate beginning,
			Coordinate current, Coordinate end, boolean engineer) {
		ArrayList<Coordinate> adjacent = new ArrayList<Coordinate>();

		if (engineer) {
			for (int i = -1; i <= 1; i += 2) {
				if (stations[current.x][current.y + i] == STATION_RAILWAY) {
					adjacent.add(new Coordinate(current.x, current.y + i));
				} else if (current.y + i * 2 >= 0
						&& current.y + i * 2 < BOARD_ARRAY_SIZE
						&& stations[current.x][current.y + i * 2] == STATION_RAILWAY) {
					adjacent.add(new Coordinate(current.x, current.y + i * 2));
				}
				if (stations[current.x + i][current.y] == STATION_RAILWAY) {
					adjacent.add(new Coordinate(current.x + i, current.y));
				} else if (current.x + i * 2 >= 0
						&& current.x + i * 2 < BOARD_ARRAY_SIZE
						&& stations[current.x + i * 2][current.y] == STATION_RAILWAY) {
					adjacent.add(new Coordinate(current.x + i * 2, current.y));
				}
			}
			if (adjacent.size() == 3) { // process Round_railway
				for (int i = -1; i <= 1; i += 2) {
					for (int j = -1; j <= 1; j += 2) {
						Coordinate c = new Coordinate(current.x + i, current.y
								+ j);
						if (engineerAdjacentTurnAround(c)) {
							adjacent.add(c);
						}
					}
				}
			}
		} else { // Not the engineer
			for (int i = -1; i <= 1; i += 2) { // The beginning and end are on
				// the same line/column
				if (current.x == end.x
						&& stations[current.x][current.y + i] == STATION_RAILWAY) {
					adjacent.add(new Coordinate(current.x, current.y + i));
				} else if (current.x == end.x
						&& (current.y + i * 2) >= 0
						&& (current.y + i * 2) < BOARD_ARRAY_SIZE
						&& stations[current.x][current.y + i * 2] == STATION_RAILWAY) {
					adjacent.add(new Coordinate(current.x, current.y + i * 2));
				}

				if (current.y == end.y
						&& stations[current.x + i][current.y] == STATION_RAILWAY) {
					adjacent.add(new Coordinate(current.x + i, current.y));
				} else if (current.y == end.y
						&& (current.x + i * 2) >= 0
						&& (current.x + i * 2) < BOARD_ARRAY_SIZE
						&& stations[current.x + i * 2][current.y] == STATION_RAILWAY) {
					adjacent.add(new Coordinate(current.x + i * 2, current.y));
				}
			}
			// On turn around railway
			if (onTurnAroundRailway(beginning, end)) {
				int[] turnAround = { 601, 602, 603, 604, 605, 506, 406, 306,
						206, 106, 1001, 1002, 1003, 1004, 1005, 1106, 1206,
						1306, 1406, 1506, 110, 210, 310, 410, 510, 611, 612,
						613, 614, 615, 1510, 1410, 1310, 1210, 1110, 1011,
						1012, 1013, 1014, 1015 };
				for (int i = 0; i < turnAround.length; i++) {
					if (getCostG(current, new Coordinate(turnAround[i])) <= 14) {
						adjacent.add(new Coordinate(turnAround[i]));
					}
				}
			}
		}
		return adjacent;
	}

	/**
	 * True if c1 and c2 on turn around railway
	 */
	private boolean onTurnAroundRailway(Coordinate c1, Coordinate c2) {
		int[][] turnAround = {
				{ 601, 602, 603, 604, 605, 506, 406, 306, 206, 106 },
				{ 1001, 1002, 1003, 1004, 1005, 1106, 1206, 1306, 1406, 1506 },
				{ 110, 210, 310, 410, 510, 611, 612, 613, 614, 615 },
				{ 1510, 1410, 1310, 1210, 1110, 1011, 1012, 1013, 1014, 1015 } };
		for (int i = 0; i < turnAround.length; i++) {
			boolean foundC1 = false;
			boolean foundC2 = false;
			for (int j = 0; j < turnAround[i].length; j++) {
				if (turnAround[i][j] == c1.value)
					foundC1 = true;
				if (turnAround[i][j] == c2.value)
					foundC2 = true;
			}
			if (foundC1 && foundC2)
				return true;
		}
		return false;
	}

	/**
	 * True if c on engineer turnAround rail way
	 */
	private boolean engineerAdjacentTurnAround(Coordinate c) {
		int[] turnAround = { 506, 605, 1005, 1106, 1110, 1011, 611, 510 };
		for (int i = 0; i < turnAround.length; i++) {
			if (turnAround[i] == c.value)
				return true;
		}
		return false;
	}

	/**
	 * True if openList contains the target
	 */
	private boolean openListContains(Coordinate target) {
		for (Coordinate c : openList) {
			if (c.equals(target))
				return true;
		}
		return false;
	}

	/**
	 * True if closedList contains the target
	 */
	private boolean closedListContains(Coordinate target) {
		for (Coordinate c : closedList) {
			if (c.equals(target))
				return true;
		}
		return false;
	}

	/**
	 * Look for the Coordinate that has the min F value from openList list
	 */
	private Coordinate lookForMinF(Coordinate target) {
		Coordinate c = openList.get(0);
		for (int i = 1; i < openList.size(); i++) {
			Coordinate tmp = openList.get(i);
			if (getCostG(tmp.parent, tmp) + getDistanceH(tmp, target) < getCostG(
					c.parent, c) + getDistanceH(c, target)) {
				c = tmp;
			}
		}
		return c;
	}

	/**
	 * The G function - cost from c0 to c1
	 */
	private int getCostG(Coordinate c0, Coordinate c1) {
		// c.parent compare to c, if c is the beginning, then c.parent is NULL
		if (c0 == null || c1 == null) {
			return 0;
		}

		// Validation
		if (stations[c0.x][c0.y] == BoardUtil.INVALID
				|| stations[c1.x][c1.y] == BoardUtil.INVALID) {
			return Integer.MAX_VALUE;
		}

		if (c0.x == c1.x || c0.y == c1.y) {
			return abs(c0.x - c1.x) * 10 + abs(c0.y - c1.y) * 10;
		} else if (abs(c0.x - c1.x) == 1 && abs(c0.y - c1.y) == 1) {
			return 14;
		} else {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * The H fucntion - Manhattan distance from x0,y0 to x,y
	 */
	private int getDistanceH(int x0, int y0, int x, int y) {
		return (abs(x0 - x) + abs(y0 - y)) * 10;
	}

	private int getDistanceH(Coordinate c0, Coordinate c1) {
		return getDistanceH(c0.x, c0.y, c1.x, c1.y);
	}

	/**
	 * abs()
	 */
	private int abs(int x) {
		return x >= 0 ? x : -x;
	}

	/**
	 * Road adjacent
	 */
	private boolean roadAdjacent(int x0, int y0, int x, int y) {
		return x0 == x && abs(y0 - y) == 1 || y0 == y && abs(x0 - x) == 1;
	}

	/**
	 * Camp adjacent
	 */
	private boolean campAdjacent(int x0, int y0, int x, int y) {
		return abs(x0 - x) == 1 && abs(y0 - y) == 1;
	}

	/**
	 * Valid railway road
	 */
	private boolean validRailwayRoad(int x0, int y0, int x, int y) {
		return x0 == x
				|| y0 == y
				|| (x0 == 6 && y0 >= 1 && y0 <= 5 && y == 6 && x >= 1 && x <= 5)
				|| (y0 == 6 && x0 >= 1 && x0 <= 5 && x == 6 && y >= 1 && y <= 5)
				|| (y0 == 10 && x0 >= 1 && x0 <= 5 && x == 6 && y >= 11 && y <= 15)
				|| (x0 == 6 && y0 >= 11 && y0 <= 15 && y == 10 && x >= 1 && x <= 5)
				|| (x0 == 10 && y0 >= 11 && y0 <= 15 && y == 10 && x >= 11 && x <= 15)
				|| (y0 == 10 && x0 >= 11 && x0 <= 15 && x == 10 && y >= 11 && y <= 15)
				|| (y0 == 6 && x0 >= 11 && x0 <= 15 && x == 10 && y >= 1 && y <= 5)
				|| (x0 == 10 && y0 >= 1 && y0 <= 5 && y == 6 && x >= 11 && x <= 15);
	}
}
