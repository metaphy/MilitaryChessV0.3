/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 29, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action.util;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class GameSwingExecutor extends AbstractExecutorService {
	// Singletons have a private constructor and a public factory
	private static final GameSwingExecutor single = new GameSwingExecutor();

	private GameSwingExecutor() {
	}

	public static GameSwingExecutor instance() {
		return single;
	}

	/**
	 * To ensure only the EventDispatchThread can operate Swing component
	 */
	public void execute(Runnable r) {
		if (GameSwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			GameSwingUtilities.invokeLater(r);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#awaitTermination(long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#isShutdown()
	 */
	@Override
	public boolean isShutdown() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#shutdown()
	 */
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#shutdownNow()
	 */
	@Override
	public List<Runnable> shutdownNow() {
		// TODO Auto-generated method stub
		return null;
	}

	// Plus trivial implementations of lifecycle methods
}
