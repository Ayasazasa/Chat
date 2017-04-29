/*
 * Created by JFormDesigner on Thu Apr 06 20:49:47 CST 2017
 */

package pers.SHT.ChatGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.ErrorManager;
import javax.swing.*;
import javax.swing.JOptionPane;

import pers.SHT.Chat.*;

public class Login extends JFrame {

    private boolean use_connection=false; //判断用户是否登录成功
    private String user = null;  //用户姓名

    public Login() {
        initComponents();
        initLogin();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        register = new JFrame();
        panel12 = new JPanel();
        label6 = new JLabel();
        textField2 = new JTextField();
        label7 = new JLabel();
        passwordField2 = new JPasswordField();
        label8 = new JLabel();
        passwordField3 = new JPasswordField();
        button4 = new JButton();
        login = new JFrame();
        panel11 = new JPanel();
        label4 = new JLabel();
        textField1 = new JTextField();
        label5 = new JLabel();
        passwordField1 = new JPasswordField();
        button2 = new JButton();
        button3 = new JButton();

        //======== register ========
        {
            register.setTitle("register");
            register.setAlwaysOnTop(true);
            Container registerContentPane = register.getContentPane();
            registerContentPane.setLayout(new BorderLayout());

            //======== panel12 ========
            {
                panel12.setLayout(null);

                //---- label6 ----
                label6.setText("\u8d26\u53f7");
                panel12.add(label6);
                label6.setBounds(60, 55, 45, 30);
                panel12.add(textField2);
                textField2.setBounds(60, 90, 200, textField2.getPreferredSize().height);

                //---- label7 ----
                label7.setText("\u5bc6\u7801");
                panel12.add(label7);
                label7.setBounds(60, 125, 35, 25);
                panel12.add(passwordField2);
                passwordField2.setBounds(60, 155, 200, passwordField2.getPreferredSize().height);

                //---- label8 ----
                label8.setText("\u786e\u8ba4\u5bc6\u7801");
                panel12.add(label8);
                label8.setBounds(60, 190, 65, 20);
                panel12.add(passwordField3);
                passwordField3.setBounds(60, 215, 200, passwordField3.getPreferredSize().height);

                //---- button4 ----
                button4.setText("register");
                panel12.add(button4);
                button4.setBounds(90, 290, 120, 45);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < panel12.getComponentCount(); i++) {
                        Rectangle bounds = panel12.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = panel12.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    panel12.setMinimumSize(preferredSize);
                    panel12.setPreferredSize(preferredSize);
                }
            }
            registerContentPane.add(panel12, BorderLayout.CENTER);
            register.setSize(315, 485);
            register.setLocationRelativeTo(null);
        }

        //======== login ========
        {
            login.setTitle("login");
            login.setAlwaysOnTop(true);
            Container loginContentPane = login.getContentPane();
            loginContentPane.setLayout(new BorderLayout());

            //======== panel11 ========
            {
                panel11.setLayout(null);

                //---- label4 ----
                label4.setText("\u7528\u6237\u540d\uff1a");
                panel11.add(label4);
                label4.setBounds(140, 80, 80, 60);
                panel11.add(textField1);
                textField1.setBounds(240, 80, 280, 60);

                //---- label5 ----
                label5.setText("\u5bc6\u7801\uff1a");
                panel11.add(label5);
                label5.setBounds(140, 180, 80, 60);
                panel11.add(passwordField1);
                passwordField1.setBounds(240, 180, 280, 60);

                //---- button2 ----
                button2.setText("\u767b\u5f55");
                panel11.add(button2);
                button2.setBounds(140, 295, 100, 60);

                //---- button3 ----
                button3.setText("\u6ce8\u518c");
                panel11.add(button3);
                button3.setBounds(420, 295, 100, 60);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < panel11.getComponentCount(); i++) {
                        Rectangle bounds = panel11.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = panel11.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    panel11.setMinimumSize(preferredSize);
                    panel11.setPreferredSize(preferredSize);
                }
            }
            loginContentPane.add(panel11, BorderLayout.CENTER);
            login.setSize(635, 485);
            login.setLocationRelativeTo(null);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initLogin(){

        login.setVisible(true);

        button3.addActionListener(new ActionListener() {  //点击注册打开注册界面
            @Override
            public void actionPerformed(ActionEvent e) {
                initRegister();
            }
        });

        button2.addActionListener(new ActionListener() {  //点击登陆事件
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mysqlConnect_login();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        passwordField1.addActionListener(new ActionListener() {  //login界面密码框按回车
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mysqlConnect_login();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void initRegister()
    {
        register.setVisible(true);

        button4.addActionListener(new ActionListener() {  //register界面按register
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mysqlConnect_register();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        passwordField3.addActionListener(new ActionListener() {  //register界面密码框按回车
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mysqlConnect_register();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void mysqlConnect_login() throws SQLException {
        user = textField1.getText();
        String passwd = String.valueOf(passwordField1.getPassword());
        String sql=null;
        ResultSet rs=null;
        if(user.equals(""))
            JOptionPane.showMessageDialog(login, "账号不能为空！");
        else if(passwd.equals(""))
            JOptionPane.showMessageDialog(login, "密码不能为空！");
        else
        {
            sql = "select * from user where name='"+user+"';";
            rs = Conn.selectSQL(sql,login);
            if(!rs.next())
            {
                JOptionPane.showMessageDialog(login, "没有该账户！");
                textField1.setText(null);
                passwordField1.setText(null);
            }
            else
            {
                sql = "select * from user where name='"+user+"' and password='"+passwd+"';";
                rs = Conn.selectSQL(sql,login);
                if(!rs.next())
                {
                    JOptionPane.showMessageDialog(login, "密码错误！");
                    passwordField1.setText("");
                }
                else
                {

                    //JOptionPane.showMessageDialog(login, "登录成功！");
                    login.setVisible(false);
                    use_connection = true;
                }
            }
        }
    }

    private void mysqlConnect_register() throws SQLException {
        String user = textField2.getText();
        String passwd1 = String.valueOf(passwordField2.getPassword());
        String passwd2 = String.valueOf(passwordField3.getPassword());
        String sql=null;
        ResultSet rs=null;
        if(user.equals(""))
            JOptionPane.showMessageDialog(register, "注册账号不能为空！");
        else if(passwd1.equals("")||passwd2.equals(""))
            JOptionPane.showMessageDialog(register, "注册密码不能为空！");
        else
        {
            if(!passwd1.equals(passwd2))
            {
                JOptionPane.showMessageDialog(register, "两次密码不一致！");
                passwordField2.setText("");
                passwordField3.setText("");
            }
            else
            {
                sql = "select * from user where name='"+user+"';";
                rs = Conn.selectSQL(sql,register);
                if(!rs.next())
                {
                    sql = "insert into user values('"+user+"','"+passwd1+"');";
                    if(!Conn.insertSQL(sql,register))
                    {
                        JOptionPane.showMessageDialog(register, "注册失败！");
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(register, "注册成功！");
                        register.setVisible(false);
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(register, "该用户已存在！");
                    textField2.setText("");
                }
            }
        }
    }

    public boolean getLogin(){
        return use_connection;
    }

    public String getUser(){
        return user;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JFrame register;
    private JPanel panel12;
    private JLabel label6;
    private JTextField textField2;
    private JLabel label7;
    private JPasswordField passwordField2;
    private JLabel label8;
    private JPasswordField passwordField3;
    private JButton button4;
    private JFrame login;
    private JPanel panel11;
    private JLabel label4;
    private JTextField textField1;
    private JLabel label5;
    private JPasswordField passwordField1;
    private JButton button2;
    private JButton button3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
