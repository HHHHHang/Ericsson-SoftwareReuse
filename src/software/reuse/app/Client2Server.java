package software.reuse.app;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.TimerTask;

/**
 * Created by wangdechang on 2016/3/22.
 */
public class Client2Server extends Thread {
    private String hostAddress;

    private int port;

    private String username;
    private String password;
    private Register client;
    private Socket socket;

    private PrintWriter pw;
    private BufferedReader br;
    private static int clientSucceedLogin = 0;
    private static int clientFailLogin = 0;
    private static Logger logger = Logger.getLogger(Client2Server.class);
    private static WriteIntoFile writeIntoFile = WriteIntoFile.getWriteIntoFile();

    //private ChatClient chatClient;

    public Client2Server(String hostAddress, int port, String username, String password) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.username = username;
        this.password = password;
        writeIntoFile();

    }
    private void writeIntoFile(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                writeIntoFile.writeFile(username,"succeed",clientSucceedLogin,"faile",clientFailLogin);
            }
        };
        java.util.Timer timer = new java.util.Timer();
        long delay = 0;
        long intevalPeriod = 60 * 1000;
        timer.scheduleAtFixedRate(task, delay,
                intevalPeriod);
    }

    public Client2Server(Register client, String hostAddress, int port, String username, String password) {
        this.client = client;
        this.hostAddress = hostAddress;
        this.port = port;
        this.username = username;
        this.password = password;

        //���ӷ�����
        this.connect2Server();
    }


    // ���ӷ�����
    private void connect2Server() {
        try {
            this.socket = new Socket(this.hostAddress, this.port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());

//            this.is = this.socket.getInputStream();
//            this.os = this.socket.getOutputStream();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean clientLogin() {
        try {
            socket = new Socket(hostAddress, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            sendMessage("login" + "@" + username + "@" + password + "@" + socket.getLocalAddress().toString());
            String str = br.readLine();

            System.out.println("connectServer : " + str);
            if (str.equals("succeed")) {
                sendMessage(username + "@" + password + "@" + socket.getLocalAddress().toString());

                ++clientSucceedLogin;
                logger.error("client : user ="+username +" login successfully !"+" clientSucceedLogin = "+clientSucceedLogin);
                System.out.println("clientSucceedLogin" + clientSucceedLogin);
                return true;
            } else {
                ++clientFailLogin;
                System.out.println("clientFailLogin" + clientFailLogin);
                logger.error("client : user ="+username +" login failed !"+" clientFailLogin = "+clientFailLogin);
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clientRegister() {
        try {
            pw.println("register" + "@" + username + "@" + password + "@" + socket.getLocalAddress().toString());
            pw.flush();
            String str = br.readLine();
            if(str.equals("exist")||str.equals("long")){
                return false;
            }
        }catch (IOException e){

        }
        return true;
    }

    public void sendMessage(String message) {
        pw.println(message);
        pw.flush();
    }
    public Socket getSocket(){
        return socket;
    }
}
