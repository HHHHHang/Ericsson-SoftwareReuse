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
//    int single_info = 0;
	static String writePath = "/Users/admin/Desktop/txt2.txt";
	static String writePath2 = "/Users/admin/Desktop/";
    private static String writeRecord = "nothingggg";
    public String getWriteRecord(){
    	return writeRecord;
    }

    private JFrame frame;
    private JTextArea jta_history;
    // private JTextField jtf_maxnum;
    private JTextField jtf_port;
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

    private ServerSocket server;
    private ServerThread thread;
    private ArrayList<ClientThread> clients;
    private static ArrayList<User> userArrayList;

    private boolean isStart = false;
    private static int succeedLogin = 0;
    private static int failLogin = 0;
    private static Logger logger = Logger.getLogger(Server.class);
    private static WriteIntoFile writeIntoFile = WriteIntoFile.getWriteIntoFile();

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

    public Server() {
//    	Save2File sf = new Save2File();
//		String writePath = "/Users/admin/Desktop/txt2.txt";
//    	sf.write2file(count, writePath);
        frame = new JFrame("Server");
        jta_history = new JTextArea();
        jta_history.setEditable(false);
        jtf_port = new JTextField("8888");
        jtf_message = new JTextField();
        jtf_message.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        jb_start = new JButton("start");
        jb_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isStart) {
                    JOptionPane.showMessageDialog(frame, "Server has started!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // int maxnum;
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

                    startServer(port);
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
                    jta_history.append("Stop server successfully!\n");
                    JOptionPane.showMessageDialog(frame, "Stop server successfully!\n");
                } catch (Exception e3) {
                    JOptionPane.showMessageDialog(frame, "Error happens when stop server!", "Error",
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
        northPanel.setLayout(new GridLayout(1, 6));
        // northPanel.add(new JLabel("Maximum number"));
        //northPanel.add(jtf_maxnum);
        northPanel.add(new JLabel("Port"));
        northPanel.add(jtf_port);
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

        public ClientThread(Socket socket) {
            try {
                this.socket = socket;
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());

                String userinfo = reader.readLine();
                System.out.println("ClientThread :" + userinfo);
                StringTokenizer st = new StringTokenizer(userinfo, "@");

                //user = new User(st.nextToken(), st.nextToken());
                user = new User(st.nextToken(), st.nextToken(), st.nextToken());

//                writer.println(user.getUsername() + user.getIp() + "connect to server successfully");
//                writer.flush();

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


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void run() {
            String message = null;
            while (true) {
                try {
                    message = reader.readLine();
                    if (message.equals("CLOSE")) {
                        jta_history.append(this.getUser().getUsername() + this.getUser().getIp() + "take off!");
                        reader.close();
                        writer.close();
                        socket.close();

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
                    } else {
                        dispatcherMessage(message);
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
            message = source + "said:" + content;
            jta_history.append(message + "\n");
            if (owner.equals("ALL")) {     // send to all online users
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).getWriter().println(message);
                    clients.get(i).getWriter().flush();
                    count_total++;
                    System.out.println(count_total);
                }
                count_single += count_total/clients.size();
                
                //Save2File sf = new Save2File();
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
        //  private int maxnum;

        /* public ServerThread(ServerSocket server,int maxnum){
             this.server=server;
             this.maxnum=maxnum;
         }*/
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

//                            writer.println("connect to server successfully");
//                            writer.flush();
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
		while(true)
		{	
			try {
				int ch = System.in.read();
				if(ch-'c'==0){
					timer.cancel();//使用这个方法�?出任�?
				}
			} catch (IOException e) {
			// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
// 			for(int i=0;i<client_num;i++){
// 				String sinfo = getTime() + ":" + i + " have received " + count_single + " message";
				
// //				int i2 = i++;
// //				System.out.println(i2);
// 				sf.write2fileontime(sinfo, writePath2 + i + ".txt"); 
                
//             }

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