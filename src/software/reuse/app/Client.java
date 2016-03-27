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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dell on 2016/3/22.
 */
public class Client {
    private JFrame frame;
    private JPanel panel;
    private JTextArea jta_history;
    private JTextArea nameText;
    private JTextField jtf_message;

    private JButton jb_send;

    private JScrollPane topPanel;
    private JPanel bottomPanel;
    private JScrollPane rightPanel;
    private JScrollPane leftPanel;
    private TitledBorder border;
    private JSplitPane centerSplit;
    private ImageIcon back;
    private JList userList;
    private DefaultListModel listModel;

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private MessageThread mThread;
    private Map<String, User> onlineUser = new HashMap<String, User>();
    private static int clientSucceedLogin = 0;
    private static int clientFailLogin = 0;
    private int sendmsgnum = 0;  

    private boolean isConnected = false;

    public static void main(String[] args) {
        new Client("hong");

    }

    public Client(String username) {
        frame = new JFrame("Client");
        panel=new JPanel();
        back=new ImageIcon("image/bg2.jpg");

                                 
        

        nameText=new JTextArea("hello, "+username){
            private static final long serialVersionUID = -8220994963464909915L;

            {
                setOpaque(false); // 设置透明
            }

            protected void paintComponent(Graphics g) {
                ImageIcon icon=new ImageIcon("image/top.jpg");
                g.drawImage(icon.getImage(), 0, 0, this);
                super.paintComponent(g);
            }
        };

        nameText.setFont(new java.awt.Font("Dialog", 1, 22));
        nameText.setForeground(Color.orange);
        nameText.setEditable(false);

        topPanel=new JScrollPane(nameText);



        bottomPanel=new JPanel(new BorderLayout());
        jtf_message=new JTextField("");
        jtf_message.setFont(new java.awt.Font("Dialog", 1, 15));
        jtf_message.setForeground(Color.orange);
        jtf_message.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });


        bottomPanel.add(jtf_message,"Center");
        jb_send=new JButton("send");
        jb_send.setFont(new java.awt.Font("Dialog", 1, 18));
        jb_send.setForeground(Color.orange);
        jb_send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
        bottomPanel.add(jb_send,"East");


        jta_history=new JTextArea(){
            private static final long serialVersionUID = -8220994963464909915L;

            {
                setOpaque(false); // 设置透明
            }

            protected void paintComponent(Graphics g) {
                ImageIcon icon=new ImageIcon("image/right.jpg");
                g.drawImage(icon.getImage(), 0, 0, this);
                super.paintComponent(g);
            }
        };

        jta_history.setEditable(false);

        jta_history.setFont(new java.awt.Font("Dialog", 1, 18));
        jta_history.setForeground(Color.orange);

        border=new TitledBorder("History Message");
        border.setTitleFont(new java.awt.Font("Dialog", 1, 18));
        border.setTitleColor(Color.pink);

        rightPanel=new JScrollPane(jta_history);
        rightPanel.setBorder(border);


      /*  jtf_message.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });*/

       // jb_start = new JButton("Connect");
       /* jb_start.addActionListener(new ActionListener() {
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
                    Timer timerha = new Timer();                            //��ʱ��
                    timerha.scheduleAtFixedRate(new MyTaskha(),0,2000);
                    jb_start.setEnabled(false);
                    jtf_port.setEnabled(false);
                    jb_stop.setEnabled(true);
                } catch (Exception e2) {
                    JOptionPane.showMessageDialog(frame, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
*/
       /* jb_stop = new JButton("stop");
        jb_stop.setEnabled(false);
        jb_stop.addActionListener(new ActionListener() {
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
        });*/


      //  jb_send = new JButton("send");


        listModel = new DefaultListModel();
        userList = new JList(listModel){
            private static final long serialVersionUID = -8220994963464909915L;

            {
                setOpaque(false); // 设置透明
            }

            protected void paintComponent(Graphics g) {
                ImageIcon icon=new ImageIcon("image/left.jpg");
                g.drawImage(icon.getImage(), 0, 0, this);
                super.paintComponent(g);
            }
        };

        userList.setFont(new java.awt.Font("Dialog", 1, 18));
        userList.setForeground(Color.orange);

        border=new TitledBorder("Online Users");
        border.setTitleFont(new java.awt.Font("Dialog", 1, 18));
        border.setTitleColor(Color.pink);
        leftPanel=new JScrollPane(userList);
        leftPanel.setBorder(border);

        centerSplit=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftPanel,rightPanel);
        centerSplit.setDividerLocation(200);

        frame.add(topPanel,"North");
        frame.add(bottomPanel,"South");
        frame.add(centerSplit, "Center");


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(back.getIconWidth(), back.getIconHeight());
        frame.setLocation(200, 100);
        frame.setResizable(false);
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
            Timer timerha = new Timer();   
            timerha.scheduleAtFixedRate(new MyTaskha(username),0,2000);
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
        sendmsgnum++;     
        jtf_message.setText(null);
    }
    
    class MyTaskha extends TimerTask{     
    	FileWriter fw = null;
        String username;

    	String tempsmn;
        public MyTaskha(String username){
            this.username=username;
        }
    	public void run(){
    		try{
                    String fileadd = "C:\\client_"+this.username+"_sends.txt";
    				tempsmn = Integer.toString(sendmsgnum);
    		    	fw = new FileWriter(fileadd,true);
                    fw.write(this.username+"sends:"+tempsmn+"\r\n");
    				if(isConnected ==  false){
    					cancel();
    				}
    		    	}catch(Exception e){
    		    		e.printStackTrace();
    		    	}finally{
    		    		if (fw != null)  
    		                try {  
    		                    fw.close();  
    		                } catch (IOException e) {  
    		                    e.printStackTrace();;  
    		                }  
    		    	}	
    	}
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
                    }else if(command.equals("Redo login")){
                    	jta_history.append("Relogin...please wait\r\n");
                    	sendMessage("relogin");
                    	jta_history.append("Relogin success!\r\n");
                    }else {
                        jta_message.append(message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
