package software.reuse.app;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.*;
import java.util.Timer;


/**
 * Created by dell on 2016/3/22.
 */
public class Server {
	
	//byhq
    int count_total = 0;
    static int count_single = 0;

    static String writePath = "C:\\txt2.txt";
	static String writePath2 = "C:\\";
    private static String writeRecord = "nothingggg";

    private JFrame frame;
    private JPanel panel;
    private ImageIcon back;
    private JTextArea jta_history;
    private JTextField jtf_port;
    private JLabel portLabel;
    //private JTextField jtf_msgpersec;
    //private JTextField jtf_msgperlogin;
    
    private JTextField jtf_message;

    private JButton jb_start;
    private JButton jb_stop;
    private JButton jb_send;

    private JScrollPane leftPanel;
    private JScrollPane rightPanel;
    private JScrollPane topPanel;
    private JPanel bottomPanel;
    private TitledBorder border;

    private JSplitPane centerSplit;

    private JList userList;
    private DefaultListModel listModel;

    private ServerSocket server;
    private ServerThread thread;
    private ArrayList<ClientThread> clients;
    private static ArrayList<User> userArrayList;

    private boolean isStart = false;
    private static int succeedLogin = 0;
    private static int failLogin = 0;
    private static Logger logger = Logger.getLogger(Server.class);
    private static WriteIntoFile writeIntoFile = WriteIntoFile.getWriteIntoFile();
    private int countmsgsec = 0;
    private int countmsglogin = 0;

