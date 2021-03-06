package top.lmoon.myspider.service;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import top.lmoon.myspider.constant.SysConstants;
import top.lmoon.myspider.dao.ApeInfoDAO;
import top.lmoon.myspider.dao.ApeInfoDAOH2DBImpl;
import top.lmoon.myspider.util.DownloadUtil.downloadType;
import top.lmoon.myspider.util.HttpUtil;
import top.lmoon.myspider.vo.ApeInfoVO;

public class DownloadService {

	private static Set<Integer> set = Collections.synchronizedSet(new HashSet<Integer>());

	// private static ApeFileDAO dao = new ApeFileDAOH2DBImpl();
	private static ApeInfoDAO dao = new ApeInfoDAOH2DBImpl();

	public static void asyncDownload(String urlStr, String fileName, int songId) {
		set.add(songId);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				String fileTmpName = SysConstants.FILE_TMP_PATH + fileName + ".tmp";				
				boolean result = HttpUtil.download(urlStr, fileTmpName);
				File file = new File(fileTmpName);
				if(result){
					String fileFullName = SysConstants.FILE_PATH + fileName;
					file.renameTo(new File(fileFullName));
				}else{
					file.delete();
				}
				// ApeFileVO vo = new ApeFileVO();
				ApeInfoVO vo = new ApeInfoVO();
				vo.setSongId(songId);
				vo.setDownType(result ? downloadType.FINISHED : downloadType.UNFINISHED);
				vo.setUpdateTime(System.currentTimeMillis());
				dao.update(vo);
				set.remove(songId);
			}
		};
		ThreadPool.submitDownload(r);

	}

	public static int getDownloadThreadCounter() {
		return set.size();
	}

	public static boolean isDownloading(int songId) {
		return set.contains(songId);
	}

	public static Set<Integer> getDownloadSet() {
		return set;
	}

}
