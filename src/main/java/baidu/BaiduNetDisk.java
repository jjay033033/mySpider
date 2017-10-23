/**
 * 
 */
package baidu;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 * @author LMoon
 * @date 2017年10月13日
 * 
 */

public class BaiduNetDisk {
	public static List<Map<String, Object>> getUrl1(String url) throws Exception {
		List<String> fs_id = new ArrayList<String>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
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
		// connection.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2)
		// AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152
		// Safari/537.36");
		Document doc = connection.get();

		String html = doc.toString();
		System.out.println(html);
		// String html = HttpRequest.getData(url);
		int a = html.indexOf("{\"typicalPath");
		int b = html.indexOf("yunData.getCon");
		int sign_head = html.indexOf("yunData.SIGN = \"");
		int sign_foot = html.indexOf("yunData.TIMESTAMP");
		int time_head = html.indexOf("yunData.TIMESTAMP = \"");
		int time_foot = html.indexOf("yunData.SHARE_UK");
		int share_id_head = html.indexOf("yunData.SHARE_ID = \"");
		int share_id_foot = html.indexOf("yunData.SIGN ");
		String sign = html.substring(sign_head, sign_foot);
		sign = sign.substring(sign.indexOf("\"") + 1, sign.indexOf("\";"));
		String time = html.substring(time_head, time_foot);
		time = time.substring(time.indexOf("\"") + 1, time.indexOf("\";"));
		String share_id = html.substring(share_id_head, share_id_foot);
		share_id = share_id.substring(share_id.indexOf("\"") + 1, share_id.indexOf("\";"));
		System.out.println(share_id);
		html = html.substring(a, b);
		a = html.indexOf("{\"typicalPath");
		b = html.indexOf("};");
		JSONObject jsonObject = new JSONObject(html.substring(a, b + 1));
		String uk = jsonObject.getString("uk");
		String shareid = jsonObject.getString("shareid");
		String path = URLEncoder.encode(jsonObject.getString("typicalPath"), "utf-8");
		jsonObject = new JSONObject(jsonObject.getString("file_list"));
		JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
		jsonObject = jsonArray.getJSONObject(0);
		String app_id = jsonObject.getString("app_id");
		if (jsonObject.getString("isdir").equals("1")) {
			String url1 = "http://pan.baidu.com/share/list?uk=" + uk + "&shareid=" + shareid + "&page=1&num=100&dir="
					+ path + "&order=time&desc=1&_=" + time
					+ "&bdstoken=c51077ce0e0e313a16066612a13fbcd4&channel=chunlei&clienttype=0&web=1&app_id=" + app_id;
			String fileListJson = HttpRequest.getData(url1);
			System.out.println(fileListJson);
			jsonObject = new JSONObject(fileListJson);
			jsonArray = new JSONArray(jsonObject.getString("list"));
		}
		final int size = jsonArray.length();
		for (int i = 0; i < size; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			jsonObject = jsonArray.getJSONObject(i);
			String fileName = jsonObject.getString("server_filename");
			// 储存文件名
			map.put("fileName", fileName);
			fs_id.add(jsonObject.getString("fs_id"));
			String fileInfo = HttpRequest
					.getData("http://pan.baidu.com/api/sharedownload?sign=" + sign + "&timestamp=" + time
							+ "&bdstoken=c51077ce0e0e313a16066612a13fbcd4&channel=chunlei&clienttype=0&web=1&app_id=250528&encrypt=0&product=share&uk="
							+ uk + "&primaryid=" + share_id + "&fid_list=%5B" + fs_id.get(i) + "%5D");
			JSONObject json_data = new JSONObject(fileInfo);
			if (json_data.getString("errno").equals("0")) {
				JSONArray jsonArray2 = new JSONArray(json_data.getString("list"));
				json_data = jsonArray2.getJSONObject(0);
				// 储存文件下载实链
				map.put("url", json_data.getString("dlink"));
			} else if (json_data.getString("errno").equals("-20")) {
				return null;
				// String getVerCode();
			} else {
				return null;
			}
			list.add(map);
		}
		return list;
	}

	public static List<Map<String, Object>> getUrl(String url) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
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
		// connection.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2)
		// AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152
		// Safari/537.36");
		Document doc = connection.get();

		String html = doc.toString();
		System.out.println(html);
		// String html = HttpRequest.getData(url);
		String beginStr = "yunData.setData({";
		int a = html.indexOf(beginStr);
		int b = html.indexOf("})", a);
		String info = html.substring(a + beginStr.length() - 1, b + 1);
		System.out.println("------info:" + info);
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
		params.put("channel", "chunlei");
		params.put("clienttype", 0);
		params.put("web", 1);
//		params.put("logid", "MTUwODc0NDY5MzUyNDAuNDgyMzgzNDg4ODYwNjE2OQAA");

