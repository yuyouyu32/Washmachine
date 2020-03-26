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


public class ChangeInformation extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        System.out.println("doget");
        String area = "";
        String build = "";
        String NO = "";
        String id = (String) session.getAttribute("loginName");
        String sql = "select * from student where id = '"+id+"'";
        try {
            DBconn.init();
            ResultSet rs = DBconn.selectSql(sql);
            if (rs.next()) {
                area = rs.getString("area");
                build = rs.getString("build");
                NO = rs.getString("NO");
            }
            DBconn.closeConn();
            request.setAttribute("area",area);
            request.setAttribute("build",build);
            request.setAttribute("NO",NO);
            request.getRequestDispatcher("/changeinformation.jsp").forward(request, response);
        } catch (SQLException e) {
            DBconn.closeConn();
            e.printStackTrace();
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("ci_xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        System.out.println("CIdopost");
        String id = (String) session.getAttribute("loginName");
        String building = request.getParameter("building");
        int bd = Integer.parseInt(building);
        System.out.println(bd);
        switch(bd) {
        case 0: {building ="新世纪大学生村";break;}
        
        case 1: {building = "校内学生公寓";break;}
        case 2: {building = "南区学生公寓";break;}
        		}
        String dong = request.getParameter("dong");
        String hao = request.getParameter("hao");
        System.out.println("building:"+building);
        try {
            DBconn.init();
            String sql = "select * from student where id = '"+id+"'";

            ResultSet rs = DBconn.selectSql(sql);

            if (rs.next()) {
                String SQL= "update student set area = '"+building+"', build = '"+dong+"',NO = '"+hao+
                        "' where id = '"+id+"'";
                DBconn.addUpdDel(SQL);
//					response.getWriter().write("注册成功");
                DBconn.closeConn();
                System.out.println("CI做好了");

                request.setAttribute("ci_xiaoxi", "注册成功");

                request.getRequestDispatcher("/修改.jsp").forward(request,response);
            } else {
                request.setAttribute("ci_xiaoxi", "错误，请重新注册");
                request.getRequestDispatcher("/修改.jsp").forward(request,response);
            }
        } catch ( SQLException e) {
            DBconn.closeConn();
            e.printStackTrace();
        }
    }
}
