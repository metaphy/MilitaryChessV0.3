/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 24, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.javaeye.metaphy.game.Game;
import com.javaeye.metaphy.threads.RunnableSingleton;

public class BaseAction implements MouseListener, KeyListener, ActionListener {
	/*
	 * The Game instance
	 */
	protected final Game game = Game.ME;
	/*
	 * Runnable singleton
	 */
	protected RunnableSingleton runnableSingle = RunnableSingleton.instance();
	
	/*
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	/*
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
