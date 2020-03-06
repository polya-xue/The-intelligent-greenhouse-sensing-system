package mqttClient1;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.yang.serialport.ui.SendMessage;
import com.yang.serialport.utils.ShowUtils;


public class ServerMQTT {

    //tcp://MQTT安装的服务器地址:MQTT定义的端口号
    public static final String HOST = "tcp://0dz8s1z.mqtt.iot.gz.baidubce.com:1883";
    //定义一个主题
    public static String TOPIC = "";
    //定义MQTT的ID，可以在MQTT服务配置中指定
    private static final String clientid = "client_publish";

    private MqttClient client;
    private MqttTopic topic11;
    private String userName = "0dz8s1z/polya_serialport";  
    private String passWord = "hLPvOnIbhghvosS5";  
    //public String mainMessage;
    private MqttMessage message;
    
    
    public int code=0;//初始化都为0,后续在0和1之间变化
    static public String type;
    static public String symbol;
    static public String figure;
    public String type1="00",type2="01",type3="10",type4="11";
	
    @SuppressWarnings("static-access")//对于static的警告
    /*
     * 初始化从串口包获取的数据
     * */
	public void initiate(){
    	SendMessage sendMessage=new SendMessage();
    	//sendMessage.GetString();
    	//this.code=sendMessage.code;
    	this.type=sendMessage.type;
    	this.symbol=sendMessage.symbol;
    	this.figure=sendMessage.figure;
    	
    	switch(type){
    		case "00":
    			TOPIC="temperature";
                //缺少判断符号的代码
    			break;
    		case "01":
    			TOPIC="moisture";
                //缺少判断符号的代码
    			break;
    		default:	
    			//System.out.println("发送端报文类型type出错");
    			ShowUtils.errorMessage("发送端报文类型type出错");
    			break;
    	}
//    	System.out.println("type:"+type);
//		System.out.println("figure:"+figure);
    }
    /**
     * 构造函数
     * @throws MqttException
     */
    public ServerMQTT() throws MqttException {
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        client = new MqttClient(HOST, clientid, new MemoryPersistence());
        initiate();
        connect();
    }

    /**
     *  用来连接服务器
     */
    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
            client.setCallback(new PushCallback());
            client.connect(options);

            topic11 = client.getTopic(TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param topic
     * @param message
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    public void publish(MqttTopic topic , MqttMessage message) throws MqttPersistenceException,
            MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println("message is published completely! "
                                   + token.isComplete());
    }

    /**
     *  启动入口
     * @param args
     * @throws MqttException*/
     
    public void start() throws MqttException, InterruptedException{
    	ServerMQTT server = new ServerMQTT();
        server.message = new MqttMessage();
        server.message.setQos(1);  //保证消息能到达一次
        server.message.setRetained(true);
        server.message.setPayload(figure.getBytes());
        server.publish(server.topic11 , server.message);
        Thread.sleep(2000);
        //System.out.println(server.message.isRetained() + "------ratained状态");
    }
 }