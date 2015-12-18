<%@ page import="JavaBean.Photo" %>
<%@ page import="JavaBean.Album" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: hp
  Date: 2015/12/8
  Time: 21:53
  To change this template use File | Settings | File Templates.
--%>
<%
  String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
  <script>
    function upload(){

    }

  </script>
</head>
<body>
<form action="photo?action=uploadPhoto&albumid=${albumid}" method="post" enctype="multipart/form-data">
  <p> 名字: <input type="text" photonmae="photoname" ></p>
  <p> 图片: <input type="file" photonmae="image" value="上传图片" ></p>
  <p> <input type="submit" value="提交" ></p>
</form>
<%!
  String userid;
  String albumid;
%>
<%
  List<Photo> photoList=(List<Photo>)request.getAttribute("photo_list");
  userid=(String)request.getSession().getAttribute("userid");
  albumid=(String)request.getSession().getAttribute("albumid");
  if(photoList!=null){
    for(Photo p:photoList){
      String str=p.getPhotoid() + " " + p.getPhotonmae() + " " + p.getIntroducation();

      String img="<img src=\""+basePath+"upload/images/"+p.getUrl()+ "\"> ";

//      String img="<img src=\"/WebConnector/"+userid+"/"+p.getPhotonmae()+"\"> ";
//      String img="<img src=\""+p.getUrl()+"\">";

//      String a_update="<a href=\"photo?action=updatePhoto&albumid="+albumid+"&photoid="+p.photoid+"\">编辑 </a>";
      String a_update="<a href='photo?action=updatePhoto&albumid="+albumid+"&photoid="+p.photoid+"'>编辑 </a>";

      String a_delete="<a href=\"photo?action=deletePhoto&albumid="+albumid+"&photoid="+p.photoid+"&photoname="+p.photonmae+"\">删除 </a>";

      out.println("<p>"+str+"<a href=\"photo?action=viewPhoto&photoid="+p.photoid+"\">查看 </a>"+a_update+a_delete+"</p>");
      out.println(img);
    }
  }else{
    out.println("暂无相册，快创建一个吧");
  }

%>
</body>
</html>
