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
<link href="css/style.css" rel="stylesheet" type="text/css" media="all"/>

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
<div class="agile_dwng">
<script type="text/javascript">
	function open_win() {
		window.showModalDialog("appointment.jsp", null, 'dialogWidth:' + 1000 + 'px;dialogHeight:' + 400 + 'px;help:no;unadorned:no;resizable:no;status:no;scrollbars：no')
	}
</script>
</div>
</div>
</div>
${xiaoxi} <br>


<div class="w3ls_frmrt">
	<div class="container">
	<div class="sub2">
	<h3>公告管理</h3>
	<form action="NoticeAdministration" method="post">
	<%
    String message[] = (String[])request.getAttribute("message");
    String time[] = (String[])request.getAttribute("time");
    String number[] = (String[])request.getAttribute("number");
    if(message!=null&&message.length>0){
        for(int i = 0;i<message.length;i++){
            out.print("<input name=\"notice\" type=\"checkbox\" id=\"checkbox1\" value=\""+number[i]+
                    "\" />"+ number[i]+": "+time[i]+"||"+message[i]+"<br>"
            );
        }
        out.print(" <input type=\"submit\"value=\"确定删除\">");
    }
	%>
	</form>

<form action="NoticePublish" method="post">
    <input type="text" value="" name="content">
    <input type="submit" value="确定发布">
</form>
</div>
</div>					
</div>				
						
						
			
<div class="w3ls_frmrt">
	<div class="container">
	<div class="sub2">
	<h3>机器管理</h3>
<form action="MachineAdministration" method="get">
    <input type="submit" value="进入机器管理"/>
</form>
	</div>
	</div>
</div>
</body>
</html>