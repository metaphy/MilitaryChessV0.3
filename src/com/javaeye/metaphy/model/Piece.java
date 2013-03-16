/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 3, 2009
 * [Updated]Sep 10, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.model;

import static com.javaeye.metaphy.game.BoardUtil.BOMB_S;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_S;
import static com.javaeye.metaphy.game.BoardUtil.GONGBING_S;
import static com.javaeye.metaphy.game.BoardUtil.JUNZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.LIANZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.LVZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.MINE_S;
import static com.javaeye.metaphy.game.BoardUtil.PAIZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.SHIZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.SILING_S;
import static com.javaeye.metaphy.game.BoardUtil.TUANZHANG_S;
import static com.javaeye.metaphy.game.BoardUtil.YINGZHANG_S;
import static com.javaeye.metaphy.game.Game.GRID_UNIT_LENGTH;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.javaeye.metaphy.action.PieceAction;
import com.javaeye.metaphy.game.BoardUtil;

public class Piece extends BaseElement {
	/* The side width of the Piece of chess */
	public static final int PIECE_SIDE_WIDTH = GRID_UNIT_LENGTH * 33 / 38;
	/* Colors */
	public static final Color COLOR_SOUTH = Color.ORANGE;
	public static final Color COLOR_NORTH = Color.GREEN;
	public static final Color COLOR_WEST = new Color(100, 210, 250);
	public static final Color COLOR_EAST = new Color(255, 111, 233);

	/* Show the caption or not */
	private boolean showCaption = true; // false in the PROD
	/* visible on the board or not */
	private boolean visible = true;
	private Location located = null;
	/* The widget of the model */
	private JButton widget = null;

	public Piece() {
	}

	public Piece(int x, int y, byte type) {
		super(x, y, type);
		// Initialize the widget
		widget = new JButton();
		widget.setAutoscrolls(false);
		widget.setOpaque(true);
		Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED,
				Color.white, Color.white, Color.gray, Color.gray);
		widget.setBorder(border);
		widget.setFocusable(false);
	}

	/**
	 * Render the piece widget
	 */
	public void renderWidget() {
		this.located = BoardUtil.getLocatedByByte(type);
		// Display the color of the piece
		switch (located) {
		case SOUTH:
			widget.setBackground(COLOR_SOUTH);
			break;
		case NORTH:
			widget.setBackground(COLOR_NORTH);
			break;
		case WEST:
			widget.setBackground(COLOR_WEST);
			break;
		case EAST:
			widget.setBackground(COLOR_EAST);
			break;
		}

		// Display the caption
		widget.setText(showCaption ? pieceTitle() : "");

		// Locate the piece according to piece.coordinate
		Rectangle rec = new Rectangle(getPointX() - PIECE_SIDE_WIDTH / 2,
				getPointY() - PIECE_SIDE_WIDTH / 2, PIECE_SIDE_WIDTH,
				PIECE_SIDE_WIDTH);
		widget.setBounds(rec);
		// Set the widget to be visible or not
		widget.setVisible(visible);
	}

	/**
	 * To let the widget visible or not visible
	 */
	public void renderWidgetFlicker() {
		// Set the widget to be visible or not
		widget.setVisible(isVisible());
	}

	/**
	 * Add the action on the widget. This should be invoked only once when
	 * initializing
	 */
	public void addWidgetAction() {
		// Add an action, each piece(button) has new created Action
		PieceAction action = new PieceAction(this);
		widget.addActionListener(action);
		widget.addMouseListener(action);
	}

	/**
	 * Type exchange
	 * 
	 * @param p
	 */
	public void typeExchange(Piece second) {
		byte tmp = this.getType();
		this.setType(second.getType());
		second.setType(tmp);
	}

	/**
	 * Get Piece caption
	 */
	public String pieceTitle() {
		return pieceTitle(this.type);
	}

	/**
	 * Solider type <--> caption
	 */
	public String pieceTitle(byte type) {
		String title = "n/a";
		switch (BoardUtil.pureType(type)) {
		case FLAG_S:
			title = "军旗";
			break;
		case MINE_S:
			title = "地雷";
			break;
		case BOMB_S:
			title = "炸弹";
			break;
		case SILING_S:
			title = "司令";
			break;
		case JUNZHANG_S:
			title = "军长";
			break;
		case SHIZHANG_S:
			title = "师长";
			break;
		case LVZHANG_S:
			title = "旅长";
			break;
		case TUANZHANG_S:
			title = "团长";
			break;
		case YINGZHANG_S:
			title = "营长";
			break;
		case LIANZHANG_S:
			title = "连长";
			break;
		case PAIZHANG_S:
			title = "排长";
			break;
		case GONGBING_S:
			title = "工兵";
			break;
		}
		return title;
	}

	/*
	 * Getters and Setters
	 */
	public boolean isShowCaption() {
		return showCaption;
	}

	public void setShowCaption(boolean showCaption) {
		this.showCaption = showCaption;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Location getLocated() {
		return located;
	}

	public void setLocated(Location located) {
		this.located = located;
	}

	public JButton getWidget() {
		return widget;
	}

	public void setWidget(JButton widget) {
		this.widget = widget;
	}

}
