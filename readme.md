Title:The intelligent greenhouse sensing system
This system consists of computer software and computer hardware.The system can collect the multiple information of light, temperature and humidity under greenhouse in real time to perform remote monitoring.The hardware part of the system includes controller, sensor and power management system, and the software part includes SCM program, serial port control program and MQTT client program.This is also my graduation design and selected as one of the best graduation projects in the school.The details are as follows:

Hardware part and SCM program: the hardware part includes many multiple nodes.The main control chip of each node is CMOS8-bit microcontroller STC89C52 chip with all kinds of sensors. STC89C52 is driven by SCM program and control sensors such as BH1750FVI optical sensor, DHT11 temperature and humidity sensor and DS18B20 temperature sensor to collect data.In addition, SCM program helps to parse instructions, package data,etc.CRC-16 algorithm is used to package data for accuracy and security.

Serial control program: the program is made into a graphical interface by Java in Eclipse for user  to operate easily.The main function of the program is exchanging data with the hardware system, including issuing measurement instructions and obtaining the  data package.The serial port control program decrypts, analyzes, formats and places the resulting packets into a common memory area.Data is transmitted based on Serial Communication Protoco.

MQTT master control program: the MQTT master control program mainly provides data networking service.It uses a polling thread to schedule the serial port program.Whenever there is new data passed from the latter, it will transport data to Internet based on MQTT protocol.MTQQ protocol is based on TCP/IP protocol, which is also a client-server based message publish/subscribe transmission protocol.Data is transmitted in the form of packets, which can work well in the case of low-hardware remote devices and poor network conditions.

MQTT client program.The client program provides a  graphical interface to user.A large amount of visual data captured by sensors from various nodes can be displayed automatically and scrolled constantly in the message display page, which is convenient for users to check and record.

Show:
MQTT operation window
![image](https://github.com/polya-xue/The-intelligent-greenhouse-sensing-system/blob/master/screenshots/1.png)

Client window
![image](https://github.com/polya-xue/The-intelligent-greenhouse-sensing-system/blob/master/screenshots/2.png)
