/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 2, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.ai;

import static com.javaeye.metaphy.ai.XMan.PIECE_NUMBER_EACHONE;
import static com.javaeye.metaphy.ai.XMan.PIECE_TYPE_NUMBER;
import static com.javaeye.metaphy.game.Board.STORED_LINEUP_FILES_NUMBER;
import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_S;
import static com.javaeye.metaphy.game.BoardUtil.CAMP;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_S;
import static com.javaeye.metaphy.game.BoardUtil.GONGBING_S;
import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.MINE_S;
import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.SILING_S;
import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_S;

import java.util.Random;

import org.apache.log4j.Logger;

import com.javaeye.metaphy.game.Board;
import com.javaeye.metaphy.game.BoardUtil;
import com.javaeye.metaphy.model.Coordinate;
import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.model.Piece;

public class XManAgent {
	// Who's agent
	private Location whos;
	// Board instance
	private Board board; // = new Board();
	// All XMan
	private XMan[] xmen = new XMan[PIECE_NUMBER_EACHONE * 4];
	// logger
	private static final Logger logger = Logger.getLogger(XManAgent.class);

	public XManAgent() {
	}

	public XManAgent(Location whos) {
		this.whos = whos;
	}

	/**
	 * The lineupFiles Statics to initialize
	 */
	public void init(int[][] result) {
		int index = 0;
		for (int j = 11; j <= 16; j++) { // South
			for (int i = 6; i <= 10; i++) {
				if (board.getStations()[i][j] != CAMP) {
					xmen[index] = new XMan();
					int id = j * 100 + i;
					xmen[index].setId(id);
					xmen[index].setCoordinate(new Coordinate(id));
					if (whos == Location.SOUTH) {
						xmen[index].setPiece(board.getBoard()[i][j]);
					} else {
						xmen[index].setPiece(BoardUtil.INVALID);
						xmen[index].setProbability12(result[index]);
					}
					index++;
				}
			}
		}

		for (int i = 5; i >= 0; i--) { // West
			for (int j = 6; j <= 10; j++) {
				if (board.getStations()[i][j] != CAMP) {
					xmen[index] = new XMan();
					int id = j * 100 + i;
					xmen[index].setId(id);
					xmen[index].setCoordinate(new Coordinate(id));
					if (whos == Location.WEST) {
						xmen[index]
								.setPiece(board.getPureType(board.getBoard()[i][j]));
					} else {
						xmen[index].setPiece(BoardUtil.INVALID);
						xmen[index].setProbability12(result[index
								- PIECE_NUMBER_EACHONE]);
					}
					index++;
				}
			}
		}

		for (int j = 5; j >= 0; j--) { // North
			for (int i = 10; i >= 6; i--) {
				if (board.getStations()[i][j] != CAMP) {
					xmen[index] = new XMan();
					int id = j * 100 + i;
					xmen[index].setId(id);
					xmen[index].setCoordinate(new Coordinate(id));
					if (whos == Location.NORTH) {
						xmen[index]
								.setPiece(board.getPureType(board.getBoard()[i][j]));
					} else {
						xmen[index].setPiece(BoardUtil.INVALID);
						xmen[index].setProbability12(result[index
								- PIECE_NUMBER_EACHONE * 2]);
					}
					index++;
				}
			}
		}

		for (int i = 11; i <= 16; i++) { // East
			for (int j = 10; j >= 6; j--) {
				if (board.getStations()[i][j] != CAMP) {
					xmen[index] = new XMan();
					int id = j * 100 + i;
					xmen[index].setId(id);
					xmen[index].setCoordinate(new Coordinate(id));
					if (whos == Location.EAST) {
						xmen[index]
								.setPiece(board.getPureType(board.getBoard()[i][j]));
					} else {
						xmen[index].setPiece(BoardUtil.INVALID);
						xmen[index].setProbability12(result[index
								- PIECE_NUMBER_EACHONE * 3]);
					}
					index++;
				}
			}
		}
		// print0();
	}

