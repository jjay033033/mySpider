package top.lmoon.myspider.pipeline;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.dao.ApeInfoDAO;
import top.lmoon.myspider.dao.ApeInfoDAOH2DBImpl;
import top.lmoon.myspider.idfactory.SingerIdFactory;
import top.lmoon.myspider.idfactory.SongIdFactory;
import top.lmoon.myspider.vo.ApeInfoVO;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class DBPipeline implements Pipeline {

	private static final Logger logger = LoggerFactory.getLogger(DBPipeline.class);

	private static ApeInfoDAO apeInfoDAO = new ApeInfoDAOH2DBImpl();

	@Override
	public void process(ResultItems resultItems, Task task) {
		try {
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
			String url = MapUtils.getString(map, "url", "");
			if (StringUtils.isBlank(singer.trim())) {
				singer = "其他";
			}
			int songId = SongIdFactory.getInstance().getSongId();
			int singerId = SingerIdFactory.getInstance().getSingerId(singer);
			int songIdForSinger = SongIdFactory.getInstance().getSongIdForSinger(singerId);
			ApeInfoVO vo = new ApeInfoVO(songId, singerId, songIdForSinger, singer, title, link, pw, album, size,
					language, remark,url);
			apeInfoDAO.insert(vo);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
