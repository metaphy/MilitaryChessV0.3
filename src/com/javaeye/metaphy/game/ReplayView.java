/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Oct 28, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.game;

import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReplayView {
	private File replayFile = null;
	private int currentStep = 0;
	private boolean signal = true;

	public ReplayView() {

	}

	/**
	 * Get next step
	 * 
	 * @return
	 */
	public int[] nextStep() {
		int index = BOARD_ARRAY_SIZE * BOARD_ARRAY_SIZE;
		int[] steps = new int[2];
		DataInputStream dis;
		try {
			if (replayFile != null) {
				dis = new DataInputStream(new FileInputStream(replayFile));
				// Additional 8 bytes to store the start&end
				byte[] b = new byte[index + (currentStep + 1) * 2 * 4];
				dis.read(b);
				dis.close();

				byte[] start = new byte[4];
				byte[] end = new byte[4];
				for (int i = 0; i < start.length; i++) {
					start[i] = b[index + currentStep * 2 * 4 + i];
					end[i] = b[index + currentStep * 2 * 4 + 4 + i];
				}
				steps[0] = ReplaySave.byte2int(start);
				steps[1] = ReplaySave.byte2int(end);
				// System.out.println ("current step: " + currentStep);
				// System.out.println (steps[0]);
				// System.out.println (steps[1]);
				currentStep++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return steps;
	}

	/**
	 * Get previous step
	 * 
	 * @return
	 */
	public int[] previousStep() {
		int[] steps = new int[2];

		return steps;

	}

	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public File getReplayFile() {
		return replayFile;
	}

	public void setReplayFile(File replayFile) {
		this.replayFile = replayFile;
	}

	public boolean isSignal() {
		return signal;
	}

	public void setSignal(boolean signal) {
		this.signal = signal;
	}

	// public static void main(String[] a) {
	// ReplayView rv = new ReplayView();
	// rv.setReplayFile(new File("c:/log/test.fup"));
	// rv.nextStep();
	// }

}
