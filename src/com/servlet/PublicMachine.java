package com.servlet;

import com.mysql.cj.xdevapi.DbDoc;
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
import java.util.Date;


public class PublicMachine extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        String id = (String)session.getAttribute("loginName");

        Date d = new Date();
        int h = d.getHours();
        int m = d.getMinutes();
        int t = h*60+m;

        String area = "";
        int machine_number1[] = new int[50];
        int num  = 0;
        int area1 = -1;
        try{
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from student where id = '"+ id +"'");
            if(rs.next()){
                area = rs.getString("area");
            }
            switch (area){
                case "新世纪大学生村":
                    area1 = 0;break;
                case "校内学生公寓":
                    area1 = 1;break;
                case "南区学生公寓":
                    area1 = 2;break;
            }
            ResultSet rs_m = DBconn.selectSql("select * from Machine_condition where area = " + area1+
                    " and private = 0 and state = 1");
            while(rs_m.next()){
                machine_number1[num] = rs_m.getInt("machine_number");
                num++;
            }
            int overtime[] = new int[num];
            String machine_number[] = new String[num];
            String message[] = new String[num];
            for(int i = 0;i<num;i++) {
                machine_number[i] = ""+machine_number1[i];
                ResultSet rs_ms = DBconn.selectSql("select * from usecondition where machine_number = " + machine_number1[i]);
                if(rs_ms.next()){
                    overtime[i] = rs_ms.getInt("overtime")+50;
                }
                String time = "";
                String hour="",minutes = "";
                h = overtime[i]/60;
                m = overtime[i]%60;
                if(h<10){
                    hour = "0"+h;
                }else {
                    hour = ""+h;
                }
                if(m<10){
                    minutes = "0"+m;
                }else{
                    minutes = ""+m;
                }
                time = " "+hour+":"+minutes;
                if(overtime[i]>=t){
                    message[i] = "预计结束时间：" + time;
                }else{
                    message[i] = "空闲";
                }
            }
            request.setAttribute("message",message);
            request.setAttribute("machine_number",machine_number);
            System.out.print(message);
            request.setAttribute("biaoji","1");
            request.getRequestDispatcher("CancelWindow").forward(request,response);
        }catch (SQLException e){
            e.printStackTrace();
            request.getRequestDispatcher("CancelWindow").forward(request,response);
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        Date d = new Date();
        int h = d.getHours();
        int m = d.getMinutes();
        int t = h*60+m;
        String id = (String)session.getAttribute("loginName");
        String machine_num = request.getParameter("public");
        try{
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from usecondition where id = '"+id+"'");
            if(rs.next()){
                DBconn.closeConn();
                request.setAttribute("public_message","您已在使用洗衣机，请节约公共资源");
                doGet(request,response);
            }else {
                DBconn.addUpdDel("update usecondition set usestate = '2', overtime = " + (t + 5) + ",id = '" + id + "' where machine_number = " + machine_num);
            }
            DBconn.closeConn();
            doGet(request,response);
        }catch (Exception e){
            DBconn.closeConn();
            e.printStackTrace();
        }
    }
}
