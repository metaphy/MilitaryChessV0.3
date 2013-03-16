/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 27, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.threads;

import com.javaeye.metaphy.game.Game;
import com.javaeye.metaphy.game.Board;
import com.javaeye.metaphy.model.Piece;
import com.javaeye.metaphy.sound.SoundPlayerRunnable;

public class Adjust implements Runnable {
	/* When flickering, the "showing" time and the "hiding" time */
	private static final int ON_SHOW_TIME = 320;
	private static final int ON_HIDE_TIME = 200;

	/* The board */
	private Board gameBoard = Game.ME.getGameBoard();
	/* To avoid the exchange running twice. */
	private static boolean doOnce = false;
	/* The pieces need to be exchanged */
	private Piece first = null;
	private Piece second = null;
	/* Whether or not the piece should flicker */
	private volatile boolean flickerFlag = true;
	/* To set the piece visible or not */
	private volatile boolean visible = false;

	public Adjust() {
		super();
	}

	/*
	 * Set appropriate first/second piece
	 */
	public void setAppropriatePiece(Piece piece) {
		if (first == null) {
			setFirst(piece);
			setSecond(null);
			setFlickerFlag(true);
		} else {
			setSecond(piece);
			setFlickerFlag(false);
			doOnce = false;
		}
		// Play the sound
		Game.EXEC.execute(new SoundPlayerRunnable("pick"));
	}

	/**
	 * Run method
	 */
	public void run() {
		try {
			while (flickerFlag) {
				first.setVisible(visible);
				first.renderWidgetFlicker();
				try {
					int sleepTime = visible ? ON_SHOW_TIME : ON_HIDE_TIME;
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setVisible(!visible);
			}

			// Exchange first <-> second, exchange once per two run() method
			if (!doOnce && first != null && second != null && first.getType() != second.getType()) {
				doOnce = true;

				byte[][] board = gameBoard.getBoard();
				// Model
				first.typeExchange(second);
				
				board[first.getX()][first.getY()] = first.getType();
				board[second.getX()][second.getY()] = second.getType();

				// Lineup rules compliance check
				if (!gameBoard.lineupRulesCompliance()) {
					// fall - back
					first.typeExchange(second);
					board[first.getX()][first.getY()] = first.getType();
					board[second.getX()][second.getY()]= second.getType();
				} else {
					// Refresh the view
					first.renderWidget();
					second.renderWidget();
				}
			}
			first.setVisible(true);
			first.renderWidget();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public Piece getFirst() {
		return first;
	}

	public void setFirst(Piece first) {
		this.first = first;
	}

	public Piece getSecond() {
		return second;
	}

	public void setSecond(Piece second) {
		this.second = second;
	}

	public boolean needClearSingleton() {
		return second != null;
	}

}
