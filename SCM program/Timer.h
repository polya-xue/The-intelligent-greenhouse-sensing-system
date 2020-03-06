#ifndef __TIMER_H__
#define __TIMER_H__

#define uchar unsigned char
#define uint unsigned int



#define CMD_LENGTH				4
#define CMD_ERROR				0
#define CMD_READ_STOP			1
#define CMD_READ_TEMP			2
#define CMD_READ_HUMI			3
#define CMD_READ_TEMP_HUMI		5

extern bit recvCmdFlag;
extern uchar sampleType;
extern uchar Sys300ms_flag, Sys500ms_flag, Sys1s_flag;
extern uint count;
extern uchar recvCmd[CMD_LENGTH];

extern uchar Parse_Cmd(uchar *recvCmd);
extern void UART_init();
extern void UART_Send_Byte(uchar Byte);
extern void UART_Send_String(uchar * Str);
extern void UART_Send_BytesArray(uchar * bytes, uchar len);
extern void Timer0_init();


#endif

