/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 25, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.ai;

import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_S;
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
import static com.javaeye.metaphy.game.BoardUtil.BOMB_N;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_N;
import static com.javaeye.metaphy.game.BoardUtil.GONGBING_N;
import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.MINE_N;
import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.SILING_N;
import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_W;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_W;
import static com.javaeye.metaphy.game.BoardUtil.GONGBING_W;
import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_W;
import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_W;
import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_W;
import static com.javaeye.metaphy.game.BoardUtil.MINE_W;
import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_W;
import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_W;
import static com.javaeye.metaphy.game.BoardUtil.SILING_W;
import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_W;
import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_W;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_E;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_E;
import static com.javaeye.metaphy.game.BoardUtil.GONGBING_E;
import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_E;
import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_E;
import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_E;
import static com.javaeye.metaphy.game.BoardUtil.MINE_E;
import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_E;
import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_E;
import static com.javaeye.metaphy.game.BoardUtil.SILING_E;
import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_E;
import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_E;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.javaeye.metaphy.game.Board;
import com.javaeye.metaphy.game.BoardUtil;
import com.javaeye.metaphy.model.Coordinate;
import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.model.Movement;

/**
 * Alpha-beta search for the game
 */
public class ABSearch {
	// who's turn to play
	private Location whosTurn;

	protected static final int FLAG_VALUE = 100000;
	protected Board board = new Board();
	protected PathFinding pf = new PathFinding();
	protected Movement bestMove = null;
	// logger
	private static final Logger logger = Logger.getLogger(ABSearch.class);

	public ABSearch() {

	}

