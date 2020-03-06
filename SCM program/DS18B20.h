#ifndef __DS18B20_H__
#define __DS18B20_H__

extern unsigned char sdata;			//测量到的温度的整数部分

extern void delay(unsigned char i);
extern void ReadTemperature(void);


#endif

