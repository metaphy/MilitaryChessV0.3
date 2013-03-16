/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Nov 23, 2009
 * [Updated]Sep 10, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.game;

import static com.javaeye.metaphy.game.Game.GRID_UNIT_LENGTH;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import com.javaeye.metaphy.action.operation.OperationSaveLineupFileCancel;
import com.javaeye.metaphy.action.operation.OperationSaveLineupFileOK;
import com.javaeye.metaphy.model.Location;

@SuppressWarnings("serial")
public class SaveLineupRadioPane extends JPanel {
	/* which one is selected */
	private Location locSelected = Location.SOUTH;

	public SaveLineupRadioPane() {
		// Set the border
		Border paneBorder = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "保存布局");
		this.setBorder(paneBorder);

		// Locate the RadioPanel on the Game Panel
		Rectangle rec = new Rectangle(GRID_UNIT_LENGTH * 12,
				GRID_UNIT_LENGTH * 12, GRID_UNIT_LENGTH * 9 / 2,
				GRID_UNIT_LENGTH * 10 / 3);
		this.setBounds(rec);
		this.setVisible(false);

		// Add RadioButtons and buttons
		ButtonGroup radiosGroup = new ButtonGroup();
		this.add(generateRadio(radiosGroup, Location.SOUTH));
		this.add(generateRadio(radiosGroup, Location.NORTH));
		this.add(generateRadio(radiosGroup, Location.WEST));
		this.add(generateRadio(radiosGroup, Location.EAST));
		this.add(buttonOKCancel(true));
		this.add(buttonOKCancel(false));
	}

	/**
	 * Get the JRadioButton instance
	 * 
	 * @param radiosGroup
	 * @param caption
	 * @param selected
	 * @return
	 */
	private JRadioButton generateRadio(ButtonGroup radiosGroup, Location loc) {
		String caption = "ERR";
		boolean selected = false;
		if (loc == Location.SOUTH) {
			caption = "橙方   ";
			selected = true;
		} else if (loc == Location.NORTH) {
			caption = "绿方   ";
		} else if (loc == Location.WEST) {
			caption = "蓝方   ";
		} else if (loc == Location.EAST) {
			caption = "紫方   ";
		}
		JRadioButton radio = new JRadioButton(caption,selected);
		radiosGroup.add(radio);
		radio.addActionListener(new RadioAction(loc));
		return radio;
	}

	/**
	 * Inner action class
	 */
	private class RadioAction implements ActionListener {
		private Location loc;
		public RadioAction(Location loc) {
			this.loc = loc;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			locSelected = loc;
		}
	}

	/**
	 * Get a JButton instance
	 * 
	 * @param caption
	 * @return
	 */
	private JButton buttonOKCancel(boolean buttonOK) {
		JButton button = new JButton(buttonOK? "确定":"取消");
		button.setPreferredSize(new Dimension(GRID_UNIT_LENGTH * 16 / 10,
				GRID_UNIT_LENGTH * 7 / 10));
		button.setFocusable(false);

		if (buttonOK)
			button.addActionListener(new OperationSaveLineupFileOK(this));
		else
			button.addActionListener(new OperationSaveLineupFileCancel());

		return button;
	}

	public Location getLocSelected() {
		return locSelected;
	}
}
