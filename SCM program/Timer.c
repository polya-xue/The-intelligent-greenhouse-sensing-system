#include <reg51.h>
#include <stdio.h>

#include "Timer.h"

#define uchar unsigned char
#define uint unsigned int


uint count = 0;
uchar sampleType = 0;
uchar Sys300ms_flag = 0, Sys500ms_flag = 0, Sys1s_flag = 0;
bit recvCmdFlag = 0;

/*
format:	0x7e 0x01 0x02 0x7f				// only temp
		0x7e 0x02 0x01 0x7f				// only humi
		0x7e 0x01 0x01 0x7f				// temp and humi//±»kill
		0x7e 0x02 0x02 0x7f				// Stop
		other							// Error
*/
uchar recvCmd[CMD_LENGTH] = {0};

uchar Parse_Cmd(uchar *recvCmd){
	uchar ret = 0;
		
	if (recvCmd[0] == 0x7e && recvCmd[3] == 0x7f) {
		if ((recvCmd[1] == 0x02) && (recvCmd[2] == 0x02)){
			ret = CMD_READ_STOP;
			return ret;
		}
		if (recvCmd[1] == 0x01) {
			ret += CMD_READ_TEMP;
		}
		if (recvCmd[2] == 0x01) {
			ret += CMD_READ_HUMI;
		}
	} 
	return ret;
}

void UART_init(){
	TMOD |= 0x20;	  		 
	TH1=0xfd;	  
	TL1=0xfd;
	TR1=1;		  
 
	SM0=0;
	SM1=1;		  
    REN=1;		  
	EA=1;	      
	ES=1;		  
}

void UART_Send_Byte(uchar Byte)          
{
	SBUF=Byte;      
	while(!TI); 
	TI=0;
}

//putchar
char putchar(uchar c)
{
    UART_Send_Byte(c);
    return c;
}

void UART_Send_String(uchar * Str)
{
	while((*Str)!=0)        
	{
		UART_Send_Byte(*Str);  
		Str++;                 
	}
}

void UART_Send_BytesArray(uchar * bytes, uchar len)
{
	uchar i = 0;
	for (i=0; i<len; i++) {
		UART_Send_Byte(bytes[i]);
	}
}

void UART_Interrupt() interrupt 4 {
	static int i = 0;
    if(RI)    
    {
        ES = 0;               
        RI = 0;
		
		*(recvCmd + i) = SBUF;
		i++;
		if (i == CMD_LENGTH){		
			recvCmdFlag = 1;
			i = 0;
		}
		
        ES = 1;    
	}
}


void Timer0_init() {
	TMOD |= 0x01;
	TH0 = 0x27;
	TL0 = 0x10;
//	TH0 = 0xfc;
//	TL0 = 0x18;
	ET0 = 1;
	EA = 1;
	TR0 = 1;
}

// T = 10ms
void Timer0_interrupt() interrupt 1 {
	TH0 = 0x27;
	TL0 = 0x10;

	count++;
	if (count%30 == 0) {
		Sys300ms_flag = 1;
	}
	if (count%50 == 1) {
		Sys500ms_flag = 1;
	}
	if (count%100 == 2) {
		Sys1s_flag = 1;
	}
	if (count > 256){		// 32.7s
		count = 0;
	}
}

