package Servlet;

import dao.AlbumTools;
import dao.FileOperation;
import dao.MyDatabase;
import JavaBean.Album;
import JavaBean.Photo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
@WebServlet("/album")
public class AlbumServlet extends HttpServlet {

    private Connection dbConn = MyDatabase.getDBConnection();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        System.out.println("action=" + action);
        switch (action) {
            case "viewAlbum":
                doViewAlbum(request, response);
                break;
            case "updateAlbum":
                doUpdateAlbum(request,response);
                break;
            case "addAlbum":
                doAddAlbum(request,response);
                break;
            case "deleteAlbum":
                doDeleteAlbum(request,response);
                break;
        }
    }

    /**
     * �༭album������
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doUpdateAlbum(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userid=(String)request.getSession().getAttribute("userid");
        String albumid=request.getParameter("id");

        String curAlbumname="null";
        String newAlbumname=request.getParameter("aname");
        System.out.println("doupdatealbums___"+newAlbumname+"userid="+userid+"albumid=_"+albumid);
        String sql="update albums set albumname=? where albumid=? and userid=? ";
        try {
            String sqlGetPhotoUrl="select * from albums where albumid=?";
            PreparedStatement selectpst=dbConn.prepareStatement(sqlGetPhotoUrl);
            selectpst.setString(1,albumid);
            ResultSet resultSet=selectpst.executeQuery();
            if(resultSet.next()){
                curAlbumname=resultSet.getString(3);
            }
            String cururl=userid+"/"+curAlbumname;
            String newurl=userid+"/"+newAlbumname;
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,newAlbumname);
            preparedStatement.setInt(2, Integer.parseInt(albumid));
            preparedStatement.setString(3,userid);
            preparedStatement.executeUpdate();

            // �޸ı����ļ�����
            String realpath = getServletContext().getRealPath("/") ;
            String curpath=realpath+"/upload/images/"+cururl;
            String newPath=realpath+"/upload/images/"+newurl;

            System.out.println(curpath);

            if(FileOperation.Rename(curpath,newPath)){
                System.out.println("�������ɹ�");
            }else{
                System.out.println("������ʧ��");
                return ;
            }

            // 2016��2��1�� 03:12:40 ��ӣ�����photo���albumname

            String strUpdatephotos="update photos set albumname=? where albumid=? and userid=? ";
            PreparedStatement preparedStatementPhoto=dbConn.prepareStatement(strUpdatephotos);
            preparedStatementPhoto.setString(1,newAlbumname);
            preparedStatementPhoto.setInt(2,Integer.parseInt(albumid));
            preparedStatementPhoto.setString(3,userid);
            preparedStatementPhoto.executeUpdate();

            // �޸�photo���url
            List<Album> albumList = AlbumTools.getAlbumList(userid);
            request.getSession().setAttribute("album_list", albumList);
            request.getRequestDispatcher("albums.jsp").forward(request,response);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * albumListҳ����ɾ�����
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doDeleteAlbum(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userid=(String)request.getSession().getAttribute("userid");
        String albumid=request.getParameter("id");

        // ����ɾ��photos�е����ݣ�Ȼ������ɾ����ᣬȻ��ɾ����������
        try {
            String curAlbumname="null";
            String sqlGetPhotoUrl="select * from albums where albumid=?";
            PreparedStatement selectpst=dbConn.prepareStatement(sqlGetPhotoUrl);
            selectpst.setString(1,albumid);
            ResultSet resultSet=selectpst.executeQuery();
            if(resultSet.next()){
                curAlbumname=resultSet.getString(3);
            }
            String sqlDeletePhotos = "delete from photos where albumid=?";
            PreparedStatement dphotoPST=dbConn.prepareStatement(sqlDeletePhotos);
            dphotoPST.setInt(1,Integer.parseInt(albumid));
            dphotoPST.executeUpdate();

            String sql = "delete from albums where userid=? and albumid=?";
            PreparedStatement preparedStatement = dbConn.prepareStatement(sql);
            preparedStatement.setString(1, userid);
            preparedStatement.setInt(2, Integer.parseInt(albumid));
            preparedStatement.executeUpdate();
            preparedStatement.close();

            // ɾ�������ļ�����
            String realpath = getServletContext().getRealPath("/") ;
            String curpath=realpath+"/upload/images/"+userid+"/"+curAlbumname;
            System.out.println("curpath="+curpath);
            if(FileOperation.Delete(curpath)){
                System.out.println("ɾ���ɹ�");
            }else{
                System.out.println("ɾ��ʧ��");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        List<Album> albumList = AlbumTools.getAlbumList(userid);
        request.getSession().setAttribute("album_list", albumList);
        request.getRequestDispatcher("albums.jsp").forward(request,response);
    }

    /**
     * albumListҳ��������
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doAddAlbum(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userid=(String)request.getSession().getAttribute("userid");
        String albumname=request.getParameter("aname");

        System.out.println("doAddAlbum,name="+albumname+",userid="+userid);
        String sql = "insert into albums(userid,albumname) values(?,?)";
        PreparedStatement preparedStatement;
        int changedLineCount=0;
        try {
            preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,userid);
            preparedStatement.setString(2,albumname);
            changedLineCount=preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // �����½��ļ���
        String realpath = getServletContext().getRealPath("/") ;
        String savePath=realpath+"/upload/images/"+userid+"/"+albumname;
        if(FileOperation.Mkdir(savePath)){
            System.out.println("�½��ļ��гɹ�");
        }else{
            System.out.println("�½��ļ���ʧ��");
        }
        System.out.println(changedLineCount);
        List<Album> albumList = AlbumTools.getAlbumList(userid);

        request.getSession().setAttribute("album_list", albumList);
        request.getRequestDispatcher("albums.jsp").forward(request,response);
    }
    /**
     * ����鿴��ť���������鿴����
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doViewAlbum(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ����Ҫuserid
        String albumid=request.getParameter("id");
       String albumname="null";

        try {
            String sqlSeleceAlbumName="select * from albums where albumid=?";
            PreparedStatement namePST=dbConn.prepareStatement(sqlSeleceAlbumName);
            namePST.setString(1,albumid);
            ResultSet res=namePST.executeQuery();
            if(res.next()) {
                albumname = res.getString(3);
            }
            System.out.println("albumname="+albumname);

            List<Photo> photoList=AlbumTools.getPhotoList(albumid);
            // ����ʾ��Ƭ
            request.getSession().setAttribute("albumid",albumid);
            request.getSession().setAttribute("album_name", albumname);
            request.setAttribute("photo_list", photoList);
            request.getRequestDispatcher("pics.jsp").forward(request,response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
