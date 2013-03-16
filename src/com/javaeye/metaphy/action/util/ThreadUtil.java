/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Nov 18, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.action.util;

import java.util.Arrays;
import java.util.HashSet;

public class ThreadUtil {
	/**
	 * Get all threads
	 * 
	 * @return
	 */
	public static String[] getThreadNames() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup parent = null;
		while ((parent = group.getParent()) != null) {
			group = parent;
		}
		Thread[] threads = new Thread[group.activeCount()];
		group.enumerate(threads);
		HashSet<String> set = new HashSet<String>();
		
		for (int i = 0; i < threads.length; ++i) {
			if (threads[i] != null && threads[i].isAlive()) {
				try {
					set.add(threads[i].getThreadGroup().getName() + ", "
							+ threads[i].getName() + ", "
							+ threads[i].getPriority() + ", "
							+ threads[i].getState());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		String[] result = (String[]) set.toArray(new String[0]);
		Arrays.sort(result);

		for (int i = 0; i < result.length; i++) {
//			logger.debug(result[i]);
		}

		return result;
	}
}
