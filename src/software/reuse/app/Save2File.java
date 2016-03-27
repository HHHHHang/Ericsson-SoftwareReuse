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

    Save2File() {

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

    //璇绘枃浠�
    public static void readfile(String path1) {

        File file = new File(path1);
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            line = br.readLine();
            while (line != null) {
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

    //鍐欐枃浠�
    public static void write2file(String data, String path2) throws IOException {
        File file = new File(path2);
        file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(data);
        out.flush(); // 鎶婄紦瀛樺尯鍐呭鍘嬪叆鏂囦欢
        out.close(); // 鏈�鍚庤寰楀叧闂枃浠�
    }

    public static void write2fileontime(String writeData, String writefile) {
        FileWriter fw = null;
        try {
            //濡傛灉鏂囦欢瀛樺湪锛屽垯杩藉姞鍐呭锛涘鏋滄枃浠朵笉瀛樺湪锛屽垯鍒涘缓鏂囦欢
            File f = new File(writefile);
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
            out.write(content + "\r\n");
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

    //瀹氭椂鎿嶄綔
    public static void timer1() {
        Timer timer = new Timer();
        while (true) {
            timer.schedule(new TimerTask() {
                public void run() {
                    System.out.println("-------璁惧畾瑕佹寚瀹氫换鍔�--------");
                }
            }, 3000);// 璁惧畾鎸囧畾鐨勬椂闂磘ime,姝ゅ涓�2000姣
        }
    }

    public static void timer2() {
        String str = null;
        Timer timer = new Timer();
        timer.schedule(new MyTask(), 2000, 2000);//鍦�2绉掑悗鎵ц姝や换鍔�,姣忔闂撮殧2绉�,濡傛灉浼犻�掍竴涓狣ata鍙傛暟,灏卞彲浠ュ湪鏌愪釜鍥哄畾鐨勬椂闂存墽琛岃繖涓换鍔�.
        while (true) {
            //杩欎釜鏄敤鏉ュ仠姝㈡浠诲姟鐨�,鍚﹀垯灏变竴鐩村惊鐜墽琛屾浠诲姟浜�
            try {
                int ch = System.in.read();
                if (ch - 'c' == 0) {
                    timer.cancel();//浣跨敤杩欎釜鏂规硶閫�鍑轰换鍔�
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    static class MyTask extends java.util.TimerTask {
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

    public static String getTime() {
        Date date = new Date();
        DateFormat df2 = DateFormat.getDateTimeInstance();//鍙互绮剧‘鍒版椂鍒嗙
        String a = df2.format(date);
//		System.out.println(a);
        return a;

    }

    public static void setTime(String time) {
        data2write = time;
    }


}
