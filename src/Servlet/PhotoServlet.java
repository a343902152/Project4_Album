package Servlet;

import dao.MyDatabase;
import JavaBean.Album;
import JavaBean.Photo;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2015/12/14.
 */
@WebServlet("/photo")
public class PhotoServlet extends HttpServlet {

    private Connection dbConn = MyDatabase.getDBConnection();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        System.out.println("action=" + action);
        switch (action) {
            case "updatePhoto":
                doUpdatePhoto(request,response);
                break;
            case "deletePhoto":
                doDeletePhoto(request, response);
                break;
            case "uploadPhoto":
                doUploadPhoto(request,response);
                break;
        }
    }

    /**
     * 上传照片
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doUploadPhoto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String albumid=request.getParameter("albumid");
        String albumname=(String)request.getSession().getAttribute("album_name");
        System.out.println("albumname="+albumname);
        String userid=(String)request.getSession().getAttribute("userid");
        String realpath = getServletContext().getRealPath("/") ;
        // 要保存的文件夹的路径 比如savaPath=/upload/images/a
        String savePath=realpath+"\\upload\\images\\"+userid+"\\"+albumname;

        File file = new File(savePath);
        //判断上传文件的保存目录是否存在
        if (!file.exists() && !file.isDirectory()) {
            System.out.println(savePath+"目录不存在，需要创建");
            //创建目录
            file.mkdir();
        }
        //消息提示
        String message = "";
        try{
            //使用Apache文件上传组件处理文件上传步骤：
            //1、创建一个DiskFileItemFactory工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //2、创建一个文件上传解析器
            ServletFileUpload upload = new ServletFileUpload(factory);
            //解决上传文件名的中文乱码
            upload.setHeaderEncoding("UTF-8");

            List<FileItem> list = upload.parseRequest(request);

            FileItem item=list.get(0); // 获取用户上传的文件item
            String fileurl=item.getName(); // 该文件的名字，比如img1.jpg
//            String[] arr=fileurl.split("\\.");
//            String imgname;
//            imgname=arr[0]; // 截取名字部分，比如截取出img1

            //获取item中的上传文件的输入流
            InputStream in = item.getInputStream();
            //创建一个文件输出流
            FileOutputStream out = new FileOutputStream(savePath + File.separator + fileurl);
            //创建一个缓冲区
            byte buffer[] = new byte[1024];
            //判断输入流中的数据是否已经读完的标识
            int len = 0;
            //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
            while((len=in.read(buffer))>0){
                //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                out.write(buffer, 0, len);
            }
            //关闭输入流
            in.close();
            //关闭输出流
            out.close();
            //删除处理文件上传时生成的临时文件
            item.delete();

            // 数据存到数据库里
            String sql="insert into photos(albumid,photoname,albumname,userid) values(?,?,?,?)";
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
//            System.out.println(albumid+imgname+"  "+savePath + "  "+File.separator + imgname);
            preparedStatement.setString(1, albumid);
            preparedStatement.setString(2,fileurl);
            preparedStatement.setString(3,albumname);
            preparedStatement.setString(4,userid);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            // 刷新一下页面
            List<Photo> photoList=getPhotoList(albumid);
            request.getSession().setAttribute("albumid",albumid);
            request.setAttribute("photo_list", photoList);
            request.setAttribute("album_name",albumname);
            request.getRequestDispatcher("pics.jsp").forward(request,response);
        }catch (Exception e) {
            message= "文件上传失败！";
            System.out.println(message);
            e.printStackTrace();
        }
//         request.setAttribute("message",message);
//         request.getRequestDispatcher("/message.jsp").forward(request, response);

    }

    /**
     * 删除照片
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doDeletePhoto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userid=(String)request.getSession().getAttribute("userid"); // 保障图片不会被乱删
        String photoid=request.getParameter("id");
        String albumid=request.getParameter("albumid");

        String photoname="null";
        try {
            String selectSql="select * from photos where photoid=?";
            PreparedStatement selectpst=dbConn.prepareStatement(selectSql);
            selectpst.setString(1,photoid);
            ResultSet resultSet=selectpst.executeQuery();
            if(resultSet.next()){
                photoname=resultSet.getString(4);
            }

            String sql="delete from photos where userid=? and photoid=?";
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,userid);
            preparedStatement.setString(2,photoid);
            preparedStatement.executeUpdate();

            // 删除本地的照片
            String realpath = getServletContext().getRealPath("/") ;
            String savePath=realpath+"/upload/images/"+userid;
            File file=new File(savePath+"\\"+photoname);
            if(file.exists()){
                System.out.println("delete savePath+\"/\"+photoname");
                file.delete();
            }

            // 显示相册名字到页面上
            String sqlSeleceAlbumName="select * from albums where albumid=?";
            PreparedStatement namePST=dbConn.prepareStatement(sqlSeleceAlbumName);
            namePST.setInt(1,Integer.parseInt(albumid));
            ResultSet res=namePST.executeQuery();
            String albumname="null";
            if(res.next()) {
                albumname = res.getString(3);
            }
            // 显示图片
            List<Photo> photoList=getPhotoList(albumid);
            request.getSession().setAttribute("albumid",albumid);
            request.setAttribute("photo_list", photoList);
            request.setAttribute("album_name",albumname);
            request.getRequestDispatcher("pics.jsp").forward(request,response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 编辑图片
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doUpdatePhoto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userid=(String)request.getSession().getAttribute("userid"); // 保障图片不会被乱删
        String photoid=request.getParameter("id");
        String albumid=request.getParameter("albumid");
        String newPhotoname=request.getParameter("new_name");
        String albumname=(String)request.getSession().getAttribute("album_name");
        String cururl="null";

        System.out.println(Integer.parseInt(photoid));
        try {
            String sqlGetPhotoUrl="select * from photos where photoid=?";
            PreparedStatement selectpst=dbConn.prepareStatement(sqlGetPhotoUrl);
            selectpst.setString(1,photoid);
            ResultSet resultSet=selectpst.executeQuery();
            if(resultSet.next()){
                cururl=resultSet.getString(5);
            }
            String[] arrs=cururl.split("\\.");
            String filetype=arrs[1];
            String newurl=userid+"/"+albumname+"/"+newPhotoname+"."+filetype;
            System.out.println("new url ="+newurl);


            String sql="update photos set photoname=?,url=? where photoid=? and albumid=?";
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,newPhotoname);
            preparedStatement.setString(2,newurl);
            preparedStatement.setInt(3, Integer.parseInt(photoid));
            preparedStatement.setInt(4, Integer.parseInt(albumid));
            preparedStatement.executeUpdate();

            // 修改本地图片的名字
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

//            // 显示相册名字到页面上
//            String sqlSeleceAlbumName="select * from albums where albumid=?";
//            PreparedStatement namePST=dbConn.prepareStatement(sqlSeleceAlbumName);
//            namePST.setInt(1,Integer.parseInt(albumid));
//            ResultSet res=namePST.executeQuery();
//            String albumname="null";
//            if(res.next()) {
//                albumname = res.getString(3);
//            }
            List<Photo> photoList=getPhotoList(albumid);
            request.getSession().setAttribute("albumid",albumid);
            request.setAttribute("photo_list", photoList);
            request.setAttribute("album_name",albumname);
            request.getRequestDispatcher("pics.jsp").forward(request,response);
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
