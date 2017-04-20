/**
 * Created by VULCAN on 2017/4/8.
 */

package pers.SHT.Chat;

import java.sql.*;

import javax.swing.*;

public class Conn {

    private static Connection conn;
    private static Statement stmt=null;

    public static void connection() throws ClassNotFoundException{
        try{
            //  1.定义驱动程序名
            String driver = "com.mysql.jdbc.Driver";
            //  2.定义连接串数据库名
            String url = "jdbc:mysql://112.74.58.116/SChat?characterEncoding=UTF8";  //112.74.58.116是连接数据库IP,SChat是连接的数据库名称;
            //  3.加载驱动
            Class.forName(driver);
            //  4.创建连接
            conn = DriverManager.getConnection(url, "Ubuntu", "0008");
            stmt = conn.createStatement();
            //JOptionPane.showMessageDialog(null, "数据库连接成功!");
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "数据库连接失败!");
            e.printStackTrace();
        }
    }

    public static boolean insertSQL(String sql,JFrame jFrame){       //插入数据
        try{
            stmt.execute(sql);
            JOptionPane.showMessageDialog(jFrame, "插入数据成功！");
            return true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(jFrame, "插入数据失败！");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteSQL(String sql,JFrame jFrame){       //删除数据
        try{
            stmt.executeUpdate(sql);
            JOptionPane.showMessageDialog(jFrame, "删除数据成功！");
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(jFrame, "删除数据时出错！");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateSQL(String sql,JFrame jFrame){       //更新数据
        try{
            stmt.executeUpdate(sql);
            JOptionPane.showMessageDialog(jFrame, "修改数据成功！");
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(jFrame, "修改数据时出错！");
            e.printStackTrace();
        }
        return false;
    }

    public static ResultSet selectSQL(String sql,JFrame jFrame){    //查询数据
        ResultSet rs=null;
        try{
            rs = stmt.executeQuery(sql);
        }catch(Exception e) {
            JOptionPane.showMessageDialog(jFrame, "查询数据出错！");
            e.printStackTrace();
        }
        return rs;
    }
}
