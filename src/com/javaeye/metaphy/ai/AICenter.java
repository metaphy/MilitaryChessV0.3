/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 24, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.ai;

import com.javaeye.metaphy.game.Board;
import com.javaeye.metaphy.game.BoardUtil;
import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.model.Movement;

public class AICenter {
	// who's turn to play
	private Location whosTurn;
	// GameBoard
	private Board board;
	// LineupFilesStatistics
	private LineupFilesStatistics lfs = new LineupFilesStatistics();
	// All the agents
	private XManAgent[] xManAgents = new XManAgent[4];
	// Search agent
	private ABSearch aiSearch = new ABSearch();

	public AICenter() {
	}

	/**
	 * initProbabilityTable
	 */
	public void initXManAgents() {
		xManAgents[0] = new XManAgent(Location.SOUTH);
		xManAgents[1] = new XManAgent(Location.WEST);
		xManAgents[2] = new XManAgent(Location.NORTH);
		xManAgents[3] = new XManAgent(Location.EAST);

		int[][] result = lfs.statisticsResult();

		for (int i = 0; i < xManAgents.length; i++) {
			xManAgents[i].setBoard(board);
			xManAgents[i].init(result);
		}
		// board.print(xManAgents[1].probabilityBoard());
	}

	public void go() {
		int agentIndex = BoardUtil.locationToOrder(whosTurn);
		int loop = 3;

		// Debug
		while (loop-- > 0) {
			byte[][] pb = xManAgents[agentIndex].probabilityBoard();
			aiSearch.setBoardBytes(pb);
			//aiSearch.bestMove();
			// add(beasMove);
		}

	}

	public Movement getFrequentMove() {
		return null;
	}

	public Location getWhosTurn() {
		return whosTurn;
	}

	public void setWhosTurn(Location whosTurn) {
		this.whosTurn = whosTurn;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public LineupFilesStatistics getLfs() {
		return lfs;
	}

	public void setLfs(LineupFilesStatistics lfs) {
		this.lfs = lfs;
	}
}
