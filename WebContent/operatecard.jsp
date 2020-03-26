<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html >
<head>
<title>Laundry</title>
<link rel="stylesheet" href="css/style_xiyiji.css">
</head>
<style>
.ant-btn {
    line-height: 1.499;
    position: relative;
    display: inline-block;
    font-weight: 400;
    white-space: nowrap;
    text-align: center;
    background-image: none;
    border: 1px solid transparent;
    -webkit-box-shadow: 0 2px 0 rgba(0,0,0,0.015);
    box-shadow: 0 2px 0 rgba(0,0,0,0.015);
    cursor: pointer;
    -webkit-transition: all .3s cubic-bezier(.645, .045, .355, 1);
    transition: all .3s cubic-bezier(.645, .045, .355, 1);
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    -ms-touch-action: manipulation;
    touch-action: manipulation;
    height: 32px;
    padding: 0 15px;
    font-size: 14px;
    border-radius: 4px;
    color: rgba(0,0,0,0.65);
    background-color: #fff;
    border-color: #d9d9d9;
}

.ant-btn-primary {
    color: #fff;
    background-color: #1890ff;
    border-color: #1890ff;
    text-shadow: 0 -1px 0 rgba(0,0,0,0.12);
    -webkit-box-shadow: 0 2px 0 rgba(0,0,0,0.045);
    box-shadow: 0 2px 0 rgba(0,0,0,0.045);
}
.ant-btn-red {
    color: #fff;
    background-color: #FF5A44;
    border-color: #FF5A44;
    text-shadow: 0 -1px 0 rgba(0,0,0,0.12);
    -webkit-box-shadow: 0 2px 0 rgba(0,0,0,0.045);
    box-shadow: 0 2px 0 rgba(0,0,0,0.045);
}
input {
    color:#333;
    line-height:normal;
    font-family:"Microsoft YaHei",Tahoma,Verdana,SimSun;
    font-style:normal;
    font-variant:normal;
    font-size-adjust:none;
    font-stretch:normal;
    font-weight:normal;
    margin-top:0px;
    margin-bottom:0px;
    margin-left:0px;
    padding-top:4px;
    padding-right:4px;
    padding-bottom:4px;
    padding-left:4px;
    font-size:15px;
    outline-width:medium;
    outline-style:none;
    outline-color:invert;
    border-top-left-radius:3px;
    border-top-right-radius:3px;
    border-bottom-left-radius:3px;
    border-bottom-right-radius:3px;
    text-shadow:0px 1px 2px #fff;
    background-attachment:scroll;
    background-repeat:repeat-x;
    background-position-x:left;
    background-position-y:top;
    background-size:auto;
    background-origin:padding-box;
    background-clip:border-box;
    background-color:rgb(255,255,255);
    margin-right:8px;
    border-top-color:#ccc;
    border-right-color:#ccc;
    border-bottom-color:#ccc;
    border-left-color:#ccc;
    border-top-width:1px;
    border-right-width:1px;
    border-bottom-width:1px;
    border-left-width:1px;
    border-top-style:solid;
    border-right-style:solid;
    border-bottom-style:solid;
    border-left-style:solid;
}</style>
<body>
				<div>
				<form action="OperateCardServlet" method="post"> 
				<p style="font-size:16px;color:white">学号：</p><input type="text" name="id">
				<br>
      	       <p style="font-size:16px;color:white;">机器号:</p>
      	       <select name="Machinenumber"  style="width:150px; height:30px;font-size:14px">
				<option value="0" <c:if test="${Machinenumber=='0'}"></c:if>>本楼机器</option>
				<option value="1" <c:if test="${Machinenumber=='1'}"></c:if>>1号机器</option>
				<option value="2" <c:if test="${Machinenumber=='2'}"></c:if>>2号机器</option>
				<option value="3" <c:if test="${Machinenumber=='3'}"></c:if>>3号机器</option>
				<option value="4" <c:if test="${Machinenumber=='4'}"></c:if>>4号机器</option>
				<option value="5" <c:if test="${Machinenumber=='5'}"></c:if>>5号机器</option>
				<option value="6" <c:if test="${Machinenumber=='6'}"></c:if>>6号机器</option>
				<option value="7" <c:if test="${Machinenumber=='7'}"></c:if>>7号机器</option>
				<option value="8" <c:if test="${Machinenumber=='8'}"></c:if>>8号机器</option>
				<option value="30" <c:if test="${Machinenumber=='30'}"></c:if>>30号机器</option>
				<option value="31" <c:if test="${Machinenumber=='31'}"></c:if>>31号机器</option>
				<option value="32" <c:if test="${Machinenumber=='32'}"></c:if>>32号机器</option>
				<option value="33" <c:if test="${Machinenumber=='33'}"></c:if>>33号机器</option>
			</select>
	       <br>
	       <br>
	       <div style="width: 80px;float: left;margin-left: 30px"><button class="ant-btn ant-btn-red">插卡</button></div>
    </form> 
    </div>
    <br>
	<span style="color: red">${xiaoxi}</span>
<div class="main">
  <div class="l">
    <div class="l__face l__face--front">
      <div class="l__control"></div>
      <div class="l__control"></div>
      <div class="l__buttons">
        <div class="l__button"></div>
        <div class="l__button"></div>
        <div class="l__button"></div>
      </div>
      <div class="l__c1">
        <div class="l__c2">
          <div class="l__clothes">
            <div class="l__clothes-i"></div>
            <div class="l__clothes-i"></div>
          </div>
        </div>
      </div>
    </div>
    <div class="l__face l__face--back"></div>
    <div class="l__face l__face--right"></div>
    <div class="l__face l__face--left"></div>
    <div class="l__face l__face--top"></div>
    <div class="l__face l__face--bottom"></div>
  </div>
  <div class="s">
    <div class="l__face s__shadow"></div>
  </div>
</div>
</body>
</html>