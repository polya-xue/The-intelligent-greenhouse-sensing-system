package mqttClient1;

import java.util.concurrent.ScheduledExecutorService;  
import org.eclipse.paho.client.mqttv3.MqttClient;  
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;  
import org.eclipse.paho.client.mqttv3.MqttException;  
import org.eclipse.paho.client.mqttv3.MqttTopic;  
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;  
import com.yang.serialport.ui.SendMessage;
import com.yang.serialport.utils.ShowUtils;

/**
 * 模拟一个客户端接收消息
 *
 */
public class ClientMQTT {  

    public static final String HOST = "tcp://0dz8s1z.mqtt.iot.gz.baidubce.com:1883";  
    public static String TOPIC1 = "temperature";  
    private static final String clientid = "client_suscribe";  //做唯一标识符使用，自定义
    private MqttClient client;  
    private MqttConnectOptions options;  
    private String userName = "0dz8s1z/polya_serialport";    
    private String passWord = "hLPvOnIbhghvosS5";  
    @SuppressWarnings("unused")
    private ScheduledExecutorService scheduler;  
    
    public String type="00";
    public String type1="00",type2="01",type3="10",type4="11";
	
    @SuppressWarnings("static-access")//对于static的警告
    /*
     * 获取主题名
     * */
	public void initiate(){
    	SendMessage sendMessage=new SendMessage();
    	this.type=sendMessage.type;
    	//System.out.println("initiate启动");
    	switch(this.type){
    		case "00":
    			TOPIC1="temperature";
    			break;
    		case "01":
    			TOPIC1="moisture";
    			break;
    		default:	
    			ShowUtils.errorMessage("订阅端报文类型type出错");
    			break;
    	}
    	//System.out.println("type:"+type);
    }	
    
    private void start() {  
        try {  
            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存  
            client = new MqttClient(HOST, clientid, new MemoryPersistence());  
            // MQTT的连接设置  
            options = new MqttConnectOptions();  
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接  
            options.setCleanSession(false);  
            // 设置连接的用户名  
            options.setUserName(userName);  
            // 设置连接的密码  
            options.setPassword(passWord.toCharArray());  
            // 设置超时时间 单位为秒  
            options.setConnectionTimeout(10);  
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制  
            options.setKeepAliveInterval(20);  
            // 设置回调  
            client.setCallback(new PushCallback());  
            MqttTopic topic = client.getTopic(TOPIC1);  
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息  
//遗嘱        options.setWill(topic, "close".getBytes(), 2, true);  
            client.connect(options);  
            //订阅消息  
            int[] Qos  = {1};  
            String[] topic1 = {TOPIC1};  
            client.subscribe(topic1, Qos);  

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    /*
    public static void main(String[] args) throws MqttException, InterruptedException {  
        ClientMQTT client = new ClientMQTT();  
        client.start();  
        ServerMQTT server =new ServerMQTT();
        server.start();
    }  */
    public void mqtt_start()  throws MqttException, InterruptedException {
    	 ClientMQTT client = new ClientMQTT(); 
    	 initiate();//设置主题名
    	 //System.out.println("initiate finished");
         client.start();  
         ServerMQTT server =new ServerMQTT();
         server.start();
    }
}  