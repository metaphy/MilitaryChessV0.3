/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Oct 11, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import com.javaeye.metaphy.action.util.GameSwingExecutor;
import com.javaeye.metaphy.game.Game.GameStatus;
import com.javaeye.metaphy.model.SoldierStation;
import com.javaeye.metaphy.threads.Move;

public class SoldierStationAction extends BaseAction {
	private SoldierStation ss = null;

	/**
	 * @param ss
	 */
	public SoldierStationAction(SoldierStation ss) {
		super();
		this.ss = ss;
	}

	/*
	 * Mouse clicked action
	 */
	public void mouseClicked(MouseEvent e) {
		if (game.getGameStatus() == GameStatus.PLAYING) {
			// if(game.getTimer().getCurrentLocated() == Located.SOUTH) {
			Move moveRunnable = runnableSingle.getMoveRunnable();
			// First != null and Second == null
			if (moveRunnable != null && moveRunnable.getFirst() != null
					&& moveRunnable.getSecond() == null) {

				moveRunnable.setAppropriateMovable(ss);
				// GUI Executor singleton will execute the task
				GameSwingExecutor.instance().execute(moveRunnable);
			}
		}
	}

	/*
	 * Mouse entered into the SoldierStation
	 */
	public void mouseEntered(MouseEvent e) {
		if (game.getGameStatus() == GameStatus.PLAYING) {
			game.getPanel().setCursor(
					Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			game.getPanel().setCursor(Cursor.getDefaultCursor());
		}
	}

	/*
	 * Mouse exit the SoldierStation
	 */
	public void mouseExited(MouseEvent e) {
		game.getPanel().setCursor(Cursor.getDefaultCursor());
	}
}