	/**
	 * Possible moves
	 */
	public Vector<Movement> possibleMoves(Location player) {
		Vector<Movement> moves = new Vector<Movement>();
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				// 把工兵的行动放到最后
				if (player == BoardUtil
						.getLocatedByByte(board.getBoard()[i][j])
						&& BoardUtil.pureType(board.getBoard()[i][j]) != GONGBING_S) {
					for (int tj = 0; tj < BOARD_ARRAY_SIZE; tj++) {
						for (int ti = 0; ti < BOARD_ARRAY_SIZE; ti++) {
							Vector<Coordinate> path = pf.pathFinding(i, j, ti,
									tj);
							if (path != null && path.size() > 0) {
								Movement move = new Movement(i, j, ti, tj);
								moves.add(move);
							}
						}
					}
				}
			}
		}
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				// 把工兵的行动放到最后
				if (player == BoardUtil
						.getLocatedByByte(board.getBoard()[i][j])
						&& BoardUtil.pureType(board.getBoard()[i][j]) == GONGBING_S) {
					for (int tj = 0; tj < BOARD_ARRAY_SIZE; tj++) {
						for (int ti = 0; ti < BOARD_ARRAY_SIZE; ti++) {
							Vector<Coordinate> path = pf.pathFinding(i, j, ti,
									tj);
							if (path != null && path.size() > 0) {
								Movement move = new Movement(i, j, ti, tj);
								moves.add(move);
							}
						}
					}
				}
			}
		}
		logger.debug("[" + whosTurn + "] possibleMoves: " + moves.size());
		for (Movement m: moves){
			logger.debug(m.toString());
		}
		return moves;
	}

	/**
	 * Evaluation
	 */
	public int evaluation(Location player) {
		// byte[][] stations = board.getStations();
		byte[][] pb = board.getBoard();
		int value = 0;
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				// 子力加和分数
				if (pb[i][j] == SILING_S || pb[i][j] == SILING_N) {
					value += 350;
				} else if (pb[i][j] == JUNZHANG_S || pb[i][j] == JUNZHANG_N) {
					value += 260;
				} else if (pb[i][j] == SHIZHANG_S || pb[i][j] == SHIZHANG_N) {
					value += 170;
				} else if (pb[i][j] == LVZHANG_S || pb[i][j] == LVZHANG_N) {
					value += 120;
				} else if (pb[i][j] == TUANZHANG_S || pb[i][j] == TUANZHANG_N) {
					value += 90;
				} else if (pb[i][j] == YINGZHANG_S || pb[i][j] == YINGZHANG_N) {
					value += 70;
				} else if (pb[i][j] == LIANZHANG_S || pb[i][j] == LIANZHANG_N) {
					value += 40;
				} else if (pb[i][j] == PAIZHANG_S || pb[i][j] == PAIZHANG_N) {
					value += 20;
				} else if (pb[i][j] == GONGBING_S || pb[i][j] == GONGBING_N) {
					value += 60;
				} else if (pb[i][j] == BOMB_S || pb[i][j] == BOMB_N) {
					value += 130;
				} else if (pb[i][j] == MINE_S || pb[i][j] == MINE_N) {
					value += 39;
				} else if (pb[i][j] == FLAG_S || pb[i][j] == FLAG_N) {
					value += FLAG_VALUE;
				} else if (pb[i][j] == SILING_W || pb[i][j] == SILING_E) {
					value -= 350;
				} else if (pb[i][j] == JUNZHANG_W || pb[i][j] == JUNZHANG_E) {
					value -= 260;
				} else if (pb[i][j] == SHIZHANG_W || pb[i][j] == SHIZHANG_E) {
					value -= 170;
				} else if (pb[i][j] == LVZHANG_W || pb[i][j] == LVZHANG_E) {
					value -= 120;
				} else if (pb[i][j] == TUANZHANG_W || pb[i][j] == TUANZHANG_E) {
					value -= 90;
				} else if (pb[i][j] == YINGZHANG_W || pb[i][j] == YINGZHANG_E) {
					value -= 70;
				} else if (pb[i][j] == LIANZHANG_W || pb[i][j] == LIANZHANG_E) {
					value -= 40;
				} else if (pb[i][j] == PAIZHANG_W || pb[i][j] == PAIZHANG_E) {
					value -= 20;
				} else if (pb[i][j] == GONGBING_W || pb[i][j] == GONGBING_E) {
					value -= 60;
				} else if (pb[i][j] == BOMB_W || pb[i][j] == BOMB_E) {
					value -= 130;
				} else if (pb[i][j] == MINE_W || pb[i][j] == MINE_E) {
					value -= 39;
				} else if (pb[i][j] == FLAG_W || pb[i][j] == FLAG_E) {
					value -= FLAG_VALUE;
				}

				// 旗左右的位置是killer招法，这主要弥补搜索深度的不足
				if ((pb[i][j] == FLAG_W || pb[i][j] == FLAG_E)
						&& (BoardUtil.getLocatedByByte(pb[i][j - 1]) == Location.NORTH
								|| BoardUtil.getLocatedByByte(pb[i][j - 1]) == Location.SOUTH
								|| BoardUtil.getLocatedByByte(pb[i][j + 1]) == Location.NORTH || BoardUtil
								.getLocatedByByte(pb[i][j + 1]) == Location.SOUTH)) {
					value += FLAG_VALUE / 10;
				} else if ((pb[i][j] == FLAG_S || pb[i][j] == FLAG_N)
						&& (BoardUtil.getLocatedByByte(pb[i - 1][j]) == Location.NORTH
								|| BoardUtil.getLocatedByByte(pb[i - 1][j]) == Location.SOUTH
								|| BoardUtil.getLocatedByByte(pb[i + 1][j]) == Location.NORTH || BoardUtil
								.getLocatedByByte(pb[i + 1][j]) == Location.SOUTH)) {
					value -= FLAG_VALUE / 10;
				}
			}
		}
		// if (BoardUtil.isEnemy(whosTurn, player)) {
		// value = -value;
		// }
		return value;
	}

	/**
	 * Whether the game is over.
	 */
	public boolean isGameOver(Location player) {
		return evaluation(player) > FLAG_VALUE / 2;
	}

	/**
	 * Alpha-beta search
	 */
	public int alphaBeta(int depth, Location player, int alpha, int beta) {
		if (depth == 0 || isGameOver(player)) {
			return evaluation(player);
		}
		// Movement best = null;
		Vector<Movement> moves = possibleMoves(player);
		// For each possible move
		for (int i = 0; i < moves.size(); i++) {
			Movement move = moves.get(i);
			byte[][] boardCopy = board.newCopyOfBoard();
			// Make move
			board.boardMoveAndAttack(move.startx(), move.starty(), move.endx(),
					move.endy());
			// _________________________________________________________
			int value = -alphaBeta(depth - 1, player, -beta, -alpha);
			// _________________________________________________________

			// Undo the move
			board.recoverBoard(boardCopy);
			if (value >= alpha) {
				alpha = value;
			}
			if (alpha >= beta) {
				break;
			}
		}
		return alpha;
	}

	public void bestMovement() {

	}

	/**
	 * set the board byte array
	 * 
	 * @param pb
	 */
	public void setBoardBytes(byte[][] pb) {
		this.board.setBoard(pb);
	}

	public Location getWhosTurn() {
		return whosTurn;
	}

	public void setWhosTurn(Location whosTurn) {
		this.whosTurn = whosTurn;
	}

}
