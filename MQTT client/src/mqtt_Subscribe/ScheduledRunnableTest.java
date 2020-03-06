package mqtt_Subscribe;


import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduledRunnableTest extends TimerTask {
	public static String time="´ý¶¨";
	ClientMQTT clientMqtt=new ClientMQTT();
    public void run() {
        try {
            Thread.sleep(2000);
            clientMqtt.start();
            time=new Timestamp(System.currentTimeMillis()).toString();
        } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }

     public static void main(String[] args) {
    	 
    	 
         Timer timer = new Timer();
         timer.scheduleAtFixedRate(new ScheduledRunnableTest(), 0, 1000);
     }
 }
 
