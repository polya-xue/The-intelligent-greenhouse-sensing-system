package mqtt_Subscribe;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.eclipse.paho.client.mqttv3.MqttClient;  
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;  
import org.eclipse.paho.client.mqttv3.MqttException;  
import org.eclipse.paho.client.mqttv3.MqttTopic;  
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;  
//import com.yang.serialport.ui.SendMessage;
//import com.yang.serialport.utils.ShowUtils;

/**
 * ģ��һ���ͻ��˽�����Ϣ
 *
 */
public class ClientMQTT extends JFrame {  

    public static final String HOST = "tcp://0dz8s1z.mqtt.iot.gz.baidubce.com:1883";  
    public static String TOPIC1 = "temperature";  
    private static final String clientid = "client_suscribe";  //��Ψһ��ʶ��ʹ�ã��Զ���
    private MqttClient client;  
    private MqttConnectOptions options;  
    private String userName = "0dz8s1z/polya_serialport";    
    private String passWord = "hLPvOnIbhghvosS5";  
    @SuppressWarnings("unused")
    private ScheduledExecutorService scheduler;  
    
    /**
	 * ���������
	 */
	public static final int WIDTH = 400;

	/**
	 * �������߶�
	 */
	public static final int HEIGHT = 700;
	//�½��ı����ؼ�
	private JLabel Label1 = new JLabel("�¶�");
	private JTextArea dataView1 = new JTextArea();
	private JScrollPane scrollDataView1 = new JScrollPane(dataView1);
	private JLabel Label2 = new JLabel("ʪ��");
	private JTextArea dataView2 = new JTextArea();
	private JScrollPane scrollDataView2 = new JScrollPane(dataView2);
	private JLabel Label3 = new JLabel("���");
	private JTextArea dataView3 = new JTextArea();
	private JScrollPane scrollDataView3 = new JScrollPane(dataView3);
	// �����������
	//private JPanel serialPortPanel = new JPanel();
    
	public ClientMQTT(){
		// �رճ���
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		// ��ֹ�������
		setResizable(false);
		
		//���ñ���ͼƬ
		ImageIcon background = new ImageIcon("Ƥ������ɫ����.jpg");
		Image temp = background.getImage().getScaledInstance((int)WIDTH,
								(int)HEIGHT, background.getImage().SCALE_DEFAULT);//����ͼƬ��СΪ������С
		background = new ImageIcon(temp);//����ͼƬ�������úú��С��ͼƬ
		JLabel BackgroundLabel = new JLabel(background);
		BackgroundLabel.setBounds(0,0,background.getIconWidth(),background.getIconHeight());
		//��ӱ���
		getLayeredPane().add(BackgroundLabel,new Integer(-30001));//����ͼҪ����contentPaneͼ�����
		JPanel contentPane=(JPanel)getContentPane();
		contentPane.setOpaque(false);
		
		// ���ó��򴰿ھ�����ʾ
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getCenterPoint();
		setBounds(p.x - WIDTH / 2, p.y - HEIGHT / 2, WIDTH, HEIGHT);
		this.setLayout(null);
        
		Label1.setForeground(Color.white);//���¶ȡ���ǩ������
		Label1.setBounds(20, 20, 40, 20);
		add(Label1);
		dataView1.setFocusable(false);
		scrollDataView1.setBounds(20, 40, 350, 100);
		add(scrollDataView1);
		
		Label2.setForeground(Color.white);//��ʪ�ȡ���ǩ������
		Label2.setBounds(20, 160, 40, 20);
		add(Label2);
		dataView2.setFocusable(false);
		scrollDataView2.setBounds(20, 180, 350, 100);
		add(scrollDataView2);
		
		Label3.setForeground(Color.white);//����С���ǩ������
		Label3.setBounds(20, 300, 40, 20);
		add(Label3);
		dataView3.setFocusable(false);
		scrollDataView3.setBounds(20, 320, 350, 100);
		add(scrollDataView3);
		
		setTitle("�û�����");
		
		setVisible(true);//�ɼ�
		
		//start();
		
		
		
	}
	
    void start() {  
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
        dataView1.append("temperature"+ ": "+"24" + "\r\n"
            +"ʱ�̣� "+ScheduledRunnableTest.time + "\r\n");
        dataView2.append("moisture"+ ": "+"78" + "\r\n"
                +"ʱ�̣� "+ScheduledRunnableTest.time + "\r\n");
        dataView3.append("lux"+ ": "+"200" + "\r\n"
                +"ʱ�̣� "+ScheduledRunnableTest.time + "\r\n");
    }  
    /*
    public static void main(String[] args) throws MqttException, InterruptedException {  
        ClientMQTT client = new ClientMQTT();  
        client.start();  
        ServerMQTT server =new ServerMQTT();
        server.start();
    }  */
//    public static void main(String args[]){
//    	 new ClientMQTT().setVisible(true);
//         
//    }
}  