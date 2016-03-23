package software.reuse.app;

import java.io.*;
import java.net.Socket;

/**
 * Created by wangdechang on 2016/3/22.
 */
public class Client2Server extends Thread{
    private String hostAddress;

    private int port;

    private String username;
    private String password;

    private Register client;

    private Socket socket;

    private PrintWriter pw;
    private BufferedReader br;

    //private ChatClient chatClient;

    public Client2Server(Register client, String hostAddress, int port, String username,String password)
    {
        this.client = client;
        this.hostAddress = hostAddress;
        this.port = port;
        this.username = username;
        this.password = password;

        //连接服务器
        this.connect2Server();
    }
    // 连接服务器，由构造方法调用
    private void connect2Server()
    {
        try
        {
            this.socket = new Socket(this.hostAddress, this.port);
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw=new PrintWriter(socket.getOutputStream());

//            this.is = this.socket.getInputStream();
//            this.os = this.socket.getOutputStream();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean clientRegister(){
        pw.println("register"+"@"+username+"@"+password+"@"+socket.getLocalAddress().toString());
        pw.flush();

        // sendMessage(username+"@"+socket.getLocalAddress().toString());
//        try {
//            String res = br.readLine();
//            System.out.println("res = "+res);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return true;
    }

    public void sendMessage(String message){
        pw.println(message);
        pw.flush();
    }
}
