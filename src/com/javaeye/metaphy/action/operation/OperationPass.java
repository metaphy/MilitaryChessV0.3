/**
 * Author by metaphy
 * Nov 20, 2009
 * All Rights Reserved.
 */
package com.javaeye.metaphy.action.operation;

import java.awt.event.ActionEvent;

import com.javaeye.metaphy.action.BaseAction;
import com.javaeye.metaphy.game.PlayingTimer;
import com.javaeye.metaphy.game.Game.GameStatus;

public class OperationPass extends BaseAction {

	public void actionPerformed(ActionEvent e) {
		// Validate the game status firstly
		if (game.getGameStatus() == GameStatus.PLAYING) {
			PlayingTimer timer = game.getTimer();
			// Change current player
			timer.changeCurrentPlayer();
			
			// Save a step for Replay
			game.getReplaySave().oneStep();
		}
	}
}
