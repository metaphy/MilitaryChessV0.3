/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 22, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action.command;

import static com.javaeye.metaphy.model.Piece.PIECE_SIDE_WIDTH;
import static com.javaeye.metaphy.game.Game.GRID_UNIT_LENGTH;
import static com.javaeye.metaphy.model.SoldierStation.SS_SIDE_WIDTH;

import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class CommandBox extends JTextField {
	private static final long serialVersionUID = -1441293421915322036L;
	private CommandOutputBox commandOutputBox = new CommandOutputBox();
	private CommandOutputBoxWithScrollPane cobwsp = new CommandOutputBoxWithScrollPane(
			commandOutputBox);

	/*
	 * To determine whether the command is displayed on the screen
	 */
	public CommandBox() {
		super();
		this.setOpaque(false);
		Rectangle rec = new Rectangle(GRID_UNIT_LENGTH - SS_SIDE_WIDTH / 2,
				GRID_UNIT_LENGTH * 12, GRID_UNIT_LENGTH * 5 + SS_SIDE_WIDTH / 2,
				GRID_UNIT_LENGTH * 3 / 5);
		this.setBounds(rec);
		this.setVisible(false);
		// Add actions
		this.addKeyListener(new CommandBoxAction(this));
	}

	public CommandOutputBoxWithScrollPane getCobwsp() {
		return cobwsp;
	}

	public void setCobwsp(CommandOutputBoxWithScrollPane cobwsp) {
		this.cobwsp = cobwsp;
	}

	public void setVisible(boolean flag) {
		super.setVisible(flag);
		cobwsp.setVisible(flag);
	}

	public String getInputText() {
		return this.getText();
	}

	public void setInputText(String input) {
		this.setText(input);
	}

	public String getOutputText() {
		return cobwsp.getOutputText();
	}

	public void setOutputText(String output) {
		cobwsp.setOutputText(output);
	}

	/*
	 * Clear all texts of the inputbox
	 */
	public void clearInputBox() {
		this.setText("");
	}

	public void clearOutputBox() {
		cobwsp.setOutputText("");
	}
}

/*
 * Output box with scroll pane
 */
class CommandOutputBoxWithScrollPane extends JScrollPane {
	private static final long serialVersionUID = 154981954840952488L;
	private CommandOutputBox commandOutputBox = null;

	/*
	 * Constructor
	 * 
	 * @param commandOutputBox
	 */
	public CommandOutputBoxWithScrollPane(CommandOutputBox commandOutputBox) {
		super(commandOutputBox);
		this.setOpaque(commandOutputBox.isOpaque());
		this.commandOutputBox = commandOutputBox;
		this.setBounds(commandOutputBox.getBounds());
		this.getViewport().setOpaque(commandOutputBox.isOpaque());
	}

	/*
	 * Re-write setVisible method
	 */
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		commandOutputBox.setVisible(aFlag);
		if (!aFlag) {
			commandOutputBox.setText("请在上面文本框输入命令,查看所有命令请输入help或?\n按Esc键退出.\n");
		}
	}

	public String getOutputText() {
		return commandOutputBox.getText();
	}

	public void setOutputText(String outputText) {
		commandOutputBox.setText(outputText);
	}
}

/*
 * Output box
 */
class CommandOutputBox extends JTextArea {
	private static final long serialVersionUID = 7871209039904952714L;

	public CommandOutputBox() {
		super();
		this.setOpaque(false);
		Rectangle rec = new Rectangle(GRID_UNIT_LENGTH - SS_SIDE_WIDTH / 2,
				GRID_UNIT_LENGTH * 12 + GRID_UNIT_LENGTH * 3 / 5, GRID_UNIT_LENGTH * 5
						+ SS_SIDE_WIDTH / 2, GRID_UNIT_LENGTH * 4 + GRID_UNIT_LENGTH * 2
						/ 5 + PIECE_SIDE_WIDTH / 2);
		this.setBounds(rec);
		this.setAutoscrolls(true);
		this.setLineWrap(true);
	}
}
