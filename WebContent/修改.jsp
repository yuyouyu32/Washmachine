<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

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
		<li><a href="#gonggong" class="page-scroll">使用公用洗衣机</a></li>
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
<a href="#" data-toggle="modal" data-target="#myModal1">公告 </a>


<script type="text/javascript">
	function open_win() {
		window.showModalDialog("appointment.jsp", null, 'dialogWidth:' + 1000 + 'px;dialogHeight:' + 400 + 'px;help:no;unadorned:no;resizable:no;status:no;scrollbars：no')
	}
</script>


</div>
</div>
</div>
<!-- /features -->
<div class="modal video-modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModal">
		<div class="modal-dialog" role="document">
        <div class="modal-content">
        <div class="modal-header">
            <h4 class="modal-title">查看可预约时间</h4>
        </div>
        <div><% String[] res = (String [])request.getAttribute("appoint_message");
        if (res != null && res.length > 0){
            for (int i = 0; i < res.length; i ++){
           %>  <p> <%out.print(res[i]); %></p>    <% 
            }
        }

        
 %>
 
 </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        </div>
        </div>
        </div>
</div>

<div class="modal video-modal fade" id="myModal1" tabindex="-1" role="dialog" aria-labelledby="myModal">
		<div class="modal-dialog" role="document">
        <div class="modal-content">
        <div class="modal-header">
            <h4 class="modal-title">公告</h4>
        </div>
        <c:forEach items="${map}" var="list">
	       <h5>${list["key"] }</h5>
	        <p>${list["value"]}</p>
        </c:forEach>
       
        
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
        </div>
        </div>
</div>

<div class="modal video-modal fade" id="myModal2" tabindex="-1" role="dialog" aria-labelledby="myModal">
		<div class="modal-dialog" role="document">
        <div class="modal-content">
        <div class="modal-header">
            <h4 class="modal-title">当前洗衣机使用情况</h4>
        </div>
   
        <form action="" method="post">
    <%
        String message[] = (String [])request.getAttribute("message");
        String machine_number[] = (String [])request.getAttribute("machine_number");
        String public_message = (String)request.getAttribute("public_message");
        if(machine_number!=null && machine_number.length>0){
            int key1 = 0;
            for(int i = 0;i<machine_number.length;i++){
                if(message[i] == "空闲"){
                 %> 
                  <input name="public" type="radio" id="radio" value="
                           <%=machine_number[i]%>" /><%=machine_number[i]+"||"+message[i]%><br>  <%
                    key1 = 1;
                }else{
                    out.println("<input type=\"text\" name=\"app\" value=\"" +machine_number[i]+"||"+message[i] + "\" readonly /><br>");
                }
            }
            if(public_message!=null) {
                out.print(public_message + "<br>");
            }
            if(key1 == 1){
                out.print("<input type=\"submit\" value=\"确定立即使用选中洗衣机\">\n");
            }
        }
    %>
    </form>
      
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
        </div>
        </div>
</div>
<!-- Order Form-->

<div class="w3ls_frmrt1" id="appointment">
	<div class="container">
	<h3>预约</h3>
	<div class="book-form">
		<form action="Appointment" method="post">
			<div class="col-md-3 form-time-w3layouts">
				<label><i class="fa fa-user" aria-hidden="true"></i> 小时：</label>
				<input type="text" placeholder="Name" name="time_h" required="">
			</div>
			<div class="col-md-3 form-date-w3-agileits">
				<label><i class="fa fa-map-marker" aria-hidden="true"></i>分：</label>
				<input type="text" name="time_m" placeholder="Name" required="">
			</div>
			<div class="col-md-3 form-left-agileits-submit">
				  <input type="submit" value="预约">
			</div>
			</form>
			

			<div class="col-md-3 form-left-agileits-submit">
				  <input type="submit" value="查看可预约时间"data-toggle="modal" data-target="#myModal">
			</div>
			
			<div class="clearfix"></div>

	</div>
	</div>
</div>
<!-- /Order Form-->

<div class="w3ls_frmrt" id="changeinformation">
	<div class="container">
	<h3>修改个人信息</h3>
	<div class="book-form">
		<form action="ChangeInformation" method="post">
			<div class="col-md-3 form-left-agileits-w3layouts ">
										<label><i class="fa fa-home" aria-hidden="true"></i> 公寓区</label>
										<select name="building" class="form-control">
											<option value="0">新世纪大学生村</option>
											<option value="1">校内学生公寓</option>
											<option value="2">南区学生公寓</option>
										</select>
								</div>
								<div class="col-md-3 form-date-w3-agileits">
				<label><i class="fa fa-map-marker" aria-hidden="true"></i>栋</label>
				<input type="text" name="dong" placeholder="Name" required="">
			</div>
			<div class="col-md-3 form-date-w3-agileits">
				<label><i class="fa fa-map-marker" aria-hidden="true"></i>楼号</label>
				<input type="text" name="hao" placeholder="Name" required="">
			</div>
			<div class="col-md-3 form-left-agileits-submit">
				  <input type="submit" value="确定">
			</div>
			
			<div class="clearfix"></div>
		</form>
	</div>
	</div>
