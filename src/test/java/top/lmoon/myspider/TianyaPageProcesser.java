/**
 * 
 */
package top.lmoon.myspider;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author LMoon
 * @date 2017年9月14日
 * 
 */
public class TianyaPageProcesser implements PageProcessor {

    public void process(Page page) {
        List<String> strings = page.getHtml().regex("<a[^<>]*href=[\"']{1}(/post-free.*?\\.shtml)[\"']{1}").all();
        page.addTargetRequests(strings);
        page.putField("title", page.getHtml().xpath("//div[@id='post_head']//span[@class='s_title']//span/text()"));
        page.putField("body",page.getHtml().smartContent());
    }

    public Site getSite() {
        return Site.me().setDomain("bbs.tianya.cn/");  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    public static void main(String[] args) {
    	Spider.create(new TianyaPageProcesser()).addPipeline(new JsonFilePipeline("d://tianyapage")).addUrl("http://bbs.tianya.cn").thread(1).run();
	}
}
