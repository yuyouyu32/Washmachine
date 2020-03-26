package com.servlet;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 

import javax.servlet.http.HttpSession;


import com.dao.UserDao;
import com.dao.UserDaoImpl;
import com.util.DBconn;

public class DengluServlet extends HttpServlet {  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//需要继承HttpServlet  并重写doGet  doPost方法
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);  //将信息使用doPost方法执行   对应jsp页面中的form表单中的method
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String id = request.getParameter("stuid");
		String pwd = request.getParameter("password");
		if (id.equals("888") && pwd.equals("888"))
		{
			request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
		}
		UserDao ud = new UserDaoImpl();
		if(ud.login(id, pwd)){
			//登陆成功
            //创建session对象
            HttpSession session = request.getSession();
            //把用户数据保存在session域对象中
            session.setAttribute("loginName", id);
		    session.setAttribute("dlxiaoxi", "欢迎用户"+((String)session.getAttribute("loginName"))); //向request域中放置信息
			try{
				DBconn.init();
				ResultSet rs = DBconn.selectSql("select * from usertable \n" +
						"where id = '" + id +"'");
				if(rs.next()){
					DBconn.closeConn();
					request.getRequestDispatcher("/Administration.jsp").forward(request, response);//转发到成功页面
				}else{
					DBconn.closeConn();
					request.getRequestDispatcher("NoticeAdministration").forward(request, response);

	//			request.getRequestDispatcher("SelectTimeTable").forward(request, response);
				}
			}catch (SQLException e){
				e.printStackTrace();
				DBconn.closeConn();
				request.setAttribute("xiaoxi", "服务器未知错误");
				request.getRequestDispatcher("/index.jsp").forward(request, response);
			}
		}
		else{
			request.setAttribute("xiaoxi", "用户名或密码错误，请重新登陆");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
	}
 
}