</div>
<!-- /Order Form-->




	
<!-- /Order Form-->
<!-- Services-->
<div class="kuohao">
<div class="services" id="cancel">
<div class="container">
<h3>取消预约</h3>
<h4>您已经预约的时间：</h4><br>
<form action="CancelWindow" method="post">
<%
    String cancel_key = (String)request.getAttribute("cancel_key");

    String[] key = (String [])request.getAttribute("key");
    String[] starttime = (String [])request.getAttribute("starttime");
    String[] res_alapp = (String [])request.getAttribute("alapp");

     System.out.println("cancelkey:"+cancel_key);



//res 数组里是时间段 格式是xx:xx~xx:xx已经用0补过个位数
    if (res != null && res_alapp.length > 0){
        for (int i = 0; i < res_alapp.length; i ++){
        
                out.println("<input name=\"cancel\" type=\"checkbox\" id=\"checkbox1\" value=\""+
                        starttime[i]+"\" />"+res_alapp[i]+"<br>");
            
        }
    }
 
    	out.print("<input style=\"width:250px;\" type=\"submit\" value=\"确定取消选中预约\">\n");
    
%>
</form>
</div>
</div>

</div>
<div class="kuohao2">
<div class="services" id="gonggong">
<div class="container">
<h3>使用公共洗衣机</h3>

<form action="PublicMachine" method="post">
	<div class="sub3">
    <%
        String pb_message[] = (String [])request.getAttribute("message");
        String pb_machine_number[] = (String [])request.getAttribute("machine_number");
        String pb_mes = (String)request.getAttribute("public_message");
        if(pb_machine_number!=null && pb_machine_number.length>0){
            int key1 = 0;
            for(int i = 0;i<pb_machine_number.length;i++){
                if(pb_message[i] == "空闲"){
                    out.print("<input name=\"public\" type=\"radio\" id=\"radio\" value=\""+
                            pb_machine_number[i]+"\" />"+pb_machine_number[i]+"号机器： "+pb_message[i]+"<br>");
                    key1 = 1;
                }else{
                    out.println("<p>" +pb_machine_number[i]+"号机器：	"+pb_message[i] + "</p><br>");
                }
            }
            if(pb_mes!=null) {
                out.print("<p> "+pb_mes +"</p>"+ "<br>");
            }
            if(key1 == 1){
                out.print("<input type=\"submit\" value=\"确定立即使用选中洗衣机\">\n");
            }
        }
    %>
    </div>
    </form>


    

</div>
</div>
</div>

<!-- footer -->
	<div class="footer" id="footer">
		<div class="container">
			<div class="list">
				<div class="col-md-3 wthree_fl">
					<a href="#">感谢使用</a>
					</div>
					
				<div class="col-md-6 wthree_fc">
					<h6> JavaSE Project</h6>
					<h6> Shanghai University </h6>
					<h6> CHINA </h6>
		
					</div>
					
					<div class="col-md-3 wthree_fr">
					<h6></h6>
					<h6><a href="mailto:service@decorate.com"></a></h6>
					</div>
				
				
			</div>
		</div>
	</div>
	<!-- //footer -->
<div class="copyright">
		<div class="container">
			<p>Copyright &copy; 2020上海大学JavaSE项目组（于烨泳、王韫之、何云东、宗祺、施熠晨）</p>
		</div>
	</div>

<script src="js/jquery.min.js"></script>
<script src="js/jquery.easing.min.js"></script>
<script src="js/move-top.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/grayscale.js"></script>
<script src="js/SmoothScroll.min.js"></script>
<!-- flexSlider -->
	<script src="js/owl.carousel.js"></script>
							        <script>
							    $(document).ready(function() {
							      $("#owl-demo").owlCarousel({
							        items :2,
									itemsDesktop : [800,2],
									itemsDesktopSmall : [414,1],
							        lazyLoad : true,
							        autoPlay : true,
							        navigation :true,
									
							        navigationText :  false,
							        pagination : true,
									
							      });
								  
							    });
							    </script>

<!-- //flexSlider -->
 <!-- /gallery -->
    <script src="js/jquery.tools.min.js"></script>
    <script src="js/jquery.mobile.custom.min.js"></script>
    <script src="js/jquery.cm-overlay.js"></script>

    <script>
        $(document).ready(function () {
            $('.cm-overlay').cmOverlay();
        });
    </script>
    <!-- //gallery -->

<!-- Move-to-top-->
<script type="text/javascript">
$(document).ready(function() {
var defaults = {
containerID: 'toTop', // fading element id
containerHoverID: 'toTopHover', // fading element hover id
scrollSpeed: 1200,
easingType: 'linear' 
};
$().UItoTop({ easingType: 'easeOutQuart' });
});
</script>


</body>

<%
String ci_xiaoxi = (String)request.getAttribute("ci_xiaoxi");
if(ci_xiaoxi!=null){ %>
<script type="text/javascript" language="javascript">
        alert("<%=ci_xiaoxi%>");
</script>
<%}
%>

<%
String ap_xiaoxi = (String)request.getAttribute("ap_xiaoxi");
if(ap_xiaoxi!=null){ %>
<script type="text/javascript" language="javascript">
        alert("<%=ap_xiaoxi%>");
</script>
<%}
%>



</html>