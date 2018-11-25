import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {

	private static final RequestThread thread = null;
	public static boolean[] threadRun;
	public static List<RequestThread> threads = new ArrayList<RequestThread>();
	public static int[] counts;
	public static String VPN_NAME, VPN_USERNAME, VPN_PASSWORD, LOG_DIR,
			SYSTEM_ID, SUM_SERVER;
	public static int CHANGE_IP_TIME, HTTP_TIME_OUT;
	public static String clickUrl = null;
	public static boolean connectSuccess = false;
	public static int dialStatus = 0;
	public static List<Map<String, String>> userAgents = new ArrayList<Map<String, String>>();
	public static FileWriter fw = null;
	// 当前总量
	public static int thisCount = 0;
	public static final String START_PATH = "/StartServlet";
	public static final String HEARTBEAT_PATH = "/HeartBeatServlet";
	public static final String COUNT_PATH = "/CountServlet";
	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"YYYY-MM-dd HH:mm:ss");
	public static String jobId = "";
	static {
		Properties prop = new Properties();
		InputStream in = HttpRequest.class
				.getResourceAsStream("properties.properties");
		// InputStream in =
		// Thread.currentThread().getContextClassLoader().getResourceAsStream("/proerties.properties");
		try {
			// prop.load(new FileInputStream("C:\\properties.properties"));
			prop.load(in);

			String systemId = System.getenv().get("SYSTEM_ID");
			if (systemId == null) {
				systemId = "";
			} else {
				SYSTEM_ID = systemId;
			}
			VPN_NAME = System.getenv().get("VPN_NAME");
			if (VPN_NAME == null) {
				VPN_NAME = prop.getProperty("VPN_NAME").trim();
			}
			VPN_USERNAME = System.getenv().get("VPN_USERNAME");
			if (VPN_USERNAME == null) {
				VPN_USERNAME = prop.getProperty("VPN_USERNAME").trim()
						+ systemId;
			}
			VPN_PASSWORD = System.getenv().get("VPN_PASSWORD");
			if (VPN_PASSWORD == null) {
				VPN_PASSWORD = prop.getProperty("VPN_PASSWORD").trim();
			}
			LOG_DIR = System.getenv().get("LOG_DIR");
			if (LOG_DIR == null) {
				LOG_DIR = prop.getProperty("LOG_DIR").trim();
			}
			String changeIpTime = System.getenv().get("CHANGE_IP_TIME");

			if (changeIpTime == null) {
				CHANGE_IP_TIME = Integer.parseInt(prop.getProperty(
						"CHANGE_IP_TIME").trim());
			} else {
				CHANGE_IP_TIME = Integer.parseInt(System.getenv().get(
						"CHANGE_IP_TIME"));
			}
			String httpTimeOut = System.getenv().get("HTTP_TIME_OUT");
			if (httpTimeOut == null) {
				HTTP_TIME_OUT = Integer.parseInt(prop.getProperty(
						"HTTP_TIME_OUT").trim());
			} else {
				HTTP_TIME_OUT = Integer.parseInt(httpTimeOut);
			}
			String sumServer = System.getenv("SUM_SERVER");
			if (sumServer == null) {
				SUM_SERVER = prop.getProperty("SUM_SERVER");
			} else {
				SUM_SERVER = sumServer;
			}

			System.out.println("系统配置如下");
			System.out.println("VPN名称:" + VPN_NAME);
			System.out.println("VPN用户名:" + VPN_USERNAME);
			System.out.println("VPN密码:" + VPN_PASSWORD);
			System.out.println("日志路径:" + LOG_DIR);
			System.out.println("HTTP请求超时:" + HTTP_TIME_OUT + "秒");
			System.out.println("更换IP时间:" + CHANGE_IP_TIME + "秒");
			System.out.println("统计服务器地址:" + SUM_SERVER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
	public static String sendGet(String url, Map<String, String> header)
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
			// connection.setRequestProperty("Host", "click.hunantv.com");
			if (header != null) {
				for (String key : header.keySet()) {
					connection.setRequestProperty(key, header.get(key));
				}
			}
			// 建立实际的连接
			connection.connect();
			connection.setConnectTimeout(1000 * HTTP_TIME_OUT);

			// 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			// System.out.println("发送GET请求出现异常！" + e);
			// e.printStackTrace();
			throw e;
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
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setConnectTimeout(1000 * HTTP_TIME_OUT);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					RandomUserAgent.getRandomUserAgent());
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

	public static void connectVPN() {

		int tryCount = 2;
		int pingCount = 0;
		boolean reDial = true;
		String vpnAddress = null;

		// 拨号等待时间 30秒
		int dialWaitTime = 0;

		System.out.println("开始拨号");
		if (null == VPN_NAME || "".equals(VPN_NAME)) {
			return;
		}

		outter: while (reDial) {
			pingCount = 0;
			dialWaitTime = 0;
			try {
				ConnectNetWork.cutAdsl(VPN_NAME);

				// 修改VPN地址
				vpnAddress = ConnectNetWork.changeVPNAdress();
				connectSuccess = false;
				dialStatus = 0;
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							connectSuccess = ConnectNetWork.connAdsl(VPN_NAME,
									VPN_USERNAME, VPN_PASSWORD);
							if (connectSuccess) {
								// 成功
								dialStatus = 1;
							} else {
								// 失败
								dialStatus = 2;
							}
						} catch (Exception e) {
							dialStatus = 2;
							e.printStackTrace();
						}
					}
				});
				thread.start();
				System.out.println("正在切换IP,等待中:");
				inner: while (true) {
					Thread.sleep(1000);
					dialWaitTime++;

					System.out.print(dialWaitTime + ",");
					if (dialWaitTime >= 30) {
						// 切断连接VPN的线程
						if (thread.isAlive()) {
							thread.interrupt();
						}
						System.out.println("拨号尝试30秒失败更换IP地址重新拨号~~~~");
						reDial = true;
						continue outter;
					}
					if (dialStatus == 1) {
						// 成功
						reDial = false;
						break inner;
					} else if (dialStatus == 2) {
						// 失败
						reDial = true;
						continue outter;
					}
				}
				/*
				 * if (ConnectNetWork.connAdsl(VPN_NAME, VPN_USERNAME,
				 * VPN_PASSWORD)) { reDial = false; break; }
				 */

			} catch (Exception e) {
				e.printStackTrace();
				reDial = true;
				break;
			}
		}

		// 判断网络是否连通
		while (true) {
			try {
				//
				System.out.println("尝试连接网络..");
				String result = ConnectNetWork.executeCmd("ping -n 1 8.8.8.8");
				if (result.indexOf("丢失 = 0") > 0
						|| result.indexOf("Lost = 0") > 0) {
					System.out.println("网络访问成功~");
					reDial = false;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (pingCount == tryCount) {
				reDial = true;
				break;
			}
			pingCount++;
		}

	}

	public static void main(String[] args) throws UnsupportedEncodingException,
			InterruptedException {

		String url = args[0];
		int threadCount = Integer.parseInt(args[1]);

		thisCount = Integer.parseInt(args[2]);

		Pattern pattern = Pattern
				.compile("(http://|https://){1}[\\w\\.\\-/:]+");
		pattern = Pattern.compile("/(\\d{3,})/[\\w\\W]*/(\\d+).html");
		Matcher matcher = pattern.matcher(url);
		matcher.find();
		String cid = matcher.group(1);
		String vid = matcher.group(2);
		clickUrl = "http://click.hunantv.com/click.php?vid=" + vid + "&cid="
				+ cid + "&callback=playerBack";
		String fileName = LOG_DIR + "totalcount" + new Date().getTime()
				+ ".txt";
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		// 记录开始
		String param = "systemId=" + SYSTEM_ID + "&url="
				+ URLEncoder.encode(url, "UTF-8") + "&threadNum=" + threadCount
				+ "&startTime="
				+ URLEncoder.encode(sdf.format(new Date()), "UTF-8");
		String serverUrl = SUM_SERVER + "/" + START_PATH + "?" + param;
		try {
			jobId = sendGet(serverUrl, null);
			if ("".equals(jobId)) {
				System.out.println("访问统计服务器失败,请检查统计服务器是否启动!");
				return;
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			System.out.println("访问统计服务器失败,请检查统计服务器是否启动!");
			return;

		}
		try {
			fw = new FileWriter(fileName);
			fw.write("程序启动,时间为:" + new Date()
					+ "--------------------------------\r\n");
			fw.write("链接为:" + url + "\r\n");
			fw.flush();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 更换IP
		connectVPN();
		HttpRequest.threadRun = new boolean[threadCount];
		counts = new int[threadCount];
		for (int i = 0; i < threadCount; i++) {
			HttpRequest.threadRun[i] = true;
			RequestThread thread = new RequestThread(url, i + 1);
			thread.start();
			HttpRequest.threads.add(thread);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				int countOld = 0;
				int increaseCount = 0;
				int waitingTime = 0;
				//未发送到服务器的数量
				int currIncreaseCount = 0;
				long paste = 0;
				long startDate = 0;
				long endDate = 0;

				while (true) {
					// 计算更换IP等待时间
					boolean waiting = false;
					if(startDate == 0){
						startDate = new Date().getTime();
					}
					
					for (int i = 0; i < HttpRequest.threadRun.length; i++) {
						if (!HttpRequest.threadRun[i]) {
							waiting = true;
						}
					}
					if (waiting) {
						try {
							Thread.sleep(1000);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						waitingTime++;
						continue;
					} else {
						waitingTime = 0;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					int count = 0;
					for (int i = 0; i < counts.length; i++) {
						count += counts[i];
					}
					if (countOld != 0) {
						increaseCount = count - countOld;
					}
					countOld = count;
					System.out.println("当前链接点击总数为:" + count + "增量:"
							+ increaseCount);
					Date date = new Date();
					try {
						fw.write("本次统计时间为:" + date + "\t本次统计次数为:{" + count
								+ "}\t本次统计增量为:{" + increaseCount + "}\r\n");
						fw.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}

					SimpleDateFormat sdf = new SimpleDateFormat(
							"YYYY-MM-dd HH:mm:ss");

					// 记录数据
					String serverUrl = null;
					currIncreaseCount = currIncreaseCount + increaseCount;
					endDate = new Date().getTime();
					paste = endDate - startDate;
					serverUrl = HttpRequest.SUM_SERVER + "/"
							+ HttpRequest.COUNT_PATH + "?systemId="
							+ HttpRequest.SYSTEM_ID + "&timestamp="
							+ date.getTime() + "&count=" + currIncreaseCount
							+ "&totalCount=" + count + "&paste=" + paste
							+ "&jobId=" + HttpRequest.jobId;

					try {
						String result = HttpRequest.sendGet(serverUrl, null);
						currIncreaseCount = 0;
						startDate = 0;
						// 心跳
						
						serverUrl = HttpRequest.SUM_SERVER + "/"
								+ HttpRequest.HEARTBEAT_PATH + "?jobId="
								+ HttpRequest.jobId + "&systemId="
								+ HttpRequest.SYSTEM_ID;

						result = HttpRequest.sendGet(serverUrl, null);
						
						
						if ("stop".equals(result)) {
							try {
								ConnectNetWork.cutAdsl(HttpRequest.VPN_NAME);

							} catch (Exception e) {
								e.printStackTrace();
							}
							System.out.println("服务器请求停止~");
							System.exit(0);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						
					}
					if (thisCount < count && thisCount != -1) {
						System.out.println("系统已完成量:" + count
								+ "---------------------------------");
						try {
							ConnectNetWork.cutAdsl(VPN_NAME);
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.exit(0);
					}
				}
			}
		}).start();

		// 监控esc按钮事件

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					KeyEvent evt = (KeyEvent) e;
					if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
						System.out.println("退出");
						System.exit(0);
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

		// 一段时间后更换IP
		Random random = new Random();// 随机时间范围为30分钟
		long sleeptime = 1000 * 60 * CHANGE_IP_TIME;// 10分钟
		// sleeptime = 1;

		while (true) {
			long randomtime = random.nextInt(6000);
			sleeptime += randomtime;
			// 睡眠一段时间更换IP
			Thread.sleep(sleeptime);

			// 更改状态,使其他线程等待
			for (int i = 0; i < threadCount; i++) {
				HttpRequest.threadRun[i] = false;
			}
			// Thread.sleep(500);
			System.out.println("开始更换IP地址");
			// Thread.sleep(5000);
			// 更换IP
			connectVPN();

			// 恢复状态
			for (int i = 0; i < threadCount; i++) {
				HttpRequest.threadRun[i] = true;
			}

			for (int i = 0; i < threadCount; i++) {
				synchronized (threads.get(i)) {
					threads.get(i).notify();
				}
			}
		}

		// while(true){
		// String result =
		// HttpRequest.sendGet("http://click.hunantv.com/click.php?vid=40178&cid=360&callback=playerBack",
		// "");
		// System.out.println(result);
		//
		// }
		// System.out.println(new String(result.getBytes(),"UTF-8"));
	}
}

class RequestThread extends Thread {
	String url;
	int tId;
	int totalCount;
	long startTime;
	long endTime;

	public RequestThread(String url, int tId) {
		this.url = url;
		this.tId = tId;
	}

	public void run() {
		int count = 0;
		String result = null;
		boolean success = false;
		Map<String, String> header = new HashMap<String, String>();
		header.put("Referer", url);
		header.put("Host", "click.hunantv.com");
		while (true) {

			while (!HttpRequest.threadRun[tId - 1]) {
				// 等待
				synchronized (this) {
					try {
						// 线程等待
						System.out.println("线程:" + tId + "等待中..");
						// HttpRequest.threads.wait();
						this.wait();
						System.out.println("线程:" + tId + "被唤醒..");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			startTime = new Date().getTime();
			try {

				success = false;

				result = HttpRequest.sendGet(HttpRequest.clickUrl, header);
				// System.out.println(result);
				if (!"".equals(result) && result != null) {
					totalCount++;
					success = true;
				}
				HttpRequest.counts[tId - 1] = totalCount;

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!success) {
				// System.out.println("当前请求被拦截~");
				// try {
				// HttpRequest.fw.write("当前请求被拦截~");
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				continue;
			}
			endTime = new Date().getTime();

			// count++;
			// if (count == 10) {
			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// System.out.println("当前线程:" + tId + "耗时:" + (endTime - startTime)
			// + "毫秒");
			// count = 0;
			// }

		}
	}
}
/*
 * class HeartBeatThread extends Thread{ public String jobId; public String
 * systemId; public HeartBeatThread(String jobId,String systemId){ this.jobId =
 * jobId; this.systemId = systemId; }
 * 
 * @Override public void run() { super.run(); String serverUrl =
 * HttpRequest.SUM_SERVER + "/" + HttpRequest.HEARTBEAT_PATH + "?jobId=" + jobId
 * + "&systemId=" + systemId; try { String result =
 * HttpRequest.sendGet(serverUrl, null); // if("stop".) } catch (Exception e) {
 * e.printStackTrace(); } } }
 */