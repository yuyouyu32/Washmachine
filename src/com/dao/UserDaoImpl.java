package com.dao;
 
import java.sql.ResultSet;
import java.sql.SQLException;
import com.util.DBconn;
 
public class UserDaoImpl implements UserDao{
    public boolean login(String id, String pwd) {
		boolean flag = false;
		try {
			    DBconn.init();
				ResultSet rs = DBconn.selectSql("select * from student where id ='"+id+"' and password ='"+pwd+"'");
				while(rs.next()){
					if(rs.getString("id").equals(id) && rs.getString("password").equals(pwd)){
						flag = true;
					}
				}
				DBconn.closeConn();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	public boolean update(String id,String pwd) {
		boolean flag = false;
		DBconn.init();
		String sql ="update student set"
				+" password ='"+pwd
				+"' where id = '"+id+"'";
		int i =DBconn.addUpdDel(sql);
		if(i>0){
			flag = true;
		}
		DBconn.closeConn();
		return flag;
	}
}