/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Oct 11, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.threads;

public class RunnableSingleton {
	private final static RunnableSingleton single = new RunnableSingleton();
	private volatile Adjust adjustRunnable;
	private volatile Move moveRunnable;

	private RunnableSingleton() {
	}

	/*
	 * Singleton
	 */
	public synchronized static RunnableSingleton instance() {
		return single;
	}

	public synchronized Adjust getAdjustRunnable() {
		return adjustRunnable;
	}

	public synchronized void setAdjustRunnable(Adjust flickerRunnable) {
		this.adjustRunnable = flickerRunnable;
	}

	public synchronized Move getMoveRunnable() {
		return moveRunnable;
	}

	public synchronized void setMoveRunnable(Move moveRunnable) {
		this.moveRunnable = moveRunnable;
	}

}
