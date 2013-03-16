/**
 * Author by metaphy
 * Sep 26, 2009
 * All Rights Reserved.
 */
package com.javaeye.metaphy.action.operation;

import java.awt.event.ActionEvent;

import com.javaeye.metaphy.action.BaseAction;
import com.javaeye.metaphy.action.operation.OperationButton.Operations;
import com.javaeye.metaphy.action.util.GameSwingExecutor;
import com.javaeye.metaphy.game.Game;
import com.javaeye.metaphy.game.Game.GameStatus;
import com.javaeye.metaphy.game.GamePanel;
import com.javaeye.metaphy.game.PlayingTimer;
import com.javaeye.metaphy.game.ReplaySave;
import com.javaeye.metaphy.sound.SoundPlayerRunnable;
import com.javaeye.metaphy.threads.Adjust;

public class OperationStartGame extends BaseAction {
	
	public void actionPerformed(ActionEvent e) {
		GamePanel panel = game.getPanel();
		// Verify the game status
		if (game.getGameStatus() == GameStatus.BEFORE_GAME) {
			panel.viewAllOpButtons(false);
			panel.viewOneOpButton(Operations.PASS, true);
			panel.viewOneOpButton(Operations.GIVE_UP, true);

			game.setGameStatus(GameStatus.PLAYING);

			// When the game "starts", the ChessmanAdjust Runnable should stop
			Adjust adjustRunnable = runnableSingle.getAdjustRunnable();
			if (adjustRunnable != null) {
				adjustRunnable.setFlickerFlag(false);
				// new Thread.run (runnable)
				GameSwingExecutor.instance().execute(adjustRunnable);
			}

			ReplaySave replaySave = new ReplaySave();
			game.setReplaySave(replaySave);
			
			replaySave.setBoard(game.getGameBoard().newCopyOfBoard());

			// Play the "Start Game" sound
			Game.EXEC.execute(new SoundPlayerRunnable("start"));
			
			// Initialize the probabliityTable
			game.getAiCenter().setBoard(game.getGameBoard());
			game.getAiCenter().initXManAgents();

			// Show the playing timer
			PlayingTimer timer = game.getTimer();
			timer.start();
			timer.setVisible(true);

			// Run the timer
			Game.EXEC.execute(timer);
		}
	}
}
