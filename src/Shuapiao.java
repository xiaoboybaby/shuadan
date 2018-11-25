import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Shuapiao {
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static String sendGet(String url, String referer,
			StringBuffer yunsuo_session_verify, Map<String, String> header)
			throws Exception {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			// System.out.println(urlNameString);
			URL realUrl = new URL(urlNameString);

			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();

			// 设置通用的请求属性
			// connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("accept", "*/*");
			// connection.setRequestProperty("Accept-Encoding",
			// "gzip, deflate, sdch");
			connection.setRequestProperty("Accept-Language", "gzh-CN,zh;q=0.8");

			String userAgent = RandomUserAgent.getRandomUserAgent();
			// System.out.println("userAgent:"+userAgent);
			connection.setRequestProperty("user-agent", userAgent);

			for (String key : header.keySet()) {
				connection.setRequestProperty(key, header.get(key));
			}

			// connection.setRequestProperty("Referer", referer);
			// connection.setRequestProperty("Host", "click.hunantv.com");
			// 建立实际的连接
			connection.connect();
			// connection.setConnectTimeout(1000 * HTTP_TIME_OUT);
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// System.out.println(map.get("Set-Cookie").get(0));

			yunsuo_session_verify.append(map.get("Set-Cookie").get(0));
			// System.out.println(yunsuo_session_verify);
			// 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			// result = new String(result.getBytes("GB231"));
		} catch (Exception e) {
			// System.out.println("发送GET请求出现异常！" + e);
			// e.printStackTrace();
			// throw new Exception();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param,
			Map<String, String> header) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			// conn.setConnectTimeout(1000 * HTTP_TIME_OUT);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					RandomUserAgent.getRandomUserAgent());
			for (String key : header.keySet()) {
				conn.setRequestProperty(key, header.get(key));
			}
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static int getNum(int start, int end) {
		return (int) (Math.random() * (end - start + 1) + start);
	}

	/**
	 * 返回手机号码
	 */
	private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153"
			.split(",");

	private static String getTel() {
		int index = getNum(0, telFirst.length - 1);
		String first = telFirst[index];
		String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
		String thrid = String.valueOf(getNum(1, 9100) + 10000).substring(1);
		return first + second + thrid;
	}

	public static void main(String[] args) throws Exception {
		Random r = new Random();
		int count = 0;
		int needCount = 0;
		needCount = 1;
		int tryCount = 0;
		boolean changeIP = true;
//		needCount = r.nextInt(10);
		while (true) {
			int rdm = r.nextInt(2000);
			String userId = args[0];

			System.out.println("重试次数:" + needCount + "当前次数:" + count);
			if (needCount == count) {
//				needCount = r.nextInt(10) + 1;
				count = 0;
				changeIP = true;
				tryCount = 0;
				while (changeIP) {

					ConnectNetWork.cutAdsl("VPSVPN");
					if (!ConnectNetWork.connAdsl("VPSVPN", "by09", "1")) {
						changeIP = true;
						System.out.println("连接VPN失败,重新连接");
						continue;
					}
					// 测试网络
					// 判断网络是否连通
					while (true) {

						try {
							//
							System.out.println("尝试连接网络..");
							String result = ConnectNetWork
									.executeCmd("ping -n 1 www.baidu.com");

							if (result.indexOf("丢失 = 0") > 0) {
								System.out.println("网络访问成功~");
								changeIP = false;
								break;
							}
							tryCount++;
							if (tryCount == 3) {
								changeIP = true;
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			count++;
			StringBuffer verifycode = new StringBuffer();
			Map<String, String> header = new HashMap<String, String>();
			header.put("Host", "lin.ebsig.com");
			header.put("Connection", "keep-alive");
			header.put("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			header.put("Upgrade-Insecure-Requests", "1");
			header.put("Referer", "http://lin.ebsig.com/index.php?for="
					+ userId + "&to=" + userId
					+ "&from=singlemessage&isappinstalled=0");

			String result = sendGet("http://lin.ebsig.com/index.php?for="
					+ userId + "&to=" + userId
					+ "&from=singlemessage&isappinstalled=0", "", verifycode,
					header);
			if (verifycode == null || verifycode.equals("")) {
				continue;
			}
			String cookie = null;
			try {
				cookie = verifycode.substring(
						verifycode.indexOf("_verify=") + 8,
						verifycode.indexOf("; expires="));
			} catch (Exception e) {

			}

			// System.out.println(cookie);

			// yunsuo_session_verify=85043cb118d54e1ee3c70e66539c1209
			header.clear();
			header.put("Host", "lin.ebsig.com");
			header.put("Connection", "keep-alive");
			header.put("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			header.put("Upgrade-Insecure-Requests", "1");
			// header.put("Referer",
			// "http://lin.ebsig.com/vote.html?assistantID=1252&from=1252");
			header.put("Cookie", "yunsuo_session_verify=" + cookie);
			header.put("Origin", "http://lin.ebsig.com");
			header.put("X-Requested-With", "XMLHttpRequest");
			header.put("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			header.put("Origin", "http://lin.ebsig.com");
			header.put("Referer", "http://lin.ebsig.com/vote.html?assistantID="
					+ userId + "&from=" + userId);
			result = sendPost("http://lin.ebsig.com/vote.php", "tel="
					+ getTel() + "&from=" + userId + "&to=" + userId, header);

			System.out.println(result);

			// result = sendPost(
			// "http://lin.ebsig.com/vote.php", "tel=" + getTel()
			// + "&from=132&to=132");
			//
			// System.out.println("结果" + result);
			// System.out.println("等待" + rdm + "毫秒");

			Thread.sleep(rdm);
		}
	}
}
