/**
 * 
 */
package top.lmoon.myspider.apefile;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LMoon
 * @date 2017年9月30日
 * 
 */
public class FileLock {

	private static ConcurrentHashMap<String, File> map = new ConcurrentHashMap<String, File>();

	public static File getFile(String singer) {
		File file = new File(FileName.getApeListFileFullName(singer));
		File result = map.putIfAbsent(singer, file);
		return result == null ? file : result;
	}

}