    public static void main(String[] args) {
        new Server();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
              writeIntoFile.writeFile("server_result","succeed",succeedLogin,"faile",failLogin);
            }
        };
        java.util.Timer timer = new java.util.Timer();
        long delay = 0;
        long intevalPeriod = 60 * 1000;
        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay,
                intevalPeriod);
    }

    public String getWriteRecord(){
        return writeRecord;
    }

    public Server() {
        frame = new JFrame("Server");

        back=new ImageIcon("image/bg2.jpg");

        panel=new JPanel(){
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

        portLabel=new JLabel("Port:");
        portLabel.setSize(100, 30);
        portLabel.setLocation(0,0);
        portLabel.setFont(new java.awt.Font("Dialog", 1, 18));
        portLabel.setForeground(Color.orange);
        panel.add(portLabel);

        jtf_port=new JTextField("8888");
        jtf_port.setFont(new java.awt.Font("Dialog", 1, 18));
        jtf_port.setForeground(Color.white);
        jtf_port.setEditable(false);
        panel.add(jtf_port);

        jb_start=new JButton("Start");
        jb_start.setFont(new java.awt.Font("Dialog", 1, 18));
        jb_start.setForeground(Color.orange);
        jb_start.setContentAreaFilled(false);
        jb_start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isStart) {
                    JOptionPane.showMessageDialog(frame, "Server has started!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int port;
                try {
                    try {
                        port = Integer.parseInt(jtf_port.getText());
                    } catch (Exception e1) {
                        throw new Exception("Port must be integer!");
                    }
                    if (port <= 0) {
                        throw new Exception("Port must be integer!");
                    }

                    try {
                        countmsgsec=5;
                        //countmsgsec = Integer.parseInt(jtf_msgpersec.getText());
                    } catch (Exception e1) {
                        throw new Exception("Msgpersec must be integer!");
                    }
                    if (countmsgsec <= 0) {
                        throw new Exception("Msgpersec must be integer!");
                    }

                    try {
                        countmsglogin=100;
                        //countmsglogin = Integer.parseInt(jtf_msgperlogin.getText());
                    } catch (Exception e1) {
                        throw new Exception("Msgperlogin must be integer!");
                    }
                    if (countmsglogin <= 0) {
                        throw new Exception("Msgperlogin must be integer!");
                    }

                    startServer(port);
                    jta_history.append("Server has started\n");
                    JOptionPane.showMessageDialog(frame, "Start server successfully!");
                    jb_start.setEnabled(false);
                    jtf_port.setEnabled(false);
                    // jtf_msgpersec.setEnabled(false);
                    // jtf_msgperlogin.setEnabled(false);
                    jb_stop.setEnabled(true);
                } catch (Exception e2) {
                    JOptionPane.showMessageDialog(frame, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(jb_start);

        jb_stop=new JButton("Stop");
        jb_stop.setFont(new java.awt.Font("Dialog", 1, 18));
        jb_stop.setForeground(Color.orange);
        jb_stop.setContentAreaFilled(false);
        jb_stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isStart) {
                    JOptionPane.showMessageDialog(frame,
                            "Server hasn't start yet", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    closeServer();
                    jb_stop.setEnabled(false);
                    jb_start.setEnabled(true);
                    jtf_port.setEnabled(true);
                    //   jtf_msgpersec.setEnabled(true);
                    //    jtf_msgperlogin.setEnabled(true);
                    jta_history.append("Stop server successfully!\n");
                    JOptionPane.showMessageDialog(frame, "Stop server successfully!\n");
                } catch (Exception e3) {
                    JOptionPane.showMessageDialog(frame, "Error happens when stop server!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(jb_stop);


        topPanel=new JScrollPane(panel);

        bottomPanel=new JPanel(new BorderLayout());
        jtf_message=new JTextField("he");
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


        //jtf_msgpersec = new JTextField("5");
        //jtf_msgperlogin = new JTextField("100");



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
                if (isStart) {
                    closeServer();
                }
                System.exit(0);
            }
        });
    }

    public void startServer(int port) throws java.net.BindException {
        try {
            clients = new ArrayList<ClientThread>();
            userArrayList = new ArrayList<User>();
            userArrayList.add(new User("hong", "hongpass", "127.0.0.1"));
            userArrayList.add(new User("zhao", "zhaopass", "127.0.0.1"));
            userArrayList.add(new User("wang", "wangpass", "127.0.0.1"));
            userArrayList.add(new User("ni", "nipass", "127.0.0.1"));
            server = new ServerSocket(port);
            thread = new ServerThread(server);
            thread.start();
            isStart = true;

        } catch (BindException e) {
            isStart = false;
            throw new BindException("Post is already used,please change another one!\n");
        } catch (Exception e1) {
            e1.printStackTrace();
            isStart = false;
            throw new BindException("Server start error!\n");
        }
    }

    public void closeServer() {
        try {
            if (thread != null)
                thread.stop();
            for (int i = 0; i < clients.size(); i++) {
                clients.get(i).getWriter().println("CLOSE");
                clients.get(i).getWriter().flush();
                clients.get(i).stop();
                clients.get(i).socket.close();
                clients.get(i).reader.close();
                clients.get(i).writer.close();
                clients.remove(i);
            }
            if (server != null) {
                server.close();
            }
            listModel.removeAllElements();
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
            isStart = true;
        }
    }

    public void sendServerMessage(String message) {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).getWriter().println("Server:" + message);
            clients.get(i).getWriter().flush();
            
            count_total++;
        }
    }

    public void send() {
        if (!isStart) {
            JOptionPane.showMessageDialog(frame, "Can't send message for server doesn't be start!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (clients.size() == 0) {
            JOptionPane.showMessageDialog(frame, "Can't send message for no user online!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = jtf_message.getText().trim();
        if (message == null || message.equals("")) {
            JOptionPane.showMessageDialog(frame, "Message shouldn't be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        sendServerMessage(message);
        jta_history.append("Server:" + message + "\n");
        jtf_message.setText(null);
    }

    class ClientThread extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private User user;
        int eachreceivedmsg = 0;    //��ÿ���յ�����Ϣ
        int eachignoredmsg = 0;   //��ÿ�����Ե���Ϣ
        int msgperlogin = 0;
        Timer timer = new Timer();   //��ʱ��
        
        class MyTask1 extends TimerTask{     //��ʱ����
        	FileWriter fw = null;
        	String fileadd = "c:\\server"+user.getUsername()+".txt";
        	String tempeachreceivedmsg;
        	String tempeachignoredmsg;
        	public void run(){
        		try{
        				tempeachreceivedmsg = Integer.toString(eachreceivedmsg);
        				tempeachignoredmsg = Integer.toString(eachignoredmsg);
        		    	fw = new FileWriter(fileadd,false);
        		    	fw.write("�յ���"+tempeachreceivedmsg+"\r\n"+"���ԣ�"+tempeachignoredmsg);

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

        public ClientThread(Socket socket) {
            try {
                this.socket = socket;
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());

                String userinfo = reader.readLine();
                System.out.println("ClientThread :" + userinfo);
                StringTokenizer st = new StringTokenizer(userinfo, "@");

                user = new User(st.nextToken(), st.nextToken(), st.nextToken());


                if (clients.size() > 0) {
                    String str = "";
                    for (int i = 0; i < clients.size(); i++) {
                        str = str + clients.get(i).getUser().getUsername() + "@"
                                + clients.get(i).getUser().getIp() + "@";
                    }
                    writer.println("USERLIST@" + clients.size() + "@" + str);
                    writer.flush();
                }
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).getWriter().println("ADD@" + user.getUsername() + "@" + user.getIp());
                    clients.get(i).getWriter().flush();
                }
                timer.scheduleAtFixedRate(new MyTask1(),0,2000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void run() {
            String message = null;
            long nowtime = 0;
            ArrayList<Long> timelist = new ArrayList();
            while (true) {
                try {
                    message = reader.readLine();
                    eachreceivedmsg++;
                    nowtime = System.currentTimeMillis();
                    if(timelist.size()<countmsgsec+1){
                   	 timelist.add(nowtime);
                   }
                   else{
                   	timelist.remove(0);
                   	timelist.add(nowtime);
                   }
                    if (message.equals("CLOSE")) {
                    	eachreceivedmsg--;
                        jta_history.append(this.getUser().getUsername() + this.getUser().getIp() + "take off!");
                        reader.close();
                        writer.close();
                        socket.close();
                        timer.cancel();
                        msgperlogin = 0;

                        for (int i = 0; i < clients.size(); i++) {
                            clients.get(i).getWriter().println("DELETE@" + user.getUsername());
                            clients.get(i).getWriter().flush();
                        }

                        listModel.removeElement(user.getUsername());

                        for (int i = 0; i < clients.size(); i++) {
                            if (clients.get(i).getUser() == user) {
                                ClientThread cthread = clients.get(i);
                                clients.remove(i);
                                cthread.stop();
                                return;
                            }
                        }
                    }
                    else if(message.equals("relogin")){
                    	eachreceivedmsg--;
                    	msgperlogin = 0;
                    	jta_history.append(user.getUsername()+"���µ�¼\r\n");
                    }
                    else {
                    	if(msgperlogin<countmsglogin-1){
                    		if(timelist.size()<countmsgsec+1){
                        		msgperlogin++;
                        		dispatcherMessage(message);// ת����Ϣ  
                        	}
                        	else{
                        		if((timelist.get(timelist.size()-1)-timelist.get(0))>10000){    //����
                        			msgperlogin++;
                        			dispatcherMessage(message);
                        		}
                        		else{
                        			 eachignoredmsg++;
                        			 for (int q = clients.size() - 1; q >= 0; q--) {  
                        	                if(clients.get(q).getUser().getUsername()==user.getUsername()) {
                        	                	clients.get(q).getWriter().println("�㷢��̫������Ϣ����ɣ�");
                        	                	clients.get(q).getWriter().flush();
                        	                }
                        	            }  
                        		}
                        	}
                    	}     //���ÿ�ε�½С��100
                    	else{    //����100
                    		for (int q = clients.size() - 1; q >= 0; q--) {  
            	                if(clients.get(q).getUser().getUsername()==user.getUsername()) {
            	                	clients.get(q).getWriter().println("Redo login");
            	                	clients.get(q).getWriter().flush();
            	                }
            	            } 
                    	}
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void dispatcherMessage(String message) {
            StringTokenizer st = new StringTokenizer(message, "@");
            String source = st.nextToken();
            String owner = st.nextToken();
            String content = st.nextToken();
            //message = source + "said:" + content;
            message = source + content;
            jta_history.append(message + "\n");
            for (int i = clients.size() - 1; i >= 0; i--) {  
                if(clients.get(i).getUser().getUsername()==user.getUsername()) {
              //  	clients.get(i).getWriter().println("���յ���"+user.getName()+"����Ϣ");
                	clients.get(i).getWriter().println("OK");
                	clients.get(i).getWriter().flush();
                }
            } 
            if (owner.equals("ALL")) {     // send to all online users
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).getWriter().println(message);
                    clients.get(i).getWriter().flush();
                    count_total++;
                    System.out.println(count_total);
                    System.out.println("server message = "+message);
                }
                count_single += count_total/clients.size();
                

        		writeRecord = " Forwarded Message Number:" + Integer.toString(count_total);
            	//sf.write2file(writeRecord, writePath);
				//sf.write2fileontime(writeRecord, writePath); 
				
				timer2(clients.size(), count_single);

            }
        }

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public BufferedReader getReader() {
            return reader;
        }

        public void setReader(BufferedReader reader) {
            this.reader = reader;
        }

        public PrintWriter getWriter() {
            return writer;
        }

        public void setWriter(PrintWriter writer) {
            this.writer = writer;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }


    }

    class ServerThread extends Thread {
        private ServerSocket server;
        public ServerThread(ServerSocket server) {
            this.server = server;
        }

        public void run() {
            while (true) {
                try {
                    Socket socket = server.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream());
                    String str = reader.readLine();
                    System.out.println("severThread :" + str);
                    StringTokenizer stringTokenizer = new StringTokenizer(str, "@");
                    String userName = stringTokenizer.nextToken();
                    if (userName.equals("login")) {
                        boolean isLogin = false;
                        String name = stringTokenizer.nextToken();
                        for (User user : userArrayList) {
                            if (user.getUsername().equals(name)) {
                                String password = stringTokenizer.nextToken();
                                if (user.getPassword().equals(password)) {
                                    isLogin = true;
                                    break;
                                }
                            }
                        }
                        if (isLogin) {
                            writer.println("succeed");
                            writer.flush();
                            succeedLogin++;
                            logger.info("server : user = " + name + " login Successfully!" + ", succeedLogin = " + succeedLogin);

                            System.out.println("succeedLogin " + succeedLogin);

                            ClientThread client = new ClientThread(socket);
                            client.start();
                            clients.add(client);
                            listModel.addElement(client.getUser().getUsername());
                            jta_history.append(client.getUser().getUsername() +
                                    client.getUser().getIp() + "online\n");
                        } else {
                            writer.println("fail");
                            writer.flush();
                            failLogin++;
                            logger.info("server : user = " + name + " login failed!" + ", failLogin = " + failLogin);
                        }

                    } else if (userName.equals("register")) {
                        System.out.println("register ;" + str);
                        userArrayList.add(new User(stringTokenizer.nextToken(), stringTokenizer.nextToken(), stringTokenizer.nextToken()));
                        for (User user : userArrayList) {
                            System.out.println(user);
                        }
                    } else {
                        writer.println("fail");
                        writer.flush();
                    }

//                    ClientThread client=new ClientThread(socket);
//                    client.start();
//                    clients.add(client);
//                    listModel.addElement(client.getUser().getUsername());
//                    jta_history.append(client.getUser().getUsername()+
//                            client.getUser().getIp()+"online\n");

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }

            }
        }
    }
    
    
    
  //byhq
    public static void timer2(int client_num , int single_info)
	{
		String str = null;
		Timer timer = new Timer();
		timer.schedule(new MyTask(client_num, single_info), 10000, 10000);
	/*	while(true)
		{	
			try {
				int ch = System.in.read();
				if(ch-'c'==0){
					timer.cancel();
				}
			} catch (IOException e) {
			// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
	}

	static class MyTask extends java.util.TimerTask{
//		int single_info;
		int client_num;
		MyTask(int cn, int si){
//			single_info = si;
			client_num = cn;
		}


		@Override
		public void run() {
			
            Save2File sf = new Save2File();
//			setTime(getTime() + writeRecord);
			System.out.println(getTime() + writeRecord);
			sf.write2fileontime(getTime() + writeRecord, writePath);
			
			//write to single info file
			for(int i=0;i<client_num;i++){
				String sinfo = getTime() + ":" + i + " have received " + count_single + " message";
				
//				int i2 = i++;
//				System.out.println(i2);
				sf.write2fileontime(sinfo, writePath2 + i + ".txt"); 
                
            }

		 }
	}
		
	public static String getTime(){
		Date date = new Date();
		DateFormat df2 = DateFormat.getDateTimeInstance();//可以精确到时分秒
		String a = df2.format(date);
//		System.out.println(a);
		return a;

	}
	
//	public static void setTime(String time){
//		data2write = time;
//	}

}