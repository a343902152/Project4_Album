package Servlet;

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
     * 编辑album的属性
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
            String cururl=userid+"\\"+curAlbumname;
            String newurl=userid+"\\"+newAlbumname;
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,newAlbumname);
            preparedStatement.setInt(2, Integer.parseInt(albumid));
            preparedStatement.setString(3,userid);
            preparedStatement.executeUpdate();

            // 修改本地文件夹名
            String realpath = getServletContext().getRealPath("/") ;
            String savePath=realpath+"\\upload\\images\\";
            String curpath=savePath+"\\"+cururl;
            System.out.println(curpath);
            File file=new File(savePath+"\\"+cururl);
            if(file.exists()){
                String newPath=getServletContext().getRealPath("/")+"\\upload\\images\\"+newurl;
                System.out.println(newPath);
                file.renameTo(new File(getServletContext().getRealPath("/")+"\\upload\\images\\"+newurl));
            }
            // 修改photo里的url

            List<Album> albumList = getAlbumList(userid);
            request.getSession().setAttribute("album_list", albumList);
            request.getRequestDispatcher("albums.jsp").forward(request,response);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * albumList页面点击删除相册
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doDeleteAlbum(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userid=(String)request.getSession().getAttribute("userid");
        String albumid=request.getParameter("id");

        // todo 首先删除photos中的数据+本地图片，然后再是删除相册
        try {
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
        }catch (SQLException e){
            e.printStackTrace();
        }
        List<Album> albumList = getAlbumList(userid);
        request.getSession().setAttribute("album_list", albumList);
        request.getRequestDispatcher("albums.jsp").forward(request,response);
    }

    /**
     * albumList页面添加相册
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
        // 本地新建文件夹
        String realpath = getServletContext().getRealPath("/") ;
        String savePath=realpath+"upload\\images\\"+userid+"\\"+albumname;
        File file = new File(savePath);
        //判断上传文件的保存目录是否存在
        if (!file.exists() || !file.isDirectory()) {
            System.out.println(savePath+"目录不存在，需要创建");
            //创建目录
            file.mkdir();
            System.out.println("mkdir success");
        }

        System.out.println(changedLineCount);
        List<Album> albumList = getAlbumList(userid);

        request.getSession().setAttribute("album_list", albumList);
        request.getRequestDispatcher("albums.jsp").forward(request,response);
    }
    /**
     * 点击查看按钮，进入相册查看详情
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doViewAlbum(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 不需要userid
        String albumid=request.getParameter("id");
        String albumname="null";

        try {
            System.out.println("begin doviewalbum");
            String sqlSeleceAlbumName="select * from albums where albumid=?";
            PreparedStatement namePST=dbConn.prepareStatement(sqlSeleceAlbumName);
            namePST.setString(1,albumid);
            ResultSet res=namePST.executeQuery();
            if(res.next()) {
                albumname = res.getString(3);
            }
            System.out.println("albumname="+albumname);

            List<Photo> photoList=getPhotoList(albumid);
            System.out.println("listsze="+photoList.size());
            // 就显示照片
            request.getSession().setAttribute("albumid",albumid);
            request.getSession().setAttribute("album_name", albumname);
            request.setAttribute("photo_list", photoList);
            request.getRequestDispatcher("pics.jsp").forward(request,response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户id，获取他拥有的相册list
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

    private List<Photo> getPhotoList(String albumid){
        String sql="select * from photos where albumid=?";
        PreparedStatement preparedStatement;
        List<Photo> list=new ArrayList<Photo>();
        try{
            preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,albumid);

            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                Photo photo=new Photo(resultSet.getString(1),resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),resultSet.getString(5));
                list.add(photo);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
}
