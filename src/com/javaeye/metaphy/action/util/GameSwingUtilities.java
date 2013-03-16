/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 29, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action.util;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class GameSwingUtilities {
	private static final ExecutorService SINGLE_EXEC = Executors
			.newSingleThreadExecutor(new SwingThreadFactory());
	private static volatile Thread swingThread;

	/*
	 * Inner class to construct the ExecutorService EXEC
	 */
	private static class SwingThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			swingThread = new Thread(r);
			return swingThread;
		}
	}

	public static boolean isEventDispatchThread() {
		return Thread.currentThread() == swingThread;
	}

	public static void invokeLater(Runnable task) {
		SINGLE_EXEC.execute(task);
	}

	public static void invokeAndWait(Runnable task)
			throws InterruptedException, InvocationTargetException {
		Future f = SINGLE_EXEC.submit(task);
		try {
			f.get();
		} catch (ExecutionException e) {
			throw new InvocationTargetException(e);
		}
	}
}
