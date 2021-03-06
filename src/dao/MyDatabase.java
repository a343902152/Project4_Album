package dao;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by hp on 2015/10/16.
 * 保证全局都使用唯一一个数据库
 */
public class MyDatabase {
    private static Connection dbConn;
    private MyDatabase(){
    }

    private static void DBInit(){
        //连接数据库
        String driverName="com.mysql.jdbc.Driver";
        String dbURL="jdbc:mysql://localhost/netAlbum";
        String userName="root";
        String userPwd="123";
        try{
            Class.forName(driverName);
            dbConn= DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("连接数据库成功");
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.print("连接数据库失败");
        }
    }

    public static Connection getDBConnection(){
        if(dbConn==null){
            DBInit();
        }
        return dbConn;
    }
}
