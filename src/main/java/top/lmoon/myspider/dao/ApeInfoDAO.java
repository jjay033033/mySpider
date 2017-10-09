package top.lmoon.myspider.dao;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import top.lmoon.myspider.vo.ApeInfoVO;

public interface ApeInfoDAO {
	
	int createTable();
	
	int insert(ApeInfoVO vo);
	
	List<ApeInfoVO> select(int pageNo,int pageSize,String singer,String title);
	
	int count(String singer,String title);
	
	int selectMaxSingerId();
	
	ConcurrentHashMap<String, Integer> selectSingerIdMap();
	
	int selectMaxSongId();
	
	ConcurrentHashMap<Integer, Integer> selectSongIdForSingerMap();

}
