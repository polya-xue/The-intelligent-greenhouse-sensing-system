#include <reg51.h>
#include <stdio.h>
#include <intrins.h>

#include "DHT11.h"

typedef unsigned char BYTE;
typedef unsigned int WORD;
#define uint unsigned int 
#define uchar unsigned char 
	
sbit io = P1^0;
unsigned char data_byte; 
unsigned char RH,RL,TH,TL; 

typedef bit BOOL;

void delay_ms(uchar ms) //��ʱģ��// 
{ 
	uchar i; 
	while(ms--)                        
		for(i=0;i<100;i++); 
} 


void delay1()//һ��forѭ�������Ҫ8����������ڪ�һ����������Ϊ1us������Ϊ12MHz����Ҳ����˵��������ʱ8us�ડ����ʱ�����������΢��ȷһ�㪢 
{ 
	uchar i; 
	for(i=0;i<1;i++); 
} 


void start()//��ʼ�ź� 
{ 
	io=1; 
	delay1(); 
	io=0; 
	delay_ms(25);// �������������ͱ������18ms����֤DHT11�ܼ�⵽��ʼ�ź� 
	io=1;    //���Ϳ�ʼ�źŽ��������ߵ�ƽ��ʱ20-40us 
	delay1();//����������ʱ�������Ϊ24us������Ҫ�� 
	delay1(); 
	delay1(); 
} 

 
uchar receive_byte()//����һ���ֽ�// 
{ 
	uchar i,temp; 
	for(i=0;i<8;i++)//����8bit������ 
	{ 
		while(!io);//�ȴ�50us�ĵ͵�ƽ��ʼ�źŽ��� 
		delay1();//��ʼ�źŽ���֮����ʱ26us-28us������������ʱ������ 
		delay1(); 
		delay1(); 
		temp=0;//ʱ��Ϊ26us-28us����ʾ���յ�Ϊ����'0' 
		  
		if(io==1) 
			temp=1; //���26us-28us֮�󪣻�Ϊ�ߵ�ƽ�����ʾ���յ�����Ϊ'1' 

		while(io);//�ȴ������źŸߵ�ƽ��'0'Ϊ26us-28us��'1'Ϊ70us�� 
			data_byte<<=1;//���յ�����Ϊ��λ��ǰ�����ƪ� 
		 data_byte|=temp; 
	}
	return data_byte; 
} 

    

void receive()//��������// 
{ 
	uchar T_H,T_L,R_H,R_L,check,num_check,i; 
	start();//��ʼ�ź�// 
	io=1;   //������Ϊ���몣�жϴӻ���DHT11����Ӧ�ź� 
	if(!io)//�жϴӻ��Ƿ��е͵�ƽ��Ӧ�ź�// 
	{  
		while(!io);//�жϴӻ����� 80us �ĵ͵�ƽ��Ӧ�ź��Ƿ����// 
		while(io);//�жϴӻ����� 80us �ĸߵ�ƽ�Ƿ������������������������ݽ���״̬ 
		R_H=receive_byte();//ʪ�ȸ�λ 
		R_L=receive_byte();//ʪ�ȵ�λ 
		T_H=receive_byte();//�¶ȸ�λ 
		T_L=receive_byte();//�¶ȵ�λ 
		check=receive_byte();//У��λ 
		io=0; //�����һbit���ݽ���Ϻ󪣴ӻ����͵�ƽ50us// 
		for(i=0;i<7;i++)//���50us����ʱ 
			delay1(); 
		io=1;//�����������������ߪ��������״̬ 
		num_check=R_H+R_L+T_H+T_L; 
		if(num_check==check)//�ж϶������ĸ�����֮���Ƿ���У��λ��ͬ 
		{ 
			RH=R_H; 
			RL=R_L; 
			TH=T_H; 
			TL=T_L; 
			check=num_check; 
		} 
	}	 
} 
