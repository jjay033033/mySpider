/**
 * 
 */
package top.lmoon.myspider.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.constant.SysConstants;
import top.lmoon.myspider.dao.ApeFileDAO;
import top.lmoon.myspider.dao.ApeFileDAOH2DBImpl;
import top.lmoon.myspider.util.DownloadUtil;
import top.lmoon.myspider.util.HttpUtil;
import top.lmoon.myspider.vo.ApeFileVO;
import top.lmoon.myspider.vo.ApeInfoVO;
import top.lmoon.myspider.util.DownloadUtil.downloadType;

/**
 * @author LMoon
 * @date 2017年10月13日
 * 
 */

public class BaiduCloudService {

	private static final Logger logger = LoggerFactory.getLogger(BaiduCloudService.class);

	private final static int clienttype = 0;
	private final static String channel = "chunlei";
	private final static int web = 1;
	
	private static ApeFileDAO dao = new ApeFileDAOH2DBImpl();
	
	static{
		init();
	}
	
	/**
	 * 
	 */
	private static void init() {
		File downloadPath = new File(SysConstants.FILE_PATH);
		if(!downloadPath.exists()||!downloadPath.isDirectory()){
			downloadPath.mkdirs();
		}
	}

	public static BaiduCloudInfo downloadAndGetFile(String url,ApeInfoVO vo) {
		return downloadAndGetFile(url,vo, null, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BaiduCloudInfo downloadAndGetFile(String url,ApeInfoVO vo, String vcode_input, String vcode_str) {
		try {
			Connection connection = Jsoup.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36")
					.referrer("https://pan.baidu.com").header("Host", "pan.baidu.com");
			connection.header("Cookie",
					"bdshare_firstime=1430100467345; yundetect_httpport=10000; PANWEB=1; BAIDUID=0E5FA8063809E5A490BE361C4478E8AC:FG=1; BIDUPSID=0E5FA8063809E5A490BE361C4478E8AC; PSTM=1504521830; MCITY=-%3A; BDUSS=d3elRTdHlSUGc4LXlGR2VXWk9zNlUyQjlUcGN-eTFXMi1ua3A0OWRrSWItdkpaTVFBQUFBJCQAAAAAAAAAAAEAAACwn7oKampheTAzMzAzMwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABtty1kbbctZbz; STOKEN=e66b0ebd517cc632976a6a405289803dd988b171fe127d9add7ed3467386ffe8; SCRC=0bd4d4126f875e2ae446a664d2083cd7; BDCLND=d7az66%2BRO8YzNC0nbnAw2Nx3DRo0gQPD; Hm_lvt_7a3960b6f067eb0085b7f96ff5e660b0=1507625685,1507625751,1507886431,1507887009; BDRCVFR[mkUqnUt8juD]=mk3SLVN4HKm; PSINO=7; H_PS_PSSID=1438_21091");
			connection.header("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			connection.header("Accept-Encoding", "gzip, deflate, br");
			connection.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
			connection.header("Connection", "keep-alive");
			connection.header("Upgrade-Insecure-Requests", "1");

			Document doc = connection.get();
			String html = doc.toString();
			// System.out.println(html);
			// String html = HttpRequest.getData(url);
			String beginStr = "yunData.setData({";
			int a = html.indexOf(beginStr);
			int b = html.indexOf("})", a);

			String info = html.substring(a + beginStr.length() - 1, b + 1);
			// System.out.println("------info:" + info);
			JSONObject jo = new JSONObject(info);

			// sign:4ee143aa567f2a8498b2f1bea580c80081e24fe6
			// timestamp:1508744212
			// bdstoken:02e218d159e28931b27315726f540d69
			// channel:chunlei
			// clienttype:0
			// web:1
			// app_id:250528
			// logid:MTUwODc0NDY5MzUyNDAuNDgyMzgzNDg4ODYwNjE2OQ==
			//
			//
			// encrypt:0
			// product:share
			// vcode_input:cr9a
			// vcode_str:3332423865633234636166333465663732323763363637363764323966666433666231343830353334373737303030303030303030303030303031353038373434363838B1DA7E657B6BE70FDD484FEF356B6324
			// uk:660976564
			// primaryid:47135972
			// fid_list:[677635646695251]
			String sign = jo.getString("sign");
			long timestamp = jo.getLong("timestamp");
			String bdstoken = jo.getString("bdstoken");
			long uk = jo.getLong("uk");
			long primaryid = jo.getLong("shareid");

			JSONObject fileList = jo.getJSONObject("file_list");
			JSONArray ja = fileList.getJSONArray("list");
			// List<JSONObject> fileJoList = new js;
			List<Long> fid_list = new ArrayList<Long>();
			String app_id = "";

			for (int i = 0; i < ja.length(); i++) {
				JSONObject fileJo = ja.getJSONObject(i);
				app_id = fileJo.getString("app_id");
				fid_list.add(fileJo.getLong("fs_id"));
			}

			Map params = new HashMap<>();
			params.put("sign", sign);
			params.put("timestamp", timestamp);
			params.put("bdstoken", bdstoken);
			params.put("app_id", app_id);

			params.put("channel", channel);
			params.put("clienttype", clienttype);
			params.put("web", web);
			// params.put("logid",
			// "MTUwODc0NDY5MzUyNDAuNDgyMzgzNDg4ODYwNjE2OQAA");

			Map formParams = new HashMap<>();
			formParams.put("uk", uk);
			formParams.put("primaryid", primaryid);
			formParams.put("fid_list", fid_list);
			formParams.put("encrypt", 0);
			formParams.put("product", "share");
			formParams.put("vcode_input", vcode_input);
			formParams.put("vcode_str", vcode_str);
			// formParams.put("vcode_input", "xd64");
			// formParams.put("vcode_str",
			// "33324238656332346361663334656637323237633636373637643239666664336662343330393135363338303030303030303030303030303031353038393132353335E409471BF6D09C6E8853D11AFC018DC8");
			return download(vo,bdstoken, app_id, params, formParams);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	/**
	 * @param bdstoken
	 * @param app_id
	 * @param params
	 * @param formParams
	 * @return
	 */
	public static BaiduCloudInfo download(ApeInfoVO infoVo,String bdstoken, String app_id, Map params, Map formParams) {
		String postResult = HttpUtil.post("https://pan.baidu.com/api/sharedownload", params, formParams);

		System.out.println("------postResult:" + postResult);

		JSONObject json_data = new JSONObject(postResult);
		int resultInt = json_data.getInt("errno");
		String fileUrl = "";
		String fileName = "";
		downloadType hasDownload = downloadType.NOTSTART;
		if (resultInt == 0) {
			JSONArray jsonArray2 = json_data.getJSONArray("list");
			json_data = jsonArray2.getJSONObject(0);
			// 储存文件下载实链
			fileUrl = json_data.getString("dlink");
			fileName = json_data.getString("server_filename");
			if (StringUtils.isNotBlank(fileUrl)) {
				hasDownload = downloadType.ONGOING;
				ApeFileVO vo = new ApeFileVO();
				long currentTimeMillis = System.currentTimeMillis();
				vo.setSongId(infoVo.getSongId());
				vo.setName(fileName);
				vo.setUpdateTime(currentTimeMillis);
				vo.setDownType(hasDownload);
				dao.update(vo);
				DownloadService.asyncDownload(fileUrl, SysConstants.FILE_PATH + fileName);								
			}

		} else if (resultInt == -20) {
			// String getVCode();
		}
		return new BaiduCloudInfo(fileName, fileUrl, bdstoken, app_id, hasDownload,params,formParams);
	}

	public static class BaiduCloudInfo {

		private String fileName = "";
		private String fileUrl = "";
		private String bdstoken = "";
		private String app_id = "";
		private String vcode_str = "";
		private String vcode_url = "";
		private String vcode_input = "";
		private downloadType hasDownload = downloadType.NOTSTART;

		private Map params;
		private Map formParams;
		
		@SuppressWarnings("unchecked")
		public BaiduCloudInfo(String fileName, String fileUrl, String bdstoken, String app_id, downloadType hasDownload,
				Map params, Map formParams) {
			this.fileName = fileName;
			this.fileUrl = fileUrl;
			this.bdstoken = bdstoken;
			this.app_id = app_id;
			this.hasDownload = hasDownload;
			this.params = params;
			this.formParams = formParams;
			if (hasDownload==downloadType.NOTSTART && StringUtils.isBlank(fileUrl)) {
				String vCodeGetUrl = "https://pan.baidu.com/api/getvcode?prod=pan&bdstoken=" + bdstoken + "&channel="
						+ channel + "&clienttype=" + clienttype + "&web=" + web + "&app_id=" + app_id;
				String result = HttpUtil.get(vCodeGetUrl);
				JSONObject jo = new JSONObject(result);
				if (jo.getInt("errno") == 0) {
					this.vcode_str = jo.getString("vcode");
					this.vcode_url = jo.getString("img");
					this.formParams.put("vcode_str", vcode_str);
				}
			}
		}
		
		public void setVcodeInput(String vcode_input){
			this.vcode_input = vcode_input;
			this.formParams.put("vcode_input", vcode_input);
		}

		public String getBdstoken() {
			return bdstoken;
		}

		public String getAppId() {
			return app_id;
		}

		public Map getParams() {
			return params;
		}

		public Map getFormParams() {
			return formParams;
		}

		public downloadType getHasDownload() {
			return hasDownload;
		}

		public String getFileName() {
			return fileName;
		}

		public String getFileUrl() {
			return fileUrl;
		}

		public String getVcodeStr() {
			return vcode_str;
		}

		public String getVcodeUrl() {
			return vcode_url;
		}

		@Override
		public String toString() {
			return "BaiduCloudInfo [fileName=" + fileName + ", fileUrl=" + fileUrl + ", bdstoken=" + bdstoken
					+ ", app_id=" + app_id + ", vcode_str=" + vcode_str + ", vcode_url=" + vcode_url + "]";
		}

	}

	public static void main(String[] args) throws Exception {
		// System.out.println(getUrl("https://pan.baidu.com/s/1qXZHS08"));
//		System.out.println(downloadAndGetFile("http://pan.baidu.com/s/1bpD3aUF"));

		        ExecutorService threadPool = Executors.newSingleThreadExecutor();
		        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
		            public Integer call() throws Exception {
		            	 Thread.sleep(5000);// 可能做一些事情
		                return new Random().nextInt(100);
		            }
		        });
		        try {
		        	System.out.println("1");
//		            Thread.sleep(5000);// 可能做一些事情
		            System.out.println(future.get());
		            System.out.println("afdf");
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        } catch (ExecutionException e) {
		            e.printStackTrace();
		        }
		    
	}
}