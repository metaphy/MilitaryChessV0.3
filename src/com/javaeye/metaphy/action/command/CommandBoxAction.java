/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 24, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action.command;

import java.awt.event.KeyEvent;
import java.util.Vector;

import com.javaeye.metaphy.action.BaseAction;
import com.javaeye.metaphy.game.GamePanel;

public class CommandBoxAction extends BaseAction {
	private CommandBox commandBox;
	private CommandCenter commandCenter;

	// Cached commands
	private static Vector<String> cachedCommands = new Vector<String>();
	private static int commandIndex = -1;
	
	private static final int MAX_CACHED_COMMANDS = 200;

	public CommandBoxAction(CommandBox commandBox) {
		this.commandBox = commandBox;
		this.commandCenter = new CommandCenter();
	}

	/*
	 * Key pressed Esc - Hide the command box Enter - call command center to
	 * execute the command
	 */
	public void keyPressed(KeyEvent e) {
		GamePanel panel = game.getPanel();
		switch (e.getKeyCode()) {
		// Hide the command input box and output box
		case KeyEvent.VK_ESCAPE:
			String text = commandBox.getText();
			if (text.length() > 0) {
				commandBox.clearInputBox();
			} else {
				if (cachedCommands.size() > MAX_CACHED_COMMANDS) {
					cachedCommands = new Vector<String>();
					commandIndex = -1;
				}
				commandBox.setVisible(false);
				panel.requestFocus();
			}
			break;
		case KeyEvent.VK_ENTER: // Enter
			String command = commandBox.getInputText();
			cachedCommands.add(command);
			commandIndex = -1;
			
			commandCenter.input(command, commandBox.getOutputText());
			commandCenter.execute();
			commandBox.setOutputText(commandCenter.output());
			break;
		case KeyEvent.VK_UP: // UP
			if (cachedCommands.size() > 0) {
				if (commandIndex <0) {
					commandIndex = cachedCommands.size() - 2;
				} else if (commandIndex == 0) {
					commandIndex = cachedCommands.size() - 1;
				} else {
					commandIndex--;
				}
				if (commandIndex >= 0) {
					String cached = cachedCommands.get(commandIndex);
					commandBox.setInputText(cached);
				}
			}
			break;
		case KeyEvent.VK_DOWN: // DOWN
			if (cachedCommands.size() > 0) {
				commandIndex++;

				if (commandIndex >= cachedCommands.size()) {
					commandIndex = 0;
				}

				String cached = cachedCommands.get(commandIndex);
				commandBox.setInputText(cached);
			}
			break;
		}
	}
}