	/**
	 * Generate a random board
	 * 
	 * @return
	 */
	public byte[][] probabilityBoard() {
		byte[][] pb = new byte[BOARD_ARRAY_SIZE][BOARD_ARRAY_SIZE];
		byte[][] pbs = probabilityBoard(Location.SOUTH); // Only for SOUTH
		byte[][] pbw = probabilityBoard(Location.WEST);
		byte[][] pbn = probabilityBoard(Location.NORTH);
		byte[][] pbe = probabilityBoard(Location.EAST);

		// Add four boards array into ONE
		for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
			for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
				pb[i][j] = (byte) (pbs[i][j] + pbw[i][j] + pbn[i][j] + pbe[i][j]);
			}
		}
		boolean valid = probabilityBoardValidate(pb);
		logger.debug("[" + whos + "] ProbabilityBoard valid: " + valid);

		return pb;
	}

	/**
	 * Generate a random board by Location
	 * 
	 * @return
	 */
	public byte[][] probabilityBoard(Location loc) {
		byte[][] pb = new byte[BOARD_ARRAY_SIZE][BOARD_ARRAY_SIZE];
		Random r = new Random();
		int baseStart = BoardUtil.locationToOrder(loc) * PIECE_NUMBER_EACHONE;
		byte[] pieceArray = { FLAG_S, MINE_S, BOMB_S, GONGBING_S, SILING_S,
				JUNZHANG_S, SHIZHANG_S, LVZHANG_S, TUANZHANG_S, YINGZHANG_S,
				PAIZHANG_S, LIANZHANG_S };
		byte[] pieceArrayNumber = { 1, 3, 2, 3, 1, 1, 2, 2, 2, 2, 2, 2 };
		// index represents the position
		byte[] piece25 = new byte[PIECE_NUMBER_EACHONE];
		int[] pieceTypeConfirmedNum = new int[12];
		int pieceNum = 0;

		long timeBegin = System.currentTimeMillis();
		int counter = 0;

		for (int i = baseStart; i < baseStart + PIECE_NUMBER_EACHONE; i++) {
			if (xmen[i].getPiece() != BoardUtil.INVALID) { // Piece confirmed
				piece25[i - baseStart] = xmen[i].getPiece();
				// Piece number
				pieceTypeConfirmedNum[xmen[i].getPiece() - 2]++;
			}
		}

		for (int i = 0; i < pieceArray.length; i++) {
			pieceNum = pieceTypeConfirmedNum[BoardUtil
					.indexFromPiece12(pieceArray[i])];
			while (pieceNum < pieceArrayNumber[i]) {
				counter++;
				int ri = r.nextInt(STORED_LINEUP_FILES_NUMBER) + 1;
				int sum = 0, k = 0;
				for (k = baseStart; k < baseStart + PIECE_NUMBER_EACHONE; k++) {
					sum += xmen[k].getProbability()[BoardUtil
							.indexFromPiece(pieceArray[i])];
					if (sum >= ri) {
						break;
					}
				}
				k = k - baseStart;
				if (k != PIECE_NUMBER_EACHONE
						&& piece25[k] == BoardUtil.INVALID) {
					piece25[k] = pieceArray[i];
					pieceNum++;
				}
				// ERROR Handle
				if (counter > 1000) {
					logger.error("Error in probabilityBoard()");
					i = pieceArray.length;
					break;
				}
			}
		}
		// The last two are NOT arranged randomly
		int indx = 11;
		for (int i = 0; i < piece25.length; i++) {
			if (piece25[i] == BoardUtil.INVALID) {
				piece25[i] = pieceArray[indx--];
			}
		}

		byte offset = 0;
		if (loc == Location.NORTH) {
			offset = 0x10;
		} else if (loc == Location.WEST) {
			offset = 0x20;
		} else if (loc == Location.EAST) {
			offset = 0x30;
		}
		for (int i = baseStart; i < baseStart + PIECE_NUMBER_EACHONE; i++) {
			if (!xmen[i].isDead()) {
				pb[xmen[i].getCoordinate().x][xmen[i].getCoordinate().y] = (byte) (piece25[i
						- baseStart] + offset);
			}
		}

		long timeDone = System.currentTimeMillis();
		logger.debug("ProbabilityBoard loop:" + counter);
		//logger.debug("ProbabilityBoard Generate Time:" + (timeDone - timeBegin)+ " ms");
		// --------------------------------------------
		/*
		 * Piece p = new Piece(); for (int i = 0; i < PIECE_NUMBER_EACHONE; i++)
		 * { System.out.print(i); System.out.print("-");
		 * System.out.print(p.pieceTitle(tmp25[i])); System.out.print(", "); if
		 * ((i + 1) % 5 == 0) { System.out.print("\n"); } }
		 */
		return pb;
	}

	/**
	 * probabilityBoardValidate
	 * 
	 * @return
	 */
	public boolean probabilityBoardValidate(byte[][] pb) {
		final int LOCATION_NUMBER = 4;
		int[][] counter = new int[LOCATION_NUMBER][PIECE_TYPE_NUMBER];
		int index = 0;

		for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
			for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
				Location loc = BoardUtil.getLocatedByByte(pb[i][j]);
				if (loc != null) {
					index = BoardUtil.locationToOrder(loc);
					counter[index][board.getPureType(pb[i][j]) - 2]++;
				}
			}
		}

		for (int loci = 0; loci < LOCATION_NUMBER; loci++) {
			if (counter[loci][0] > 1 || counter[loci][3] > 1
					|| counter[loci][4] > 1) {
				return false;
			}
			if (counter[loci][2] > 2 || counter[loci][5] > 2
					|| counter[loci][6] > 2 || counter[loci][7] > 2
					|| counter[loci][8] > 2) {
				return false;
			}

			if (counter[loci][1] > 3 || counter[loci][9] > 3
					|| counter[loci][10] > 3 || counter[loci][11] > 3) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Update the probability table
	 */
	public void updateTable(byte p) {

	}

	public Location getWhos() {
		return whos;
	}

	public void setWhos(Location whos) {
		this.whos = whos;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Print the XMan.probabilityTable
	 */
	private void print0() {
		Piece piece = new Piece();
		for (int i = 0; i < 25; i++) {
			System.out.print(piece.pieceTitle(BoardUtil.pieceFromIndex(
					Location.SOUTH, i)));
			System.out.print("\t");
		}
		System.out.print("\n");
		for (int i = 25; i < 25 * 2; i++) {
			for (int j = 0; j < xmen[i].getProbability().length; j++) {
				System.out.print(xmen[i].getProbability()[j]);
				System.out.print("\t");
			}
			System.out.print("\n");
		}
	}

	// public static void main(String[] args) {
	// long t0 = System.currentTimeMillis();
	// XManAgent xma = new XManAgent(Location.SOUTH);
	// int i = 0;
	// byte[][] pb = xma.probabilityBoard();
	// Board b = new Board();
	// b.print(pb);
	//
	// System.out.println(xma.probabilityBoardValidate(pb));
	// long t1 = System.currentTimeMillis();
	// System.out.println(t1 - t0);
	// }
}
