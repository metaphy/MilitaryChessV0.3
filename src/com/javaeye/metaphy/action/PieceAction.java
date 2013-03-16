/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 3, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import com.javaeye.metaphy.action.util.GameSwingExecutor;
import com.javaeye.metaphy.game.Game.GameStatus;
import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.model.Piece;
import com.javaeye.metaphy.threads.Adjust;
import com.javaeye.metaphy.threads.Move;

public class PieceAction extends BaseAction {
	private Piece piece = null;
//	private static Logger logger = Logger.getLogger(PieceAction.class);
	
	/**
	 * Constructor
	 */
	public PieceAction(Piece piece) {
		super();
		this.piece = piece;
	}

	/**
	 * Clicking on a piece
	 */
	public void actionPerformed(ActionEvent event) {
		// Before the game, click on this to do "lineup"
		if (game.getGameStatus() == GameStatus.BEFORE_GAME) {
			// Can operate only on the SOUTH piece
			if (piece.getLocated() == Location.SOUTH) {
				Adjust adjustRunnable = runnableSingle.getAdjustRunnable();
				// Get the Runnable from cache or get it new created
				if (adjustRunnable == null) {
					adjustRunnable = new Adjust();
					runnableSingle.setAdjustRunnable(adjustRunnable);
				}
				// Initialize the first/second piece
				adjustRunnable.setAppropriatePiece(piece);

				// Clear the cached Runnable
				if (adjustRunnable.needClearSingleton()) {
					runnableSingle.setAdjustRunnable(null);
				}

				// new Thread.run (cfr)
				GameSwingExecutor.instance().execute(adjustRunnable);
			}
		} else if (game.getGameStatus() == GameStatus.PLAYING) {
			// if (game.getTimer().getCurrentLocated() == Located.SOUTH) {
				Move moveRunnable = runnableSingle.getMoveRunnable();
				// Get the Runnable from cache or get it new created
				if (moveRunnable == null) {
					moveRunnable = new Move();
					runnableSingle.setMoveRunnable(moveRunnable);
				}

				// Set first/second
				moveRunnable.setAppropriateMovable(piece);

				// GUI Executor singleton will execute the task
				GameSwingExecutor.instance().execute(moveRunnable);
			//}
		}
	}

	/*
	 * Mouse entered into the piece
	 */
	public void mouseEntered(MouseEvent e) {
		game.getPanel().setCursor(
				Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	/*
	 * Mouse exit the piece
	 */
	public void mouseExited(MouseEvent e) {
		game.getPanel().setCursor(Cursor.getDefaultCursor());
	}
}
