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
 * ģ��һ���ͻ��˽�����Ϣ
 *
 */
public class ClientMQTT {  

    public static final String HOST = "tcp://0dz8s1z.mqtt.iot.gz.baidubce.com:1883";  
    public static String TOPIC1 = "temperature";  
    private static final String clientid = "client_suscribe";  //��Ψһ��ʶ��ʹ�ã��Զ���
    private MqttClient client;  
    private MqttConnectOptions options;  
    private String userName = "0dz8s1z/polya_serialport";    
    private String passWord = "hLPvOnIbhghvosS5";  
    @SuppressWarnings("unused")
    private ScheduledExecutorService scheduler;  
    
    public String type="00";
    public String type1="00",type2="01",type3="10",type4="11";
	
    @SuppressWarnings("static-access")//����static�ľ���
    /*
     * ��ȡ������
     * */
	public void initiate(){
    	SendMessage sendMessage=new SendMessage();
    	this.type=sendMessage.type;
    	//System.out.println("initiate����");
    	switch(this.type){
    		case "00":
    			TOPIC1="temperature";
    			break;
    		case "01":
    			TOPIC1="moisture";
    			break;
    		default:	
    			ShowUtils.errorMessage("���Ķ˱�������type����");
    			break;
    	}
    	//System.out.println("type:"+type);
    }	
    
    private void start() {  
        try {  
            // hostΪ��������clientid������MQTT�Ŀͻ���ID��һ����Ψһ��ʶ����ʾ��MemoryPersistence����clientid�ı�����ʽ��Ĭ��Ϊ���ڴ汣��  
            client = new MqttClient(HOST, clientid, new MemoryPersistence());  
            // MQTT����������  
            options = new MqttConnectOptions();  
            // �����Ƿ����session,�����������Ϊfalse��ʾ�������ᱣ���ͻ��˵����Ӽ�¼������Ϊtrue��ʾÿ�����ӵ������������µ��������  
            options.setCleanSession(false);  
            // �������ӵ��û���  
            options.setUserName(userName);  
            // �������ӵ�����  
            options.setPassword(passWord.toCharArray());  
            // ���ó�ʱʱ�� ��λΪ��  
            options.setConnectionTimeout(10);  
            // ���ûỰ����ʱ�� ��λΪ�� ��������ÿ��1.5*20���ʱ����ͻ��˷��͸���Ϣ�жϿͻ����Ƿ����ߣ������������û�������Ļ���  
            options.setKeepAliveInterval(20);  
            // ���ûص�  
            client.setCallback(new PushCallback());  
            MqttTopic topic = client.getTopic(TOPIC1);  
            //setWill�����������Ŀ����Ҫ֪���ͻ����Ƿ���߿��Ե��ø÷������������ն˿ڵ�֪ͨ��Ϣ  
//����        options.setWill(topic, "close".getBytes(), 2, true);  
            client.connect(options);  
            //������Ϣ  
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
    	 initiate();//����������
    	 //System.out.println("initiate finished");
         client.start();  
         ServerMQTT server =new ServerMQTT();
         server.start();
    }
}  