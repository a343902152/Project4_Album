<%--
  Created by IntelliJ IDEA.
  User: hp
  Date: 2015/12/8
  Time: 20:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title></title>
    <script type="text/javascript" src="JS/jquery-1.9.1.js"></script>
    <script type="text/javascript">
      localStorage.userid;

      function checkForm(form){
        for(i=0;i<form.length;i++){
          if(form.elements[i].value==""){
            alert(form.elements[i].title+"不能为空.");
            form.elements[i].focus();
            return false;
          }
        }
      }

      function btnlogin(form){
        for(i=0;i<form.length;i++){
          if(form.elements[i].value==""){
            alert(form.elements[i].title+"不能为空.");
            form.elements[i].focus();
            return false;
          }
        }
          document.form1.action="controller?action=login";
          document.form1.submit();
      }
      function btnregister(){
        alert("register");

      }
    </script>
  </head>
  <body>

  <form name="form1" method="post" >
    <tr>
      <td>用户名: </td>
      <td><input title="用户名" type="text" name="userid"> </td>
    </tr>
    <tr>
      <td>密 码: </td>
      <td><input title="密码" type="password" name="password"></td>
    </tr>
    <tr>
      <td colspan="2">
        <input type="button" value="登 录" onclick="btnlogin(form1)">
        <input type="submit" value="注 册" onclick="btnregister()">
      </td>
    </tr>
  </form>

  <%
    String info=(String)request.getAttribute("info");
    if(info!=null) {
      out.println(info);
      request.removeAttribute("userid");
    }
    request.removeAttribute("info");
  %>

  </body>
</html>
