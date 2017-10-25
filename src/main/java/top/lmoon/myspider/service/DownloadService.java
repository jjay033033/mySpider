package top.lmoon.myspider.service;

import java.util.concurrent.atomic.AtomicInteger;

import top.lmoon.myspider.dao.ApeFileDAO;
import top.lmoon.myspider.dao.ApeFileDAOH2DBImpl;
import top.lmoon.myspider.util.DownloadUtil.downloadType;
import top.lmoon.myspider.util.HttpUtil;
import top.lmoon.myspider.vo.ApeFileVO;

public class DownloadService {
	
	private static AtomicInteger counter = new AtomicInteger();
	
	private static ApeFileDAO dao = new ApeFileDAOH2DBImpl();
	
	public static void asyncDownload(String urlStr,String fileName){
		counter.incrementAndGet();
		Runnable r = new Runnable() {		
			@Override
			public void run() {
				boolean result = HttpUtil.download(urlStr, fileName);
				ApeFileVO vo = new ApeFileVO();
				vo.setDownType(result?downloadType.FINISHED:downloadType.UNFINISHED);
				vo.setUpdateTime(System.currentTimeMillis());
				dao.update(vo);
				counter.decrementAndGet();
			}
		};
		ThreadPool.submit(r);
		
	}
	
	public static int getDownloadThreadCounter(){
		return counter.get();
	}

}
