package top.lmoon.myspider.dao;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import top.lmoon.myspider.util.DownloadUtil.downloadType;
import top.lmoon.myspider.vo.ApeInfoVO;

public interface ApeInfoDAO {
	
	int createTable();
	
	int insert(ApeInfoVO vo);
	
	int update(ApeInfoVO vo);
	
	int update(int songId,downloadType type);
	
	ApeInfoVO select(int songId);
	
	List<ApeInfoVO> select(int pageNo,int pageSize,String singer,String title);
	
	int count(String singer,String title);
	
	int selectMaxSingerId();
	
	ConcurrentHashMap<String, Integer> selectSingerIdMap();
	
	int selectMaxSongId();
	
	ConcurrentHashMap<Integer, Integer> selectSongIdForSingerMap();

}
