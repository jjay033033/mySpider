/**
 * 
 */
package top.lmoon.myspider.service;

import java.util.HashMap;
import java.util.Map;

import top.lmoon.myspider.dao.ApeInfoDAO;
import top.lmoon.myspider.dao.ApeInfoDAOH2DBImpl;

/**
 * @author LMoon
 * @date 2017年10月10日
 * 
 */
public class CacheService {
	
	private static Map<String,Integer> searchTotalMap = new HashMap<>();
	
	private static ApeInfoDAO dao = new ApeInfoDAOH2DBImpl();
	
	public static int getSearchTotal(String singer,String title){
		int total = 0;
		String key = "k_"+(singer==null?"":singer)+"_"+(title==null?"":title);
		if(searchTotalMap.containsKey(key)){
			total = searchTotalMap.get(key);
		}else{
			total = dao.count(singer, title);
			searchTotalMap.put(key, total);			
		}
		return total;
	}

}
