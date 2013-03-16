/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 24, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.javaeye.metaphy.game.GamePanel;

public class GamePanelAction extends BaseAction {
	private GamePanel gamePanel;

	public GamePanelAction(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	public void mouseClicked(MouseEvent e) {
//		GameBoard gameBoard = game.getGameBoard();
//		gameBoard.print();
	}

	/**
	 * Key board input event
	 * Enter - to page the Command Box
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		// call to display the command input box and output box
		case KeyEvent.VK_ENTER: 
			gamePanel.getCommandBox().setVisible(true);
			gamePanel.getCommandBox().requestFocus();
			break;
		case KeyEvent.VK_ESCAPE:
			game.quit();
		}
	}
}
