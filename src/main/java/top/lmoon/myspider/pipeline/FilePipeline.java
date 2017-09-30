package top.lmoon.myspider.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.apefile.FileLock;
import top.lmoon.myspider.constant.SysConstants;
import top.lmoon.myspider.idfactory.SingerIdFactory;
import top.lmoon.myspider.idfactory.SongIdFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class FilePipeline implements Pipeline {

	private static final Logger logger = LoggerFactory.getLogger(FilePipeline.class);

	private static final int MAX_LINE_NUM = 5000;

	private static AtomicInteger counter = new AtomicInteger(0);

	@Override
	public void process(ResultItems resultItems, Task task) {
		System.out.println("get page: " + resultItems.getRequest().getUrl());
		Map<String, Object> map = resultItems.getAll();
		String remark = MapUtils.getString(map, "remark", "");
		String singer = MapUtils.getString(map, "singer", "");
		String title = MapUtils.getString(map, "title", "");
		String link = MapUtils.getString(map, "link", "");
		String pw = MapUtils.getString(map, "pw", "");
		String album = MapUtils.getString(map, "album", "");
		String size = MapUtils.getString(map, "size", "");
		String language = MapUtils.getString(map, "language", "");
		if (StringUtils.isBlank(singer.trim())) {
			singer = "其他";
		}
		File file = FileLock.getFile(singer);
		synchronized (file) {
			String data = toWriterString(singer, title, link, pw, album, size, language, remark);
			try {
				FileUtils.write(file, data, SysConstants.ENCODING_CHARSET, true);
			} catch (IOException e) {
				logger.error("写数据出错:", e);
			}
		}
		
	}

	private static String toWriterString(String singer, String title, String link, String pw, String album, String size,
			String language, String remark) {
		StringBuffer sb = new StringBuffer();
		String songId = SongIdFactory.getInstance().getSongId();
		int singerId = SingerIdFactory.getInstance().getSingerId(singer);
		int songIdForSinger = SongIdFactory.getInstance().getSongIdForSinger(singerId);
		sb.append(songId).append(SysConstants.COLUMN_SYMBOL).append(singerId).append(SysConstants.COLUMN_SYMBOL).append(songIdForSinger)
				.append(SysConstants.COLUMN_SYMBOL).append(singer).append(SysConstants.COLUMN_SYMBOL).append(title).append(SysConstants.COLUMN_SYMBOL)
				.append(link).append(SysConstants.COLUMN_SYMBOL).append(pw).append(SysConstants.COLUMN_SYMBOL).append(album).append(SysConstants.COLUMN_SYMBOL)
				.append(size).append(SysConstants.COLUMN_SYMBOL).append(language).append(SysConstants.COLUMN_SYMBOL).append(remark)
				.append(SysConstants.LINE_SYMBOL);
		return sb.toString();
	}

}
