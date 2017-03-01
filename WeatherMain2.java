import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class WeatherMain2{
    private static Logger logger = Logger.getLogger(WeatherMain2.class);

    protected static long startTime;
    protected static long endTime;

    /**
     * 写入到数据库中
     */
    ArrayList<String> allurlSet = new ArrayList<String>();
    int threadCount = 6; // 线程数量
    static int count = 0; //表示有多少个线程处于wait状态
    public static final Object signal = new Object(); // 线程间通信变量

    public static void main(String[] args) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 控制时
        calendar.set(Calendar.MINUTE, 0); // 控制分
        calendar.set(Calendar.SECOND, 0); // 控制秒

        Date time = calendar.getTime(); // 得出执行任务的时间,此处为9：00：00

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
	   public void run() {
	       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
	       System.out.println(df.format(new Date()) + " 开始");
	       logger.info("开始");

	       WeatherMain2.startTime = System.currentTimeMillis();// Date()为获取当前系统时间

	       WeatherMain2 wc = new WeatherMain2();
	       wc.addUrl();
	       count = 0;
	       wc.begin();//固定任务在此

	       while(true){
		  if(wc.allurlSet.isEmpty()&& Thread.activeCount() == 1||wc.count==wc.threadCount){
		      WeatherMain2.endTime = System.currentTimeMillis();
		      System.out.println("程序运行时间：" + (endTime - startTime) / (1000 * 60) + "min");
		      logger.info("程序运行时间：" + (endTime - startTime) / (1000 * 60) + "min");

		      SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		      System.out.println(df2.format(new Date()) + " 结束");
		      logger.info("结束");
		      break;
		  }

	       }

	   }
        }, time,  (1000 * 60 * 60 * 24)-(endTime - startTime));// 这里设定将延时每天固定执行

    }

    private void begin() {
        for(int i=0;i<threadCount;i++){
	   new Thread(new Runnable(){
	       public void run() {
		  while (true) {
		      String tmp = getAUrl();
		      if(tmp!=null){
			 crawler(tmp);
		      }else{
			 synchronized(signal) {
			     try {
				count++;
				System.out.println("当前有"+count+"个线程在等待");
				signal.wait();  //线程等待
			     } catch (InterruptedException e) {
				e.printStackTrace();
			     }
			 }

		      }
		  }
	       }
	   },"thread-"+i).start();
        }
    }

    public void crawler(String sUrl){
        Connection con = null;
        PreparedStatement pre = null;

        try {
	   Class.forName("oracle.jdbc.driver.OracleDriver");

	   String url = "jdbc:oracle:thin:@192.168.0.11:1521:orcl";
	   String user = "weather";
	   String password = "weather";

	   con = DriverManager.getConnection(url, user, password);

	   String sql = "Insert into weatherdata values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	   pre = con.prepareStatement(sql);

	   //String url0 = allurlSet.get(0);
	   //String url1 = url0.substring(1, url0.length());
	   ArrayList<WeatherData> str = OneN2.getOne2(sUrl);
	   System.out.println(sUrl);
	   logger.info(sUrl);

	   for (WeatherData ss : str) {
	       pre.setString(1,ss.getRoad());
	       pre.setString(2, ss.getDate());
	       pre.setString(3, ss.getTime());
	       pre.setString(4, ss.getT());
	       pre.setString(5, ss.getP0());
	       pre.setString(6, ss.getP());
	       pre.setString(7, ss.getPa());
	       pre.setString(8, ss.getU());
	       pre.setString(9, ss.getDD());
	       pre.setString(10, ss.getFf());
	       pre.setString(11, ss.getFf10());
	       pre.setString(12, ss.getFf3());
	       pre.setString(13, ss.getN());
	       pre.setString(14, ss.getWW());
	       pre.setString(15, ss.getW1());
	       pre.setString(16, ss.getW2());
	       pre.setString(17, ss.getTn());
	       pre.setString(18, ss.getTx());
	       pre.setString(19, ss.getCl());
	       pre.setString(20, ss.getNh());
	       pre.setString(21, ss.getH());
	       pre.setString(22, ss.getCm());
	       pre.setString(23, ss.getCh());
	       pre.setString(24, ss.getVV());
	       pre.setString(25, ss.getTd());
	       pre.setString(26, ss.getRRR());
	       pre.setString(27, ss.gettR());
	       pre.setString(28, ss.getE());
	       pre.setString(29, ss.getTg());
	       pre.setString(30, ss.getE1());
	       pre.setString(31, ss.getSss());

	       pre.executeUpdate();
	       //System.out.println(ss);
	   }
	   str.clear();

	   if(count>0){ //如果有等待的线程，则唤醒
	       synchronized(signal) {
		  count--;
		  signal.notify();  //线程唤醒
	       }
	   }

        } catch (Exception e) {
	   e.printStackTrace();
	   logger.error(sUrl + " " + e);
        }
    }

    public synchronized void  addUrl(){
        FileReader file = null;
        try {
	   file = new FileReader("D:/weather/lishiurl(405)_https.csv");

	   BufferedReader br = new BufferedReader(file);

	   String line = null;

	   while ((line = br.readLine()) != null) {
	       allurlSet.add(line);
	   }
	   br.close();
	   file.close();
        } catch (FileNotFoundException e1) {
	   e1.printStackTrace();
	   logger.error(e1);
        } catch (IOException e1) {
	   e1.printStackTrace();
	   logger.error(e1);
        }
    }

    public synchronized String getAUrl() {
        String tmpAUrl;
        if (allurlSet.isEmpty())return null;
        if (allurlSet.size() == 405){
	   String url0 = allurlSet.get(0);
	   tmpAUrl = url0.substring(1, url0.length());
        }else{
	   tmpAUrl = allurlSet.get(0);
        }
        allurlSet.remove(0);

        return tmpAUrl;
    }


}
