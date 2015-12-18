package Servlet;

import dao.AlbumTools;
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
            case "logout":
                doLogout(request,response);
                break;
            case "getAlbumlist":
                dogetAlbumlist(request,response);
                break;
        }
    }

    private void dogetAlbumlist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String userid=request.getParameter("userid");
        List<Album> albumList = AlbumTools.getAlbumList(userid);

        request.getSession().setAttribute("userid",userid);
        request.getSession().setAttribute("album_list", albumList);
        request.getRequestDispatcher("albums.jsp").forward(request,response);

    }

    private void doLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        request.getSession().removeAttribute("userid");
        request.getSession().removeAttribute("album_list");
        request.getRequestDispatcher("login.jsp").forward(request,response);
    }

    /**
     * 输入用户名和密码，
     * 登录成功则跳转到相册列表，登陆失败则显示账号或密码错误
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
                // 登陆成功，跳转
                List<Album> albumList = AlbumTools.getAlbumList(userid);

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
}
