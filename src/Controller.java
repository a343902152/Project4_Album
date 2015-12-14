import dao.MyDatabase;
import domain.Album;
import domain.Photo;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.print.attribute.standard.PresentationDirection;
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
 * Created by hp on 2015/12/8.
 */
//@WebServlet(name = "Controller",urlPatterns = {"/controller"})
@WebServlet("/controller")
public class Controller extends HttpServlet {

    private static String defaultAddress="D:\\JAVA_project\\Project4_Album";
    private Connection dbConn= MyDatabase.getDBConnection();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("do post___________");
        String action=request.getParameter("action");

        System.out.println("action="+action);
        switch (action){
            case "login":
                System.out.println("begein to do login");
                doLogin(request,response);
                break;
            case "register":
                // todo register
                doRegister(request,response);
                break;
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


            case "viewPhoto":
                doViewPhoto(request,response);
                break;
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

        String albumname="NewAlbumName";
        String introducation="NewIntroducatoinXXXXXX";

        String sql="update albums set albumname=?, introducation=? " +
                "where albumid=?";
        try {
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,albumname);
            preparedStatement.setString(2,introducation);
            preparedStatement.setString(3,albumid);
            preparedStatement.executeUpdate();

            List<Album> albumList = getAlbumList(userid);

            request.setAttribute("album_list", albumList);
            request.getRequestDispatcher("albumList.jsp").forward(request,response);

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

        String sql="delete from albums where userid=? and albumid=?";
        try{
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,userid);
            preparedStatement.setString(2,albumid);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        List<Album> albumList = getAlbumList(userid);
        request.setAttribute("album_list", albumList);
        request.getRequestDispatcher("albumList.jsp").forward(request,response);
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

        // fixme ��ô֪�����ĸ��û��ϴ������ݣ���������"a"������
        String userid=(String)request.getSession().getAttribute("userid");
        String albumname="nnnnnmammma";
        String introducation="introducatoinXXXXXX";

        String sql = "insert into albums(userid,albumname,introducation) values(?,?,?)";
        PreparedStatement preparedStatement;
        int changedLineCount=0;
        try {
            preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,userid);
            preparedStatement.setString(2,albumname);
            preparedStatement.setString(3,introducation);
            changedLineCount=preparedStatement.executeUpdate();
            preparedStatement.close();
//            pstmt = (PreparedStatement) conn.prepareStatement(sql);
//            pstmt.setString(1, student.getName());
//            pstmt.setString(2, student.getSex());
//            pstmt.setString(3, student.getAge());
//            i = pstmt.executeUpdate();
//            pstmt.close();
//            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(changedLineCount);
        List<Album> albumList = getAlbumList(userid);

        request.setAttribute("album_list", albumList);
        request.getRequestDispatcher("albumList.jsp").forward(request,response);
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
//        String userid=request.getParameter("userid");
        // ����Ҫuserid
        String albumid=request.getParameter("id");
        System.out.println("albumid="+albumid);

        // todo ���Լ�һ��userid ���Ʒ���Ȩ��
        List<Photo> photoList=getPhotoList(albumid);
        // ����Ƭ�Ļ�������ʾ��Ƭ
        request.getSession().setAttribute("albumid",albumid);
        request.setAttribute("photo_list", photoList);
        request.getRequestDispatcher("photoList.jsp").forward(request,response);
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
        /*******************************���������ݹ��������ݣ�����List��������-����:FileItem***********/

        //�õ��ϴ��ļ��ı���Ŀ¼�����ϴ����ļ������WEB-INFĿ¼�£����������ֱ�ӷ��ʣ���֤�ϴ��ļ��İ�ȫ
//         String savePath = this.getServletContext().getRealPath(defaultAddress+"/web/WEB-INF/upload");



        String albumid=request.getParameter("albumid");
        String userid=(String)request.getSession().getAttribute("userid");


        String realpath = getServletContext().getRealPath("/") ;
        String savePath=realpath+"/upload/images/"+userid;

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

//             //3���ж��ύ�����������Ƿ����ϴ���������
//             if(!ServletFileUpload.isMultipartContent(request)){
//                     //���մ�ͳ��ʽ��ȡ����
//                     return;
//             }

             //4��ʹ��ServletFileUpload�����������ϴ����ݣ�����������ص���һ��List<FileItem>���ϣ�ÿһ��FileItem��Ӧһ��Form����������
             List<FileItem> list = upload.parseRequest(request);
             // todo �ĳ�i->n?
             String imgname=list.get(0).getString("UTF-8");

             String introducation="dsafsdf"; // ͼƬ���������ⲿ����
             String url; // ��¼ͼƬ�����·��
             System.out.println("img name="+imgname);


             // ��ȡ����
             for(FileItem item : list){
                 //���fileitem�з�װ������ͨ�����������
                 if(item.isFormField()){
                     // ��ȡÿ��input��name
                     String name = item.getFieldName();
                     //�����ͨ����������ݵ�������������
                     String value = item.getString("UTF-8");
                     //value = new String(value.getBytes("iso8859-1"),"UTF-8");
                     System.out.println(name + "=" + value);
                 }else{//���fileitem�з�װ�����ϴ��ļ�
                     //�õ��ϴ����ļ����ƣ�
                     String filename = item.getName();
                     System.out.println(filename);
                     if(filename==null || filename.trim().equals("")){
                             continue;
                     }
//                         System.out.println(filename);
                     //ע�⣺��ͬ��������ύ���ļ����ǲ�һ���ģ���Щ������ύ�������ļ����Ǵ���·���ģ��磺  c:\a\b\1.txt������Щֻ�ǵ������ļ������磺1.txt
                     //�����ȡ�����ϴ��ļ����ļ�����·�����֣�ֻ�����ļ�������
                     filename = filename.substring(filename.lastIndexOf(File.separator)+1);
                     String[] arr=filename.split("\\.");
                     String fileType=arr[1];
                     imgname=imgname+"."+fileType;
                     //��ȡitem�е��ϴ��ļ���������
                     InputStream in = item.getInputStream();
                     //����һ���ļ������
                     FileOutputStream out = new FileOutputStream(savePath + File.separator + imgname);
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
                     message = "�ļ��ϴ��ɹ���";
                     System.out.println(message);
                 }
             }

             // ���ݴ浽���ݿ���
             String sql="insert into photos(albumid,photoname,url,introducation,userid) values(?,?,?,?,?)";
             PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
             System.out.println(albumid+imgname+"  "+savePath + "  "+File.separator + imgname+"  "+introducation);
             preparedStatement.setString(1, albumid);
             preparedStatement.setString(2,imgname);
             preparedStatement.setString(3,userid+"/"+imgname);
             preparedStatement.setString(4,introducation);
             preparedStatement.setString(5,userid);

             preparedStatement.executeUpdate();
             preparedStatement.close();

             // ˢ��һ��ҳ��
             List<Photo> photoList=getPhotoList(albumid);
             request.getSession().setAttribute("albumid",albumid);
             request.setAttribute("photo_list", photoList);
             request.getRequestDispatcher("photoList.jsp").forward(request,response);
             // fixme ����������֮����ܿ������ϴ���ͼƬ����Ȼ��ô�������У�����
         }catch (Exception e) {
             message= "�ļ��ϴ�ʧ�ܣ�";
             System.out.println(message);
             e.printStackTrace();

         }
//         request.setAttribute("message",message);
//         request.getRequestDispatcher("/message.jsp").forward(request, response);

}

    /**
     * ���ͼƬ���Ŵ�ͼƬ�鿴
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doViewPhoto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String photoid= request.getParameter("id");
        System.out.println("photoid="+photoid);

        PrintWriter out=response.getWriter();
        out.println("get photo");
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

            // todo ɾ�����ص���Ƭ
            String realpath = getServletContext().getRealPath("/") ;
            String savePath=realpath+"/upload/images/"+userid;
            File file=new File(savePath+"/"+photoname);
            if(file.exists()){
                System.out.println("delete savePath+\"/\"+photoname");
                file.delete();
            }
            // ��ʾͼƬ
            List<Photo> photoList=getPhotoList(albumid);
            if(photoList.size()>0){
                // ����Ƭ�Ļ�������ʾ��Ƭ
                request.getSession().setAttribute("albumid",albumid);
                request.setAttribute("photo_list", photoList);
                request.getRequestDispatcher("photoList.jsp").forward(request,response);
            }else{
                // todo û����Ƭ������ʾ����Ӱ�
            }
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
        String photoid=request.getParameter("id");

        String photoname="NewPhotoname____________";
        String introducation="NewIntroducation_______________";

        //  TODO ���Կ��Ǽ�һ�� �ƶ����������ѡ��
        String sql="update photos set photoname=?, introducation=? " +
                "where photoid=?";
        try {
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,photoname);
            preparedStatement.setString(2,introducation);
            preparedStatement.setString(3,photoid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

    private List<Photo> getPhotoList(String albumid){
        String sql="select * from photos where albumid=?";
        PreparedStatement preparedStatement;
        List<Photo> list=new ArrayList<Photo>();
        try{
            preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,albumid);

            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                Photo photo=new Photo(resultSet.getString(3),resultSet.getString(4),
                        resultSet.getString(5),resultSet.getString(6));
                list.add(photo);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
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
                request.setAttribute("album_list", albumList);
                request.getRequestDispatcher("albumList.jsp").forward(request,response);
            }else{
                System.out.println("failed");
                request.setAttribute("info","login_failed");
                request.getRequestDispatcher("index.jsp").forward(request,response);
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
}
