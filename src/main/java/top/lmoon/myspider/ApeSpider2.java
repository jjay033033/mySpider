package top.lmoon.myspider;

import top.lmoon.myspider.h2db.H2DBServer;
import top.lmoon.myspider.pipeline.DBPipeline;
import top.lmoon.myspider.pipeline.FilePipeline;
import top.lmoon.myspider.util.CommonUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

public class ApeSpider2 implements PageProcessor {

	private static final String CURSOR_FILE_PATH = "./res/cursor/";

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

	public void process(Page page) {
		page.addTargetRequests(page.getHtml().links().regex("(http://www\\.51ape\\.com/.*)").all());
		String remark = page.getHtml().xpath("//title/tidyText()").toString();
		int i = -1;
		if ((i = remark.lastIndexOf("_")) > -1) {
			remark = remark.substring(0, i);
		}
		String singer = "";
		String title = "";
		String[] remarks = remark.split(" - ");
		if (remarks.length > 1) {
			singer = CommonUtil.replacePathSpecilChar(remarks[0]);
			int indexTitle = remarks[1].indexOf(".");
			if (indexTitle > -1) {
				title = remarks[1].substring(0, indexTitle);
			}
		}
		String link = page.getHtml().xpath("//div[@class='fl over w638']/a/@href").toString();
		String pw = page.getHtml().xpath("//div[@class='fl over w638']/b[@class='mt_1 yh d_b']/regex('密码：(\\w{4})',1)")
				.toString();
		String album = page.getHtml().xpath("//div[@class='fl over w638']/h3/regex('选自专辑《([^》]*)》',1)").toString();
		String size = page.getHtml().xpath("//div[@class='fl over w638']/h3[3]/text()").toString();
		String language = page.getHtml().xpath("//div[@class='fl over w638']/h3[4]/text()").toString();
		String url = page.getUrl().get();

		// 所有字符串过滤换行符换列符
		page.putField("remark", CommonUtil.replaceJavaSpecilChar(remark));
		page.putField("singer", CommonUtil.replaceJavaSpecilChar(singer));
		page.putField("title", CommonUtil.replaceJavaSpecilChar(title));
		page.putField("link", CommonUtil.replaceJavaSpecilChar(link));
		page.putField("pw", CommonUtil.replaceJavaSpecilChar(pw));
		page.putField("album", CommonUtil.replaceJavaSpecilChar(album));
		page.putField("size", CommonUtil.replaceJavaSpecilChar(size));
		page.putField("language", CommonUtil.replaceJavaSpecilChar(language));
		page.putField("url", url);

		ResultItems resultItems = page.getResultItems();
		if (resultItems.get("title") == null || resultItems.get("link") == null) {
			// skip this page
			page.setSkip(true);
		}
	}

	public Site getSite() {
		return site;
	}
	
	public static void run(){
		Spider.create(new ApeSpider2()).addUrl("http://www.51ape.com").addPipeline(new DBPipeline())
		.setScheduler(new FileCacheQueueScheduler(CURSOR_FILE_PATH)).thread(5).run();
	}

	public static void main(String[] args) {
		System.out.println("started!");
		Spider.create(new ApeSpider2()).addUrl("http://www.51ape.com").addPipeline(new FilePipeline())
				.setScheduler(new FileCacheQueueScheduler(CURSOR_FILE_PATH)).thread(5).run();
	}
}
