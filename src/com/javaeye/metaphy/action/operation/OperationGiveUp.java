/**
 * Author by metaphy
 * Nov 21, 2009
 * All Rights Reserved.
 */
package com.javaeye.metaphy.action.operation;

import java.awt.event.ActionEvent;

import com.javaeye.metaphy.action.BaseAction;
import com.javaeye.metaphy.game.Game;
import com.javaeye.metaphy.game.PlayingTimer;
import com.javaeye.metaphy.game.Game.GameStatus;
import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.sound.SoundPlayer;

public class OperationGiveUp extends BaseAction {

	public void actionPerformed(ActionEvent e) {
		PlayingTimer timer = game.getTimer();
		// Validate the game status firstly
		if (game.getGameStatus() == GameStatus.PLAYING) {
			timer.stop();
			// Sound playing
			Game.EXEC.execute(new Runnable() {
				public void run() {
					SoundPlayer.play("game_end");
				}
			});
			game.roundOver(Location.EAST);
		}
	}
}
