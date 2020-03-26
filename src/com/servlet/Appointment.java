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

public class Appointment extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static int interval = 50;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        String id = (String) session.getAttribute("loginName");
        System.out.println(id);
        Date d = new Date();
        int h = d.getHours();
        int m = d.getMinutes();
        int t = h*60+m;
        int overtime = 0;

        String time_h = request.getParameter("time_h");//小时
        String time_m = request.getParameter("time_m");//分钟
        int start_time = Integer.parseInt(time_h,10)*60+Integer.parseInt(time_m,10); //  0 < time < 1395
        int key = 0;//机器存在
        int machine_number = 0;
        System.out.println("执行1");
        if(start_time <0||start_time >(1440-interval)){
            request.setAttribute("ap_xiaoxi", "您填写的时间有误"); //向request域中放置信息
            System.out.println("执行9");
            request.getRequestDispatcher("/修改.jsp").forward(request, response);
            return;
        }
        try {
            DBconn.init();
            int NO = 0;
            ResultSet rs_min = DBconn.selectSql("select * from student \n" +
                    "where id = '" + id +"'");
            if(rs_min.next()) {
                NO = Integer.parseInt(rs_min.getString("NO"), 10);
            }
            //这里要结合机器代号表作一个这台机器是否正常工作的判断，不ok返回机器故障或者怎么样的消息。
            ResultSet rs_m = DBconn.selectSql("select * from Machine_condition \n" +
                    "where Machine_condition.build_num = " + NO +
                    " and Machine_condition.state = 1 and Machine_condition.private = 1");
            if(rs_m.next()){
                key = 1;
                machine_number = Integer.parseInt(rs_m.getString("machine_number"),10);
            }else {
                key = 0;
            }
            ResultSet rs_md = DBconn.selectSql("select * from usecondition where machine_number = "+machine_number);
            if(rs_md.next()){
                overtime = rs_md.getInt("overtime");
                if(overtime<0){
                    overtime = 0;
                }
            }
            if (key == 1) { // 机器存在且正常
                ResultSet rs_ts = DBconn.selectSql("select * from timetable \n" //开始时间在已预约时间段中
                        + " where machine_number = " + machine_number
                        + " and time_part <= "+ start_time
                        + " and time_part + " + interval + " > "+start_time);
                ResultSet rs_te = DBconn.selectSql("select * from timetable \n" //结束时间在已预约时间段中
                        + " where machine_number = " + machine_number
                        + " and time_part < "+ (start_time + interval)
                        + " and time_part + " + interval + " >= "+(start_time + interval));
                if(start_time< overtime){
                    request.setAttribute("ap_xiaoxi", "当前机器正在被使用"); //向request域中放置信息
                    System.out.println("执行2");
                    request.getRequestDispatcher("PublicMachine").forward(request, response);
                return;
                }
                if(rs_ts.next()||rs_te.next()||start_time<t-1) {
                    if(start_time<t-1){
                        request.setAttribute("ap_xiaoxi", "预约时间不得早于当前时间"); //向request域中放置信息
                        System.out.println("执行3");
                        request.getRequestDispatcher("PublicMachine").forward(request, response);
                        return;
                    }else {
                        System.out.println("执行4");
                        request.setAttribute("ap_xiaoxi", "时间段冲突，预约失败"); //向request域中放置信息
                        request.getRequestDispatcher("PublicMachine").forward(request, response);
                        return;
                    }
                }else {// 时间不冲突
//                    DBconn.addUpdDel("update timetable set time_part = " + 0 + ", id ='" + id + "' where machine_number = " + machine_number);
                    DBconn.addUpdDel("insert into timetable(time_part,id,machine_number,state) values\n"
                            + "("+start_time+", '"+id+"' , "+machine_number+",0)");
                    request.setAttribute("ap_xiaoxi", "预约成功"); //向request域中放置信息
                    System.out.println("执行5");
                    request.getRequestDispatcher("PublicMachine").forward(request, response);
                    return;
                }
            }else{
                request.setAttribute("ap_xiaoxi","机器故障或被管理员暂时禁用");
                request.getRequestDispatcher("PublicMachine").forward(request,response);
            }
            DBconn.closeConn();
        }catch (SQLException e){
            DBconn.closeConn();
            e.printStackTrace();
        }
    }
}
