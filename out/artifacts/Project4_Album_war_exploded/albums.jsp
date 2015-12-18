<%@ page import="JavaBean.Album" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>WebAlbum</title>
	<link rel="stylesheet" href="./static/css/main.css">
	<script src="./static/js/main.js"></script>
</head>
<body>
	<header>
		<div class="wrapper">
			<a class="logo">My Albums</a>
			<a class="logout" href="user?action=logout"></a> <!-- 退出登录，修改url -->
		</div>
	</header>
	<%!
		String userid;
	%>
	<div class="main">
		<div class="wrapper">
			<div class="wrapper_help">
				<%
					userid=(String)request.getSession().getAttribute("userid");
					List<Album> albumList=(List<Album>)request.getSession().getAttribute("album_list");
					if(albumList!=null){
						System.out.println("albumlist,userid="+userid);
						for(Album album:albumList){
							String s_begin="<div class=\"card album\" style=\"" +
									"background-image: url('"+basePath+"upload/images/"+album.getFirstImgUrl()+"');\" " +
									"onclick=\"window.location.href='album?action=viewAlbum&id="+album.getId()+"';\">\n" +
									"\t\t\t\t\t<div class=\"img\"></div>\n" +
									"\t\t\t\t\t<div class=\"controller_bar\">";
							String sname="<div class=\"fl_name\">"+album.getName()+"</div> ";
							String sdelete="<a class=\"delete\" href='album?action=deleteAlbum&id="+album.getId()+
									"' onclick=\"event.cancelBubble=true;\"></a> ";
							String supdate="<a class=\"edit\" onclick=\"event.cancelBubble=true;edit_name('album?action=updateAlbum&id="+album.getId()+
									"');\"></a> ";
							String s_end="<div class=\"clear\"></div>\n" +
									"\t\t\t\t\t</div>\n" +
									"\t\t\t\t</div>";
							out.println(s_begin+sname+sdelete+supdate+s_end);
						}
					}
				%>
				<div class="card add" onclick="edit_name('album?action=addAlbum&userid=${userid}');"></div>
				<div class="clear"></div>
			</div>
			<div class="clear"></div>
		</div>

		<div id="full" class="full" style="visibility: hidden;" onclick="hide_full();">
			<img id="full_img" class="full_img">
		</div>

		<div id="name_editor" style="visibility: hidden;" onclick="hide_full_form();">
			<!-- 这是编辑相册名的表单，edit_name()中传入的url会在这个表单显示的时候放到这个action里面 -->
			<form method='post' action="" id='name_edit_form' >
				<input id="new_name" name="aname" type="text" placeholder="输入相册名" onclick="event.cancelBubble=true;"/>
				<input id="submit_new_name" type="submit" value="submit" onclick="event.cancelBubble=true;" />
			</form>
		</div>
	</div>

	<!-- 这是添加图片使用的表单，始终隐藏，不要修改action以外的属性 -->
	<form style="display:none;" method='post' action="/">
		<input id="img_file" name="img_file" type="file" onChange="document.getElementById('submit').click();" accept="image/*"/>
		<input type="submit" value="submit" id="submit" />
	</form>
	<!-- 表单结束 -->


</body>
</html>