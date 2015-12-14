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
     * albumList页面添加相册
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doAddAlbum(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // fixme 怎么知道是哪个用户上传的数据？这里先用"a"来测试
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
     * 点击查看按钮，进入相册查看详情
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doViewAlbum(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        String userid=request.getParameter("userid");
        // 不需要userid
        String albumid=request.getParameter("id");
        System.out.println("albumid="+albumid);

        // todo 可以加一个userid 控制访问权限
        List<Photo> photoList=getPhotoList(albumid);
        // 有照片的话，就显示照片
        request.getSession().setAttribute("albumid",albumid);
        request.setAttribute("photo_list", photoList);
        request.getRequestDispatcher("photoList.jsp").forward(request,response);
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
        /*******************************解析表单传递过来的数据，返回List集合数据-类型:FileItem***********/

        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
//         String savePath = this.getServletContext().getRealPath(defaultAddress+"/web/WEB-INF/upload");



        String albumid=request.getParameter("albumid");
        String userid=(String)request.getSession().getAttribute("userid");


        String realpath = getServletContext().getRealPath("/") ;
        String savePath=realpath+"/upload/images/"+userid;

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

//             //3、判断提交上来的数据是否是上传表单的数据
//             if(!ServletFileUpload.isMultipartContent(request)){
//                     //按照传统方式获取数据
//                     return;
//             }

             //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
             List<FileItem> list = upload.parseRequest(request);
             // todo 改成i->n?
             String imgname=list.get(0).getString("UTF-8");

             String introducation="dsafsdf"; // 图片的描述，外部输入
             String url; // 记录图片的相对路径
             System.out.println("img name="+imgname);


             // 读取数据
             for(FileItem item : list){
                 //如果fileitem中封装的是普通输入项的数据
                 if(item.isFormField()){
                     // 获取每个input的name
                     String name = item.getFieldName();
                     //解决普通输入项的数据的中文乱码问题
                     String value = item.getString("UTF-8");
                     //value = new String(value.getBytes("iso8859-1"),"UTF-8");
                     System.out.println(name + "=" + value);
                 }else{//如果fileitem中封装的是上传文件
                     //得到上传的文件名称，
                     String filename = item.getName();
                     System.out.println(filename);
                     if(filename==null || filename.trim().equals("")){
                             continue;
                     }
//                         System.out.println(filename);
                     //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                     //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                     filename = filename.substring(filename.lastIndexOf(File.separator)+1);
                     String[] arr=filename.split("\\.");
                     String fileType=arr[1];
                     imgname=imgname+"."+fileType;
                     //获取item中的上传文件的输入流
                     InputStream in = item.getInputStream();
                     //创建一个文件输出流
                     FileOutputStream out = new FileOutputStream(savePath + File.separator + imgname);
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
                     message = "文件上传成功！";
                     System.out.println(message);
                 }
             }

             // 数据存到数据库里
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

             // 刷新一下页面
             List<Photo> photoList=getPhotoList(albumid);
             request.getSession().setAttribute("albumid",albumid);
             request.setAttribute("photo_list", photoList);
             request.getRequestDispatcher("photoList.jsp").forward(request,response);
             // fixme 重启服务器之后才能看到新上传的图片，不然怎么做都不行？？？
         }catch (Exception e) {
             message= "文件上传失败！";
             System.out.println(message);
             e.printStackTrace();

         }
//         request.setAttribute("message",message);
//         request.getRequestDispatcher("/message.jsp").forward(request, response);

}

    /**
     * 点击图片，放大图片查看
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

            // todo 删除本地的照片
            String realpath = getServletContext().getRealPath("/") ;
            String savePath=realpath+"/upload/images/"+userid;
            File file=new File(savePath+"/"+photoname);
            if(file.exists()){
                System.out.println("delete savePath+\"/\"+photoname");
                file.delete();
            }
            // 显示图片
            List<Photo> photoList=getPhotoList(albumid);
            if(photoList.size()>0){
                // 有照片的话，就显示照片
                request.getSession().setAttribute("albumid",albumid);
                request.setAttribute("photo_list", photoList);
                request.getRequestDispatcher("photoList.jsp").forward(request,response);
            }else{
                // todo 没有照片，就提示块添加吧
            }
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
        String photoid=request.getParameter("id");

        String photoname="NewPhotoname____________";
        String introducation="NewIntroducation_______________";

        //  TODO 可以考虑加一个 移动到别的相册的选项
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
                // todo 获取列表要改成函数

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
