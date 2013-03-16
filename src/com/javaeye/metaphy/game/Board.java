/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 1, 2009
 * [Updated]Sep 10, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.game;

import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_E;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_N;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_S;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_W;
import static com.javaeye.metaphy.game.BoardUtil.CAMP;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_E;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_N;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_S;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_W;
import static com.javaeye.metaphy.game.BoardUtil.GONGBING_S;
import static com.javaeye.metaphy.game.BoardUtil.HEADQUARTER;
import static com.javaeye.metaphy.game.BoardUtil.INVALID;
import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.MINE_E;
import static com.javaeye.metaphy.game.BoardUtil.MINE_N;
import static com.javaeye.metaphy.game.BoardUtil.MINE_S;
import static com.javaeye.metaphy.game.BoardUtil.MINE_W;
import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.SILING_S;
import static com.javaeye.metaphy.game.BoardUtil.STATION_RAILWAY;
import static com.javaeye.metaphy.game.BoardUtil.STATION_ROAD;
import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_S;
import static com.javaeye.metaphy.game.Game.LINEUP_FILE_EXT;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Random;

import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.model.MoveAttackResult;
import com.javaeye.metaphy.model.Piece;

public class Board {
	/* Line-up files max index */
	public static final int STORED_LINEUP_FILES_NUMBER = 200;
	/* the default file length: 50 bytes */
	public static final int LINEUP_FILE_BYTE_LENGTH = 50;
	private static int failedCounter = 0;

	/* board byte array and station byte array */
	private byte[][] stations = new byte[BOARD_ARRAY_SIZE][BOARD_ARRAY_SIZE];
	private byte[][] board = new byte[BOARD_ARRAY_SIZE][BOARD_ARRAY_SIZE];
	/* The line up file */
	private URL lineupFileURL = null;

	// private static Logger logger = Logger.getLogger(Board.class);

	/**
	 * Constructor, to init the stations(roads) and the board
	 */
	public Board() {
		// Init the Board -SoldierStations and Roads, pieces
		initSoldierStations();
		initBoard();
	}

