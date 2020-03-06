#include <reg51.h>
#include <stdio.h>

#include "DHT11.h"
#include "Timer.h"
#include "DS18B20.h"
#include "CRC16.h"

#define uchar unsigned char

//				 0xCC   't'    t_value    'h'     h_value   CRC1  CRC2   0xAA
uchar temp[8] = {0xCC,  0x74,    00,      0x68,     00,      00,   00,   0xAA};

void main()
{
	unsigned short crc16 = 0;
	UART_init();
	Timer0_init();
	delay(1000);
	while(1)
	{
		if (recvCmdFlag) {
			sampleType = Parse_Cmd(recvCmd);
		}
		
		if (Sys300ms_flag){
			Sys300ms_flag = 0;
			ReadTemperature();
			receive();
			
		if (recvCmdFlag) {
			if(sampleType == CMD_ERROR ) {
//				UART_Send_String("CMD ERROR\r\n");
			}
			else if (sampleType == CMD_READ_STOP){
				temp[1] = 0x00;
				temp[3] = 0x00;
//				UART_Send_String("CMD STOP\r\n");
				recvCmdFlag = 0;
			}
			else if(sampleType == CMD_READ_TEMP || sampleType == CMD_READ_HUMI || sampleType == CMD_READ_TEMP_HUMI) {
//				UART_Send_String("CMD CORRECT\r\n");
				if (sampleType == CMD_READ_TEMP) {
					temp[1] = 0x01;
					temp[2] = sdata;
					temp[3] = 0x00;
					temp[4] = 0;
				} 
				else if (sampleType == CMD_READ_HUMI) {
					temp[1] = 0x00;
					temp[2] = 0;
					temp[3] = 0x01;
					temp[4] = RH;
				}
				else {										// sampleType == CMD_READ_TEMP_HUMI
					temp[1] = 0x01;
					temp[2] = sdata;
					temp[3] = 0x01;
					temp[4] = RH;

				}
				crc16 = CRC16(temp, sizeof(temp)/sizeof(uchar) - 3);
				temp[5] = (uchar)((crc16 & 0xff00) >> 8);
				temp[6] = (uchar)(crc16 & 0x00ff);
				UART_Send_BytesArray(temp, sizeof(temp)/sizeof(uchar)); 
			}
		}			
		delay(1000);	
		}			
	}
}