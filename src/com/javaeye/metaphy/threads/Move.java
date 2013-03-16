/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Oct 11, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.threads;
import static com.javaeye.metaphy.game.BoardUtil.BOMB_S;
import static com.javaeye.metaphy.game.BoardUtil.SILING_S;

import java.util.Vector;

import com.javaeye.metaphy.action.operation.OperationButton.Operations;
import com.javaeye.metaphy.ai.PathFinding;
import com.javaeye.metaphy.game.BoardUtil;
import com.javaeye.metaphy.game.Game;
import com.javaeye.metaphy.game.Game.GameStatus;
import com.javaeye.metaphy.game.GamePanel;
import com.javaeye.metaphy.game.PlayingTimer;
import com.javaeye.metaphy.game.ReplaySave;
import com.javaeye.metaphy.model.BaseElement;
import com.javaeye.metaphy.model.Coordinate;
import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.model.MoveAttackResult;
import com.javaeye.metaphy.model.Movement;
import com.javaeye.metaphy.model.Piece;
import com.javaeye.metaphy.model.SoldierStation;
import com.javaeye.metaphy.sound.SoundPlayerRunnable;

public class Move implements Runnable {
	// When flickering, the "showing" time and the "hiding" time
	private static final int ON_SHOW_TIME = 300;
	private static final int ON_HIDE_TIME = 150;
	private static final int MOVE_STANDSTILL_TIME = 55;
	private static final int THREAD_SLEEP_TIME_FOR_TIMER_STOP = 60;
	/* Game */
	private Game game = Game.ME;
	/* Replay */
	private ReplaySave replaySave = game.getReplaySave();
	/* Game board Path Finding */
	private PathFinding pf = new PathFinding(game.getGameBoard());
	/* Panel */
	private GamePanel panel = game.getPanel();
	/* To avoid running twice */
	private static boolean moveOnce = false;
	/* the first and second element */
	private Piece first = null;
	private BaseElement second = null;
	/* flicking flag */
	private volatile boolean flickerFlag = true;
	/* To set the chessman visible or not */
	private volatile boolean visible = false;
	/*
	 * Assure that after flickering the first element is visible. But it should
	 * run once only
	 */
	private boolean afterFlickingRunOnce = false;
	/* Path finding */
	private volatile Vector<Coordinate> path = null;
	/* The timer */
	private PlayingTimer timer = Game.ME.getTimer();

	public Move() {
		super();
	}

	/**
	 * Set appropriate first or second BaseElement
	 */
	public synchronized void setAppropriateMovable(BaseElement element) {
		path = null;
		if (first == null) { // Set the first
			if (element instanceof Piece
					&& ((Piece) element).getLocated() == timer
							.getCurrentLocated()) {
				setFirst((Piece) element);
				setSecond(null);
				setFlickerFlag(true);
				// Play the sound
				if (game.getGameStatus() == GameStatus.PLAYING) {
					Game.EXEC.execute(new SoundPlayerRunnable("pick"));
				}
			}
		} else if (second == null) { // Set the second
			if (first.getLocated() == timer.getCurrentLocated()) {
				// Move and attack (if it could)
				if (element instanceof SoldierStation
						|| element instanceof Piece
						&& isEnemy(((Piece) element).getLocated(),
								first.getLocated())) {
					setSecond(element);
					setFlickerFlag(false);
					path = pf.pathFinding(first.getX(), first.getY(),
							second.getX(), second.getY());
					if (path != null && path.size() > 0) {
						moveOnce = false;
					} else {
						moveOnce = true;
					}
				} else if (element instanceof Piece
						&& first.getLocated() == ((Piece) element).getLocated()) {
					// Re-select the first piece
					Piece tmp = (Piece) element;
					// Stop the first "flicking"
					setFlickerFlag(false);
					setVisible(true);
					first.setVisible(true);
					first.renderWidget();
					setFirst(tmp);
					setFlickerFlag(true);
					// Play the sound
					Game.EXEC.execute(new SoundPlayerRunnable("pick"));
				}
			}
		}
	}

