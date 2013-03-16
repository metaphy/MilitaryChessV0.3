/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Oct 28, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.action.operation;

import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;
import static com.javaeye.metaphy.game.ReplaySave.REPLAY_FILE_EXT;

import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.javaeye.metaphy.action.BaseAction;
import com.javaeye.metaphy.action.operation.OperationButton.Operations;
import com.javaeye.metaphy.action.util.GameSwingExecutor;
import com.javaeye.metaphy.game.Board;
import com.javaeye.metaphy.game.GamePanel;
import com.javaeye.metaphy.game.Game.GameStatus;
import com.javaeye.metaphy.game.ReplayView;
import com.javaeye.metaphy.threads.Adjust;

public class OperationCallInReplayFile extends BaseAction {
	/*
	 * Read the replay file
	 */
	public void actionPerformed(ActionEvent event) {
		GamePanel panel = game.getPanel();
		Board gameBoard = game.getGameBoard();

		// Stop the ChessmanAdjust Runnable
		Adjust adjustRunnable = runnableSingle.getAdjustRunnable();
		if (adjustRunnable != null) {
			adjustRunnable.setFlickerFlag(false);
			GameSwingExecutor.instance().execute(adjustRunnable);
			runnableSingle.setAdjustRunnable(null);
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("C:/log/"));
		chooser.setFileFilter(new ReplayFileFilter());
		int result = chooser.showOpenDialog(game.getContainer());

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file != null) {
				game.setGameStatus(GameStatus.REPLAY);
				panel.viewOneOpButton(Operations.START_GAME, false);
				panel.viewOneOpButton(Operations.CALLIN_LINEUP, false);
				panel.viewOneOpButton(Operations.SAVE_LINEUP, false);

				panel.viewOneOpButton(Operations.PREVIOUS_STEP, true);
				panel.viewOneOpButton(Operations.NEXT_STEP, true);
				panel.viewOneOpButton(Operations.REPLAY_END, true);

				panel.enableOneOpButton(Operations.PREVIOUS_STEP, true);
				panel.enableOneOpButton(Operations.NEXT_STEP, true);
				
				// New a replayView
				ReplayView replayView = new ReplayView();
				replayView.setReplayFile(file);
				game.setReplayView(replayView);
				
				// Reset the timer
				game.getTimer().reset();

				panel.getArrowsList().clear();
				panel.repaint();
				
				try {
					DataInputStream dis;
					// Read the file to board[][]
					byte[] b = new byte[BOARD_ARRAY_SIZE * BOARD_ARRAY_SIZE];
					dis = new DataInputStream(new FileInputStream(file));
					dis.read(b, 0, b.length);
					dis.close();

					byte[][] board = gameBoard.getBoard();
					int index = 0;
					for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
						for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
							board[i][j] = b[index++];
						}
					}
					panel.refreshAllPieces(true);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Filter of the replay files
	 */
	private class ReplayFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(REPLAY_FILE_EXT)
					|| f.isDirectory();
		}

		@Override
		public String getDescription() {
			return REPLAY_FILE_EXT.replace(".", "");
		}

	}
}
