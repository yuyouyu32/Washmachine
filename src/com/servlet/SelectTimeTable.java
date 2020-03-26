package com.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.dao.UserDao;
import com.dao.UserDaoImpl;
import com.mysql.cj.xdevapi.DbDoc;
import com.util.DBconn;

public class SelectTimeTable extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static int interval = 50;
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        HttpSession session = request.getSession();
        if(session==null)
        {
            //没有登录成功，跳转到登录页面
            request.setAttribute("xiaoxi", "您太久未操作，请重新登陆！");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        String id = (String) session.getAttribute("loginName");
//        String buliding = request.getParameter("building_num2");
//        int buliding_num = Integer.parseInt(buliding,10);
//        String floor= request.getParameter("floor_num2");
//        int floor_num = Integer.parseInt(floor,10);
//        int machine_number = (buliding_num - 1) * 10 + floor_num;
        String cancel_key = (String) request.getAttribute("cancel_key");

        Date d = new Date();
        int h = d.getHours();
        int m = d.getMinutes();
        int t = h*60+m-interval;
        int overtime = 0;

        int machine_number = 0;
        int NO = 0;
        try{
            DBconn.init();
            ResultSet rs_min = DBconn.selectSql("select * from student \n" +
                    "where id = '" + id +"'");
            if(rs_min.next()) {
                NO = Integer.parseInt(rs_min.getString("NO"), 10);
            }
            ResultSet rs_m = DBconn.selectSql("select * from Machine_condition \n" +
                    "where Machine_condition.build_num = " + NO +
                    " and Machine_condition.state = 1 and Machine_condition.private = 1");
            if(rs_m.next()){
                machine_number = Integer.parseInt(rs_m.getString("machine_number"),10);
            }
            DBconn.closeConn();
        }catch (SQLException e){ //获取机号
            DBconn.closeConn();
            e.printStackTrace();
        }

        try{
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from usecondition where machine_number = "+machine_number);
            if(rs.next()){
                overtime = rs.getInt("overtime");
                if(overtime >= interval){ //
                    overtime-=interval;
                }else{
                    overtime = 0;
                }
                if(overtime > t){
                    t = overtime;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        try{
            DBconn.init();
            String sql = "select * from timetable where id = '"+id+"'";
            ResultSet rs_alapp = DBconn.selectSql(sql);
            int timealapp [] = new int[50];
            int state[] = new int[50];
            int i = 0;
            while(rs_alapp.next()){
                timealapp[i] = rs_alapp.getInt("time_part");
                state[i] = rs_alapp.getInt("state");
                i++;
            }
      
            int timealapp1 [] = new int[i];
            int state1[] = new int[i];
            String key[] =new String[i];    // 是否可以取消
            String stime[] = new String[i];
            for(int j = 0;j < i;j++){
                timealapp1[j] = timealapp[j];
            }
            Arrays.sort(timealapp1);
            for(int j = 0;j<i;j++){
                for(int j1 = 0;j1<i;j1++){
                    if(timealapp1[j] == timealapp[j1]){
                        state1[j] = state[j1];
                    }
                }
                if((h*60+m) >= timealapp1[j]&&state1[j]==0){
                    state1[j] = 1;
                }
                stime[j] = ""+timealapp1[j];
            }
            request.setAttribute("starttime",stime);
            String time[] = new String[i];
            String hour = "";
            String hour_e = "";
            String minutes = "";
            String minutes_e = "";
            String str = "";
            for(int j = 0;j<i;j++){
                if(timealapp1[j]/60>=24){
                    str="次日";
                    timealapp1[j]-=1440;
                }else{
                    str ="";
                }
                if(timealapp1[j]/60<10){
                    hour = "0"+timealapp1[j]/60;
                }else{
                    hour = "" + timealapp1[j]/60;
                }
                if((timealapp1[j]+50)/60<10){
                    hour_e = "0"+(timealapp1[j]+50)/60;
                }else{
                    hour_e = "" +(timealapp1[j]+50)/60;
                }
                if(timealapp1[j]%60<10){
                    minutes = "0"+timealapp1[j]%60;
                }else{
                    minutes = ""+timealapp1[j]%60;
                }
                if((timealapp1[j]+50)%60<10){
                    minutes_e = "0"+(timealapp1[j]+50)%60;
                }else{
                    minutes_e = ""+(timealapp1[j]+50)%60;
                }
//                String date = "";
//                SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
//                date = df.format(new Date());// new Date()为获取当前系统时间
//                System.out.println(date);
                String mss = "";
                switch (state1[j]){
                    case -1:
                        mss = "已使用";break;
                    case 0:
                        mss = "请等待";break;
                    case 1:
                        mss = "可使用";break;
                    case 2:
                        mss = "已推迟";break;
                    case 3:
                        mss = "洗衣机故障,可无责取消";break;
                    case 4:
                        mss = "超时未到，已记录违规";break;
                }
                time[j] = str+hour+":"+minutes+"~"+hour_e+":"+minutes_e+" "+mss;
                if((timealapp1[j]>(h*60+m)||state1[j]==3)&&cancel_key=="1"){
                    key[j] = "1";
                }else {
                    key[j] = "0";
                }
            }
            request.setAttribute("key", key);
            request.setAttribute("alapp",time);
        }catch (SQLException e){ // 已预约时间
            e.printStackTrace();
        }

        String str = "";
        int time[] = new int[50];
        try{
            DBconn.init();

            ResultSet rs = DBconn.selectSql("select * from timetable \n" +
                    "where timetable.machine_number = " + machine_number);
            str = str + "可预约时间段：\n";
            String [] str1 ={};
            String hour = "";
            String minutes = "";
            String hour_e = "";
            String minutes_e = "";
            int i = 0;
            str1 = new String[50];
            int index = 0;
            for(i = 0;rs.next();i++){
                time[i] = rs.getInt("time_part");
            }

            int i1 = i;

            int time1[] = new int[i1];
            for(int j = 0;j<i1;j++){
                time1[j] = time[j];
            }
            time = time1;
            Arrays.sort(time,0,i1); // 排序算法
            for(i = 0;i < i1;i++){
                if(i == 0 &&i1 >= 1&& time[i] >= interval + t){

                    if(h<10){
                        hour = "0" + h;
                    }else {
                        hour = "" + h;
                    }
                    if(m<10){
                        minutes = "0" + m;
                    }else{
                        minutes = "" + m;
                    }

                    if((time[i])/60<10){
                        hour_e = "0"+(time[i])/60;
                    }else {
                        hour_e = ""+(time[i])/60;
                    }
                    if((time[i])%60<10){
                        minutes_e = "0" + (time[i])%60;
                    }else{
                        minutes_e = "" + (time[i])%60;
                    }
                    if(Integer.parseInt(hour_e,10)<24) {//
                        str1[index] = hour + ":" + minutes + " ~ " + hour_e + ":" + minutes_e + "\n";
                        index++;
                        if(hour.equals(hour_e)&&minutes.equals(minutes_e)){
                            index--;
                            str1[index]="";
                        }
                    }
                }
                if(i>0&&t>time[i-1]) {
                    time[i-1] = t;
                }
                if(i > 0&& time[i]-time[i-1]>=2*interval) {
                    if((time[i-1]+interval) / 60<10){
                        hour = "0"+(time[i-1]+interval) / 60;
                    }else {
                        hour = ""+(time[i-1]+interval) / 60;
                    }
                    if((time[i-1]+interval) % 60<10)
                    {
                        minutes = "0"+(time[i-1]+interval) % 60;
                    }else {
                        minutes = ""+(time[i-1]+interval) % 60;
                    }
                    if((time[i]) / 60 < 10){
                        hour_e = "0"+(time[i]) / 60;
                    }else {
                        hour_e = ""+(time[i]) / 60;
                    }
                    if((time[i]) % 60 <10){
                        minutes_e = "0"+ (time[i]) % 60;
                    }else {
                        minutes_e = ""+ (time[i]) % 60;
                    }
                    if(Integer.parseInt(hour_e,10) < 24) {    // 并且大于当前时间
                        str1[index] = hour + ":" + minutes + " ~ " + hour_e + ":" + minutes_e + "\n";
                        index++;
                        if(hour.equals(hour_e)&&minutes.equals(minutes_e)){
                            index--;
                            str1[index]="";
                        }
                    }
                }
            }
            if(i==0&&i1==0){ //处理时间表为空
                if(h<10){
                    hour = "0"+h;
                }else{
                    hour = ""+h;
                }
                if(m<10){
                    minutes = "0"+m;
                }else{
                    minutes = ""+m;
                }
                str1[index] = hour + ":" + minutes + " ~ " + "24" + ":" + "00" + "\n";
                index++;
            }
            if(i>0&&t>time[i-1]) {
                time[i-1] = t;
            }
            if(i>0 && 1440-time[i-1] >= 2*interval){
                if((time[i-1]+interval)/60 < 10){
                    hour = "0"+ (time[i-1]+interval)/60;
                }else{
                    hour = ""+ (time[i-1]+interval)/60;
                }
                if((time[i-1]+interval)%60 < 10){
                    minutes = "0"+ (time[i-1]+interval)%60;
                }else {
                    minutes = ""+ (time[i-1]+interval)%60;
                }
                if(Integer.parseInt(hour,10) < 24) {
                    str1[index] = hour + ":" + minutes + " ~ " + "24" + ":" + "00" + "\n";
                    index++;
                    if(hour.equals("24")&&minutes.equals("00")){
                        index--;
                        str1[index]="";
                    }
                }
            }
            String []str2 = new String[index];
            for(int j = 0;j<index;j++){
                str2[j] = str1[j];
            }
//            request.setAttribute("appoint_message",str);
            request.setAttribute("appoint_message",str2);
            request.getRequestDispatcher("/修改.jsp").forward(request,response);
            DBconn.closeConn();
        }catch (SQLException e) // 可预约时间
        {
            e.printStackTrace();
        }
    }
}

