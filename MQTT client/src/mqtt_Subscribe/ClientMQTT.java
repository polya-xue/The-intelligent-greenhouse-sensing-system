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
 * 模拟一个客户端接收消息
 *
 */
public class ClientMQTT extends JFrame {  

    public static final String HOST = "tcp://0dz8s1z.mqtt.iot.gz.baidubce.com:1883";  
    public static String TOPIC1 = "temperature";  
    private static final String clientid = "client_suscribe";  //做唯一标识符使用，自定义
    private MqttClient client;  
    private MqttConnectOptions options;  
    private String userName = "0dz8s1z/polya_serialport";    
    private String passWord = "hLPvOnIbhghvosS5";  
    @SuppressWarnings("unused")
    private ScheduledExecutorService scheduler;  
    
    /**
	 * 程序界面宽度
	 */
	public static final int WIDTH = 400;

	/**
	 * 程序界面高度
	 */
	public static final int HEIGHT = 700;
	//新建文本区控件
	private JLabel Label1 = new JLabel("温度");
	private JTextArea dataView1 = new JTextArea();
	private JScrollPane scrollDataView1 = new JScrollPane(dataView1);
	private JLabel Label2 = new JLabel("湿度");
	private JTextArea dataView2 = new JTextArea();
	private JScrollPane scrollDataView2 = new JScrollPane(dataView2);
	private JLabel Label3 = new JLabel("光感");
	private JTextArea dataView3 = new JTextArea();
	private JScrollPane scrollDataView3 = new JScrollPane(dataView3);
	// 串口设置面板
	//private JPanel serialPortPanel = new JPanel();
    
	public ClientMQTT(){
		// 关闭程序
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		// 禁止窗口最大化
		setResizable(false);
		
		//设置背景图片
		ImageIcon background = new ImageIcon("皮卡丘蓝色背景.jpg");
		Image temp = background.getImage().getScaledInstance((int)WIDTH,
								(int)HEIGHT, background.getImage().SCALE_DEFAULT);//设置图片大小为弹窗大小
		background = new ImageIcon(temp);//背景图片等于设置好后大小的图片
		JLabel BackgroundLabel = new JLabel(background);
		BackgroundLabel.setBounds(0,0,background.getIconWidth(),background.getIconHeight());
		//添加背景
		getLayeredPane().add(BackgroundLabel,new Integer(-30001));//背景图要放在contentPane图层后面
		JPanel contentPane=(JPanel)getContentPane();
		contentPane.setOpaque(false);
		
		// 设置程序窗口居中显示
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getCenterPoint();
		setBounds(p.x - WIDTH / 2, p.y - HEIGHT / 2, WIDTH, HEIGHT);
		this.setLayout(null);
        
		Label1.setForeground(Color.white);//“温度”标签的设置
		Label1.setBounds(20, 20, 40, 20);
		add(Label1);
		dataView1.setFocusable(false);
		scrollDataView1.setBounds(20, 40, 350, 100);
		add(scrollDataView1);
		
		Label2.setForeground(Color.white);//”湿度”标签的设置
		Label2.setBounds(20, 160, 40, 20);
		add(Label2);
		dataView2.setFocusable(false);
		scrollDataView2.setBounds(20, 180, 350, 100);
		add(scrollDataView2);
		
		Label3.setForeground(Color.white);//“光感”标签的设置
		Label3.setBounds(20, 300, 40, 20);
		add(Label3);
		dataView3.setFocusable(false);
		scrollDataView3.setBounds(20, 320, 350, 100);
		add(scrollDataView3);
		
		setTitle("用户界面");
		
		setVisible(true);//可见
		
		//start();
		
		
		
	}
	
    void start() {  
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
        dataView1.append("temperature"+ ": "+"24" + "\r\n"
            +"时刻： "+ScheduledRunnableTest.time + "\r\n");
        dataView2.append("moisture"+ ": "+"78" + "\r\n"
                +"时刻： "+ScheduledRunnableTest.time + "\r\n");
        dataView3.append("lux"+ ": "+"200" + "\r\n"
                +"时刻： "+ScheduledRunnableTest.time + "\r\n");
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