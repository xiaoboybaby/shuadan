import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Random;

public class ConnectNetWork {

	/**
	 * 执行CMD命令,并返回String字符串
	 */
	public static String executeCmd(String strCmd) throws Exception {
		Process p = Runtime.getRuntime().exec("cmd /c " + strCmd);
		StringBuilder sbCmd = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream(), Charset.forName("GBK")));
		String line;
		while ((line = br.readLine()) != null) {
			sbCmd.append(line + "\n");
		}
		return sbCmd.toString();
	}

	/**
	 * 连接ADSL
	 */
	public static boolean connAdsl(String adslTitle, String adslName,
			String adslPass) throws Exception {

		System.out.println("正在建立连接.");
		String adslCmd = "rasdial " + adslTitle + " " + adslName + " "
				+ adslPass;
		String tempCmd = executeCmd(adslCmd);

		// tempCmd = new String(tempCmd.getBytes("GBK"));
		// System.out.println(tempCmd);
		// 判断是否连接成功
		if (tempCmd.indexOf("已连接") > 0) {
			System.out.println("已成功建立连接.");
			return true;
		} else {
			System.err.println(tempCmd);
			System.err.println("建立连接失败");
			return false;
		}
	}

	/**
	 * 断开ADSL
	 */
	public static boolean cutAdsl(String adslTitle) throws Exception {
		String cutAdsl = "rasdial " + adslTitle + " /disconnect";
		String result = executeCmd(cutAdsl);

		if (result.indexOf("没有连接") != -1) {
			System.err.println(adslTitle + "连接不存在!");
			return false;
		} else {
			System.out.println("连接已断开");
			return true;
		}
	}

	public static String changeVPNAdress() {
		boolean success = false;
		String vpnAddress = null;
		try {
			//读取配置文件
			int lines = 0;
			InputStream in = HttpRequest.class.getResourceAsStream("svnaddress");
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			String profileStr = null;
		
			br.mark(0);
			while((profileStr = br.readLine()) != null){
				lines ++;
			}
			if(lines == 0){
				return null;
			}
			Random random = new Random();
			
			int rdLine = random.nextInt(lines) + 1;
			
			int currLine = 0;
			
			profileStr = null;
			br.close();
			in = HttpRequest.class.getResourceAsStream("svnaddress");
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			while((profileStr = br.readLine()) != null){
				
				currLine++;
				if(currLine == rdLine){
					break;
				}
			}
			vpnAddress = profileStr;
			br.close();
			String userprofile = System.getenv().get("USERPROFILE");
			String pbkPath = userprofile
					+ "\\Application Data\\Microsoft\\Network\\Connections\\Pbk";
			String pbkFilePath = pbkPath + "\\rasphone.pbk";
			String pbkFilePathNew = pbkPath + "\\rasphone_new.txt";
			File pbkFileNew = new File(pbkFilePathNew);
			//
			FileWriter fw = new FileWriter(pbkFileNew, false);

			InputStreamReader read = new InputStreamReader(new FileInputStream(
					new File(pbkFilePath)), "GBK");// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			BufferedWriter bw = new BufferedWriter(fw);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				if (lineTxt.indexOf("PhoneNumber") == 0) {
					lineTxt = "PhoneNumber=" + profileStr;
					System.out.println(lineTxt);
				}
				bw.append(lineTxt + "\r\n");
				bw.flush();
				// System.out.println("test--" + lineTxt);
			}
			read.close();
			bw.close();
			File pbkFile = new File(pbkFilePath);
			pbkFile.delete();
			pbkFileNew.renameTo(new File(pbkFilePath));
			
			success = true;
		} catch (IOException e) {
//			e.printStackTrace();
		}

		return vpnAddress;
	}

	public static void main(String[] args) throws Exception {
		changeVPNAdress();
		// System.out.println("当前JRE：" + System.getProperty("java.version" ));
		// System.out.println("当前JVM的默认字符集：" + Charset.defaultCharset());

		// cutAdsl("VPSVPN");
		// connAdsl("VPSVPN", "527", "527");
		// Thread.sleep(1000);
		// cutAdsl("宽带");
		// Thread.sleep(1000);
		// // 再连，分配一个新的IP
		// connAdsl("宽带", "hzhz**********", "******");
	}
}