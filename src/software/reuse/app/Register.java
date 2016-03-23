package software.reuse.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Created by wangdechang on 2016/3/22.
 */
public class Register extends JFrame {
    private Client client;
    private JButton jButton1;


    private JButton jButton2;

    private JLabel jLabel1;

    private JLabel jLabel2;

    private JLabel jLabel3;
    private JLabel jLabel4;

    private JPanel jPanel;

    private JTextField username;
    private JTextField password;
    private JTextField hostAddress;

    private JTextField port;
    private User user;

    public Register(String name,Client client){
        super(name);
        this.client = client;
        initComponents(); // initialize UI
    }
    public Register(String name) {
        super(name);

        initComponents(); // initialize UI
    }

    private void initComponents() {
        jPanel = new JPanel();

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();

        username = new JTextField(15);
        password = new JTextField(15);
        hostAddress = new JTextField(15);
        port = new JTextField(15);

        jButton1 = new JButton();
        jButton2 = new JButton();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setResizable(false);

        jPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("用户注册"));

        jLabel1.setText("用户名");
        jLabel2.setText("密 码");
        jLabel3.setText("IP地址");
        jLabel4.setText("PORT");

        jButton1.setText("注册");
        jButton2.setText("重置");

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Register.this.register(e);
            }
        });

        username.setText("wang");
        hostAddress.setText("127.0.0.1");
        port.setText("8888");

        jPanel.add(jLabel1);
        jPanel.add(username);
        jPanel.add(jLabel2);
        jPanel.add(password);
        jPanel.add(jLabel3);
        jPanel.add(hostAddress);

        jPanel.add(jLabel4);
        jPanel.add(port);

        jPanel.add(jButton1);
        jPanel.add(jButton2);

        this.getContentPane().add(jPanel);

        this.setSize(250, 300);
        this.setVisible(true);
    }

    public User getUser(){
        return user;
    }
    private void register(ActionEvent event) {
        String username = this.username.getText();
        String password = this.password.getText();
        String hostAddress = this.hostAddress.getText();
        String port = this.port.getText();
        if(password != null || !"".equals(password)){
            user = new User(username,password,hostAddress);
            Client2Server client2Server = new Client2Server(this,hostAddress,Integer.parseInt(port),username,password);
            if(client2Server.clientRegister()){
                this.setVisible(false);
                new Client("Client");
            }
        }else{
            JOptionPane.showMessageDialog(this, "some info is null", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }

        /*ClientConnection clientConnection = new ClientConnection(this,
                hostAddress, Integer.parseInt(port), username);

        if (clientConnection.login()) {
            clientConnection.start();
        } else {
            JOptionPane.showMessageDialog(this, "????????????????", "????",
                    JOptionPane.ERROR_MESSAGE);
        }*/
    }

   /* public boolean connectServer(int port,String hostIp,String name){
        try{
            Socket socket=new Socket(hostIp,port);
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw=new PrintWriter(socket.getOutputStream());
            sendMessage(name+"@"+socket.getLocalAddress().toString());
            MessageThread mThread=new MessageThread(br,jta_history);
            mThread.start();
            isConnected=true;
            return true;
        }catch (Exception e){
            jta_history.append("connect to server failed\n");
            isConnected=false;
            return false;
        }
    }*/

    public static void main(String[] args) {
        new Register("Register");
    }

}

