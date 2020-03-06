package com.yang.serialport.ui;

import com.yang.serialport.utils.ShowUtils;

public class SendMessage {
	static public String data=null;
	static public String targetData="";
	//static public byte[] byteData=null;
	static public int code=0;
	static public String type="00";
	static public String figure="wait";
	static public String symbol;
	static public String t_figure="test";	
	static public String m_figure="test";	
	static public int addFlag=0;
	static public int targetLength=0;
	@SuppressWarnings("static-access")
	public SendMessage(String data){
		this.data=data;//CC741968007E6AAA
    }
	public SendMessage(){
	}
    public void GetString(){
        int length;
        
    	synchronized(this){//添加互斥锁
    		//int test=CRC16Util.calcCrc16(byteData);//还需要修改，为前三位
    		//7e01027f
    		length=data.length();
    		
    		for(int i=0;i<length;i++){
    			//检测到前两个字符为包头CC时，开始相加字符，包尾不做判断，因为可能会和CRC码
    			//相同，并且AA和74的环境可以给CC提供一个安全的检验
    			
    			if(addFlag==1){
    				targetData=targetData.concat(data.substring(i,i+1));//加入一个字符
    				targetLength++;//用来计算目标数据的长度
    				//System.out.println(targetData);
    				if(targetLength==12){
    					addFlag=0;//字符数够了，停止相加
    					System.out.println("target:"+targetData);
    					//解析数据
    					String t_type,m_type;
    					code=(code+1)%2;//在1和2之间来回替换，作为标记位
    					t_type=targetData.substring(0,2);//温度的类型
    					t_figure=targetData.substring(2,4);//温度的数字
    					m_type=targetData.substring(4,6);//湿度的类型
    					m_figure=targetData.substring(6,8);//湿度的数字	
    					//00表示无值，01表示值为正，02表示值为负
    					if(t_type.equals("01")){
    						type="00";
    						figure=t_figure;
    					}
    					else if(m_type.equals("01")){
    						type="01";
    						figure=m_figure;
    					}
    					//清零
    					targetLength=0;
    					targetData="";
    				}
    			}
    			if(i>=1&&data.charAt(i-1)=='C'&&data.charAt(i)=='C'){
    				//targetData=targetData.concat(data.substring(i+2,length));
    				addFlag=1;//可以相加了
    				
    			}
    		}
    	}
    }
}
