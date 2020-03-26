package com.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.dao.UserDao;
import com.dao.UserDaoImpl;
import com.mysql.cj.xdevapi.DbDoc;
import com.util.DBconn;

public class MachineAdministration extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        int [] machine_num1 = new int[50];
        int [] build_num1 = new int[50];
        int [] area1 = new int[50];
        int trouble_num = 0;
        try{
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from Machine_condition \n" +
                    "where state = 0");
            while (rs.next()){
               machine_num1[trouble_num]=rs.getInt("machine_number");
               build_num1[trouble_num] = rs.getInt("build_num");
               area1[trouble_num] = rs.getInt("area");
               trouble_num++;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        String [] machine_num = new String[trouble_num];
        String  [] build_num = new String[trouble_num];
        String [] area = new String[trouble_num];
        for(int i = 0;i < trouble_num;i++){
            machine_num[i] = ""+machine_num1[i];
            build_num[i] =""+ build_num1[i];
            switch (area1[i]){
                case 0:
                    area[i] = "新世纪大学村";break;
                case 1:
                    area[i] = "校内学生公寓";break;
                case 2:
                    area[i] = "南区学生公寓";break;
            }
        }
        DBconn.closeConn();
        request.setAttribute("machine_num",machine_num);
        request.setAttribute("build_num",build_num);
        request.setAttribute("area",area);
        request.getRequestDispatcher("/machineadm.jsp").forward(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        String str[] = request.getParameterValues("checkboxname");
        if(str!=null) {
        }else {
            doGet(request,response);
            return;
        }
        try{
            DBconn.init();
            for (int i = 0; i < str.length; i++) {
                DBconn.addUpdDel("update Machine_condition set state = 1 where machine_number ="+str[i]);
                DBconn.addUpdDel("update timetable set state = 0 \n" +
                        "where machine_number = " + str[i] +
                        " and state != -1;");
            }
            DBconn.closeConn();
            doGet(request,response);
            return;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
