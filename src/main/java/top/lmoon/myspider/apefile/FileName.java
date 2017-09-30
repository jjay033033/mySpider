/**
 * 
 */
package top.lmoon.myspider.apefile;

/**
 * @author LMoon
 * @date 2017年9月30日
 * 
 */
public class FileName {
	
	public static final String FILE_PATH = "./files/";
	
	public static String getApeListFileName(String singer){
		return singer + ".alnk";
	}
	
	public static String getApeListFileFullName(String singer){
		return FILE_PATH + getApeListFileName(singer);
	}

}
