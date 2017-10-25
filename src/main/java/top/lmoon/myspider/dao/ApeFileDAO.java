package top.lmoon.myspider.dao;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import top.lmoon.myspider.vo.ApeFileVO;
import top.lmoon.myspider.vo.ApeInfoVO;

public interface ApeFileDAO {
	
	int createTable();
	
	int insert(ApeFileVO vo);
	
	int update(ApeFileVO vo);
	
	List<ApeFileVO> select(int pageNo,int pageSize);
	
	ApeFileVO select(int songId);
	
	int count();

}
