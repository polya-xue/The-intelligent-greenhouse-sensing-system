/*
 * MainFrame.java
 *
 * Created on 2016.8.19
 */

package com.yang.serialport.ui;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.eclipse.paho.client.mqttv3.MqttException;

import javax.swing.ImageIcon;//添加了一个用于背景的包

import com.yang.serialport.exception.NoSuchPort;
import com.yang.serialport.exception.NotASerialPort;
import com.yang.serialport.exception.PortInUse;
import com.yang.serialport.exception.SendDataToSerialPortFailure;
import com.yang.serialport.exception.SerialPortOutputStreamCloseFailure;
import com.yang.serialport.exception.SerialPortParameterFailure;
import com.yang.serialport.exception.TooManyListeners;
import com.yang.serialport.manage.SerialPortManager;
import com.yang.serialport.utils.ByteUtils;
import com.yang.serialport.utils.ShowUtils;
import com.yang.serialport.ui.SendMessage;
//import mqttClient1.ClientMQTT;
import mqttClient1.ServerMQTT;

import java.util.concurrent.locks.Lock;


public class MainFrame extends JFrame {

	/**
	 * 程序界面宽度
	 */
	public static final int WIDTH = 500;

	/**
	 * 程序界面高度
	 */
	public static final int HEIGHT = 360;

	private JTextArea dataView = new JTextArea();
	private JScrollPane scrollDataView = new JScrollPane(dataView);

	// 串口设置面板
	private JPanel serialPortPanel = new JPanel();
	private JLabel serialPortLabel = new JLabel("串口");
	private JLabel baudrateLabel = new JLabel("波特率");
	private JComboBox commChoice = new JComboBox();
	private JComboBox baudrateChoice = new JComboBox();

	// 操作面板
	private JPanel operatePanel = new JPanel();
	private JTextField dataInput = new JTextField();
	private JButton serialPortOperate = new JButton("打开串口");
	private JButton sendData = new JButton("发送数据");

	private List<String> commList = null;
	private SerialPort serialport;

	public MainFrame() {
		initView();//初始化frame窗口和背景
		initComponents();//添加各个panel
		actionListener();
		initData();//初始化串口和波特率窗口
	}

	private void initView() {
		// 关闭程序
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		// 禁止窗口最大化
		setResizable(false);

		//设置背景图片
		ImageIcon background = new ImageIcon("皮卡丘樱花.jpg");
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

		setTitle("串口通讯");
	}

	private void initComponents() {
		// 数据显示
		dataView.setFocusable(false);
		scrollDataView.setBounds(10, 40, 475, 150);//接收数据栏（重新设置了数据）
		add(scrollDataView);

		// 串口设置
		serialPortPanel.setBorder(BorderFactory.createTitledBorder("串口设置"));
		serialPortPanel.setBounds(10, 220, 170, 100);
		serialPortPanel.setLayout(null);
		add(serialPortPanel);

		serialPortLabel.setForeground(Color.gray);//“串口”的设置
		serialPortLabel.setBounds(10, 25, 40, 20);
		serialPortPanel.add(serialPortLabel);

		commChoice.setFocusable(false);
		commChoice.setBounds(60, 25, 100, 20);
		serialPortPanel.add(commChoice);

		baudrateLabel.setForeground(Color.gray);//"波特率"的设置
		baudrateLabel.setBounds(10, 60, 40, 20);
		serialPortPanel.add(baudrateLabel);

		baudrateChoice.setFocusable(false);
		baudrateChoice.setBounds(60, 60, 100, 20);
		serialPortPanel.add(baudrateChoice);

		// 操作
		operatePanel.setBorder(BorderFactory.createTitledBorder("操作"));
		operatePanel.setBounds(200, 220, 285, 100);
		operatePanel.setLayout(null);
		add(operatePanel);

		dataInput.setBounds(25, 25, 235, 20);
		operatePanel.add(dataInput);

		serialPortOperate.setFocusable(false);
		serialPortOperate.setBounds(45, 60, 90, 20);
		operatePanel.add(serialPortOperate);

		sendData.setFocusable(false);
		sendData.setBounds(155, 60, 90, 20);
		operatePanel.add(sendData);
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		commList = SerialPortManager.findPort();
		// 检查是否有可用串口，有则加入选项中
		if (commList == null || commList.size() < 1) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			for (String s : commList) {
				commChoice.addItem(s);
			}
		}

