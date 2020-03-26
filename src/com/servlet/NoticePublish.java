package com.servlet;

import com.util.DBconn;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NoticePublish extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        request.setCharacterEncoding("utf-8");
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        Date d = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String time = ft.format(d);
        String content = request.getParameter("content");
        String number = "";
        try{
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select max(number) n from notice");
            if(rs.next()){
                number = ""+(rs.getInt("n")+1);
            }
            DBconn.addUpdDel("insert into notice values (" + number+",'"+time+"',0,00000000,'"+content+"')");
            DBconn.init();
        }catch (SQLException e){
            DBconn.init();
            e.printStackTrace();
        }
        session.setAttribute("dlxiaoxi", "欢迎用户"+((String)session.getAttribute("loginName"))); //向request域中放置信息
        String id;
        id = (String)session.getAttribute("loginName");
		try{
			DBconn.init();
			ResultSet rs = DBconn.selectSql("select * from usertable \n" +
					"where id = '" + id +"'");
			if(rs.next()){
				DBconn.closeConn();
				request.getRequestDispatcher("/Administration.jsp").forward(request, response);//转发到成功页面
			}else{
				DBconn.closeConn();
				request.getRequestDispatcher("PublicMachine").forward(request, response);

//			request.getRequestDispatcher("SelectTimeTable").forward(request, response);
			}
		}catch (SQLException e){
			e.printStackTrace();
			DBconn.closeConn();
			request.setAttribute("xiaoxi", "服务器未知错误");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
    }
}
