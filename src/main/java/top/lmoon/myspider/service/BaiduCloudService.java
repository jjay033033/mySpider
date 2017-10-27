/**
 * 
 */
package top.lmoon.myspider.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.constant.SysConstants;
import top.lmoon.myspider.dao.ApeInfoDAO;
import top.lmoon.myspider.dao.ApeInfoDAOH2DBImpl;
import top.lmoon.myspider.util.CommonUtil;
import top.lmoon.myspider.util.DownloadUtil.downloadType;
import top.lmoon.myspider.util.HttpUtil;
import top.lmoon.myspider.vo.ApeInfoVO;

/**
 * @author LMoon
 * @date 2017年10月13日
 * 
 */

public class BaiduCloudService {

	private static final Logger logger = LoggerFactory.getLogger(BaiduCloudService.class);

	private final static int CLIENTTYPE = 0;
	private final static String CHANNEL = "chunlei";
	private final static int WEB = 1;
	private final static String APP_ID = "250528";

	// private static ApeFileDAO dao = new ApeFileDAOH2DBImpl();

	private static ApeInfoDAO dao = new ApeInfoDAOH2DBImpl();

	static {
		init();
	}

	/**
	 * 
	 */
	private static void init() {
		File downloadPath = new File(SysConstants.FILE_PATH);
		if (!downloadPath.exists() || !downloadPath.isDirectory()) {
			downloadPath.mkdirs();
		}
//		System.setProperty("http.proxySet", "true"); 
//		System.setProperty("http.proxyHost", "127.0.0.1"); 
//		System.setProperty("http.proxyPort", "8888");
	}