		baudrateChoice.addItem("9600");
		baudrateChoice.addItem("19200");
		baudrateChoice.addItem("38400");
		baudrateChoice.addItem("57600");
		baudrateChoice.addItem("115200");
	}

	private void actionListener() {
		serialPortOperate.addActionListener(new ActionListener() {
            //匿名内部类，使用new实现一个接口，接口没有关键词，并且要实现接口里所有的抽象方法
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("打开串口".equals(serialPortOperate.getText())
						&& serialport == null) {//这个serialport会导致第三次点击失效
					openSerialPort(e);
				} else {
					closeSerialPort(e);
				}
			}
		});

		sendData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendData(e);
			}
		});
	}

	/**
	 * 打开串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void openSerialPort(java.awt.event.ActionEvent evt) {
		// 获取串口名称
		String commName = (String) commChoice.getSelectedItem();
		// 获取波特率
		int baudrate = 9600;
		String bps = (String) baudrateChoice.getSelectedItem();
		baudrate = Integer.parseInt(bps);

		// 检查串口名称是否获取正确
		if (commName == null || commName.equals("")) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			try {
				serialport = SerialPortManager.openPort(commName, baudrate);
				if (serialport != null) {
					dataView.setText("串口已打开" + "\r\n");
					serialPortOperate.setText("关闭串口");
				}
			} catch (SerialPortParameterFailure e) {
				e.printStackTrace();
			} catch (NotASerialPort e) {
				e.printStackTrace();
			} catch (NoSuchPort e) {
				e.printStackTrace();
			} catch (PortInUse e) {
				e.printStackTrace();
				ShowUtils.warningMessage("串口已被占用！");
			}
		}

		try {
			SerialPortManager.addListener(serialport, new SerialListener());
		} catch (TooManyListeners e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void closeSerialPort(java.awt.event.ActionEvent evt) {
		SerialPortManager.closePort(serialport);
		dataView.setText("串口已关闭" + "\r\n");
		serialPortOperate.setText("打开串口");
	}

	/**
	 * 发送数据
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void sendData(java.awt.event.ActionEvent evt) {
		String data = null;
		data = dataInput.getText().toString();
		
		//System.out.println("data："+data+data.length());
		try {
			//if(data!=null&&data!="")
			SerialPortManager.sendToPort(serialport,
					ByteUtils.hexStr2Byte(data));//
		} catch (SendDataToSerialPortFailure e) {
			e.printStackTrace();
		} catch (SerialPortOutputStreamCloseFailure e) {
			e.printStackTrace();
		}
		
	}

	private class SerialListener implements SerialPortEventListener {
		/**
		 * 处理监控到的串口事件
		 */
		public void serialEvent(SerialPortEvent serialPortEvent) {

			switch (serialPortEvent.getEventType()) {

			case SerialPortEvent.BI: // 10 通讯中断
				ShowUtils.errorMessage("与串口设备通讯中断");
				break;

			case SerialPortEvent.OE: // 7 溢位（溢出）错误

			case SerialPortEvent.FE: // 9 帧错误

			case SerialPortEvent.PE: // 8 奇偶校验错误

			case SerialPortEvent.CD: // 6 载波检测

			case SerialPortEvent.CTS: // 3 清除待发送数据

			case SerialPortEvent.DSR: // 4 待发送数据准备好了

			case SerialPortEvent.RI: // 5 振铃指示

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
				break;

			case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
				byte[] data = null;
				try {
					if (serialport == null) {
						ShowUtils.errorMessage("串口对象为空！监听失败！");
					} else {
						// 读取串口数据
						data = SerialPortManager.readFromPort(serialport);
						//将16进制byte转为字符串
						String dataString;
						dataString=ByteUtils.byteArrayToHexString(data,false);
						//显示在控制面板
						dataView.append(dataString + "\r\n");
				
						//System.out.printf("%s", data);
						//System.out.printf("%s", dataString);//
						
						SendMessage sendMessage=new SendMessage(dataString);
						sendMessage.GetString();
					}
				} catch (Exception e) {
					ShowUtils.errorMessage(e.toString());
					// 发生读取错误时显示错误信息后退出系统
					System.exit(0);
				}
				break;
			}
		}
	}

	@SuppressWarnings("static-access")
	public static void main(String args[]){
		
		thread startThread=new thread();
	    startThread.start();
		SendMessage sendMessage=new SendMessage();//sendMessage用来解析数据包
		int code=0;
		while(true){
			
			if(code!=sendMessage.code){//有新的数据传送过来，标志位变化，激活轮询线程
				synchronized (Lock.class)
	            {
					//System.out.println("code:"+sendMessage.code);
					code=sendMessage.code;//改变标志位
	                Lock.class.notify();
	            }
	            try {
	                Thread.sleep(10);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
			}
		}
	}
}
class thread  extends Thread implements Runnable {
	
	public void run(){
		new MainFrame().setVisible(true);//启动串口程序
		ServerMQTT server =null;//启动mqtt的publish端，且publish端会获取到sendMessage里的信息
		try {
				server = new ServerMQTT();//先初始化static变量
			} catch (MqttException e1) {
				ShowUtils.errorMessage("主函数启动mqtt时出错");
				e1.printStackTrace();
			}
		while(true){
			try {
				  server.start();//mqtt客户端的start
		      } catch (MqttException | InterruptedException e) {
		    	  ShowUtils.errorMessage("server start failed");
		    	  e.printStackTrace();
		      }
			
			synchronized (Lock.class)//将数据传给MQTT后让线程阻塞，直到下次有数据变化
            {
                try {
                    Lock.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
		}
	}
}