	/*
	 * Run method
	 */
	@Override
	public void run() {
		try {
			if (first != null) {
				// Piece flickering
				while (isFlickerFlag()) {
					first.setVisible(visible);
					first.renderWidget();
					try {
						int sleepTime = visible ? ON_SHOW_TIME : ON_HIDE_TIME;
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setVisible(!visible);
				}

				// To assure that after flickering, the first one is visible
				if (!afterFlickingRunOnce) {
					first.setVisible(true);
					first.renderWidget();
					afterFlickingRunOnce = true;
				}

				// Piece moving only once when there is a path
				if (path != null && path.size() > 0 && !moveOnce) {
					moveOnce = true;
					// Need to check the currentPlayer again to fix Bug0001
					if (first.getLocated() == timer.getCurrentLocated()) {
						// Stop the timer to allow piece to move
						timer.stop();
						/*
						 * timer.stop() sometimes doesn't work; that's because
						 * just after timer.stop(), the Timer entered
						 * "Thread.sleep(normalSleepTime)". Then when Timer
						 * wakes up, the timer.start() was invoked. So let this
						 * thread sleep for a little time to make the
						 * timer.stop() work properly.
						 */
						Thread.sleep(THREAD_SLEEP_TIME_FOR_TIMER_STOP);

						// Save one step of the replay
						if (game.getGameStatus() == GameStatus.PLAYING) {
							replaySave
									.oneStep(
											new Coordinate(first.getX(), first
													.getY()).value,
											new Coordinate(second.getX(),
													second.getY()).value);
						}
						MoveAttackResult moveAndAttackResult = game
								.getGameBoard().boardMoveAndAttack(
										first.getX(), first.getY(),
										second.getX(), second.getY());
						// Move and attack
						pieceMoveAndAttack(moveAndAttackResult);
						// game.getGameBoard().print();
						if (moveAndAttackResult == MoveAttackResult.INVALID) {

						} else if (moveAndAttackResult == MoveAttackResult.GAME_OVER) {
							game.roundOver(first.getLocated());
						} else { // Game is NOT over
							/*
							 * Change player's turn after moving, but we need to
							 * check whether it's changed by the timer ---- To
							 * fix Bug0001
							 */
							if (first.getLocated() == timer.getCurrentLocated()) {
								timer.changeCurrentPlayer();
							}
							// Re-start and re-enable the timer
							if (game.getGameStatus() == GameStatus.PLAYING) {
								timer.start();
								Game.EXEC.execute(timer);
							}
						}

					}
				}
				RunnableSingleton.instance().setMoveRunnable(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determine whether the first attacks the second and move the first piece
	 */
	private synchronized void pieceMoveAndAttack(
			MoveAttackResult moveAndAttackResult) {
		try {
			boolean attack = false;
			// Determine whether the first attacks the second
			if (second instanceof Piece
					&& isEnemy(first.getLocated(),
							((Piece) second).getLocated())) {
				attack = true;
			}
			// Clear the arrows image
			panel.getArrowsList().clear();
			panel.repaint();

			// Moving the first chess man
			for (int i = 0; i < path.size(); i++) {
				Coordinate c = path.get(i);
				// Not attack, or attack but the Moving steps
				if (!attack || attack && (i < path.size() - 1)) {
					Movement mm = new Movement(first.getX(), first.getY(), c.x,
							c.y);
					panel.getArrowsList().add(mm);
					first.setXY(c.x, c.y);
					first.renderWidget();
					panel.repaint();
					// Play the "moving" sound
					Game.EXEC.execute(new SoundPlayerRunnable("move"));
					Thread.sleep(MOVE_STANDSTILL_TIME);
				} else { // Attack, and the last step
					Piece secondPiece = (Piece) second;
					Movement mm = new Movement(first.getX(), first.getY(),
							secondPiece.getX(), secondPiece.getY());
					panel.getArrowsList().add(mm);
					first.setXY(c.x, c.y);
					first.renderWidget();
					panel.repaint();
					if (moveAndAttackResult == MoveAttackResult.KILL) {
						secondPiece.setVisible(false);
						secondPiece.renderWidget();
						// Playing the sound
						Game.EXEC.execute(new SoundPlayerRunnable("kill"));
					} else if (moveAndAttackResult == MoveAttackResult.KILLED) {
						first.setVisible(false);
						first.renderWidget();
						if (game
								.getGameBoard().getPureType(first.getType()) == SILING_S) {
							// Si Ling is dead. Show the Flag.
							Piece flag = panel.getPieceFlag(BoardUtil
									.getLocatedByByte(first.getType()));
							if (flag != null) {
								flag.setShowCaption(true);
								flag.renderWidget();
								Game.EXEC.execute(new SoundPlayerRunnable(
										"showflag"));
							}
						} else {
							Game.EXEC
									.execute(new SoundPlayerRunnable("killed"));
						}
					} else if (moveAndAttackResult == MoveAttackResult.EQUAL) {
						first.setVisible(false);
						first.renderWidget();
						secondPiece.setVisible(false);
						secondPiece.renderWidget();
						// Si ling is dead
						if (game
								.getGameBoard().getPureType(first.getType()) == SILING_S
								|| game
								.getGameBoard().getPureType(secondPiece.getType()) == SILING_S) {
							Piece flag = null;
							if (game
									.getGameBoard().getPureType(first.getType()) == SILING_S) {
								flag = panel.getPieceFlag(BoardUtil
										.getLocatedByByte(first.getType()));
								flag.setShowCaption(true);
								flag.renderWidget();
							}
							if (game
									.getGameBoard().getPureType(secondPiece.getType()) == SILING_S) {
								flag = panel
										.getPieceFlag(BoardUtil
												.getLocatedByByte(secondPiece
														.getType()));
								flag.setShowCaption(true);
								flag.renderWidget();
							}
							Game.EXEC.execute(new SoundPlayerRunnable(
									"showflag"));
						} else {
							Game.EXEC.execute(new SoundPlayerRunnable("equal"));
						}
					} else if (moveAndAttackResult == MoveAttackResult.ONE_LOST) {
						// Set which one was failed
						Location[] losts = timer.getLosts();
						for (int li = 0; li < losts.length; li++) {
							if (losts[li] == null) {
								losts[li] = secondPiece.getLocated();
								break;
							}
						}
						timer.setLosts(losts);
						// Clear all pieces of the lost one
						for (Piece p : game.getPanel().getPieces()) {
							if (p.getLocated() == secondPiece.getLocated()) {
								p.setVisible(false);
								p.renderWidget();
							}
						}
						Game.EXEC.execute(new SoundPlayerRunnable("game_end"));
					} else if (moveAndAttackResult == MoveAttackResult.GAME_OVER) {
						secondPiece.setVisible(false);
						secondPiece.renderWidget();
						// The first is a Bomb
						if (game
								.getGameBoard().getPureType(first.getType()) == BOMB_S) {
							first.setVisible(false);
							first.renderWidget();
						}
					}
				}
			}
			if (game.getGameStatus() == GameStatus.REPLAY) {
				panel.enableOneOpButton(Operations.NEXT_STEP, true);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Is Enemy
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	private boolean isEnemy(Location p1, Location p2) {
		if ((p1 == Location.SOUTH || p1 == Location.NORTH)
				&& (p2 == Location.WEST || p2 == Location.EAST))
			return true;
		if ((p1 == Location.WEST || p1 == Location.EAST)
				&& (p2 == Location.SOUTH || p2 == Location.NORTH))
			return true;
		return false;
	}

	/*
	 * Need to clear the cached Runnable
	 */
	public boolean needClearSingleton() {
		return second != null;
	}

	public Piece getFirst() {
		return first;
	}

	public void setFirst(Piece first) {
		this.first = first;
	}

	public BaseElement getSecond() {
		return second;
	}

	public void setSecond(BaseElement second) {
		this.second = second;
	}

	public boolean isFlickerFlag() {
		return flickerFlag;
	}

	public void setFlickerFlag(boolean flickerFlag) {
		this.flickerFlag = flickerFlag;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}