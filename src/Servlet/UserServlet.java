package Servlet;

import dao.MyDatabase;
import JavaBean.Album;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2015/12/14.
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private Connection dbConn= MyDatabase.getDBConnection();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action=request.getParameter("action");
        System.out.println("user______action=" + action);
        switch (action) {
            case "login":
                System.out.println("begein to do login");
                doLogin(request, response);
                break;
            case "register":
                doRegister(request, response);
                break;
            case "logout":
                doLogout(request,response);
                break;
        }
    }

    private void doLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        request.getSession().removeAttribute("userid");
        request.getSession().removeAttribute("album_list");
        request.getRequestDispatcher("login.jsp").forward(request,response);
    }

    /**
     * �����û��������룬
     * ��¼�ɹ�����ת������б���½ʧ������ʾ�˺Ż��������
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userid=request.getParameter("userid");
        String password=request.getParameter("password");
        System.out.println(userid+","+password);

        String sql="select * from users where userid=? and psd=?";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) dbConn.prepareStatement(sql);
            pstmt.setString(1, userid);
            pstmt.setString(2, password);
            ResultSet resultSet = pstmt.executeQuery();
            if(resultSet.next()){
                System.out.println("login success");
                // ��½�ɹ�����ת
                // todo ��ȡ�б�Ҫ�ĳɺ���

                List<Album> albumList = getAlbumList(userid);

                request.getSession().setAttribute("userid",userid);
                request.getSession().setAttribute("album_list", albumList);
                request.getRequestDispatcher("albums.jsp").forward(request,response);
            }else{
                System.out.println("failed");
                request.setAttribute("info","login_failed");
                request.getRequestDispatcher("login.jsp").forward(request,response);
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void doRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        // todo
        String userid="cc";
        String psd="2";
    }

    /**
     * �����û�id����ȡ��ӵ�е����list
     * @param userid
     * @return
     */
    private List<Album> getAlbumList(String userid){
        String sql="select * from albums where userid=?";
        PreparedStatement preparedStatement;
        List<Album> list=new ArrayList<Album>();
        try{
            preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,userid);

            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                Album album=new Album(resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4));
                list.add(album);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return list;
    }
}