	/**
	 * Init the board
	 */
	public void initSoldierStations() {
		// All the points
		for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
			for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
				if (i >= 0 && i <= 5
						&& (j >= 0 && j <= 5 || j >= 11 && j <= 16) || i >= 11
						&& i <= 16 && (j >= 0 && j <= 5 || j >= 11 && j <= 16)
						|| (i == 6 || i == 8 || i == 10) && (j == 7 || j == 9)
						|| (i == 7 || i == 9) && (j >= 6 && j <= 10)) {
					// 无效点
					stations[i][j] = INVALID;
				} else if ((i == 7 || i == 9) && (j == 0 || j == 16)
						|| (i == 0 || i == 16) && (j == 7 || j == 9)) {
					// 大本营(司令部)
					stations[i][j] = HEADQUARTER;
				} else if ((i == 7 || i == 9)
						&& (j == 2 || j == 4 || j == 12 || j == 14) || i == 8
						&& (j == 3 || j == 13)
						|| (i == 2 || i == 4 || i == 12 || i == 14)
						&& (j == 7 || j == 9) || (i == 3 || i == 13) && j == 8) {
					// 行营
					stations[i][j] = CAMP;
				} else if ((i == 0 || i == 16) && (j == 6 || j == 8 || j == 10)
						|| (i == 2 || i == 4 || i == 12 || i == 14) && j == 8
						|| (i == 3 || i == 13) && (j == 7 || j == 9)
						|| (j == 0 || j == 16) && (i == 6 || i == 8 || i == 10)
						|| (j == 2 || j == 4 || j == 12 || j == 14) && i == 8
						|| (j == 3 || j == 13) && (i == 7 || i == 9)) {
					// 非行营、非大本营的公路节点
					stations[i][j] = STATION_ROAD;
				} else {
					// 其他，即含铁路的节点
					stations[i][j] = STATION_RAILWAY;
				}
			}
		}
	}

	/**
	 * Init all pieces of the board
	 */
	public void initBoard() {
		for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
			for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
				board[i][j] = 0;
			}
		}
		loadPieces(Location.SOUTH, false);
		loadPieces(Location.NORTH, false);
		loadPieces(Location.WEST, false);
		loadPieces(Location.EAST, false);
	}

	/**
	 * Get a random line up file from "../res/" which name is like
	 * "lineup-x.jql"
	 */
	public void randomLineupFile() {
		Random random = new Random();
		int indx = random.nextInt(STORED_LINEUP_FILES_NUMBER) + 1;
		String file = "lineup-" + indx + LINEUP_FILE_EXT;
		URLClassLoader urlLoader = (URLClassLoader) Game.class.getClassLoader();
		lineupFileURL = urlLoader.findResource("res/" + file);
	}

	/**
	 * Get the piece according to the index. Flag, mine * 3, bomb * 2, gong * 3,
	 * pai *3, lian *3, ying *2, tuan *2, lv *2, shi *2, jun, siling
	 */
	private byte sortedPiece(int index) {
		if (index == 0) {
			return FLAG_S;
		} else if (index >= 1 && index <= 3) {
			return MINE_S;
		} else if (index >= 4 && index <= 5) {
			return BOMB_S;
		} else if (index >= 6 && index <= 8) {
			return GONGBING_S;
		} else if (index >= 9 && index <= 11) {
			return PAIZHANG_S;
		} else if (index >= 12 && index <= 14) {
			return LIANZHANG_S;
		} else if (index >= 15 && index <= 16) {
			return YINGZHANG_S;
		} else if (index >= 17 && index <= 18) {
			return TUANZHANG_S;
		} else if (index >= 19 && index <= 20) {
			return LVZHANG_S;
		} else if (index >= 21 && index <= 22) {
			return SHIZHANG_S;
		} else if (index == 23) {
			return JUNZHANG_S;
		} else if (index == 24) {
			return SILING_S;
		} else {
			System.err.println("ERR");
			return INVALID;
		}
	}

	/**
	 * Whether or not obey the lineup Rules. True if all are ok.
	 */
	public boolean lineupRulesCompliance(byte[] b) {
		// 军旗没有在司令部
		if (b[46] != FLAG_S && b[48] != FLAG_S) {
			return false;
		}
		for (int i = 20; i < b.length; i++) {
			// 炸弹在第一排
			if (i >= 20 && i < 25 && b[i] == BOMB_S) {
				return false;
			}
			// 地雷没有在后两排(地雷在前四排)
			if (i < 40 && b[i] == MINE_S) {
				return false;
			}
		}
		return true;
	}

	public boolean lineupRulesCompliance() {
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				// 军旗没有在司令部
				if ((board[i][j] == FLAG_N || board[i][j] == FLAG_S
						|| board[i][j] == FLAG_W || board[i][j] == FLAG_E)
						&& stations[i][j] != HEADQUARTER) {
					return false;
				}
				// 炸弹在第一排
				if (board[i][j] == BOMB_N && j == 5 || board[i][j] == BOMB_S
						&& j == 11 || board[i][j] == BOMB_W && i == 5
						|| board[i][j] == BOMB_E && i == 11) {
					return false;
				}
				// 地雷没有在后两排
				if (board[i][j] == MINE_N && j != 0 && j != 1
						|| board[i][j] == MINE_S && j != 15 && j != 16
						|| board[i][j] == MINE_W && i != 0 && i != 1
						|| board[i][j] == MINE_E && i != 15 && i != 16) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Init the pieces
	 * 
	 * @param located
	 * @param random
	 */
	public void loadPieces(Location located, boolean random) {
		byte[] bytes = new byte[LINEUP_FILE_BYTE_LENGTH];
		int x = 0; // The x Coordinate
		int y = 0; // The y Coordinate
		DataInputStream dis = null;

		if (random) { // Generate the line-up randomly
			byte[] b = new byte[LINEUP_FILE_BYTE_LENGTH];
			// true: the piece is arranged
			boolean[] pieceArranged = new boolean[25];
			Random r = new Random();
			int leftRand = r.nextInt(100), rightRand = r.nextInt(100); // 0~99
			int flagHeadQuarterIndex, anotherHeadQuarterIndex;
			int minesArranged = 0;

			if (leftRand < 50) { // The flag is in the left head quarter
				flagHeadQuarterIndex = 46;
				anotherHeadQuarterIndex = 48;
			} else {
				flagHeadQuarterIndex = 48;
				anotherHeadQuarterIndex = 46;
			}
			b[flagHeadQuarterIndex] = FLAG_S;
			pieceArranged[0] = true; // the flag is done
			if (rightRand < 45) { // 45% Mine is in another head quarter
				b[anotherHeadQuarterIndex] = MINE_S;
				minesArranged = 1;
				pieceArranged[1] = true;
			} else if (rightRand < 98) { // 53% PaiZhang
				b[anotherHeadQuarterIndex] = PAIZHANG_S;
				pieceArranged[9] = true;
			} else { // %2 LianZhang
				b[anotherHeadQuarterIndex] = LIANZHANG_S;
				pieceArranged[12] = true;
			}
			// Arrange the Mines
			while (minesArranged < 3) {
				int positionMine = 40 + r.nextInt(10);
				while (b[positionMine] != INVALID) {
					positionMine = 40 + r.nextInt(10);
				}
				b[positionMine] = MINE_S;
				minesArranged++;
				pieceArranged[minesArranged] = true;
			}
			for (int i = 20; i < 50; i++) {
				if (i != 26 && i != 28 && i != 32 && i != 36 && i != 38
						&& b[i] == INVALID) {
					int index = r.nextInt(25);
					while (pieceArranged[index]) {
						index = r.nextInt(25);
					}
					b[i] = sortedPiece(index);
					pieceArranged[index] = true;
				}
			}
			// Verify the line-up
			if (!lineupRulesCompliance(b)) {
				// logger.debugln("Auto Line-up Failed once");
				failedCounter++;
				loadPieces(located, random);
			} else {
				// logger.debugln("Good job! Failed times counter = "
				// + failedCounter);
				failedCounter = 0;
				for (int i = 20; i < b.length; i++) {
					bytes[i] = b[i];
				}
			}
		} else { // Read bytes from the file
			// Initialize lineupFile if it's null
			if (lineupFileURL == null) {
				randomLineupFile();
			}
			// logger.debugln(located + ":\t" + lineupFileURL.getFile());
			try {
				dis = new DataInputStream(lineupFileURL.openStream());
				dis.read(bytes, 0, LINEUP_FILE_BYTE_LENGTH);
				lineupFileURL = null; // Reset the variable
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Ignore the first 20 bytes
		for (int i = 20; i < bytes.length; i++) {
			if (bytes[i] != INVALID) {
				if (located == Location.SOUTH) {
					x = 6 + (i - 20) % 5;
					y = 11 + (i - 20) / 5;
					board[x][y] = bytes[i];
				} else if (located == Location.NORTH) {
					x = 10 - (i - 20) % 5;
					y = 5 - (i - 20) / 5;
					board[x][y] = (byte) ((int) bytes[i] + 0x10);
				} else if (located == Location.WEST) {
					x = 5 - (i - 20) / 5;
					y = 6 + (i - 20) % 5;
					board[x][y] = (byte) ((int) bytes[i] + 0x20);
				} else if (located == Location.EAST) {
					x = 11 + (i - 20) / 5;
					y = 10 - (i - 20) % 5;
					board[x][y] = (byte) ((int) bytes[i] + 0x30);
				}
			}
		}
		if (!random) { // Read from file
			try {
				if (dis != null)
					dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Get a copy of board[][]
	 */
	public byte[][] newCopyOfBoard() {
		byte[][] ret = new byte[BOARD_ARRAY_SIZE][BOARD_ARRAY_SIZE];
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				ret[i][j] = board[i][j];
			}
		}
		return ret;
	}

	/**
	 * Recover the board
	 */
	public void recoverBoard(byte[][] b) {
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				this.board[i][j] = b[i][j];
			}
		}
	}

	/**
	 * Move and attack
	 */
	public MoveAttackResult boardMoveAndAttack(int x0, int y0, int x, int y) {
		// Validation
		if (stations[x0][y0] == INVALID || stations[x][y] == INVALID
				|| board[x0][y0] == INVALID || board[x][y] != INVALID
				&& BoardUtil.sameLocation(board[x0][y0], board[x][y])) {
			return MoveAttackResult.INVALID;
		}
		// Move only
		if (board[x][y] == INVALID) {
			board[x][y] = board[x0][y0];
			board[x0][y0] = INVALID;
			return MoveAttackResult.MOVE;
		}
		// Attack
		if (getPureType(board[x][y]) == FLAG_S) {
			Location locatedLost = BoardUtil.getLocatedByByte(board[x][y]);
			// One lost
			board[x][y] = board[x0][y0];
			board[x0][y0] = INVALID;
			// codes here to check the Game Over or not
			int flagsCount = 0;
			byte[] flags = new byte[3];

			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
					if (stations[i][j] == HEADQUARTER
							&& getPureType(board[i][j]) == FLAG_S) {
						flags[flagsCount++] = board[i][j];
					}
				}
			}
			// Judge the result by the flags left
			if (flagsCount == 3 || flagsCount == 2
					&& BoardUtil.isEnemy(flags[0], flags[1])) {
				// clear the lost one
				for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
					for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
						if (BoardUtil.getLocatedByByte(board[i][j]) == locatedLost) {
							board[i][j] = INVALID;
						}
					}
				}
				return MoveAttackResult.ONE_LOST;
			} else if (flagsCount == 1 || flagsCount == 2
					&& !BoardUtil.isEnemy(flags[0], flags[1])) {
				return MoveAttackResult.GAME_OVER;
			} else {
				System.err.println("ERRORs");
				return null;
			}
		} else if (getPureType(board[x0][y0]) == BOMB_S
				|| getPureType(board[x][y]) == BOMB_S) {
			// Bomb
			board[x0][y0] = INVALID;
			board[x][y] = INVALID;
			return MoveAttackResult.EQUAL;
		} else if (getPureType(board[x][y]) == MINE_S) {
			if (getPureType(board[x0][y0]) == GONGBING_S) {
				// Mine
				board[x][y] = board[x0][y0];
				board[x0][y0] = INVALID;
				return MoveAttackResult.KILL;
			} else {
				board[x0][y0] = INVALID;
				return MoveAttackResult.KILLED;
			}
		} else {
			// Soldiers
			byte soldier0 = getPureType(board[x0][y0]);
			byte soldier1 = getPureType(board[x][y]);
			if (soldier0 < soldier1) {
				board[x][y] = board[x0][y0];
				board[x0][y0] = INVALID;
				return MoveAttackResult.KILL;
			} else if (soldier0 == soldier1) {
				board[x0][y0] = INVALID;
				board[x][y] = INVALID;
				return MoveAttackResult.EQUAL;
			} else {
				board[x0][y0] = INVALID;
				return MoveAttackResult.KILLED;
			}
		}
	}

	/**
	 * Getter, setter
	 */
	public URL getLineupFile() {
		return lineupFileURL;
	}

	public void setLineupFile(URL lineupFile) {
		this.lineupFileURL = lineupFile;
	}

	public byte[][] getBoard() {
		return board;
	}

	public void setBoard(byte[][] board) {
		this.board = board;
	}

	public byte[][] getStations() {
		return stations;
	}

	/**
	 * Print the board[][], for test
	 */
	public void print(byte[][] pboard) {
		Piece piece = new Piece();
		// Print all pieces
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				if (pboard[i][j] == 0x00) {
					System.out.print("-");
				} else {
					System.out.print(piece.pieceTitle(pboard[i][j]));
					// System.out.print(pboard[i][j]);
					if (BoardUtil.getLocatedByByte(pboard[i][j]) == Location.SOUTH) {
						System.out.print("S");
					} else if (BoardUtil.getLocatedByByte(pboard[i][j]) == Location.EAST) {
						System.out.print("E");
					} else if (BoardUtil.getLocatedByByte(pboard[i][j]) == Location.NORTH) {
						System.out.print("N");
					} else if (BoardUtil.getLocatedByByte(pboard[i][j]) == Location.WEST) {
						System.out.print("W");
					}

				}
				System.out.print("\t");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}

	/**
	 * Print itself
	 */
	public void print() {
		print(board);
	}

	/*
	 * public void print(byte[][] pboard) { Piece piece = new Piece();
	 * StringBuffer sb = new StringBuffer ();
	 * 
	 * // Print all pieces for (int j = 0; j < BOARD_ARRAY_SIZE; j++) { for (int
	 * i = 0; i < BOARD_ARRAY_SIZE; i++) { if (pboard[i][j] == 0x00) {
	 * sb.append("-"); } else { sb.append(piece.pieceTitle(pboard[i][j])); //
	 * sb.append(pboard[i][j]); if (Board.getLocatedByByte(pboard[i][j]) ==
	 * Location.SOUTH) { sb.append("S"); } else if
	 * (Board.getLocatedByByte(pboard[i][j]) == Location.EAST) { sb.append("E");
	 * } else if (Board.getLocatedByByte(pboard[i][j]) == Location.NORTH) {
	 * sb.append("N"); } else if (Board.getLocatedByByte(pboard[i][j]) ==
	 * Location.WEST) { sb.append("W"); } } sb.append("\t"); } sb.append("\n");
	 * } sb.append("\n"); logger.debug(sb.toString()); }
	 */

	/**
	 * Convert the 0x3a, 0x2a, 0x1a to 0x0a
	 * 
	 * @param p
	 * @return
	 */
	public byte getPureType(byte p) {
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

	// public static void main(String[] args) {
	// Board b = new Board();
	// b.print();
	// }
}
