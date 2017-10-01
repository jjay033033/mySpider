/**
 * 
 */
package top.lmoon.myspider.apefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.constant.SysConstants;
import top.lmoon.myspider.vo.ApeInfoVO;

/**
 * @author LMoon
 * @date 2017年9月30日
 * 
 */
public class FileReader {
	
	private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

	public static final File DIRECTORY_FILE = new File(FileName.FILE_PATH);

	static {
		init();
	}

	private static void init() {
		if (!DIRECTORY_FILE.exists() || !DIRECTORY_FILE.isDirectory()) {
			DIRECTORY_FILE.mkdirs();
		}
	}

	public static List<ApeInfoVO> readAllFiles() {
		List<ApeInfoVO> list = new ArrayList<ApeInfoVO>();
		String[] fileNames = DIRECTORY_FILE.list();
		for (String fileName : fileNames) {
			File file = new File(FileName.FILE_PATH + fileName);
			try {
				List<String> readLines = FileUtils.readLines(file, SysConstants.ENCODING_CHARSET);
				for (String readLine : readLines) {
					String[] props = readLine.split(SysConstants.COLUMN_SYMBOL);
					ApeInfoVO vo = new ApeInfoVO(props[0], props[1], props[2], props[3], props[4], props[5], props[6],
							props[7], props[8], props[9], props[10]);
					list.add(vo);
				}
			} catch (Exception e) {
				logger.error("读数据出错:"+fileName,e);
			}
		}
		return list;
	}

}
