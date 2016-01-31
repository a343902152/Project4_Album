<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>WebAlbum</title>
	<link rel="stylesheet" href="./static/css/main.css">
	<script src="./static/js/main.js"></script>
	<script type="text/javascript">
		function checkForm(form){
			for(i=0;i<form.length;i++){
				if(form.elements[i].value==""){
					alert(form.elements[i].title+"不能为空.");
					form.elements[i].focus();
					return false;
				}
			}
		}

	</script>
</head>
<body>
	<div id="login_wrapper">
		<div class="login_panel">
			<a class="logo">My Photos</a>
			<form action="user?action=login" method="POST" onsubmit="return checkForm(form1)">
				<input type="text" name="userid" id="username" placeholder="username(测试账号a)">
				<input type="password" name="password" id="password" placeholder="password(测试密码1)">
				<input type="submit" name="submit" id="submit" value="登录">
			</form>
		</div>
	</div>
</body>
</html>