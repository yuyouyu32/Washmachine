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

public class SelectMachine extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        String building = request.getParameter("building");
        String build_num = request.getParameter("building_num");
        int s[] = new int[50];int p[] = new int[50]; //机器状态 是否私有
        int m_num[] = new int [50];
        int machine_number = 0;
        int area = 0;
        switch (building){
            case "新世纪大学生村":
                area = 0;break;
            case "校内学生公寓":
                area = 1;break;
            case "南区学生公寓":
                area = 2;break;
        }
        ResultSet rs = null;
        if(build_num == ""){ // 只输入了学生区
            try{
                DBconn.init();
                rs = DBconn.selectSql("select * from Machine_condition where area = "+area);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try{
                DBconn.init();
                rs = DBconn.selectSql("select * from Machine_condition where area = "+area+" and build_num = "+build_num);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            while (rs.next()) {
                m_num[machine_number] = rs.getInt("machine_number");
                s[machine_number] = rs.getInt("state");
                p[machine_number] = rs.getInt("private");
                machine_number++;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        String machine_num[] = new String[machine_number];
        String state[] = new String[machine_number];
        String pr[] = new String[machine_number];
        for(int i = 0;i < machine_number;i++){
            machine_num[i] = ""+m_num[i];
            state[i] = ""+s[i];
            pr[i]=""+p[i];
        }
        DBconn.closeConn();
        request.setAttribute("s_machine_num",machine_num);
        request.setAttribute("state",state);
        request.setAttribute("pr",pr);
        request.getRequestDispatcher("machineadm.jsp").forward(request,response);
    }


    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        String [] m = request.getParameterValues("set_machine_num");
        String [] s = request.getParameterValues("set_state");
        String [] p = request.getParameterValues("set_private");
        int machine_num = 0;int state = 0;int pr = 0;
        ResultSet rs = null;
        try{
            DBconn.init();
            for(int i = 0; i < m.length; i++){
                machine_num = Integer.parseInt(m[i],10);
                state = Integer.parseInt(s[i],10);
                pr = Integer.parseInt(p[i],10);
                DBconn.addUpdDel("update Machine_condition set state = "+state+",private = "+pr +
                        " where machine_number = "+machine_num);
            }
            int timetable_state = 0;
            switch (state){
                case 0:
                    timetable_state = 3;break;
                case 1:
                    timetable_state = 0;break;
            }
            DBconn.addUpdDel("update timetable set state = "+timetable_state+
                    " where machine_number = " + machine_num +
                    " and state != -1;");
            DBconn.closeConn();
            request.getRequestDispatcher("machineadm.jsp").forward(request,response);
        }catch (Exception e){
            DBconn.closeConn();
            e.printStackTrace();
        }
    }
}
