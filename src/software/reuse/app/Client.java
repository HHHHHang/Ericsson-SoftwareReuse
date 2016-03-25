package software.reuse.app;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by dell on 2016/3/22.
 */
public class Client {
    //byhq
    static String writePath = "/Users/admin/Desktop/";
    private static int count_client = 0;


    private JFrame frame;
    private JTextArea jta_history;
    // private JTextField jtf_maxnum;
    private JTextField jtf_hostIp;
    private JTextField jtf_port;
    private JTextField jtf_name;
    private JTextField jtf_password;
    private JTextField jtf_message;

    private JButton jb_start;
    private JButton jb_stop;
    private JButton jb_send;

    private JPanel northPanel;
    private JPanel southPanel;
    private JScrollPane rightPanel;
    private JScrollPane leftPanel;

    private JSplitPane centerSplit;

    private JList userList;
    private DefaultListModel listModel;

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private MessageThread mThread;
    private Map<String, User> onlineUser = new HashMap<String, User>();
    private static int clientSucceedLogin = 0;
    private static int clientFailLogin = 0;

    private boolean isConnected = false;

    public static void main(String[] args) {
        new Client("hong");

    }

    public Client(String username) {
        frame = new JFrame("Client");
        jta_history = new JTextArea();
        jta_history.setEditable(false);
        //   jtf_maxnum=new JTextField("30");
        jtf_port = new JTextField("8888");
        jtf_hostIp = new JTextField("127.0.0.1");
        jtf_name = new JTextField(username);
        jtf_password = new JTextField();
        jtf_message = new JTextField();
        jtf_message.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        jb_start = new JButton("Connect");
        jb_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isConnected) {
                    JOptionPane.showMessageDialog(frame, "This client has connected to server already!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int port;
                try {
                    try {
                        port = Integer.parseInt(jtf_port.getText().trim());
                    } catch (Exception e1) {
                        throw new Exception("Port must be integer!");
                    }
                    if (port <= 0) {
                        throw new Exception("Port must be integer!");
                    }
                    String hostIp = jtf_hostIp.getText();
                    String username = jtf_name.getText();
                    String pass = jtf_password.getText();
                    if (username.equals("") || hostIp.equals("")) {
                        throw new Exception("Username and ip should not by empty!\n");
                    }
                    boolean flag = connectServer(port, hostIp, username, pass);
                    if (flag == false) {
                        throw new Exception("Connect to server failed!\n");
                    }
                    frame.setTitle(username);
                    jta_history.append("Server has started\n");
                    JOptionPane.showMessageDialog(frame, "Start server successfully!");
                    jb_start.setEnabled(false);
                    jtf_port.setEnabled(false);
                    jb_stop.setEnabled(true);
                } catch (Exception e2) {
                    JOptionPane.showMessageDialog(frame, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jb_stop = new JButton("stop");
        jb_stop.setEnabled(false);
        jb_stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isConnected) {
                    JOptionPane.showMessageDialog(frame,
                            "Client disconnect to server already!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    boolean flag = closeConnection();
                    if (isConnected) {
                        throw new Exception("Error happened when disconnect to server!\n");
                    }
                    jb_stop.setEnabled(false);
                    jb_start.setEnabled(true);
                    jtf_port.setEnabled(true);
                    jta_history.append("Disconnect to server successfully!\n");
                    JOptionPane.showMessageDialog(frame, "Stop server successfully!\n");
                } catch (Exception e3) {
                    JOptionPane.showMessageDialog(frame, "Error happens when disconnect to server!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        jb_send = new JButton("send");
        jb_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        listModel = new DefaultListModel();
        userList = new JList(listModel);

        southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new TitledBorder("Write message"));
        southPanel.add(jtf_message, "Center");
        southPanel.add(jb_send, "East");

        leftPanel = new JScrollPane(userList);
        leftPanel.setBorder(new TitledBorder("online user"));

        rightPanel = new JScrollPane(jta_history);
        rightPanel.setBorder(new TitledBorder("history message"));

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        centerSplit.setDividerLocation(100);
        northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(1, 8));
        // northPanel.add(new JLabel("Maximum number"));
        //northPanel.add(jtf_maxnum);
        northPanel.add(new JLabel("Port"));
        northPanel.add(jtf_port);
        northPanel.add(new JLabel("Ip"));
        northPanel.add(jtf_hostIp);
        northPanel.add(new JLabel("Name"));
        northPanel.add(jtf_name);
        northPanel.add(new JLabel("PWD"));
        northPanel.add(jtf_password);
        northPanel.add(jb_start);
        northPanel.add(jb_stop);
        northPanel.setBorder(new TitledBorder("Settings"));

        frame.setLayout(new BorderLayout());
        frame.add(northPanel, "North");
        frame.add(southPanel, "South");
        frame.add(centerSplit, "Center");

        frame.setSize(700, 400);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screen_width - frame.getWidth()) / 2, (screen_height - frame.getHeight()) / 2);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isConnected) {
                    closeConnection();
                }
                System.exit(0);
            }
        });
    }

    public void startSendMessage(Socket socket, String username) {
        try {

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            mThread = new MessageThread(br, jta_history);
            mThread.start();
            isConnected = true;
            frame.setTitle(username);
            jta_history.append("Server has started\n");
            JOptionPane.showMessageDialog(frame, "login successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connectServer(int port, String hostIp, String name, String pass) {
        try {
            socket = new Socket(hostIp, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            sendMessage("login" + "@" + name + "@" + pass + "@" + socket.getLocalAddress().toString());
            String str = br.readLine();

            System.out.println("connectServer : " + str);
            if (str.equals("succeed")) {
                sendMessage(name + "@" + pass + "@" + socket.getLocalAddress().toString());
                mThread = new MessageThread(br, jta_history);
                mThread.start();
                isConnected = true;
                ++clientSucceedLogin;

                System.out.println("clientSucceedLogin" + clientSucceedLogin);
                Logger.getLogger(Client.class).info("In Client,java");
                return true;
            } else {
                ++clientFailLogin;

                System.out.println("clientFailLogin" + clientFailLogin);
                return false;
            }

        } catch (Exception e) {
            jta_history.append("connect to server failed\n");
            isConnected = false;
            return false;
        }
    }

    public void sendMessage(String message) {
        pw.println(message);
        pw.flush();
    }

    public void send() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(frame, "Can't send message for server doesn't be start!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String message = jtf_message.getText().trim();
        if (message == null || message.equals("")) {
            JOptionPane.showMessageDialog(frame, "Message shouldn't be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        sendMessage(frame.getTitle() + "@" + "ALL" + "@" + message);
        jtf_message.setText(null);
    }


    public synchronized boolean closeConnection() {
        try {
            sendMessage("CLOSE");
            mThread.stop();
            if (br != null)
                br.close();
            if (pw != null)
                pw.close();
            if (socket != null)
                socket.close();
            isConnected = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = true;
            return false;
        }
    }


    public class MessageThread extends Thread {
        private BufferedReader reader;
        private JTextArea jta_message;
        public MessageThread(BufferedReader reader, JTextArea jta_message) {
            this.reader = reader;
            this.jta_message = jta_message;
        }
        public synchronized void closeConn() throws Exception {
            listModel.removeAllElements();
            if (reader != null) {
                reader.close();
            }
            if (pw != null) {
                pw.close();
            }
            if (socket != null) {
                socket.close();
            }
            isConnected = false;
        }

        public void run() {
            String message = null;
            while (true) {
                try {
                    message = reader.readLine();
                    System.out.println("MessageThread message :" + message);
                    StringTokenizer st = new StringTokenizer(message, "@");
                    String command = st.nextToken();
                    if (command.equals("ADD")) {
                        String username = null;
                        String userIp = null;
                        if ((username = st.nextToken()) != null &&
                                (userIp = st.nextToken()) != null) {
                            User user = new User(username, userIp);
                            onlineUser.put(username, user);
                            listModel.addElement(username);
                            System.out.println("username " + username);
                        }
                    } else if (command.equals("DELETE")) {
                        String username = st.nextToken();
                        User user = (User) onlineUser.get(username);
                        onlineUser.remove(user);
                        listModel.removeElement(username);
                    } else if (command.equals("CLOSE")) {
                        jta_history.append("Server has been closed!\n");
                        closeConn();
                        return;
                    } else if (command.equals("USERLIST")) {
                        int size = Integer.parseInt(st.nextToken());
                        String username = null;
                        String userIp = null;
                        for (int i = 0; i < size; i++) {
                            username = st.nextToken();
                            userIp = st.nextToken();
                            User user = new User(username, userIp);
                            onlineUser.put(username, user);
                            listModel.addElement(username);
                            System.out.print("username ;" + username);
                        }
                    } else {
                        jta_message.append(message + "\n");

                        //byhq
                        //判断是不是系统自动发送的通知
                        //若为自动通知，则忽略不算坐client收到的信息
                        if(message.contains(":")){
                            count_client++;
                            String s_file_name[] = message.split(":");
                            String file_name = s_file_name[0];
                            System.out.println(file_name);
                            timer2(file_name);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //byhq
    public static void timer2(String file_name)
    {
//      System.out.println(file_name);
//      String str = null;
        Timer timer = new Timer();
        timer.schedule(new MyTask(file_name), 2000, 2000);
        while(true)
        {   
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
//      int single_info;
        String file_name;
        MyTask(String file_name22){
//          single_info = si;
            file_name = file_name22;
        }


        @Override
        public void run() {
            
            Save2File sf = new Save2File();
//          setTime(getTime() + writeRecord);
            String file_single_content = getTime() + " " + file_name + " have received " 
                    + count_client + " messages";
            String file_path = writePath + file_name + ".txt";
            
            System.out.println(file_single_content);
            System.out.println(file_path);

            //将日志写入txt文件中去
            sf.write2fileontime(file_single_content, file_path);
            
            //write to single info file
//          for(int i=0;i<client_num;i++){
//              String sinfo = getTime() + ":" + i + " have received " + count_single + " message";
//              
////                int i2 = i++;
////                System.out.println(i2);
//              //将日志写入txt文件中去
//              sf.write2fileontime(sinfo, writePath2 + i + ".txt"); 
//                
//            }

         }
    }
        
    public static String getTime(){
        Date date = new Date();
        DateFormat df2 = DateFormat.getDateTimeInstance();//可以精确到时分秒
        String a = df2.format(date);
//      System.out.println(a);
        return a;

    }
    
//  public static void setTime(String time){
//      data2write = time;
//  }


    
}
