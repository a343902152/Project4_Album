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
     * �ϴ���Ƭ
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
        // Ҫ������ļ��е�·�� ����savaPath=/upload/images/a
        String savePath=realpath+"/upload/images/"+userid+"/"+albumname;

        File file = new File(savePath);
        //�ж��ϴ��ļ��ı���Ŀ¼�Ƿ����
        if (!file.exists() && !file.isDirectory()) {
            System.out.println(savePath+"Ŀ¼�����ڣ���Ҫ����");
            //����Ŀ¼
            file.mkdir();
        }
        //��Ϣ��ʾ
        String message = "";
        try{
            //ʹ��Apache�ļ��ϴ���������ļ��ϴ����裺
            //1������һ��DiskFileItemFactory����
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //2������һ���ļ��ϴ�������
            ServletFileUpload upload = new ServletFileUpload(factory);
            //����ϴ��ļ�������������
            upload.setHeaderEncoding("UTF-8");

            List<FileItem> list = upload.parseRequest(request);

            FileItem item=list.get(0); // ��ȡ�û��ϴ����ļ�item
            String fileurl=item.getName(); // ���ļ������֣�����img1.jpg
//            String[] arr=fileurl.split("\\.");
//            String imgname;
//            imgname=arr[0]; // ��ȡ���ֲ��֣������ȡ��img1

            //��ȡitem�е��ϴ��ļ���������
            InputStream in = item.getInputStream();
            //����һ���ļ������
            FileOutputStream out = new FileOutputStream(savePath + File.separator + fileurl);
            //����һ��������
            byte buffer[] = new byte[1024];
            //�ж��������е������Ƿ��Ѿ�����ı�ʶ
            int len = 0;
            //ѭ�������������뵽���������У�(len=in.read(buffer))>0�ͱ�ʾin���滹������
            while((len=in.read(buffer))>0){
                //ʹ��FileOutputStream�������������������д�뵽ָ����Ŀ¼(savePath + "\\" + filename)����
                out.write(buffer, 0, len);
            }
            //�ر�������
            in.close();
            //�ر������
            out.close();
            //ɾ�������ļ��ϴ�ʱ���ɵ���ʱ�ļ�
            item.delete();

            // ���ݴ浽���ݿ���
            String sql="insert into photos(albumid,photoname,albumname,userid) values(?,?,?,?)";
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
//            System.out.println(albumid+imgname+"  "+savePath + "  "+File.separator + imgname);
            preparedStatement.setString(1, albumid);
            preparedStatement.setString(2,fileurl);
            preparedStatement.setString(3,albumname);
            preparedStatement.setString(4,userid);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            // ˢ��һ��ҳ��
            List<Photo> photoList=getPhotoList(albumid);
            request.getSession().setAttribute("albumid",albumid);
            request.setAttribute("photo_list", photoList);
            request.setAttribute("album_name",albumname);
            request.getRequestDispatcher("pics.jsp").forward(request,response);
        }catch (Exception e) {
            message= "�ļ��ϴ�ʧ�ܣ�";
            System.out.println(message);
            e.printStackTrace();
        }
//         request.setAttribute("message",message);
//         request.getRequestDispatcher("/message.jsp").forward(request, response);

    }

    /**
     * ɾ����Ƭ
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doDeletePhoto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userid=(String)request.getSession().getAttribute("userid"); // ����ͼƬ���ᱻ��ɾ
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

            // ɾ�����ص���Ƭ
            String realpath = getServletContext().getRealPath("/") ;
            String savePath=realpath+"/upload/images/"+userid;
            File file=new File(savePath+"/"+photoname);
            if(file.exists()){
                System.out.println("delete savePath+\"/\"+photoname");
                file.delete();
            }

            // ��ʾ������ֵ�ҳ����
            String sqlSeleceAlbumName="select * from albums where albumid=?";
            PreparedStatement namePST=dbConn.prepareStatement(sqlSeleceAlbumName);
            namePST.setInt(1,Integer.parseInt(albumid));
            ResultSet res=namePST.executeQuery();
            String albumname="null";
            if(res.next()) {
                albumname = res.getString(3);
            }
            // ��ʾͼƬ
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
     * �༭ͼƬ
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doUpdatePhoto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userid=(String)request.getSession().getAttribute("userid"); // ����ͼƬ���ᱻ��ɾ
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

            // �޸ı���ͼƬ������
            String realpath = getServletContext().getRealPath("/") ;
            String savePath=realpath+"/upload/images/";
            String curpath=savePath+"/"+cururl;
            System.out.println(curpath);
            File file=new File(savePath+"/"+cururl);
            if(file.exists()){
                String newPath=getServletContext().getRealPath("/")+"/upload/images/"+newurl;
                System.out.println(newPath);
                file.renameTo(new File(getServletContext().getRealPath("/")+"/upload/images/"+newurl));
            }

//            // ��ʾ������ֵ�ҳ����
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