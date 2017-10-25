/**
 * 
 */
package top.lmoon.myspider.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author LMoon
 * @date 2017年10月9日
 * 
 */
public class ThreadPool {

	private static ExecutorService threadPool = Executors.newFixedThreadPool(10);

	public static Future<?> submit(Runnable runnable) {
		return threadPool.submit(runnable);
	}
	
	public static <T> Future<T> submit(Callable<T> c) {
		return threadPool.submit(c);
	}

}
