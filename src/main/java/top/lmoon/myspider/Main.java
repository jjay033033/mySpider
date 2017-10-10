package top.lmoon.myspider;

import java.util.concurrent.ConcurrentHashMap;

import top.lmoon.myspider.dao.ApeInfoDAO;
import top.lmoon.myspider.dao.ApeInfoDAOH2DBImpl;
import top.lmoon.myspider.h2db.H2DBServer;
import top.lmoon.myspider.idfactory.SingerIdFactory;
import top.lmoon.myspider.idfactory.SongIdFactory;

public class Main {
	
	private static ApeInfoDAO dao = new ApeInfoDAOH2DBImpl();
	
	public static void main(String[] args) {
		System.out.println("started!");
		H2DBServer.start();
		dao.createTable();
		int selectMaxSingerId = dao.selectMaxSingerId();
		SingerIdFactory.setCounter(selectMaxSingerId);
		ConcurrentHashMap<String, Integer> selectSingerIdMap = dao.selectSingerIdMap();
		SingerIdFactory.setMap(selectSingerIdMap);
		int selectMaxSongId = dao.selectMaxSongId();
		SongIdFactory.setCounter(selectMaxSongId);
		ConcurrentHashMap<Integer, Integer> selectSongIdForSingerMap = dao.selectSongIdForSingerMap();
		SongIdFactory.setMap(selectSongIdForSingerMap);
		System.out.println(selectMaxSingerId);
		System.out.println(selectSingerIdMap);
		System.out.println(selectMaxSongId);
		System.out.println(selectSongIdForSingerMap);
		ApeSpider2.run();
		H2DBServer.stop();
	}

}
