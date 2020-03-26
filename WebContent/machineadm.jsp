<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<title>上海大学洗衣机预约平台</title>
<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" media="all">
<link href="css/font-awesome.min.css" rel="stylesheet" type="text/css" media="all">
<link href="css/owl.carousel.css" rel="stylesheet">
<link rel="stylesheet" href="css/lightbox.css">
<link type="text/css" rel="stylesheet" href="css/cm-overlay.css" />
<link href="css/style2.css" rel="stylesheet" type="text/css" media="all"/>

</head>

<body>
<!-- banner -->
<div class="w3l_banner">

<div class="w3_bandwn">
<div class="container">
<div class="col-md-3 w3_l">
</div>
<div class="col-md-6 w3_c">
</div>
<div class="col-md-3 w3_r">

</div>
<div class="clearfix"></div>
</div>
</div>
<nav class="navbar navbar-default">
  <div class="container">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
     <h1><a class="navbar-brand" href="#">欢迎使用</a></h1>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav">
        <li><a href="#index.html" class="page-scroll">查看公告</a></li>
        <li><a href="#appointment" class="page-scroll">预约</a></li>
        <li><a href="#changeinformation" class="page-scroll">修改个人信息</a></li>
		<li><a href="#cancel" class="page-scroll">取消预约</a></li>
		<li><a href="#gallery" class="page-scroll">使用公用洗衣机</a></li>
		<li><a href="index.jsp" class="page-scroll">返回登陆页</a></li>
		<li><a class="page-scroll">${dlxiaoxi}</a></li>
  
     
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
<div class="w3l_bandwn">
 <h2>上 海 大 学 洗 衣 机 预 约 平 台</h2>
 <div class="about-p text-center">
<span class="sub-title"></span>
<span class="fa fa-star" aria-hidden="true"></span>
<span class="sub-title"></span>
</div>
<h3>Washing Clothes</h3>
<div class="sub3">
<form action="SelectMachine" method="get">
    <span style="font-size:16px;color:white">住宿区：</span><select name = "building">
        <option value="新世纪大学生村" selected>新世纪大学生村</option>
        <option value="校内学生公寓">校内学生公寓</option>
        <option value="南区学生公寓">南区学生公寓</option>
    </select><br>
<span style="font-size:16px;color:white">楼号：</span><input type="text" value="" name="building_num"><br>
<%--    机号：<input type="text" value="" name="machine_num"><br>--%>
    <input type="submit" value="查询机器信息"/>
</form>

<form action="SelectMachine" method="post">
<%
    String[] s_machine_num = (String [])request.getAttribute("s_machine_num");
    String[] state = (String [])request.getAttribute("state");
    String[] pr = (String [])request.getAttribute("pr");
    String s1 = "";String s2 = "";String p1 ="";String p2 = "";
    if (s_machine_num != null && s_machine_num.length > 0) {
        for (int i = 0; i < s_machine_num.length; i++) {
            switch (state[i]) {
                case "0":
                    s1 = "selected";
                    s2 = "";
                    break;
                case "1":
                    s1 = "";
                    s2 = "selected";
                    break;
            }
            switch (pr[i]) {
                case "0":
                    p1 = "selected";
                    p2 = "";
                    break;
                case "1":
                    p1 = "";
                    p2 = "selected";
                    break;
            }
            out.print("<span style=\"font-size:16px;color:white\">机号：</span>"+"<input style=\"color:black;\" name = \"set_machine_num\" value = \""+s_machine_num[i] + "\"readonly/>        |");
            out.print("<select name = \"set_state\">\n" +
                    "    <option style=\"black\" value=0 " + s1 + ">故障</option>\n" +
                    "    <option style=\"black\" value=1 " + s2 + ">正常</option>\n" +
                    "</select>");
            out.print("<select name = \"set_private\">\n" +
                    "    <option value=0 " + p1 + ">公开使用</option>\n" +
                    "    <option value=1 " + p2 + ">仅限本楼</option>\n" +
                    "</select><br>");
        }
        out.print("<input type=\"submit\" value=\"确定修改\"/>");
    }
%>

</form>

<form action="MachineAdministration" method="get">
    <%--        楼号：<input type="text" value="" name="building_num2"><br>--%>
    <%--        机号：<input type="text" value="" name="floor_num2"><br>--%>
    <input type="submit" value="显示故障机器"/>
</form>
<form>
<%
    String[] machine_num = (String [])request.getAttribute("machine_num");
    String[] build_num = (String [])request.getAttribute("build_num");
    String[] area = (String [])request.getAttribute("area");
    out.print("<form action=\"MachineAdministration\" method=\"post\">");
    if (area != null && area.length > 0){
        for (int i = 0; i < area.length; i ++){
            out.print("<input name=\"checkboxname\" type=\"checkbox\" id=\"checkbox1\" value=\""+
                    machine_num[i]+"\" /><span style=\"font-size:16px;color:white\">机号："+machine_num[i]+"  |  楼号："+build_num[i]+"  |  住宿区："+area[i]+"</span><br>");
        }
    }
%>
</form>
</div>
</div>
</div>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>


</body>
</html>

