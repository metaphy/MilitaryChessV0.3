/**
 * Author by metaphy
 * Oct 28, 2010
 * All Rights Reserved.
 */
package com.javaeye.metaphy.action.operation;

import java.awt.event.ActionEvent;

import com.javaeye.metaphy.action.BaseAction;
import com.javaeye.metaphy.action.operation.OperationButton.Operations;
import com.javaeye.metaphy.game.Board;
import com.javaeye.metaphy.game.GamePanel;
import com.javaeye.metaphy.game.Game.GameStatus;

public class OperationReplayEnd extends BaseAction {

	public void actionPerformed(ActionEvent e) {
		GamePanel panel = game.getPanel();
		Board gameBoard = game.getGameBoard();

		game.setGameStatus(GameStatus.BEFORE_GAME);
		
		panel.enableOneOpButton(Operations.PREVIOUS_STEP, true);
		panel.enableOneOpButton(Operations.NEXT_STEP, true);

		panel.viewOneOpButton(Operations.START_GAME, true);
		panel.viewOneOpButton(Operations.CALLIN_LINEUP, true);
		panel.viewOneOpButton(Operations.SAVE_LINEUP, true);
		panel.viewOneOpButton(Operations.CALLIN_REPLAY, true);
		panel.viewOneOpButton(Operations.PREVIOUS_STEP, false);
		panel.viewOneOpButton(Operations.NEXT_STEP, false);
		panel.viewOneOpButton(Operations.REPLAY_END, false);

		// Re-load all pieces
		gameBoard.initBoard();
		panel.refreshAllPieces(false);

		// Clear the arrows image
		panel.getArrowsList().clear();
		panel.repaint();

		// Reset the timer
		game.getTimer().reset();
	}
}
