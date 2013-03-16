/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Nov 23, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action.operation;

import java.awt.event.ActionEvent;

import com.javaeye.metaphy.action.BaseAction;
import com.javaeye.metaphy.game.GamePanel;

public class OperationSaveLineupFile extends BaseAction {
	/*
	 * Display the Radio-Panel
	 */
	public void actionPerformed(ActionEvent e) {
		GamePanel panel = game.getPanel();
		panel.getSaveLineupRadioPane().setVisible(true);
		
		// Hide other Operation buttons
		panel.viewAllOpButtons(false);
	}

}
