/**
 * Created by VULCAN on 2017/4/8.
 */

package pers.SHT.Chat;

import pers.SHT.ChatGui.Login;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.lang.Exception;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class Client {

    private JFrame frame;
    private JList userList;
    private JTextArea textArea;
    private JTextField textField;
    private JTextField port;
    private JTextField hostIp;
    private JButton start;
    private JButton stop;
    private JButton send;
    private JPanel northPanel;
    private JPanel southPanel;
    private JScrollPane rightScroll;
    private JScrollPane leftScroll;
    private JSplitPane centerSplit;
    private JMenuBar mb;
    private JMenu menu;
    private JMenuItem singleChat;
    private JMenuItem file;
    private DefaultListModel listModel;

    private boolean isConnected = false;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    private Login login = null;

    private MessageThread messageThread;// 负责接收消息的线程
    //private Map<String, User> onLineUsers = new HashMap<String, User>();// 所有在线用户

    public static void main(String args[]) throws ClassNotFoundException {
        new Client();
    }

    public Client() throws ClassNotFoundException{

        //连接数据库
        try {
            Conn.connection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        login = new Login();

        //等待登录成功
        while(!login.getLogin())
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //这里必须等待一些时间，不然会出错，不知道为什么
        }

        //客户机界面
        textArea = new JTextArea();
        textArea.setEditable(false);
        textField = new JTextField();
        port = new JTextField("6666");
        hostIp = new JTextField("127.0.0.1");
        start = new JButton("连接");
        stop = new JButton("断开");
        stop.setEnabled(false);
        send = new JButton("发送");
        listModel = new DefaultListModel();
        userList = new JList(listModel);
        mb = new JMenuBar();
        menu = new JMenu("菜单");
        singleChat = new JMenuItem("单聊");
        file = new JMenuItem("文件传输");

        menu.add(singleChat);
        menu.add(file);
        mb.add(menu);

        northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(1,7));
        northPanel.add(new JLabel("端口"));
        northPanel.add(port);
        northPanel.add(new JLabel("服务器IP"));
        northPanel.add(hostIp);
        northPanel.add(start);
        northPanel.add(stop);
        northPanel.setBorder(new TitledBorder("连接信息"));

        rightScroll = new JScrollPane(textArea);
        rightScroll.setBorder(new TitledBorder("消息显示区"));

        leftScroll = new JScrollPane(userList);
        leftScroll.setBorder(new TitledBorder("在线用户"));

        southPanel = new JPanel(new BorderLayout());
        southPanel.add(textField,"Center");
        southPanel.add(send,"East");
        southPanel.setBorder(new TitledBorder("写消息"));

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftScroll,rightScroll);
        centerSplit.setDividerLocation(100);

        frame = new JFrame(login.getUser());
        frame.setJMenuBar(mb);
        frame.setLayout(new BorderLayout());
        frame.add(northPanel,"North");
        frame.add(centerSplit,"Center");
        frame.add(southPanel,"South");
        frame.setSize(600,400);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screen_width-frame.getWidth())/2,(screen_height-frame.getHeight())/2);
        frame.setVisible(true);

        //写消息的文本框中按回车键时事件
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send_Message();
            }
        });

        //单击发送按钮时事件
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send_Message();
            }
        });

        //单击连接按钮时事件
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port_int;
                if(isConnected)
                {
                    JOptionPane.showMessageDialog(frame,"已处于连接上状态，不要重复连接!");
                }
                else
                {
                    try {
                        try {
                            port_int = Integer.parseInt(port.getText().trim());
                        } catch (NumberFormatException e1){
                            throw new Exception("端口号不符合要求!端口为整数!");
                        }
                        String hostip = hostIp.getText().trim();
                        String name = login.getUser();
                        if(hostip.equals(""))
                            throw new Exception("服务器IP不能为空！");
                        boolean flag = connectServer(port_int,hostip,name);
                        if(!flag)
                            throw new Exception("与服务器连接失败!");
                        JOptionPane.showMessageDialog(frame,"成功连接!");
                        stop.setEnabled(true);
                        start.setEnabled(false);
                        port.setEnabled(false);
                        hostIp.setEnabled(false);
                    } catch (Exception e2){
                        JOptionPane.showMessageDialog(frame,e2.getMessage());
                    }
                }
            }
        });

        // 单击断开按钮时事件
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!isConnected)
                    JOptionPane.showMessageDialog(frame,"已处于断开状态");
                else
                {
                    try {
                        boolean flag = closeConnection();  // 断开连接
                        if(!flag)
                            throw new Exception("断开连接发生异常！");
                        JOptionPane.showMessageDialog(frame,"成功断开！");
                        stop.setEnabled(false);
                        start.setEnabled(true);
                        port.setEnabled(true);
                        hostIp.setEnabled(true);
                    } catch (Exception exc){
                        JOptionPane.showMessageDialog(frame,exc.getMessage());
                    }
                }
            }
        });

        // 关闭窗口时事件
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if(isConnected)
                    closeConnection();
                System.exit(0);
            }
        });
    }

    //消息发送
    private void send_Message(){
        if(!isConnected)
            JOptionPane.showMessageDialog(frame,"还没有连接服务器，无法发送消息！");
        else
        {
            String message = textField.getText().trim();
            if(message.equals(""))
                JOptionPane.showMessageDialog(frame,"消息不能为空！");
            else
            {
                writer.println(frame.getTitle() + "@" + message);
                writer.flush();
                textField.setText(null);
            }
        }
    }

    //连接服务器
    private boolean connectServer(int port,String hostip,String name){
        try {
            socket = new Socket(hostip,port);
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // socket.getLocalAddress().toString()得到的是 /0000.0000.0000.0000
            writer.println(name + "@" + socket.getLocalAddress().toString());
            writer.flush();
            isConnected = true;
            messageThread = new MessageThread(reader,textArea);
            messageThread.start();
            Thread.sleep(100);
            return isConnected;
        } catch (Exception e){
            textArea.append("与端口号为：" + port + " IP地址为：" + hostip+ " 的服务器连接失败!" + "\n");
            isConnected = false;
            return false;
        }
    }

    //客户端主动关闭连接
    private boolean closeConnection(){
        try {
            // 发送断开连接命令给服务器
            writer.println("CLOSE");
            writer.flush();
            // 清空用户列表
            listModel.removeAllElements();
            // 停止接受消息线程
            messageThread.stop();
            // 释放资源
            writer.close();
            reader.close();
            socket.close();
            isConnected = false;
            return true;
        } catch (IOException exc){
            exc.printStackTrace();
            return false;
        }
    }

    // 不断接收消息的线程
    class MessageThread extends Thread{
        private BufferedReader in = null;
        private JTextArea textArea = null;
        private String name = null;

        private MessageThread(BufferedReader in,JTextArea textArea){
            this.in = in;
            this.textArea = textArea;
        }

        // 服务器关闭 客户端被动关闭连接
        private boolean closeCon(){
            try {
                // 清空用户列表
                listModel.removeAllElements();
                // 释放资源
                writer.close();
                reader.close();
                socket.close();
                isConnected = false;
                return true;
            } catch (Exception exc){
                exc.printStackTrace();
                return false;
            }
        }

        public void run(){
            String news=null;
            while(true){
                try {
                    news = in.readLine();
                    StringTokenizer str = new StringTokenizer(news,"/@");
                    String cmd = str.nextToken();  //命令
                    if(cmd.equals("CLOSE"))
                    {
                        textArea.append("服务器已关闭!\n");
                        try {
                            boolean flag = closeCon();  // 断开连接
                            if(!flag)
                                throw new Exception("断开连接发生异常！");
                            JOptionPane.showMessageDialog(frame,"成功断开！");
                            stop.setEnabled(false);
                            start.setEnabled(true);
                            port.setEnabled(true);
                            hostIp.setEnabled(true);
                            return;
                        } catch (Exception exc){
                            JOptionPane.showMessageDialog(frame,exc.getMessage());
                        }
                    }
                    else if(cmd.equals("ADD"))
                    {
                        // 有用户上线更新在线列表
                        name = str.nextToken();
                        listModel.addElement(name);
                        textArea.append(name + "上线！\n");
                    }
                    else if(cmd.equals("DELETE"))
                    {
                        // 有用户下线更新在线列表
                        name = str.nextToken();
                        listModel.removeElement(name);
                        textArea.append(name + "下线！\n");
                    }
                    else if(cmd.equals("USERLIST"))
                    {
                        int size = Integer.parseInt(str.nextToken());
                        for(int i=0;i<size;i++)
                        {
                            name = str.nextToken();
                            listModel.addElement(name);
                        }
                    }
                    else if(cmd.equals("MAX"))
                    {
                        // 人数已达上限
                        textArea.append(str.nextToken() + "/" + str.nextToken() + "\n");
                        // 被动的关闭连接
                        try {
                            boolean flag = closeCon();  // 断开连接
                            if(!flag)
                                throw new Exception("断开连接发生异常！");
                            JOptionPane.showMessageDialog(frame,"成功断开！");
                            stop.setEnabled(false);
                            start.setEnabled(true);
                            port.setEnabled(true);
                            hostIp.setEnabled(true);
                            return;
                        } catch (Exception e){
                            JOptionPane.showMessageDialog(frame,e.getMessage());
                        }
                        JOptionPane.showMessageDialog(frame, "服务器缓冲区已满！");
                        return;
                    }
                    else
                        textArea.append(news + "\n");
                } catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        }
    }
}
