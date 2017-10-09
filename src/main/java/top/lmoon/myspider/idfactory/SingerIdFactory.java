/**
 * 
 */
package top.lmoon.myspider.idfactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LMoon
 * @date 2017年9月30日
 * 
 */
public class SingerIdFactory {

	private static ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();

	private static AtomicInteger counter = new AtomicInteger(0);

	private static SingerIdFactory instance;

	private SingerIdFactory() {
	}

	public static SingerIdFactory getInstance() {
		if (instance == null) {
			synchronized (SingerIdFactory.class) {
				if (instance == null) {
					instance = new SingerIdFactory();
				}
			}
		}
		return instance;
	}

	public int getSingerId(String singer) {
		int id = 0;
		synchronized (map) {
			if (map.containsKey(singer)) {
				id = map.get(singer);
			} else {
				id = counter.incrementAndGet();
				map.put(singer, id);
			}
		}
		return id;
	}
	
	public static void setMap(ConcurrentHashMap<String, Integer> map){
		SingerIdFactory.map = map;
	}
	
	public static void setCounter(int num){
		counter.set(num);;
	}

}
