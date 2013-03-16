/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 25, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.ai;

import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_N;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_S;
import static com.javaeye.metaphy.game.BoardUtil.CAMP;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_N;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_S;
import static com.javaeye.metaphy.game.BoardUtil.GONGBING_N;
import static com.javaeye.metaphy.game.BoardUtil.GONGBING_S;
import static com.javaeye.metaphy.game.BoardUtil.HEADQUARTER;
import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.MINE_N;
import static com.javaeye.metaphy.game.BoardUtil.MINE_S;
import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.SILING_N;
import static com.javaeye.metaphy.game.BoardUtil.SILING_S;
import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_N;
import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_S;

import java.util.Vector;

import com.javaeye.metaphy.game.Board;
import com.javaeye.metaphy.game.BoardUtil;
import com.javaeye.metaphy.game.Game;
import com.javaeye.metaphy.game.GamePanel;
import com.javaeye.metaphy.model.Coordinate;
import com.javaeye.metaphy.model.Movement;

/**
 * Alpha-beta search for the game
 */
public class GameSearch1V1 {
	protected static final int FLAG_VALUE = 10000000;
	protected Game game = Game.ME;
	protected Board gameBoard = game.getGameBoard();
	protected PathFinding pf = new PathFinding(gameBoard);
	protected GamePanel gamePanel = game.getPanel();
	protected Movement bestMove = null;

	public GameSearch1V1() {
	}

