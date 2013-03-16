/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 24, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.ai;

import static com.javaeye.metaphy.ai.XMan.PIECE_NUMBER_EACHONE;
import static com.javaeye.metaphy.ai.XMan.PIECE_TYPE_NUMBER;
import static com.javaeye.metaphy.game.Board.STORED_LINEUP_FILES_NUMBER;
import static com.javaeye.metaphy.game.Game.LINEUP_FILE_EXT;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

import com.javaeye.metaphy.game.Game;
import com.javaeye.metaphy.model.Piece;

public class LineupFilesStatistics {
	// lineup files statics
	private int[][] result = new int[PIECE_NUMBER_EACHONE][PIECE_TYPE_NUMBER];

	/* Logger */
	private static Logger logger = Logger.getLogger(LineupFilesStatistics.class);

	public LineupFilesStatistics() {
	}

	public int[][] statisticsResult() {
		long timeBegin = System.currentTimeMillis();
		boolean redo = true;
		for (int i = 0; i < PIECE_NUMBER_EACHONE; i++) {
			for (int j = 0; j < PIECE_TYPE_NUMBER; j++) {
				if (result[i][j] != 0) {
					redo = false;
					break;
				}
			}
		}
		if (redo) {
			for (int lineupIndex = 1; lineupIndex <= STORED_LINEUP_FILES_NUMBER; lineupIndex++) {
				String lineupFile = "lineup-" + lineupIndex + LINEUP_FILE_EXT;
				URLClassLoader urlLoader = (URLClassLoader) Game.class
						.getClassLoader();
				URL lineupFileURL = urlLoader.findResource("res/" + lineupFile);
				byte[] b = new byte[50];

				try {
					DataInputStream dis = new DataInputStream(
							lineupFileURL.openStream());
					dis.read(b);
					dis.close();
					int resultPos = 0;
					for (int i = 20; i < b.length; i++) {
						if (b[i] != 0x00) {
							result[resultPos++][b[i] - 2]++;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			long timeDone = System.currentTimeMillis();
			logger.debug("Lineup statics:" + (timeDone - timeBegin) + " ms");
			// print();
		}
		return result;
	}

	/**
	 * Print the result
	 * 
	 * @param result
	 */
	private void print() {
		Piece piece = new Piece();
		byte b = 0x02;
		for (int j = 0; j < 12; j++) {
			System.out.print(piece.pieceTitle((byte) (b + j)));
			System.out.print("\t");
		}
		System.out.print("\n");

		// DecimalFormat df = new DecimalFormat("#0.000");
		for (int i = 0; i < 25; i++) {
			if (i != 0 && i % 5 == 0) {
				// System.out.print("\n");
			}
			for (int j = 0; j < 12; j++) {
				// System.out
				// .print((int) Math
				// .round((result[i][j] * 1000.0 / STORED_LINEUP_FILE_NUMBER)));
				System.out.print(result[i][j]);
				System.out.print("\t");
				if (result[i][j] != 0) {
					// System.out.print("(");
					// System.out.print(df.format(result[i][j] * 1.0
					// / Board.STORED_LINEUP_FILE_NUMBER));
					// System.out.print(")");
				}
				// System.out.print("\t");
			}
			System.out.print("\n");
		}
	}

}
