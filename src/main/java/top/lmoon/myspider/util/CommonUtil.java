/**
 * 
 */
package top.lmoon.myspider.util;

import java.util.UUID;

/**
 * @author LMoon
 * @date 2017年9月30日
 * 
 */
public class CommonUtil {
	
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}


}
