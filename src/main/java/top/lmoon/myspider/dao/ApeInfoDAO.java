package top.lmoon.myspider.dao;

import java.util.List;

import top.lmoon.myspider.vo.ApeInfoVO;

public interface ApeInfoDAO {
	
	int insert(ApeInfoVO vo);
	
	List<ApeInfoVO> select(int pageNo,int pageSize,String singer,String title);
	
	int count(String singer,String title);

}
