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
        
    	synchronized(this){//��ӻ�����
    		//int test=CRC16Util.calcCrc16(byteData);//����Ҫ�޸ģ�Ϊǰ��λ
    		//7e01027f
    		length=data.length();
    		
    		for(int i=0;i<length;i++){
    			//��⵽ǰ�����ַ�Ϊ��ͷCCʱ����ʼ����ַ�����β�����жϣ���Ϊ���ܻ��CRC��
    			//��ͬ������AA��74�Ļ������Ը�CC�ṩһ����ȫ�ļ���
    			
    			if(addFlag==1){
    				targetData=targetData.concat(data.substring(i,i+1));//����һ���ַ�
    				targetLength++;//��������Ŀ�����ݵĳ���
    				//System.out.println(targetData);
    				if(targetLength==12){
    					addFlag=0;//�ַ������ˣ�ֹͣ���
    					System.out.println("target:"+targetData);
    					//��������
    					String t_type,m_type;
    					code=(code+1)%2;//��1��2֮�������滻����Ϊ���λ
    					t_type=targetData.substring(0,2);//�¶ȵ�����
    					t_figure=targetData.substring(2,4);//�¶ȵ�����
    					m_type=targetData.substring(4,6);//ʪ�ȵ�����
    					m_figure=targetData.substring(6,8);//ʪ�ȵ�����	
    					//00��ʾ��ֵ��01��ʾֵΪ����02��ʾֵΪ��
    					if(t_type.equals("01")){
    						type="00";
    						figure=t_figure;
    					}
    					else if(m_type.equals("01")){
    						type="01";
    						figure=m_figure;
    					}
    					//����
    					targetLength=0;
    					targetData="";
    				}
    			}
    			if(i>=1&&data.charAt(i-1)=='C'&&data.charAt(i)=='C'){
    				//targetData=targetData.concat(data.substring(i+2,length));
    				addFlag=1;//���������
    				
    			}
    		}
    	}
    }
}
