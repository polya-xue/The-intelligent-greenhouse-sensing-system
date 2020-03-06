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

    //tcp://MQTT��װ�ķ�������ַ:MQTT����Ķ˿ں�
    public static final String HOST = "tcp://0dz8s1z.mqtt.iot.gz.baidubce.com:1883";
    //����һ������
    public static String TOPIC = "";
    //����MQTT��ID��������MQTT����������ָ��
    private static final String clientid = "client_publish";

    private MqttClient client;
    private MqttTopic topic11;
    private String userName = "0dz8s1z/polya_serialport";  
    private String passWord = "hLPvOnIbhghvosS5";  
    //public String mainMessage;
    private MqttMessage message;
    
    
    public int code=0;//��ʼ����Ϊ0,������0��1֮��仯
    static public String type;
    static public String symbol;
    static public String figure;
    public String type1="00",type2="01",type3="10",type4="11";
	
    @SuppressWarnings("static-access")//����static�ľ���
    /*
     * ��ʼ���Ӵ��ڰ���ȡ������
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
                //ȱ���жϷ��ŵĴ���
    			break;
    		case "01":
    			TOPIC="moisture";
                //ȱ���жϷ��ŵĴ���
    			break;
    		default:	
    			//System.out.println("���Ͷ˱�������type����");
    			ShowUtils.errorMessage("���Ͷ˱�������type����");
    			break;
    	}
//    	System.out.println("type:"+type);
//		System.out.println("figure:"+figure);
    }
    /**
     * ���캯��
     * @throws MqttException
     */
    public ServerMQTT() throws MqttException {
        // MemoryPersistence����clientid�ı�����ʽ��Ĭ��Ϊ���ڴ汣��
        client = new MqttClient(HOST, clientid, new MemoryPersistence());
        initiate();
        connect();
    }

    /**
     *  �������ӷ�����
     */
    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // ���ó�ʱʱ��
        options.setConnectionTimeout(10);
        // ���ûỰ����ʱ��
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
     *  �������
     * @param args
     * @throws MqttException*/
     
    public void start() throws MqttException, InterruptedException{
    	ServerMQTT server = new ServerMQTT();
        server.message = new MqttMessage();
        server.message.setQos(1);  //��֤��Ϣ�ܵ���һ��
        server.message.setRetained(true);
        server.message.setPayload(figure.getBytes());
        server.publish(server.topic11 , server.message);
        Thread.sleep(2000);
        //System.out.println(server.message.isRetained() + "------ratained״̬");
    }
 }