	/**
	 * Possible moves
	 */
	public Vector<Movement> possibleMoves(boolean player) {
		Vector<Movement> moves = new Vector<Movement>();
		for (int j = BOARD_ARRAY_SIZE - 1; j >= 0; j--) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				if (player && gameBoard.getBoard()[i][j] > 0x10 || !player
						&& gameBoard.getBoard()[i][j] < 0x10
						&& gameBoard.getBoard()[i][j] != BoardUtil.INVALID) {
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
		// Collections.sort(moves);
		// for (int i = 0; i < moves.size(); i++) {
		// logger.debug(moves.get(i));
		// }
		return moves;
	}

	/**
	 * Evaluation, @parameter true = NORTH
	 */
	public int evaluation(boolean player) {
		byte[][] stations = gameBoard.getStations();
		byte[][] board = gameBoard.getBoard();
		int value = 0;
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				// 子力加和分数
				if (board[i][j] == SILING_N) {
					value += 350;
				} else if (board[i][j] == JUNZHANG_N) {
					value += 260;
				} else if (board[i][j] == SHIZHANG_N) {
					value += 170;
				} else if (board[i][j] == LVZHANG_N) {
					value += 120;
				} else if (board[i][j] == TUANZHANG_N) {
					value += 90;
				} else if (board[i][j] == YINGZHANG_N) {
					value += 70;
				} else if (board[i][j] == LIANZHANG_N) {
					value += 40;
				} else if (board[i][j] == PAIZHANG_N) {
					value += 20;
				} else if (board[i][j] == GONGBING_N) {
					value += 60;
				} else if (board[i][j] == BOMB_N) {
					value += 130;
				} else if (board[i][j] == MINE_N) {
					value += 39;
				} else if (board[i][j] == FLAG_N) {
					value += FLAG_VALUE;
				} else if (board[i][j] == SILING_S) {
					value -= 350;
				} else if (board[i][j] == JUNZHANG_S) {
					value -= 260;
				} else if (board[i][j] == SHIZHANG_S) {
					value -= 170;
				} else if (board[i][j] == LVZHANG_S) {
					value -= 120;
				} else if (board[i][j] == TUANZHANG_S) {
					value -= 90;
				} else if (board[i][j] == YINGZHANG_S) {
					value -= 70;
				} else if (board[i][j] == LIANZHANG_S) {
					value -= 40;
				} else if (board[i][j] == PAIZHANG_S) {
					value -= 20;
				} else if (board[i][j] == GONGBING_S) {
					value -= 60;
				} else if (board[i][j] == BOMB_S) {
					value -= 130;
				} else if (board[i][j] == MINE_S) {
					value -= 39;
				} else if (board[i][j] == FLAG_S) {
					value -= FLAG_VALUE;
				}

				// 旗左右的位置是killer招法，这主要弥补搜索深度的不足
				if (board[i][j] == FLAG_S
						&& (board[i + 1][j] > 0x10 || board[i - 1][j] > 0x10)) {
					value += FLAG_VALUE / 100;
				} else if (board[i][j] == FLAG_N
						&& board[i + 1][j] < 0x10
						&& board[i + 1][j] != BoardUtil.INVALID
						|| board[i][j] == FLAG_N
						&& board[i - 1][j] < 0x10
						&& board[i - 1][j] != BoardUtil.INVALID) {
					value -= FLAG_VALUE / 100;
				}

				// 要破三角雷
				if (board[i][j] == FLAG_N
						&& board[i + 1][j] == MINE_N
						&& board[i - 1][j] == MINE_N
						&& board[i][j + 1] == MINE_N) {
					value += 200;
				} else if (board[i][j] == FLAG_S
						&& board[i + 1][j] == MINE_S
						&& board[i - 1][j] == MINE_S
						&& board[i][j - 1] == MINE_S) {
					value -= 200;
				}

				// 不要进非旗的大本营
				if (stations[i][j] == HEADQUARTER && board[i][j] > 0x10) {
					value -= 100;
				} else if (stations[i][j] == HEADQUARTER
						&& board[i][j] < 0x10
						&& board[i][j] != BoardUtil.INVALID) {
					value += 100;
				}

				// 占对方旗上的行营，这个位置很重要
				if (board[i][j] == FLAG_S && board[i][j - 2] > 0x10) {
					value += 45;
				} else if (board[i][j] == FLAG_N
						&& board[i][j + 2] < 0x10
						&& board[i][j + 2] != BoardUtil.INVALID) {
					value -= 45;
				}

				// 旗上面的位置也很重要
				if (board[i][j] == FLAG_S && board[i][j - 1] > 0x10) {
					value += 20;
				} else if (board[i][j] == FLAG_N
						&& board[i][j + 1] < 0x10
						&& board[i][j + 1] != BoardUtil.INVALID) {
					value -= 20;
				}

				// 攻占对方底线加分（鼓励进攻和加强防守）
				if (j == 15 && board[i][j] > 0x10) {
					value += 8;
				} else if (j == 1 && board[i][j] < 0x10
						&& board[i][j] != BoardUtil.INVALID) {
					value -= 8;
				}

				// 占有利位置
				if (i == 6 && j == 6 && board[i][j] > 0x10 || i == 6 && j == 10
						&& board[i][j] > 0x10 || i == 10 && j == 6
						&& board[i][j] > 0x10 || i == 10 && j == 10
						&& board[i][j] > 0x10) {
					value += 6;
				} else if (i == 6 && j == 6 && board[i][j] < 0x10
						&& board[i][j] != BoardUtil.INVALID || i == 6
						&& j == 10 && board[i][j] < 0x10
						&& board[i][j] != BoardUtil.INVALID || i == 10
						&& j == 6 && board[i][j] < 0x10
						&& board[i][j] != BoardUtil.INVALID || i == 10
						&& j == 10 && board[i][j] < 0x10
						&& board[i][j] != BoardUtil.INVALID) {
					value -= 6;
				}

				// 其他行营占分
				if (stations[i][j] == CAMP && board[i][j] > 0x10) {
					value += 5;
				} else if (stations[i][j] == CAMP && board[i][j] < 0x10
						&& board[i][j] != BoardUtil.INVALID) {
					value -= 5;
				}
			}
		}

		if (!player) { // 负值最大搜索。对当前一方而言，如果占优则返回正数，否则返回负数
			value = -value;
		}
		return value;
	}

	/**
	 * True if game is over.
	 */
	public boolean isGameOver(boolean player) {
		return evaluation(player) > FLAG_VALUE / 2;
	}

	/**
	 * Alpha-beta search
	 */
	public int alphaBeta(int depth, boolean player, int alpha, int beta) {
		if (depth == 0 || isGameOver(player)) {
			return evaluation(player);
		}
		// Movement best = null;
		Vector<Movement> moves = possibleMoves(player);
		// For each possible move
		for (int i = 0; i < moves.size(); i++) {
			Movement move = moves.get(i);
			byte[][] boardCopy = gameBoard.newCopyOfBoard();
			// Make move
			gameBoard.boardMoveAndAttack(move.startx(), move.starty(),
					move.endx(), move.endy());
			int value = -alphaBeta(depth - 1, !player, -beta, -alpha);
			// Undo the move
			gameBoard.recoverBoard(boardCopy);
			if (value >= alpha) {
				alpha = value;
			}
			if (alpha >= beta) {
				break;
			}
		}
		return alpha;
	}
}