	public static BaiduCloudInfo downloadAndGetFile(String url, ApeInfoVO vo) {
		return downloadAndGetFile(url, vo, null, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BaiduCloudInfo downloadAndGetFile(String url, ApeInfoVO vo, String vcode_input, String vcode_str) {
		try {
			Connection connection = Jsoup.connect(url);
			connection.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
			connection.referrer("https://pan.baidu.com");
			connection.header("Host", "pan.baidu.com");
			connection.header("Cookie",
					"PANWEB=1; BAIDUID=C45C3ACB0DAE46D34927F30EEC9A920F:FG=1; BDCLND=kR%2BvhmAQ63n%2Bps2G3R%2F6cAkJt2Pwk8NTgeQ2pzTGTyw%3D; Hm_lvt_7a3960b6f067eb0085b7f96ff5e660b0=1507625685%2C1507625751%2C1507886431%2C1507887009; Hm_lpvt_7a3960b6f067eb0085b7f96ff5e660b0=1509099846");
			connection.header("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			connection.header("Accept-Encoding", "gzip, deflate, br");
			connection.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
			connection.header("Connection", "keep-alive");
			connection.header("Upgrade-Insecure-Requests", "1");

			//
			if (StringUtils.isNotBlank(vo.getPw())) {
				Document doc = connection.get();
				String html = doc.toString();
				 System.out.println("------html111:" +html);
				String beginStr = "yunData.setData({";
				int a = html.indexOf(beginStr);
				int b = html.indexOf("})", a);

				String info = html.substring(a + beginStr.length() - 1, b + 1);
				System.out.println("------info:" + info);
				JSONObject jo = new JSONObject(info);
//				String bdstoken = jo.getString("bdstoken");
				Object bdstoken = jo.get("bdstoken");
				
//				BaiduCloudVcode vcodeInfo = getVcodeInfo(bdstoken, APP_ID);
//				System.out.println("vcodeInfo.getVcode_url():"+vcodeInfo.getVcode_url());
				// https://pan.baidu.com/share/verify?surl=o8hv18m&t=1509069658888
				//&bdstoken=02e218d159e28931b27315726f540d69&channel=chunlei
				//&clienttype=0&web=1&app_id=250528&logid=MTUwOTA2OTY1ODg5MjAuOTE2MTg2MzA5OTg1NDY4NA==
				Map params = new HashMap<>();
				params.put("bdstoken", bdstoken);
				params.put("app_id", APP_ID);
				params.put("channel", CHANNEL);
				params.put("clienttype", CLIENTTYPE);
				params.put("web", WEB);
				params.put("surl", CommonUtil.getBaiduCloudSUrl(url));
				
				Map formParams = new HashMap<>();
				formParams.put("pwd", vo.getPw());
//				formParams.put("vcode_str", "33324238656332346361663334656637323237633636373637643239666664336662313531383836363438343030303030303030303030303030313530393038393039328FA7B61CF4564B12842EC82B900D126E");
//				formParams.put("vcode", "8wua");
				String verify = HttpUtil.postWithBaiduCookies("https://pan.baidu.com/share/verify", params, formParams);
				System.out.println("------verify:" + verify);
				System.out.println("------verify:" + new String(verify.getBytes("ISO8859_1"), "UTF-8"));
			}
			
			connection.header("Cookie",
					"PANWEB=1; BAIDUID=C45C3ACB0DAE46D34927F30EEC9A920F:FG=1; BDCLND=kR%2BvhmAQ63n%2Bps2G3R%2F6cAkJt2Pwk8NTgeQ2pzTGTyw%3D; BIDUPSID=C45C3ACB0DAE46D34927F30EEC9A920F; PSTM=1509100435; BDRCVFR[mkUqnUt8juD]=mk3SLVN4HKm; PSINO=6; H_PS_PSSID=1457_19033_21113_23384; Hm_lvt_7a3960b6f067eb0085b7f96ff5e660b0=1507625685%2C1507625751%2C1507886431%2C1507887009; Hm_lpvt_7a3960b6f067eb0085b7f96ff5e660b0=1509100923");
			connection.referrer("https://pan.baidu.com/share/init?surl=hsDZC6g");

			Document doc = connection.get();
			String html = doc.toString();
			 System.out.println("------html222:" +html);

			// String html = HttpRequest.getData(url);
			String beginStr = "yunData.setData({";
			int a = html.indexOf(beginStr);
			int b = html.indexOf("})", a);

			String info = html.substring(a + beginStr.length() - 1, b + 1);
			// System.out.println("------info:" + info);
			JSONObject jo = new JSONObject(info);

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

			params.put("channel", CHANNEL);
			params.put("clienttype", CLIENTTYPE);
			params.put("web", WEB);
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
			return download(vo, bdstoken, app_id, params, formParams);
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
	public static BaiduCloudInfo download(ApeInfoVO infoVo, String bdstoken, String app_id, Map params,
			Map formParams) {
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
				// ApeFileVO vo = new ApeFileVO();
				ApeInfoVO vo = new ApeInfoVO();
				long currentTimeMillis = System.currentTimeMillis();
				vo.setSongId(infoVo.getSongId());
				vo.setName(fileName);
				vo.setUpdateTime(currentTimeMillis);
				vo.setDownType(hasDownload);
				dao.update(vo);
				DownloadService.asyncDownload(fileUrl, SysConstants.FILE_PATH, fileName, infoVo.getSongId());
			}

		} else if (resultInt == -20) {
			// String getVCode();
		}
		return new BaiduCloudInfo(fileName, fileUrl, bdstoken, app_id, hasDownload, params, formParams);
	}

	/**
	 * @param bdstoken
	 * @param app_id
	 */
	public static BaiduCloudVcode getVcodeInfo(String bdstoken, String app_id) {
		BaiduCloudVcode baiduCloudVcode = new BaiduCloudVcode();
		String vCodeGetUrl = "https://pan.baidu.com/api/getvcode?prod=pan&bdstoken=" + bdstoken + "&channel=" + CHANNEL
				+ "&clienttype=" + CLIENTTYPE + "&web=" + WEB + "&app_id=" + app_id;
		String result = HttpUtil.get(vCodeGetUrl);
		JSONObject jo = new JSONObject(result);
		if (jo.getInt("errno") == 0) {
			baiduCloudVcode.setVcode_str(jo.getString("vcode"));
			baiduCloudVcode.setVcode_url(jo.getString("img"));
		}
		return baiduCloudVcode;
	}

	public static class BaiduCloudVcode {
		private String vcode_str = "";
		private String vcode_url = "";

		public String getVcode_str() {
			return vcode_str;
		}

		public void setVcode_str(String vcode_str) {
			this.vcode_str = vcode_str;
		}

		public String getVcode_url() {
			return vcode_url;
		}

		public void setVcode_url(String vcode_url) {
			this.vcode_url = vcode_url;
		}
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
			if (hasDownload == downloadType.NOTSTART && StringUtils.isBlank(fileUrl)) {
				BaiduCloudVcode vcodeInfo = getVcodeInfo(bdstoken, app_id);
				this.vcode_str = vcodeInfo.getVcode_str();
				this.vcode_url = vcodeInfo.getVcode_url();
				this.formParams.put("vcode_str", vcode_str);
			}
		}

		public void setVcodeInput(String vcode_input) {
			this.vcode_input = vcode_input;
			this.formParams.put("vcode_input", vcode_input);
		}

		public void setVcodeStr(String vcode_str) {
			this.vcode_str = vcode_str;
			this.formParams.put("vcode_str", vcode_str);
		}

		public void setVcodeUrl(String vcode_url) {
			this.vcode_url = vcode_url;
			this.formParams.put("vcode_url", vcode_url);
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
		ApeInfoVO vo = new ApeInfoVO();
		vo.setPw("ni1w");
		System.out.println(downloadAndGetFile("http://pan.baidu.com/s/1hsDZC6g", vo));

	}
}