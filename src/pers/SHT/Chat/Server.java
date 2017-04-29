/**
 * Created by VULCAN on 2017/4/7.
 */

package pers.SHT.Chat;

import pers.SHT.ChatGui.Login;

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
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.border.TitledBorder;


public class Server {

    private JFrame frame;
    private JTextArea contentArea;
    private JTextField message;
    private JTextField max_people;
    private JTextField port;
    private JButton start;
    private JButton stop;
    private JButton send;
    private JPanel northPanel;
    private JPanel southPanel;
    private JScrollPane rightPanel;
    private JScrollPane leftPanel;
    private JSplitPane centerSplit;   //分割窗口控件
    private JList userList;
    private DefaultListModel listModel;  //设置Jlist能动态改变

    private ServerSocket serverSocket;
    private ServerThread serverThread;
    private ArrayList<ClientThread> clients;

    private boolean isStart = false;

    // 主方法,程序执行入口
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        new Server();
    }


    private Server() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        frame = new JFrame("服务器");
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        message = new JTextField();
        max_people = new JTextField("10");
        port = new JTextField("6666");
        start = new JButton("启动");
        stop = new JButton("停止");
        send = new JButton("发送");
        stop.setEnabled(false);
        listModel = new DefaultListModel();
        userList = new JList(listModel);

        southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new TitledBorder("写消息"));
        southPanel.add(message, "Center");
        southPanel.add(send, "East");

        leftPanel = new JScrollPane(userList);
        leftPanel.setBorder(new TitledBorder("在线用户"));

        rightPanel = new JScrollPane(contentArea);
        rightPanel.setBorder(new TitledBorder("消息显示区"));

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftPanel,rightPanel);
        centerSplit.setDividerLocation(100);

        northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(1, 6));
        northPanel.add(new JLabel("人数上限"));
        northPanel.add(max_people);
        northPanel.add(new JLabel("端口"));
        northPanel.add(port);
        northPanel.add(start);
        northPanel.add(stop);
        northPanel.setBorder(new TitledBorder("配置信息"));

        frame.setLayout(new BorderLayout());
        frame.add(northPanel, "North");
        frame.add(centerSplit, "Center");
        frame.add(southPanel, "South");
        frame.setSize(600, 400);
        //frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());//设置全屏
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screen_width - frame.getWidth()) / 2,
                (screen_height - frame.getHeight()) / 2);
        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  //设置windows观感
        frame.setVisible(true);

        // 关闭窗口时事件
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(isStart)
                    closeServer(); //关闭服务器
                System.exit(0);
            }
        });

        // 文本框按回车键时事件
        message.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send_Message();
            }
        });

        // 单击发送按钮时事件
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                send_Message();
            }
        });

        // 单击启动服务器按钮时事件
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int people_max;
                int port_int;
                try {

                    try {
                        people_max = Integer.parseInt(max_people.getText());
                    } catch (Exception e1){
                        throw new Exception("人数上限为正整数！");
                    }
                    if(people_max<=0)
                        throw new Exception("人数上限为正整数！");

                    try {
                        port_int = Integer.parseInt(port.getText());
                    } catch (Exception e1){
                        throw new Exception("端口号为正整数！");
                    }
                    if(port_int<=0)
                        throw new Exception("端口号为正整数！");

                    serverStart(people_max,port_int);
                    contentArea.append("服务器已成功启动!人数上限：" + people_max + " 端口：" + port_int + "\n");  //往消息框里写信息
                    JOptionPane.showMessageDialog(frame,"服务器成功启动!");
                    start.setEnabled(false);
                    max_people.setEnabled(false);
                    port.setEnabled(false);
                    stop.setEnabled(true);

                } catch (Exception exc){
                    JOptionPane.showMessageDialog(frame,exc.getMessage());
                }
            }
        });

        // 单击停止服务器按钮时事件
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    closeServer();
                    start.setEnabled(true);
                    max_people.setEnabled(true);
                    port.setEnabled(true);
                    stop.setEnabled(false);
                    contentArea.append("服务器成功停止!\n");
                    JOptionPane.showMessageDialog(frame,"服务器成功停止！");
                } catch (Exception exc){
                    JOptionPane.showMessageDialog(frame,exc.getMessage());
                }
            }
        });
    }

    // 执行消息发送
    private void send_Message(){
        if(!isStart)
            JOptionPane.showMessageDialog(frame,"服务器还未启动,不能发送消息！");
        else if(clients.size()==0)
            JOptionPane.showMessageDialog(frame,"没有用户在线,不能发送消息！");
        else
        {
            String news = message.getText().trim();
            if(news.equals(""))
                JOptionPane.showMessageDialog(frame,"消息不能为空！");
            else
            {
                for(int i=0;i<clients.size();i++)
                {
                    clients.get(i).getWriter().println("服务器：" + news);
                    clients.get(i).getWriter().flush();
                    contentArea.append("服务器：" + message.getText() + "\n");
                    message.setText(null);
                }
            }
        }
    }

    // 启动服务器
    private void serverStart(int people_max,int port_int) throws IOException {

        try {
            clients = new ArrayList<ClientThread>();
            serverSocket = new ServerSocket(port_int);
            serverThread = new ServerThread(serverSocket,people_max);
            serverThread.start();
            isStart = true;
        } catch (BindException e){
            e.printStackTrace();
            isStart = false;
            throw new BindException("端口号已被占用，请换一个！");
        } catch (Exception exc){
            exc.printStackTrace();
            isStart = false;
            throw new BindException("启动服务器异常！");
        }
    }

    // 关闭服务器
    private void closeServer(){
        try {

            if(serverThread != null)
                serverThread.stop();  // 停止服务器线程

            for(int i=0;i<clients.size();i++)
            {
                // 给所有在线用户发送关闭命令
                clients.get(i).getWriter().println("CLOSE");
                clients.get(i).getWriter().flush();
                // 释放资源
                clients.get(i).stop();  // 停止此条为客户端服务的线程
                clients.get(i).in.close();
                clients.get(i).out.close();
                clients.get(i).socket.close();
                clients.remove(i);
            }

            if(serverSocket != null)
                serverSocket.close();  // 关闭服务器端连接
            listModel.removeAllElements();  // 清空用户列表
            isStart = false;
        } catch (IOException e){
            e.printStackTrace();
            isStart = true;
        }
    }

    // 服务器线程
    class ServerThread extends Thread{

        private Socket socket=null;
        private ServerSocket serverSocket=null;
        private int max; //人数上限


        private ServerThread(ServerSocket serverSocket,int max) {
            this.serverSocket = serverSocket;
            this.max = max;
        }

        public void run(){
            while (true)
            {
                try {
                    socket = serverSocket.accept();
                    if(clients.size()==max)
                    {
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        // 接收客户端的用户信息
                        String news = in.readLine();
                        StringTokenizer str = new StringTokenizer(news,"@");
                        // 反馈人数超限信息
                        out.println("MAX@服务器：对不起，" + str.nextToken() + "/" + str.nextToken() + "，服务器在线人数已达上限，请稍后尝试连接！");
                        out.flush();
                        // 释放资源
                        in.close();
                        out.close();
                        socket.close();
                        continue; // 该连接结束  开启下一个socket线程
                    }
                    else
                    {
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        // 接收客户端的用户信息
                        String news = in.readLine();
                        StringTokenizer str = new StringTokenizer(news,"@");
                        String name = str.nextToken();
                        String ip = str.nextToken();
                        //检查该用户是否已经在线
                        if(clients.size() > 0)
                        {
                            boolean flag=false;
                            for(int i=0;i<clients.size();i++)
                            {
                                if(clients.get(i).get_name().equals(name))
                                {
                                    //返回已经在线信息
                                    out.println("Online");
                                    out.flush();
                                    //释放资源
                                    in.close();
                                    out.close();
                                    socket.close();
                                    //用户在线
                                    flag = true;
                                }
                                if(flag) break;
                            }
                            if(flag) continue;
                        }
                        //用户不在线，开启线程
                        ClientThread client = new ClientThread(socket,in,out,name,ip);
                        client.start();
                        listModel.addElement(name);// 更新在线列表
                        contentArea.append(name + ip + "上线!\n");
                    }
                } catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        }
    }

    // 为一个客户端服务的线程
    class ClientThread extends Thread{
        private Socket socket=null;
        private BufferedReader in=null;
        private PrintWriter out=null;
        private String name=null;
        private String ip=null;

        public PrintWriter getWriter(){
            return out;
        }

        public String get_name(){
            return name;
        }

        public String getIp(){
            return ip;
        }

        private ClientThread(Socket test_socket,BufferedReader test_in,PrintWriter test_out,String test_name,String test_ip){
            socket = test_socket;
            in = test_in;
            out = test_out;
            name = test_name;
            ip = test_ip;
            // 反馈连接成功信息
            out.println(name + ip + "与服务器连接成功!");
            out.flush();
            // 反馈当前在线用户信息
            if (clients.size() > 0)
            {
                String temp = "";
                for (int i=0;i<clients.size();i++)
                    temp += clients.get(i).get_name() + "@";
                out.println("USERLIST@" + clients.size() + "@" + temp);
                out.flush();
            }
            // 向所有在线用户发送该用户上线命令
            clients.add(this);
            for(int i=0;i<clients.size();i++)
            {
                clients.get(i).getWriter().println("ADD@" + name);
                clients.get(i).getWriter().flush();
            }
        }

        public void run(){// 不断接收客户端的消息 进行处理
            while(true)
            {
                try {
                    String news = in.readLine();
                    if(news.equals("CLOSE"))  // 下线命令
                    {
                        contentArea.append(name + ip + "下线!\n");

                        // 向所有在线用户发送该用户的下线命令
                        for(int i=0;i<clients.size();i++)
                        {
                            clients.get(i).getWriter().println("DELETE@" + name);
                            clients.get(i).getWriter().flush();
                        }

                        // 断开连接释放资源
                        in.close();
                        out.close();
                        socket.close();

                        // 更新在线列表
                        listModel.removeElement(name);

                        // 删除此条客户端服务线程
                        for(int i=0;i<clients.size();i++)
                        {
                            if(clients.get(i).get_name().equals(name))
                            {
                                ClientThread tmp = clients.get(i);
                                clients.remove(i);// 删除此用户的服务线程
                                tmp.stop();// 停止这条服务线程
                                return;
                            }
                        }
                    }
                    else
                    {
                        // 服务器转发消息
                        StringTokenizer str = new StringTokenizer(news,"@");
                        String fname = str.nextToken();
                        String content = str.nextToken();
                        news = fname + "：" + content;
                        contentArea.append(news + "\n");
                        for(int i=0;i<clients.size();i++)
                        {
                            clients.get(i).getWriter().println(news);
                            clients.get(i).getWriter().flush();
                        }
                    }
                } catch (IOException exc){
                    exc.printStackTrace();
                }
            }
        }
    }
}
