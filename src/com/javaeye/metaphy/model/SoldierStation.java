/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 2, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.model;
import static com.javaeye.metaphy.game.BoardUtil.CAMP;
import static com.javaeye.metaphy.game.BoardUtil.HEADQUARTER;
import static com.javaeye.metaphy.game.BoardUtil.STATION_RAILWAY;
import static com.javaeye.metaphy.game.BoardUtil.STATION_ROAD;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import com.javaeye.metaphy.action.SoldierStationAction;
import com.javaeye.metaphy.game.Game;

public class SoldierStation extends BaseElement {
	/* Side width of the Widget */
	public static final int SS_SIDE_WIDTH = Game.GRID_UNIT_LENGTH / 2;

	/* The widget of the model */
	private JLabel widget = new JLabel();

	/**
	 * @param type
	 * @param x
	 * @param y
	 */
	public SoldierStation(int x, int y, byte type) {
		super(x, y, type);
	}

	/**
	 * Render the widget
	 */
	public void renderWidget() {
		widget.setOpaque(true);
		widget.setAutoscrolls(false);
		widget.setBorder(new LineBorder(Color.BLACK));

		// Set the location of the widget
		Rectangle rec = new Rectangle(getPointX() - SS_SIDE_WIDTH / 2,
				getPointY() - SS_SIDE_WIDTH / 2, SS_SIDE_WIDTH, SS_SIDE_WIDTH);

		if (type == HEADQUARTER) { // 司令部
			// Color of the Headquarter
			Color headQuarterColor = new Color(140, 140, 140);
			widget.setBackground(headQuarterColor);
		} else if (type == CAMP) { // 行营
			widget.setBackground(Color.LIGHT_GRAY);
			rec = new Rectangle(getPointX() - (SS_SIDE_WIDTH + 2) / 2,
					getPointY() - (SS_SIDE_WIDTH + 2) / 2, SS_SIDE_WIDTH + 2,
					SS_SIDE_WIDTH + 2);
		} else if (type == STATION_ROAD
				|| type == STATION_RAILWAY) { // 兵站
			widget.setBackground(Color.white);
		}
		widget.setBounds(rec);
	}

	/**
	 * Add the action on the widget. This should be invoked only once when
	 * initializing
	 */
	public void addWidgetAction() {
		// Add an action
		SoldierStationAction action = new SoldierStationAction(this);
		widget.addMouseListener(action);
	}

	public JLabel getWidget() {
		return widget;
	}

	public void setWidget(JLabel widget) {
		this.widget = widget;
	}

}
