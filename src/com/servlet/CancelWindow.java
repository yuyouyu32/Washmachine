package com.servlet;

import com.util.DBconn;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class CancelWindow extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
       String biaoji = (String) request.getAttribute("biaoji");
       if (biaoji==null) {
        	request.getRequestDispatcher("PublicMachine").forward(request, response);
        }else {
    request.getRequestDispatcher("SelectTimeTable").forward(request,response);}
//       request.getRequestDispatcher("PublicMachine").forward(request,response);
        
    }
    


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        String id = (String)session.getAttribute("loginName");
        String[] cancel = request.getParameterValues("cancel");
        if(cancel!=null) {
        }else {
            doGet(request,response);
            return;
        }
        try{
            DBconn.init();
            for(int i = 0;i < cancel.length;i++) {
                DBconn.addUpdDel("delete from timetable where id = '"+id+"' and time_part = "+cancel[i]);
            }
            DBconn.closeConn();
            doGet(request,response);
        }catch (Exception e){
            DBconn.closeConn();
            e.printStackTrace();
        }
    }

}
