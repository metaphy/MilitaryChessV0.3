/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 23, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action.operation;

import static com.javaeye.metaphy.game.Game.LINEUP_FILE_EXT;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.javaeye.metaphy.action.BaseAction;
import com.javaeye.metaphy.action.util.GameSwingExecutor;
import com.javaeye.metaphy.game.Board;
import com.javaeye.metaphy.game.GamePanel;
import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.threads.Adjust;

public class OperationCallInLineupFile extends BaseAction {
	/*
	 * Read the line up file and re-line-up all pieces of the player
	 */
	public void actionPerformed(ActionEvent event) {
		GamePanel panel = game.getPanel();
		Board gameBoard = game.getGameBoard();
		JFileChooser chooser = new JFileChooser();
		try {
			chooser.setCurrentDirectory(new File("./src/res"));
			chooser.setFileFilter(new LineupFileFilter());
			int result = chooser.showOpenDialog(game.getContainer());

			if (result == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				if (f != null) {
					try {
						gameBoard.setLineupFile(f.toURI().toURL());
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}

					// Stop the ChessmanAdjust Runnable
					Adjust adjustRunnable = runnableSingle.getAdjustRunnable();
					if (adjustRunnable != null) {
						adjustRunnable.setFlickerFlag(false);
						GameSwingExecutor.instance().execute(adjustRunnable);
						runnableSingle.setAdjustRunnable(null);
					}

					// re-load for the model
					gameBoard.loadPieces(Location.SOUTH, false);
					panel.refreshAllPieces(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Filter of the lineup files
	 */
	private class LineupFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(LINEUP_FILE_EXT)
					|| f.isDirectory();
		}

		@Override
		public String getDescription() {
			return LINEUP_FILE_EXT.replace(".", "");
		}

	}
}
