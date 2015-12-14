<%@ page import="domain.Photo" %>
<%@ page import="domain.Album" %>
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
<form action="controller?action=uploadPhoto&albumid=${albumid}" method="post" enctype="multipart/form-data">
  <p> 名字: <input type="text" name="photoname" ></p>
  <p> 图片: <input type="file" name="image" value="上传图片" ></p>
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
      String str=p.getId() + " " + p.getName() + " " + p.getIntroducation();

      String img="<img src=\""+basePath+"upload/images/"+p.getUrl()+ "\"> ";
//      String img="<img src=\"/WebConnector/"+userid+"/"+p.getName()+"\"> ";
//      String img="<img src=\""+p.getUrl()+"\">";
      String a_update="<a href=\"controller?action=updatePhoto&albumid="+albumid+"&id="+p.id+"\">编辑 </a>";
      String a_delete="<a href=\"controller?action=deletePhoto&albumid="+albumid+"&id="+p.id+"\">删除 </a>";

      out.println("<p>"+str+"<a href=\"controller?action=viewPhoto&id="+p.id+"\">查看 </a>"+a_update+a_delete+"</p>");
      out.println(img);
    }
  }else{
    out.println("暂无相册，快创建一个吧");
  }

%>
</body>
</html>
