package com.dao;
 
import java.util.List;
 
import com.entity.Usertable;
 
public interface UserDao {
	public boolean login(String id,String pwd);//登录
	public boolean update(String id, String pwd) ;//更新用户信息
}