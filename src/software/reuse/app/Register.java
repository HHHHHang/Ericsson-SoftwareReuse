package software.reuse.app;

import org.apache.log4j.Logger;

import java.awt.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;


/**
 * Created by dell on 2016/3/24.
 */
public class Register extends JFrame {
    private ImageIcon back;
    private JFrame frame;
    private JLabel userLabel;
    private JLabel pwdLabel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JTextField userText;
   // private JTextField pwdText;
    private JPasswordField pwdText;
    private JTextField ipText;
    private JTextField portText;
    private JButton loginBT;
    private JButton registerBT;
    private JPanel panel;

    private User user;
    private Client client;
    private static Logger logger = Logger.getLogger(Register.class);


    public Register(String name,Client client){
        super(name);
        this.client=client;
        initComponents();  //initialize UI
    }

    public Register(String name){
        super(name);
        initComponents();
    }
    private void initComponents(){

        frame=new JFrame();
        panel=new JPanel();
        back=new ImageIcon("image/bg.jpg");



        panel = new JPanel() {
            public void paintComponent(Graphics g) {
                ImageIcon icon =
                        new ImageIcon("image/bg.jpg");
                // 鍥剧墖闅忕獥浣撳ぇ灏忚�屽彉鍖�
                g.drawImage(icon.getImage(), 0, 0, frame.getSize().width,frame.getSize().height,frame);
            }
        };

        panel.setOpaque(false);
        panel.setLayout(null);

        userLabel=new JLabel("Username:");
        userLabel.setSize(100,30);
        userLabel.setLocation(180,90);
        userLabel.setFont(new java.awt.Font("Dialog", 1, 18));
        userLabel.setForeground(Color.orange);
        panel.add(userLabel);


        userText=new JTextField();
        userText.setSize(120,30);
        userText.setLocation(290,90);
        userText.setFont(new java.awt.Font("Dialog", 1, 15));
        userText.setForeground(Color.black);
        panel.add(userText);


        pwdLabel=new JLabel("Password:");
        pwdLabel.setSize(100,30);
        pwdLabel.setLocation(180,130);
        pwdLabel.setFont(new java.awt.Font("Dialog", 1, 18));
        pwdLabel.setForeground(Color.orange);
        panel.add(pwdLabel);

        //pwdText=new JTextField();
        pwdText=new JPasswordField();
        pwdText.setSize(120,30);
        pwdText.setLocation(290,130);
        pwdText.setFont(new java.awt.Font("Dialog", 1, 15));
        pwdText.setForeground(Color.black);
        panel.add(pwdText);

        ipLabel=new JLabel("IP:      ");
        ipLabel.setSize(100,30);
        ipLabel.setLocation(180,210);
        ipLabel.setFont(new java.awt.Font("Dialog", 1, 18));
        ipLabel.setForeground(Color.orange);
        panel.add(ipLabel);

        ipText=new JTextField();
        ipText.setSize(120,30);
        ipText.setLocation(290,210);
        ipText.setText("127.0.0.1");
        ipText.setFont(new java.awt.Font("Dialog", 1, 15));
        ipText.setForeground(Color.black);
        panel.add(ipText);

        portLabel=new JLabel("Port:    ");
        portLabel.setSize(100,30);
        portLabel.setLocation(180,170);
        portLabel.setFont(new java.awt.Font("Dialog", 1, 18));
        portLabel.setForeground(Color.orange);
        panel.add(portLabel);

        portText=new JTextField();
        portText.setSize(120,30);
        portText.setLocation(290,170);
        portText.setText("8888");
        portText.setFont(new java.awt.Font("Dialog", 1, 15));
        portText.setForeground(Color.black);
        panel.add(portText);

        loginBT=new JButton("Login");
        loginBT.setFont(new java.awt.Font("Dialog", 1, 18));
        loginBT.setForeground(Color.orange);
        loginBT.setSize(100, 30);
        loginBT.setLocation(180, 260);
        loginBT.setContentAreaFilled(false);
        loginBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login(e);
            }
        });
        panel.add(loginBT);


        registerBT=new JButton("Register");
        registerBT.setFont(new java.awt.Font("Dialog", 1, 18));
        registerBT.setForeground(Color.orange);
        registerBT.setSize(110, 30);
        registerBT.setLocation(300, 260);
        registerBT.setContentAreaFilled(false);
        registerBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Register.this.register(e);
            }
        });
        panel.add(registerBT);

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(back.getIconWidth(),back.getIconHeight());
        frame.setLocation(400,100);
        frame.setResizable(false);
        frame.setVisible(true);


    }


    public User getUser(){
        return user;
    }


    private void login(ActionEvent e) {
        String username = this.userText.getText();
        String password = this.pwdText.getText();
        String hostAddress = this.ipText.getText();
        String port = this.portText.getText();

        if (password != null || !"".equals(password)) {
            user = new User(username, password, hostAddress);
            Client2Server client2Server = new Client2Server(hostAddress, Integer.parseInt(port), username,
                    password);
            if (client2Server.clientLogin()) {
                this.setVisible(false);
                new Client(username).startSendMessage(client2Server.getSocket(), username);
                this.frame.hide();
            } else {
                JOptionPane.showMessageDialog(this, "login fail", "Warning",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "some info is null", "Warning",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void register(ActionEvent event) {
        String username = this.userText.getText();
        String password = this.pwdText.getText();
        String hostAddress = this.ipText.getText();
        String port = this.portText.getText();
        if (password != null || !"".equals(password)) {
            user = new User(username, password, hostAddress);
            Client2Server client2Server = new Client2Server(this, hostAddress, Integer.parseInt(port),
                    username, password);
            if (client2Server.clientRegister()) {

                JOptionPane.showMessageDialog(this, "register successfully,Please login !", "Warning",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        } else {
            JOptionPane.showMessageDialog(this, "some info is null", "Warning",
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public static void main(String [] args){
        new Register("Register");
    }
}



