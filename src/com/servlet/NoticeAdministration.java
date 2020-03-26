package com.servlet;

import com.mysql.cj.xdevapi.DbDoc;
import com.util.DBconn;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class NoticeAdministration extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        int index = 0;
        String message1[] =  new String[50];
        int number1[] = new int[50];
        String time1[] = new String[50];
        try{
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from notice");
            while(rs.next()){
                message1[index] = rs.getString("content");
                number1[index] = rs.getInt("number");
                time1[index] = rs.getString("time");
                index++;
            }
            String message[] = new String[index];
            String number[] = new String[index];
            String time[] = new String[index];
            for(int i = 0;i<index;i++){
                message[i] = message1[i];
                number[i] = ""+number1[i];
                time[i] = ""+time1[i];
            }
            DBconn.closeConn();
            Map<String,String> map = new HashMap<String, String>();
            for(int i = 0;i <index;i++){
                map.put(time[i],message[i]);
            }
            request.setAttribute("map",map);
            System.out.println(map);
            request.getRequestDispatcher("PublicMachine").forward(request,response);
        }catch (SQLException e){
            DBconn.closeConn();
            e.printStackTrace();
        }
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
        String[] notice_num = request.getParameterValues("notice");
        try{
            DBconn.init();
            for(int i = 0;i<notice_num.length;i++) {
                DBconn.addUpdDel("delete from notice where number = " + notice_num[i]);
            }
            DBconn.closeConn();
        }catch (Exception e){
            DBconn.closeConn();
            e.printStackTrace();
        }
        doGet(request,response);
    }
}
