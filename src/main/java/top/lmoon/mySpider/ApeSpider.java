/**
 * 
 */
package top.lmoon.mySpider;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.ConsolePageModelPipeline;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * @author LMoon
 * @date 2017年9月13日
 * 
 */
@TargetUrl("http://www.51ape.com/ape/\\w+.html")
@HelpUrl("http://www.51ape.com/\\w+/?")
public class ApeSpider {
	
	@ExtractBy(value = "//title/tidyText()", notNull = true)
    private String title;

    @ExtractBy(value = "//div[@class='fl over w638']/a/@href")
    private String link;

    @ExtractBy("//div[@class='fl over w638']/b[@class='mt_1 yh d_b']/tidyText()")
    private String pw;

    public static void main(String[] args) {
        OOSpider.create(Site.me().setSleepTime(1000)
                , new ConsolePageModelPipeline(), ApeSpider.class)
                .addUrl("http://www.51ape.com").thread(5).run();
    }
}
