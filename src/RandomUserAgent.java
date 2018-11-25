import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class RandomUserAgent {
	public static List<Map<Integer,String>> userAgents = new ArrayList<Map<Integer,String>>();
	static{
		addUserAgent(8,"MAC safari 5.1","User-Agent:Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
		addUserAgent(12,"IE9","User-Agent:Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;");
		addUserAgent(5,"IE8","User-Agent:Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)");
		addUserAgent(2,"IE7","User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
		addUserAgent(1,"Firefox 4.0.1 Mac","User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
		addUserAgent(7,"Firefox 4.0.1 Windows","User-Agent:Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
		addUserAgent(12,"Chrome 17.0 – MAC","User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
		addUserAgent(11,"腾讯TT","User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; TencentTraveler 4.0)");
		addUserAgent(19,"360浏览器","User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)");
		addUserAgent(14,"safari iOS 4.33 – iPhone","User-Agent:Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
		addUserAgent(7,"Android QQ浏览器 For android","User-Agent: MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		addUserAgent(2,"UC Web","User-Agent: UCWEB7.0.2.37/28/999");
	}
	
	public static void addUserAgent(int property,String name,String value){
		Map<Integer,String> propertiesMap = new HashMap<Integer,String>();
//		Map<String,String> userAgentMap = new HashMap<String,String>();
//		userAgentMap.put(name, value);
		propertiesMap.put(property, value);
		userAgents.add(propertiesMap);
	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
//		while(true){
//			System.out.println(getRandomNum(new int[]{1,2,3,4},new int[]{50,20,20,10}));
//		}
		
		while(true){
			System.out.println(getRandomUserAgent());
			Thread.sleep(1000);
		}
		
	}

	// probability与arr一一对应的表示arr中各个数的概率，且满足probability各元素和不能超过100；
	public static String getRandomUserAgent() {
	
		Random ran = new Random();
		int ran_num = ran.nextInt(100);
		int temp = 0;
		for (int i = 0; i < userAgents.size(); i++) {
			Iterator<Integer> iterator = userAgents.get(i).keySet().iterator();
			int properties = iterator.next();
			temp += properties;
			if (ran_num < temp)
				return userAgents.get(i).get(properties);
		}
		return "";
	}
}
