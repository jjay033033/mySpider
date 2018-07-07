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

	private SingerIdFactory() {
	}

	public static SingerIdFactory getInstance() {
		return SingerIdFactoryHolder.SINGER_ID_FACTORY;
	}
	
	private static class SingerIdFactoryHolder{
		private static final SingerIdFactory SINGER_ID_FACTORY = new SingerIdFactory();
	}

	public int getSingerId(String singer) {
		int id = 0;
		synchronized (this) {
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
