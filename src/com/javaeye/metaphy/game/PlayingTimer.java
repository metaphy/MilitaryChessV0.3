/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Oct 14, 2009
 * [Updated]Sep 10, 2010
 * [Updated]Oct 28, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.game;

import static com.javaeye.metaphy.game.Game.GRID_UNIT_LENGTH;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import com.javaeye.metaphy.action.operation.OperationButton.Operations;
import com.javaeye.metaphy.action.util.GameSwingExecutor;
import com.javaeye.metaphy.game.Game.GameStatus;
import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.model.Piece;
import com.javaeye.metaphy.sound.SoundPlayerRunnable;
import com.javaeye.metaphy.threads.Move;
import com.javaeye.metaphy.threads.RunnableSingleton;

@SuppressWarnings("serial")
public class PlayingTimer extends JComponent implements Runnable {
	// The most time for each player to think and take actions (in seconds)
	public static final int PLAYER_THINKING_TIME = 30;
	// The time for the computer to think (in seconds)
	public static final int COMPUTER_THINKING_TIME = 15;
	// Timer sleep time (ms)
	public static final int TIMER_SLEEP_TIME = 5;
	// Playing the alert sound to alert the player after the ALERT_PLAYER_TIME
	private static final int ALERT_PLAYER_TIME = PLAYER_THINKING_TIME / 4 * 3;

	// Timer to keep running
	private volatile boolean keepRunning = false;
	// For painting
	private volatile int arcLen = 0;
	private volatile int counter = 0;
	// Current player (Located)
	private volatile Location currentLocated = Location.SOUTH;
	private Location[] losts = new Location[4];

	// private Logger logger = Logger.getLogger(PlayingTimer.class);
	public PlayingTimer() {
		super();
		this.setVisible(false);
		// Locate the Timer on the JPanel
		Rectangle rec = new Rectangle(GRID_UNIT_LENGTH * 13,
				GRID_UNIT_LENGTH * 2, GRID_UNIT_LENGTH * 2,
				GRID_UNIT_LENGTH * 2);
		this.setBounds(rec);
		this.setBackground(Color.black);
	}

