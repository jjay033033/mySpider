/**
 * 
 */
package top.lmoon.myspider.util;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * @author LMoon
 * @date 2017年9月30日
 * 
 */
public class CommonUtil {
	
	public static boolean isBaiduCloudUrl(String str){
		String regex = "http(s)?://pan.baidu.com/.*";
		return str.matches(regex);
	}

	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 替换换行、换列符
	 * @param str
	 * @return
	 */
	public static String replaceJavaSpecilChar(String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		StringBuffer sb = new StringBuffer();
		char[] cs = str.toCharArray();
		for (char c : cs) {
			switch (c) {
			case '\n':
			case '\t':
				sb.append(" ");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 效果等同String.replace("/", "&").replace("\\", "*");
	 * @param str
	 * @return
	 */
	public static String replacePathSpecilChar(String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		StringBuffer sb = new StringBuffer();
		char[] cs = str.toCharArray();
		for (char c : cs) {
			switch (c) {
			case '/':
				sb.append('&');
				break;
			case '\\':
				sb.append('*');
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

}
