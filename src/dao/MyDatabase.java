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
        String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbURL="jdbc:sqlserver://localhost:1433;DatabaseName=networkAlbum";

        String userName="sa";
        String userPwd="123321";
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
