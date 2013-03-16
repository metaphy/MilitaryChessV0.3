/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Oct 22, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.game;

import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ReplaySave {
	public static final String REPLAY_FILE_EXT = ".fup"; // FuPan
	public static final String REPLAY_FILE_NAME_SUFFIX = "jq_";

	private String replayFilePath = "C:/log/";
	private byte[][] board = new byte[BOARD_ARRAY_SIZE][BOARD_ARRAY_SIZE];
	private Vector<Integer> steps = new Vector<Integer>();

	public ReplaySave() {
	}

	/**
	 * Save one step
	 * 
	 * @param start
	 * @param end
	 */
	public void oneStep(int start, int end) {
		steps.add(start);
		steps.add(end);
	}

	/**
	 * Save a "pass"
	 */
	public void oneStep() {
		steps.add(0);
		steps.add(0);
	}

	/**
	 * Replay file EOF
	 */
	public void stepEnd() {
		steps.add(-1);
		steps.add(-1);
	}

	/**
	 * save the replay to file
	 * 
	 * @return
	 */
	public void save() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String fileName = REPLAY_FILE_NAME_SUFFIX + sdf.format(new Date())
				+ REPLAY_FILE_EXT;

		if (!createTheReplayFolder()){ 
			replayFilePath = "c:/";
		}
		
		// fileName = "test.fup"; // Test
		File file = new File(replayFilePath + fileName);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			byte[] b = new byte[BOARD_ARRAY_SIZE * BOARD_ARRAY_SIZE
					+ steps.size() * 4];
			// Save the board bytes
			int index = 0;
			for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
				for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
					b[index++] = board[i][j];
				}
			}
			// Save the replay steps
			for (int step : steps) {
				// System.out.println(step);
				byte[] stepbytes = int2bytes(step);
				b[index++] = stepbytes[0];
				b[index++] = stepbytes[1];
				b[index++] = stepbytes[2];
				b[index++] = stepbytes[3];
			}
			fos.write(b);
			fos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Create the replay save directory if it doesn't exist
	 * 
	 * @return
	 */
	public boolean createTheReplayFolder() {
		File folder = new File(replayFilePath);

		if (!folder.exists() || !folder.isDirectory()) {
			boolean createdDone = folder.mkdirs();
			if (createdDone) {
				return true;
			} else {
				System.err.println("创建复盘保存文件夹"+replayFilePath+"失败，复盘文件将保存于C:根目录下"  );
				return false;
			}
		} else {
			return true;
		}

	}

	public String getReplayFilePath() {
		return replayFilePath;
	}

	public void setReplayFilePath(String savedReplayFilePath) {
		this.replayFilePath = savedReplayFilePath;
	}

	public byte[][] getBoard() {
		return board;
	}

	public void setBoard(byte[][] board) {
		this.board = board;
	}

	/**
	 * int <-> byte[]
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] int2bytes(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) (n >>> (24 - i * 8));
		}
		return b;
	}

	public static int byte2int(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

//	public static void main(String[] a) {
//		ReplaySave rs = new ReplaySave();
//		// int in = -1;
//		// byte[] b = int2bytes(in);
//		// int x = byte2int(b);
//		//
//		// System.out.println(x);
//		rs.createTheReplayFolder();
//	}

}
