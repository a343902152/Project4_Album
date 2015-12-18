<%@ page import="JavaBean.Album" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: hp
  Date: 2015/12/8
  Time: 21:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>

</head>
<body>
  <%!
    String userid;
  %>
  <%--FIXME 怎么记录用户id？ 怎么知道是哪个用户在操作？？--%>
  <a href="album?action=addAlbum&userid=${userid}">添加相册</a>

  <%--<c:forEach items="${ album_list}" var="album">--%>
    <%--<tr>--%>
      <%--<td>${album.photoid}</td>--%>
    <%--</tr>--%>
  <%--</c:forEach>--%>

  <%
    List<Album> albumList=(List<Album>)request.getAttribute("album_list");
    if(albumList!=null){
      userid=(String)request.getSession().getAttribute("userid");
      System.out.println("albumlist,userid="+userid);
      for(Album album:albumList){
        String str=album.getId() + " " + album.getName() + " " + album.getIntroducation();

//        String a0="<a href=\"album?action=updateAlbum&userid="+userid+"&photoid="+album.photoid+"\">编辑 </a>";
//        String a1="<a href=\"album?action=viewAlbum&userid="+userid+"&photoid="+album.photoid+"\">查看 </a>";
//        String a2="<a href=\"album?action=deleteAlbum&userid="+userid+"&photoid="+album.photoid+"\">删除 </a>";

        String a0="<a href=\"album?action=updateAlbum&photoid="+album.getId()+"\">编辑 </a>";
        String a1="<a href=\"album?action=viewAlbum&photoid="+album.getId()+"\">查看 </a>";
        String a2="<a href=\"album?action=deleteAlbum&photoid="+album.getId()+"\">删除 </a>";

        out.println("<p>"+str+a0+a1+a2+"</p>");
      }
    }else{
      out.println("暂无相册，快创建一个吧");
    }

  %>
</body>
</html>
