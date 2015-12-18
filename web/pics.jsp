<%@ page import="JavaBean.Photo" %>
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
			<a class="logo">My Photos</a>
			<a class="logout" href="user?action=logout"></a> <!-- 退出登录，修改url -->
		</div>
	</header>
	<%!
		String userid;
		String albumid;
		String albumname;
	%>

	<%
		userid=(String)request.getSession().getAttribute("userid");
		albumid=(String)request.getSession().getAttribute("albumid");
		albumname=(String)request.getSession().getAttribute("album_name");

		System.out.println(albumid+","+albumname);
	%>

	<div class="main">
		<div class="wrapper">
			<nav>
				<!-- 在这里放上退回链接 -->
				<%
					String s1="<a class=\"nav_albums\" href='albums.jsp'>返回</a>";
					String s2="<a class=\"nav_now\">"+albumname+"</a>";
					out.println(s1+s2);
				%>
				<%--<a class="nav_albums" href="albums.jsp">相册</a>--%>
				<%--<a class="nav_now">${albumname}</a>--%>
				<div class="clear"></div>
			</nav>

			<div class="wrapper_help">
				<%
					// 打印所有照片
					List<Photo> photoList=(List<Photo>)request.getAttribute("photo_list");
					if(photoList!=null){
						for(Photo p:photoList){
							String s_begin="<div class=\"card\" style=\"background-image: url('"+basePath+"upload/images/"+p.getUrl()+ "');\" " +
									"onclick=\"show_full('"+basePath+"upload/images/"+p.getUrl()+ "')\">\n" +
									"\t\t\t\t\t<div class=\"img\"></div>\n" +
									"\t\t\t\t\t<div class=\"controller_bar\">";
							String sname="<div class=\"fl_name\">"+p.getPhotoname()+"</div> ";
							String sdelete="<a class=\"delete\" href='photo?action=deletePhoto&albumid="
									+albumid+"&id="+p.getPhotoid()+"' onclick=\"event.cancelBubble=true;\"></a> ";
							String supdate="<a class=\"edit\" onclick=\"event.cancelBubble=true;" +
									"edit_name('photo?action=updatePhoto&albumid="+albumid+"&id="+p.getPhotoid()+"');\"></a> ";
							String s_end="<div class=\"clear\"></div>\n" +
									"\t\t\t\t\t</div>\n" +
									"\t\t\t\t</div>";
							out.println(s_begin+sname+sdelete+supdate+s_end);
						}
					}
					System.out.println(albumname);
				%>

				<div class="card add" onclick="add_img();"></div>
				<div class="clear"></div>
			</div>
			<div class="clear"></div>
		</div>

		<div id="full" class="full" style="visibility: hidden;" onclick="hide_full();">
			<img id="full_img" class="full_img">
		</div>

		<div id="name_editor" style="visibility: hidden;" onclick="hide_full_form();">
			<!-- 这是编辑文件名/相册名的表单，你在本页35行edit_name()中传入的url会在这个表单显示的时候放到这个action里面 -->
			<form method='post' action="" id='name_edit_form' >
				<input id="new_name" name="new_name" type="text" placeholder="输入新的文件名" onclick="event.cancelBubble=true;"/>
				<input id="submit_new_name" type="submit" value="submit" onclick="event.cancelBubble=true;" />
			</form>
		</div>
		
	</div>

	<!-- 这是添加图片使用的表单，始终隐藏，不要修改action以外的属性 -->
	<form style="display:none;" method='post'
		  action="photo?action=uploadPhoto&albumid=${albumid}"
		  enctype="multipart/form-data" >
		<input id="img_file" name="img_file" type="file" onChange="document.getElementById('submit').click();" accept="image/*"/>
		<input type="submit" value="submit" id="submit" />
	</form>
	<!-- 表单结束 -->


</body>
</html>