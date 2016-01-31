package dao;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by hp on 2015/10/16.
 * ��֤ȫ�ֶ�ʹ��Ψһһ�����ݿ�
 */
public class MyDatabase {
    private static Connection dbConn;
    private MyDatabase(){
    }

    private static void DBInit(){
        //�������ݿ�
        String driverName="com.mysql.jdbc.Driver";
        String dbURL="jdbc:mysql://localhost/netAlbum";
        String userName="root";
        String userPwd="123";
        try{
            Class.forName(driverName);
            dbConn= DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("�������ݿ�ɹ�");
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.print("�������ݿ�ʧ��");
        }
    }

    public static Connection getDBConnection(){
        if(dbConn==null){
            DBInit();
        }
        return dbConn;
    }
}
