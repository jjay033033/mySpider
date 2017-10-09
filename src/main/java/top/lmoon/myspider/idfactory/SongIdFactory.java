/**
 * 
 */
package top.lmoon.myspider.idfactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import top.lmoon.myspider.util.CommonUtil;

/**
 * @author LMoon
 * @date 2017年9月30日
 * 
 */
public class SongIdFactory {

	private static ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<Integer, Integer>();

	private static AtomicInteger counter = new AtomicInteger(1);

	private static SongIdFactory instance;

	private SongIdFactory() {
	}

	public static SongIdFactory getInstance() {
		if (instance == null) {
			synchronized (SongIdFactory.class) {
				if (instance == null) {
					instance = new SongIdFactory();
				}
			}
		}
		return instance;
	}

	public int getSongId() {
		return counter.getAndIncrement();
	}

	public int getSongIdForSinger(int singerId) {
		int id = 1;
		synchronized (map) {
			Integer preId = map.get(singerId);
			if (preId != null) {
				id = preId + 1;
			}
			map.put(singerId, id);
		}
		return id;
	}

}
