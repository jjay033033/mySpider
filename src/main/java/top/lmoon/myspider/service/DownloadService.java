package top.lmoon.myspider.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import top.lmoon.myspider.dao.ApeFileDAO;
import top.lmoon.myspider.dao.ApeFileDAOH2DBImpl;
import top.lmoon.myspider.util.DownloadUtil.downloadType;
import top.lmoon.myspider.util.HttpUtil;
import top.lmoon.myspider.vo.ApeFileVO;

public class DownloadService {
	
//	private static AtomicInteger counter = new AtomicInteger();
	
	private static Set<Integer> set = Collections.synchronizedSet(new HashSet<Integer>());
	
	private static ApeFileDAO dao = new ApeFileDAOH2DBImpl();
	
	public static void asyncDownload(String urlStr,String filePath,String fileName,int songId){
//		counter.incrementAndGet();
		set.add(songId);
		Runnable r = new Runnable() {		
			@Override
			public void run() {
				boolean result = HttpUtil.download(urlStr, filePath+fileName);
				ApeFileVO vo = new ApeFileVO();
				vo.setDownType(result?downloadType.FINISHED:downloadType.UNFINISHED);
				vo.setUpdateTime(System.currentTimeMillis());
				dao.update(vo);
				set.remove(songId);
//				counter.decrementAndGet();
			}
		};
		ThreadPool.submit(r);
		
	}
	
	public static int getDownloadThreadCounter(){
		return set.size();
	}
	
//	public static Set<Integer> getDownloadThreadSet(){
//		return set;
//	}
	
	public static boolean isDownloading(int songId){
		return set.contains(songId);
	}
	

}
