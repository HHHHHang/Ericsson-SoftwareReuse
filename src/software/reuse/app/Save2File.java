package software.reuse.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import software.reuse.app.Server.ClientThread;

public class Save2File {
	private static String data2write = "nothing";
	private static String writePath = "D:\\txt2.txt";

	
	Save2File(){
		
	}

	public static void main(String[] args) throws IOException {
		String readPath = "txt1.txt";
		String writePath = "txt2.txt";
		String data = "zhende ky写出来?\nfksal\n";
		
		readfile(readPath);
		write2file(data, writePath);
//		method1(writePath);
		write2fileontime(writePath, data);
		
		getTime();
		timer2();

	}
	
	//读文件
	public static void readfile(String path1){
		
		File file = new File(path1);
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			line = br.readLine();
			while(line!= null){
				line = br.readLine();
				System.out.println(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//写文件
	public static void write2file(String data, String path2) throws IOException{
		File file = new File(path2);
		file.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(data);
		out.flush(); // 把缓存区内容压入文件
		out.close(); // 最后记得关闭文件
	}
	
	public static void write2fileontime(String writeData, String writefile) {
		FileWriter fw = null;
		try {
		//如果文件存在，则追加内容；如果文件不存在，则创建文件
		File f=new File(writefile);
		fw = new FileWriter(f, true);
		} catch (IOException e) {
		e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println(writeData);
		pw.flush();
		try {
		fw.flush();
		pw.close();
		fw.close();
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
	
	public static void write2fileontime2(String file, String content) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			out.write(content+"\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			out.close();
			} catch (IOException e) {
			e.printStackTrace();
			}
		}
	} 
	
	//定时操作
	public static void timer1() {
        Timer timer = new Timer();
        while(true)
        {
	        timer.schedule(new TimerTask() {
	            public void run() {
	                System.out.println("-------设定要指定任务--------");
	            }
	        }, 3000);// 设定指定的时间time,此处为2000毫秒
        }
    }
	
	public static void timer2()
	{
		String str = null;
		Timer timer = new Timer();
		timer.schedule(new MyTask(), 2000, 2000);//在2秒后执行此任务,每次间隔2秒,如果传递一个Data参数,就可以在某个固定的时间执行这个任务.
		while(true)
		{	
			//这个是用来停止此任务的,否则就一直循环执行此任务了
			try {
				int ch = System.in.read();
				if(ch-'c'==0){
					timer.cancel();//使用这个方法退出任务
				}
			} catch (IOException e) {
			// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}

	static class MyTask extends java.util.TimerTask{
		ClientThread ct;
//		String writeRecord = ct.getWriteRecord();
		
//		Save2File sf = new Save2File();
//		String writePath = "/Users/admin/Desktop/txt2.txt";
//		writeRecord = " Forwarded Message Number:" + Integer.toString(count);
		
		@Override
		public void run() {
			
//			setTime(getTime() + writeRecord);
			System.out.println(getTime());
			write2fileontime(data2write, writePath); 

		 }
	}
		
	public static String getTime(){
		Date date = new Date();
		DateFormat df2 = DateFormat.getDateTimeInstance();//可以精确到时分秒
		String a = df2.format(date);
//		System.out.println(a);
		return a;

	}
	
	public static void setTime(String time){
		data2write = time;
	}

		
	

}