	/**
	 * Run
	 */
	@Override
	public void run() {
		counter = 0;
		while (keepRunning) {
			counter++;

			double counterSecs = counter * TIMER_SLEEP_TIME / 1000.0;
			arcLen = ((int) counterSecs) * 360 / PLAYER_THINKING_TIME;

			repaint();
			try {
				Thread.sleep(TIMER_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Change player when the time exceeds x seconds
			if (counter / (1000 / TIMER_SLEEP_TIME) >= PLAYER_THINKING_TIME) {
				// Replay
				Game.ME.getReplaySave().oneStep();

				// Change player
				changeCurrentPlayer();
			}

			// Play sound to alert the player to take action
			if (counter / (1000 / TIMER_SLEEP_TIME) >= ALERT_PLAYER_TIME
					&& counter % (1000 / TIMER_SLEEP_TIME) == 0) {
				Game.EXEC.execute(new SoundPlayerRunnable("timer"));
			}
		}
		// logger.debug("Timer stopped! -- Current player = " + currentLocated
		// + "! Counter = " + (counterForLogger++));
	}

	/**
	 * Timer start
	 */
	public synchronized void start() {
		keepRunning = true;
	}

	/**
	 * Timer stop
	 */
	public synchronized void stop() {
		keepRunning = false;
	}

	/**
	 * one is lost
	 * 
	 * @param location
	 * @return
	 */
	private boolean isLost(Location l) {
		for (Location lost : losts) {
			if (lost != null && lost == l) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get Next Player Located
	 * 
	 * @param curr
	 * @return
	 */
	public synchronized Location nextPlayer(Location curr) {
		Location next = null;
		// Change current player.
		if (curr == Location.SOUTH) {
			if (!isLost(Location.EAST)) {
				next = Location.EAST;
			} else {
				if (!isLost(Location.NORTH)) {
					next = Location.NORTH;
				} else {
					next = Location.WEST;
				}
			}
		} else if (curr == Location.EAST) {
			if (!isLost(Location.NORTH)) {
				next = Location.NORTH;
			} else {
				if (!isLost(Location.WEST)) {
					next = Location.WEST;
				} else {
					next = Location.SOUTH;
				}
			}
		} else if (curr == Location.NORTH) {
			if (!isLost(Location.WEST)) {
				next = Location.WEST;
			} else {
				if (!isLost(Location.SOUTH)) {
					next = Location.SOUTH;
				} else {
					next = Location.EAST;
				}
			}
		} else if (curr == Location.WEST) {
			if (!isLost(Location.SOUTH)) {
				next = Location.SOUTH;
			} else {
				if (!isLost(Location.EAST)) {
					next = Location.EAST;
				} else {
					next = Location.NORTH;
				}
			}
		}

		return next;
	}

	/**
	 * Change current player
	 */
	public synchronized void changeCurrentPlayer() {
		// Initialize the variable
		counter = 0;
		// Change current player.
		currentLocated = nextPlayer(currentLocated);

		if (Game.ME.getGameStatus() == GameStatus.PLAYING) {
			// Stop the piece flickering after changing current player
			Move moveRunnable = RunnableSingleton.instance().getMoveRunnable();
			if (moveRunnable != null) {
				moveRunnable.setFlickerFlag(false);
				GameSwingExecutor.instance().execute(moveRunnable);
			}
			// AI robot begins thinking
			Game.ME.getAiCenter().setWhosTurn(currentLocated);
			Game.ME.getAiCenter().go();
			
			Game.ME.getPanel().viewOneOpButton(Operations.PASS, true);
			Game.ME.getPanel().viewOneOpButton(Operations.GIVE_UP, true);
		}
	}

	/**
	 * Whether or not the thread should keep running
	 */
	public synchronized boolean isKeepRunning() {
		return keepRunning;
	}

	public Color getBgColor(Location l) {
		// Draw background
		Color bgcolor = null;
		if (l == Location.SOUTH) {
			bgcolor = Piece.COLOR_SOUTH;
		} else if (l == Location.EAST) {
			bgcolor = Piece.COLOR_EAST;
		} else if (l == Location.NORTH) {
			bgcolor = Piece.COLOR_NORTH;
		} else if (l == Location.WEST) {
			bgcolor = Piece.COLOR_WEST;
		} else {
			bgcolor = Color.BLACK;
		}
		return bgcolor;
	}

	/**
	 * Paint
	 */
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// Anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw background
		g2.setColor(getBgColor(currentLocated));
		Ellipse2D circle = new Ellipse2D.Float(0, 0, GRID_UNIT_LENGTH * 2 - 1,
				GRID_UNIT_LENGTH * 2 - 1);
		g2.fill(circle);

		// Draw the used portion
		g2.setColor(Color.WHITE);
		Arc2D arc = new Arc2D.Float(0, 0, GRID_UNIT_LENGTH * 2 - 1,
				GRID_UNIT_LENGTH * 2 - 1, 90, -arcLen, Arc2D.PIE);
		g2.fill(arc);

		// Draw the number - The time left (seconds)
		g2.setColor(Color.black);

		int timeLeft = PLAYER_THINKING_TIME - counter
				/ (1000 / TIMER_SLEEP_TIME);

		int drawingX = 0, drawingY = 0;
		if (timeLeft < 10) {
			drawingX = GRID_UNIT_LENGTH - GRID_UNIT_LENGTH / 6 + 1;
			drawingY = GRID_UNIT_LENGTH + GRID_UNIT_LENGTH / 6;
		} else {
			drawingX = GRID_UNIT_LENGTH - GRID_UNIT_LENGTH / 3 + 2;
			drawingY = GRID_UNIT_LENGTH + GRID_UNIT_LENGTH / 6;
		}
		g2.drawString(String.valueOf(timeLeft), drawingX, drawingY);

		// Set stroke
		g2.setStroke(new BasicStroke(1.3F, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));

		g2.setColor(Color.GRAY);

		// Draw the border of the timer
		g2.draw(circle);
	}

	/**
	 * Timer reset
	 */
	public void reset() {
		this.setVisible(false);
		this.setLosts(new Location[4]);
		this.setCurrentLocated(Location.SOUTH);
	}

	/**
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * @param counter
	 */
	public void setCounter(int counter) {
		this.counter = counter;
	}

	public Location[] getLosts() {
		return losts;
	}

	public void setLosts(Location[] losts) {
		this.losts = losts;
	}

	public Location getCurrentLocated() {
		return currentLocated;
	}

	public void setCurrentLocated(Location currentPlayer) {
		this.currentLocated = currentPlayer;
	}
}