		Map formParams = new HashMap<>();
		formParams.put("uk", uk);
		formParams.put("primaryid", primaryid);
		formParams.put("fid_list", fid_list);
		formParams.put("encrypt", 0);
		formParams.put("product", "share");
		String postResult = HttpUtil.post("https://pan.baidu.com/api/sharedownload", params, formParams, 3000, 3000, "UTF-8");

		System.out.println("------postResult:" + postResult);

		// int sign_head = html.indexOf("yunData.SIGN = \"");
		// int sign_foot = html.indexOf("yunData.TIMESTAMP");
		// int time_head = html.indexOf("yunData.TIMESTAMP = \"");
		// int time_foot = html.indexOf("yunData.SHARE_UK");
		// int share_id_head = html.indexOf("yunData.SHARE_ID = \"");
		// int share_id_foot = html.indexOf("yunData.SIGN ");
		// String sign = html.substring(sign_head, sign_foot);
		// sign = sign.substring(sign.indexOf("\"") + 1, sign.indexOf("\";"));
		// String time = html.substring(time_head, time_foot);
		// time = time.substring(time.indexOf("\"") + 1, time.indexOf("\";"));
		// String share_id = html.substring(share_id_head, share_id_foot);
		// share_id = share_id.substring(share_id.indexOf("\"") + 1,
		// share_id.indexOf("\";"));
		// System.out.println(share_id);
		// html = html.substring(a, b);
		// a = html.indexOf("{\"typicalPath");
		// b = html.indexOf("};");
		// JSONObject jsonObject = new JSONObject(html.substring(a, b + 1));
		// String uk = jsonObject.getString("uk");
		// String shareid = jsonObject.getString("shareid");
		// String path = URLEncoder.encode(jsonObject.getString("typicalPath"),
		// "utf-8");
		// jsonObject = new JSONObject(jsonObject.getString("file_list"));
		// JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
		// jsonObject = jsonArray.getJSONObject(0);
		// String app_id = jsonObject.getString("app_id");
		// if (jsonObject.getString("isdir").equals("1")) {
		// String url1 = "http://pan.baidu.com/share/list?uk="
		// + uk
		// + "&shareid="
		// + shareid
		// + "&page=1&num=100&dir="
		// + path
		// + "&order=time&desc=1&_="
		// + time
		// +
		// "&bdstoken=c51077ce0e0e313a16066612a13fbcd4&channel=chunlei&clienttype=0&web=1&app_id="
		// + app_id;
		// String fileListJson = HttpRequest.getData(url1);
		// System.out.println(fileListJson);
		// jsonObject =new JSONObject(fileListJson);
		// jsonArray = new JSONArray(jsonObject.getString("list"));
		// }
		// final int size = jsonArray.length();
		// for (int i = 0; i < size; i++) {
		// Map<String, Object> map = new HashMap<String, Object>();
		// jsonObject = jsonArray.getJSONObject(i);
		// String fileName = jsonObject.getString("server_filename");
		// //储存文件名
		// map.put("fileName", fileName);
		// fs_id.add(jsonObject.getString("fs_id"));
		// String fileInfo = HttpRequest
		// .getData("http://pan.baidu.com/api/sharedownload?sign="
		// + sign
		// + "&timestamp="
		// + time
		// +
		// "&bdstoken=c51077ce0e0e313a16066612a13fbcd4&channel=chunlei&clienttype=0&web=1&app_id=250528&encrypt=0&product=share&uk="
		// + uk + "&primaryid=" + share_id + "&fid_list=%5B"
		// + fs_id.get(i) + "%5D");
		// JSONObject json_data = new JSONObject(fileInfo);
		// if (json_data.getString("errno").equals("0")) {
		// JSONArray jsonArray2 = new JSONArray(json_data.getString("list"));
		// json_data = jsonArray2.getJSONObject(0);
		// //储存文件下载实链
		// map.put("url", json_data.getString("dlink"));
		// } else if (json_data.getString("errno").equals("-20")) {
		// return null;
		// // String getVerCode();
		// } else {
		// return null;
		// }
		// list.add(map);
		// }
		return list;
	}

	public static void main(String[] args) throws Exception {
		// System.out.println(getUrl("https://pan.baidu.com/s/1qXZHS08"));
		System.out.println(getUrl("https://pan.baidu.com/s/1qXZHS08"));
	}
}