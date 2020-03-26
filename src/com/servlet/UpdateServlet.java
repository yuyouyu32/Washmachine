package com.servlet;
 
import java.io.IOException;
import java.io.PrintWriter;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dao.UserDao;
import com.dao.UserDaoImpl;
import com.entity.Usertable;
 
public class UpdateServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String id = request.getParameter("id");	
		String oldpwd = request.getParameter("oldpwd");
		String pwd = request.getParameter("pwd");
		String ackpwd =request.getParameter("ackpwd");
				
		UserDao ud = new UserDaoImpl();
		if(ud.login(id, oldpwd))
		{
			if(pwd.equals(ackpwd))
			{
				UserDao ud1=new UserDaoImpl();
				if(ud1.update(id,pwd)){
					request.setAttribute("xiaoxi", id+"更新成功,请重新登陆");
					request.getRequestDispatcher("/index.jsp").forward(request, response);//转发到成功页面
				}
				else
				{
					request.setAttribute("xiaoxi", "数据库更新失败，请稍后重试。");
					response.sendRedirect("index.jsp");
				}	
			}
			else
			{
				request.setAttribute("xiaoxi", "新密码与确认密码不同，请重新输入");
				request.getRequestDispatcher("/changepwd.jsp").forward(request, response);	
			}
		}
		else{
			request.setAttribute("xiaoxi", "用户名或原密码错误，请重新输入");
			request.getRequestDispatcher("/changepwd.jsp").forward(request, response);			
		}
	}
}
 