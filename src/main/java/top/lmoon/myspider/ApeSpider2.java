package top.lmoon.myspider;

import top.lmoon.myspider.pipeline.ConsolePipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class ApeSpider2 implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

	public void process(Page page) {
		page.addTargetRequests(page.getHtml().links().regex("(http://www\\.51ape\\.com/.*)").all());
		String remark = page.getHtml().xpath("//title/tidyText()").toString();
		int i = -1;
		if ((i = remark.lastIndexOf("_")) > -1) {
			remark = remark.substring(0, i);
		}
		page.putField("remark", remark);
		String singer = "";
		String title = "";
//		String size = "";
		String[] remarks = remark.split(" - ");
		if(remarks.length>1){
			singer = remarks[0];
			int indexTitle = remarks[1].indexOf(".");
			if(indexTitle>-1){
				title = remarks[1].substring(0, indexTitle);
//				size = remarks[1].substring(indexTitle+1);
			}
		}
		page.putField("singer", singer);
		page.putField("title", title);
//		page.putField("size", size);
		String link = page.getHtml().xpath("//div[@class='fl over w638']/a/@href").toString();
		page.putField("link", link);
		String pw = page.getHtml().xpath("//div[@class='fl over w638']/b[@class='mt_1 yh d_b']/regex('密码：(\\w{4})',1)")
				.toString();
		page.putField("pw", pw);
		ResultItems resultItems = page.getResultItems();
		if (resultItems.get("title") == null || resultItems.get("link") == null) {
			// skip this page
			page.setSkip(true);
		}
	}

	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		System.out.println("started!");
		Spider.create(new ApeSpider2()).addUrl("http://www.51ape.com").addPipeline(new ConsolePipeline()).thread(5)
				.run();
	}
}
