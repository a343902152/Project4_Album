<!DOCTYPE html>
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
			<a class="logout" href=""></a> <!-- 退出登录，修改url -->
		</div>
	</header>
	
	<div class="main">

		<div class="wrapper">
			<div class="wrapper_help">
				<!-- 一张照片 START -->
				<!-- 这两个是相同的url。一个用来显示缩略图一个用来显示大图 -->
				<div class="card" style="background-image: url( './static/data/test.jpg' );" href="" onclick="show_full('./static/data/test.jpg')">
					<div class="controller_bar">
						<div class="fl_name">some villyhahahahah</div> <!-- 照片名/文件名 -->
						<a class="delete" href="/delete" onclick="event.cancelBubble=true;"></a> <!-- 删除按钮，把href替换成你的删除指令url -->
						<div class="clear"></div>
					</div>
				</div>
				<!-- 一张照片 END -->

				<!-- 在这里循环上面的块 -->

				<div class="card add" onclick="add_img();"></div>
				<div class="clear"></div>
			</div>
			<div class="clear"></div>
		</div>

		<div id="full" style="visibility: hidden;" onclick="hide_full();">
			<img id="full_img">
		</div>
	</div>

	<!-- 这是添加图片使用的表单，不要修改action以外的属性 -->
	<form style="display:none;" method='post' action="/" id='fm' enctype="multipart/form-data" >
		<input id="img_file" name="img_file" type="file" onChange="document.getElementById('submit').click();" accept="image/*"/>
		<input type="submit" value="submit" id="submit" />
	</form>
	<!-- 表单结束 -->

</body>
</html>