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
import javax.servlet.annotation.WebServlet;

public class OperateCardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static int interval = 50;
    private static	String reason1="插队";
    private static	String reason2="迟到";//违规记录中记录的时间是当前时间、
//    private static	int pscore=10;//每次违规增加10分
    
    
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        
        String id = request.getParameter("id");    
        String check = request.getParameter("myCheck");
        check="插取卡";
        String mmachinenumber=request.getParameter("Machinenumber");
        
        System.out.println(check);
        
        
        int machinenumber=Integer.parseInt(mmachinenumber);
        int privatetemp = 0;   //privatetemp表示该机器是否公用
                
        Date d = new Date();
        
        int year = d.getYear()+1900;
        int month = d.getMonth()+1;
        int day = d.getDate();
        
        int h = d.getHours();
        int m = d.getMinutes();
        
        int t = h*60+m;   //当前时间
        int overtime=t+50;   //当前时间+50
        

        
	    try {
	            DBconn.init();   
	         if(machinenumber!=0&&check.equals("插取卡"))   //使用非本楼的洗衣机
	         {
	        	 //首先根据当前时间更新usecondition表，如果usestate为1且表中的overtime<当前时间，则更新usestate为0，id为null，overtime为0
         		DBconn.addUpdDel("update usecondition set usestate=0,id=null,overtime=0 where usestate=1 and overtime<" + t);
	        	 
	        	 
		            //判断选择的洗衣机是公用还是非公用
		        ResultSet rs00 = DBconn.selectSql("select * from Machine_condition where machine_number=" + machinenumber);
		        
		        int numberOfRows=0;
		         while(rs00.next())
		           {
		        	 numberOfRows=rs00.getRow(); 
		           }
		        
		        if(numberOfRows==0)   //该机器不存在
		        {
	        		request.setAttribute("xiaoxi", "不存在该洗衣机，请重新选择"); //向request域中放置信息
	        		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
	//        		DBconn.closeConn();
		        }
		        else   //该机器存在
		        {
		        	rs00.beforeFirst();//把指针再移到初始化的位置
		        	rs00.next();
		            privatetemp=Integer.parseInt(rs00.getString("private"));  //该机器是否公用，0表示公用，1表示非公用
		            if(privatetemp==1)  //非公用
		            {
		        		request.setAttribute("xiaoxi", "该洗衣机非公用且不是你所在楼的洗衣机，请重新选择"); //向request域中放置信息
		        		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
		//        		DBconn.closeConn();      	
		            }
		            else    //公用
		            {
		            	//先判断该公用洗衣机是否为故障
		            	ResultSet rsttemp = DBconn.selectSql("select * from Machine_condition where machine_number=" + machinenumber);
		            	 rsttemp.next();
		            	 int ssstate=Integer.parseInt(rsttemp.getString("state"));
		            	 
		            	 
		            	ResultSet rs01 = DBconn.selectSql("select * from usecondition where machine_number=" + machinenumber);
		            	rs01.next();
		            	int usestate=Integer.parseInt(rs01.getString("usestate"));  //该公用洗衣机的状态（0未使用，1已使用，2已被预约）
		            	if(ssstate==0)  //该机器故障
		            	{
		            		request.setAttribute("xiaoxi", "公用洗衣机故障，插取卡失败"); //向request域中放置信息
		    		        request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
		            	}
		            	else if(usestate==0)  //该公用洗衣机未被使用
		            	{
		            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +machinenumber);
		            		request.setAttribute("xiaoxi", "公用洗衣机插取卡成功"); //向request域中放置信息
		    		        request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
		            	}
		            	else if(usestate==1)//该公用洗衣机已被使用
		            	{
	            			request.setAttribute("xiaoxi", "公用洗衣机插取卡失败，正在被使用"); //向request域中放置信息
	    		        	request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
		            	}
		            	else  //该公用洗衣机已被预约(即usestate=2)
		            	{
		            		//先判断当前时间是否已经超时(设定预约后五分钟之内就要插取卡操作，否则超时)
		            		ResultSet rs02 = DBconn.selectSql("select * from usecondition where machine_number=" + machinenumber);
		            		rs02.next();
		            		String yuyueid= rs02.getString("id");  //得到表中已经预约者的id
		            		if(t>Integer.parseInt(rs02.getString("overtime")))  //该用户已经超时
		            		{
		            			if(id.equals(yuyueid))  //当前用户就是预约者
		            			{
		            				DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +machinenumber);
		            				request.setAttribute("xiaoxi", "公用洗衣机插取卡成功，但你已迟到，记入你的违规信息"); //向request域中放置信息
			    		        	request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
									String weigui="('"+id+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
									String SQL1= "insert into vlinformation values "+weigui;
									DBconn.addUpdDel(SQL1);									
									DBconn.addUpdDel("update scoretable set score=score+10 where id=" +id);  //更新积分表
			    		        	
		            			}
		            			else	//当前用户不是预约者
		            			{
		            				DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +machinenumber);
		            				request.setAttribute("xiaoxi", "公用洗衣机插取卡成功"); //向request域中放置信息
			    		        	request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
									String weigui="('"+yuyueid+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
									String SQL1= "insert into vlinformation values "+weigui;
									DBconn.addUpdDel(SQL1);
									DBconn.addUpdDel("update scoretable set score=score+10 where id=" +yuyueid);  //更新积分表

		            			}
		            		}
		            		else	//当前用户没有超时
		            		{
		            			if(id.equals(yuyueid))  //当前用户就是预约者
		            			{
		            				DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +machinenumber);
		            				request.setAttribute("xiaoxi", "公用洗衣机插取卡成功"); //向request域中放置信息
			    		        	request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
			    		        	
		            			}
		            			else		//该用户不是预约者
		            			{
			            			request.setAttribute("xiaoxi", "公用洗衣机插取卡失败，已被预约"); //向request域中放置信息
			    		        	request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
		            			}
		            		}
		            	}
		            		
		            }
		            
		            
		        }
	        
	         }
	         
	         
//	         if((machinenumber==0&&check.equals("插取卡")))	//使用本楼的洗衣机(即输入0)  (用到timetable预约记录表)
	         
	         else				//使用本楼的洗衣机(即输入0)
	         {
	        	 //首先根据当前时间更新usecondition表，如果usestate为1且表中的overtime<当前时间，则更新usestate为0，id为null，overtime为0
         		DBconn.addUpdDel("update usecondition set usestate=0,id=null,overtime=0 where usestate=1 and overtime<" + t);
	        	
         		         		
	 	        ResultSet rs03 = DBconn.selectSql("select * from student where  id=" + id);        
	 	        rs03.next();
	 	        
	 	       
	 	        int no=Integer.parseInt(rs03.getString("NO"));//no为该学生的楼号
	 	        String aarea=rs03.getString("area");//aarea为该学生的区域
	 	        int area=0;
	 	        //在输入0，即非公用的情况下，此时，一栋楼只有一台洗衣机，也就是(一个area和number)联合对应一个machine_number
	 	        
	 	        if(aarea.equals("新世纪大学生村"))
	 	        {
	 	        	area=0;
	 	        }
	 	        else if(aarea.equals("校内"))
	 	        {
	 	        	area=1;
	 	        }
	 	        else  //南区
	 	        {
	 	        	area=2;
	 	        }
	 	        
//	 	       System.out.println(area);
	 	        //到Machine_condition表中找到该学生所在区域楼的洗衣机机器号
	 	       ResultSet rs04 = DBconn.selectSql("select * from Machine_condition where area=" + area + " and build_num=" + no);
	 	       rs04.next();	 	     
	 
	 	       int number=Integer.parseInt(rs04.getString("machine_number"));//number为洗衣机的机器号
	 	       
	 	       //判断该机器是否故障
	            ResultSet rsttemp = DBconn.selectSql("select * from Machine_condition where machine_number=" +number);
	            rsttemp.next();
	           int ssstate=Integer.parseInt(rsttemp.getString("state"));
	 	       
	 	       	       	 	       
	 	       
	 	       ResultSet rs004 = DBconn.selectSql("select * from usecondition where machine_number=" + number);
	 	       rs004.next();	
	 	       String ustate=rs004.getString("usestate");  //得到该机器的使用状态	 	       
	 	       
		 	       
		 	    if(ssstate==0)  //该机器故障
	           	{
	           		request.setAttribute("xiaoxi", "该洗衣机故障，插取卡失败"); //向request域中放置信息
	   		        request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
	           	}
	 	       
		 	    else if(ustate.equals("1"))  //正在被使用
	 	       {
	 	    	   request.setAttribute("xiaoxi", "洗衣机插取卡失败，正在被使用"); //向request域中放置信息
		           request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
	 	       }
	 	       else  		//该楼层洗衣机没被使用(是否被预约不一定)，到timetable表中查询是否已经被预约
	 	       {
	 	    	   
		 	       ResultSet rs1 = DBconn.selectSql("select * from timetable where  machine_number=" + number); 
			 	      
		 	       int rowcount=0;//该洗衣机的预约表中的记录数
		           while(rs1.next())
		           {
		        	   rowcount=rs1.getRow(); 
		           }
		           
		           
		 	       rs1.beforeFirst();//把指针再移到初始化的位置
		 	       
				    int count1 = 0;	//统计该楼层洗衣机出故障的数目
				    while(rs1.next()) {
				    	if((rs1.getString("state").equals("3")))
				         {count1++;}
				    }
				    
			 	    rs1.beforeFirst();//把指针再移到初始化的位置
			 	       
					int count2 = 0;	//统计该楼层洗衣机state为1或者4的预约记录数
					while(rs1.next()) {
						if((rs1.getString("state").equals("1"))||(rs1.getString("state").equals("4")))
					     {count2++;}
					}
					
					
					
	 	    	   
	            	if(rowcount==0||count2==rowcount)   //该楼层洗衣机没有预约记录或者state全部都是1/4，直接插取卡，改变usecondition的内容
	            	{
	            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
	            		request.setAttribute("xiaoxi", "插取卡成功"); //向request域中放置信息
	            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
	            	}
	            	else if(count1!=0)  //洗衣机故障
	            	{
	            		request.setAttribute("xiaoxi", "洗衣机故障，插取卡失败"); //向request域中放置信息
	            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
	            	}
	            	else   //正常使用
	            	{
						String jilu="('"+t+"','"+id+"','"+number+"','"+0+"')"  ;
						String SQL= "insert into timetable values "+jilu;
						DBconn.addUpdDel(SQL);  //插入甲0的临时预约记录，方便排序		
						
				        ResultSet rs2 = DBconn.selectSql("select * from timetable where  state=0 and machine_number=" + number+ " order by time_part"); 
				        //rs2为插入甲0临时记录后的按预约时间排序的timetable记录(只取state为0的记录)
				        ResultSet rs3 = DBconn.selectSql("select * from timetable where  state=0 and machine_number=" + number+ " order by time_part desc"); 
				        //rs3为插入甲0临时记录后的按预约时间逆序排序的timetable记录(只取state为0的记录)
				        rs2.next();
				        rs3.next();	
				        
				        
				        
				    	
				        if(Integer.parseInt(rs2.getString("time_part"))==t)  //如果第一个就是甲0，也就是甲0的前面没有预约但未使用、未记录迟到的预约记录
				        {
				        	rs2.next();
				            String id2=rs2.getString("id");//得到下一位学生的id
				            int t2=Integer.parseInt(rs2.getString("time_part"));//得到下一位学生的预约时间
				            int inter=overtime-t2;
				            if(inter<=0)   //插取卡不会影响后者
				            {
			            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
			            		request.setAttribute("xiaoxi", "插取卡成功"); //向request域中放置信息
			            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
			            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
				            }
				            else   //插卡会影响后者，要判断后面的人是否为自己(甲2)
				            {
				            	if(id2.equals(id))   //当前时间与后面自己的预约时间有交集，取消后面的那个预约
				            	{
				            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
				            		request.setAttribute("xiaoxi", "插取卡成功，取消了你后面的预约"); //向request域中放置信息
				            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
				            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
				            		DBconn.addUpdDel("delete from timetable where time_part="+t2);   //删除甲2的临时预约记录
				            	}
				            	else		//当前时间与后面其他人的预约时间有交集，属于插队，记入违规信息，并将后面的人预约时间延迟
				            	{
				            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
				            		DBconn.addUpdDel("update timetable set time_part=time_part+" +inter+" where machine_number=" +number+" and time_part>"+t);
				            		request.setAttribute("xiaoxi", "插取卡成功，但推迟了后面其他人的预约，属于插队，记入你的插队违规信息"); //向request域中放置信息
				            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
				            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
				            		
									String weigui="('"+id+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason1+"')";
									String SQL2= "insert into vlinformation values "+weigui;
									DBconn.addUpdDel(SQL2);
									
									DBconn.addUpdDel("update scoretable set score=score+10 where id=" +id);  //更新积分表
																		
				            	}
				            }
				        }
				        
				        else if(Integer.parseInt(rs3.getString("time_part"))==t)  //如果最后一个是甲0
				        {
				        	rs3.next();
				            String id1=rs3.getString("id");//得到上一位学生的id(递增记录中)
				            int t1=Integer.parseInt(rs3.getString("time_part"));//得到上一位学生的预约时间(递增记录中)
				            //因为甲0是最后一个，所以不必考虑后面，只需考虑前面人
				            if(id1.equals(id))   //如果前面预约的就是自己甲1
				            {
				            	if(t-t1<=5)   //未迟到，插卡成功
				            	{
				            		DBconn.addUpdDel("update timetable set state=1 where time_part=" +t1);
				            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
				            		request.setAttribute("xiaoxi", "插取卡成功，你未迟到"); //向request域中放置信息
				            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
				            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
				            		

				            		//临时表rstemp记录当前者前面所有迟到者的信息
				    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + t1);
				    	 	        
				    	 	        while(rstemp.next())
				    	 	        {
				    	 	        	
					            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
										String weigui4="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
										String SQL4= "insert into vlinformation values "+weigui4;
										DBconn.addUpdDel(SQL4);
										DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
				    	 	        }	
				            		
				            	}
				            	else	//迟到，插卡成功，记入前面所有人的迟到信息
				            	{
				            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
				            		
				            		
				            		//临时表rstemp记录当前者前面所有迟到者的信息
				    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + t);
				    	 	        
				    	 	        while(rstemp.next())
				    	 	        {
				    	 	        	
					            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
										String weigui="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
										String SQL2= "insert into vlinformation values "+weigui;
										DBconn.addUpdDel(SQL2);
										DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
				    	 	        }		            		
				            		request.setAttribute("xiaoxi", "插取卡成功，你已迟到，记入所有人的迟到信息"); //向request域中放置信息
				            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
				            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
				            		

				            	}
				            }
				            else		//如果前面预约的是其他人乙
				            {
				            	if(t-t1<=5)   //乙未迟到，插卡失败
				            	{
				            		request.setAttribute("xiaoxi", "插取卡失败，已有人预约"); //向request域中放置信息
				            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
				            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
				            	}
				            	else	//乙迟到，插卡成功，记入乙和乙前面所有人的迟到信息
				            	{
				            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);   		
				            		//临时表rstemp记录当前者前面所有迟到者的信息
				    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + t);
				    	 	        
				    	 	        while(rstemp.next())
				    	 	        {
				    	 	        	
					            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
										String weigui="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
										String SQL2= "insert into vlinformation values "+weigui;
										DBconn.addUpdDel(SQL2);
										DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
				    	 	        }	
				            		request.setAttribute("xiaoxi", "插取卡成功，你前面的人已迟到，记入所有人的迟到信息"); //向request域中放置信息
				            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
				            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
				            	}
				            }
				        }
				        
				        
				        else   //甲0的前后都有人
				        {
						    rs2.beforeFirst();//把指针再移到初始化的位置
						    rs3.beforeFirst();//把指针再移到初始化的位置
						    
						    String idd1="";//甲前一位学生的id
						    String idd2="";//甲后一位学生的id
						    
						    while(rs2.next())
						    {
						    	if(Integer.parseInt(rs2.getString("time_part"))==t) {break;}
						    }					    
						    rs2.next();
						    idd2=rs2.getString("id");	//甲后一位学生的id
						    int tt2=Integer.parseInt(rs2.getString("time_part"));	//甲后一位学生的预约时间
						    
						    while(rs3.next())
						    {
						    	if(Integer.parseInt(rs3.getString("time_part"))==t) {break;}
						    }					    
						    rs3.next();
						    idd1=rs3.getString("id");	//甲前一位学生的id
						    int tt1=Integer.parseInt(rs3.getString("time_part"));	//甲前一位学生的预约时间
						    
						    int inter=overtime-tt2;
						    
						    
						    if(idd1.equals(id))  //甲0前面的就是自己甲1
						    {
						    	if(inter<=0)   //不会导致后面的人推迟预约时间
						    	{
						    		if(t-tt1<=5)  //没有迟到
						    		{
					            		DBconn.addUpdDel("update timetable set state=1 where time_part=" +tt1);
					            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
					            		request.setAttribute("xiaoxi", "插取卡成功，你未迟到"); //向request域中放置信息
					            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
					            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
					            		
					            		//临时表rstemp记录当前者前面所有迟到者的信息
					    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + tt1);
					    	 	        
					    	 	        while(rstemp.next())
					    	 	        {
					    	 	        	
						            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
											String weigui4="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
											String SQL4= "insert into vlinformation values "+weigui4;
											DBconn.addUpdDel(SQL4);
											DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
					    	 	        }	
						    		}
						    		else	//甲0迟到,将所有迟到的人state改为4，且记入迟到信息 
						    		{	
					            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
					            		request.setAttribute("xiaoxi", "插取卡成功，你已迟到，但没有插队，记入所有人的迟到信息"); //向request域中放置信息
					            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
					    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + t);
					    	 	        
					    	 	        while(rstemp.next())
					    	 	        {
						            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
											String weigui="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
											String SQL2= "insert into vlinformation values "+weigui;
											DBconn.addUpdDel(SQL2);
											DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
					    	 	        }	
					    	 	        
					    	 	        
					            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
						    		}
						    	}
						    	else	//导致后面的人推迟预约时间，如果是自己那么取消后面自己的预约(甲2)，如果是其他人，那么推迟预约时间，产生插队现象
						    	{
						    		if(t-tt1<=5)  //甲0没有迟到
						    		{
						    			if(idd2.equals(id))		//取消后面自己的预约
						    			{					    				
						            		DBconn.addUpdDel("update timetable set state=1 where time_part=" +tt1);
						            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
						            		request.setAttribute("xiaoxi", "插取卡成功，你未迟到，取消了你后面的预约"); //向request域中放置信息
						            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
						            		
						            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
						            		DBconn.addUpdDel("delete from timetable where time_part="+tt2);   //删除甲2的预约记录
						            		
						            		//临时表rstemp记录当前者前面所有迟到者的信息
						    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + tt1);
						    	 	        
						    	 	        while(rstemp.next())
						    	 	        {
						    	 	        	
							            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
												String weigui4="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
												String SQL4= "insert into vlinformation values "+weigui4;
												DBconn.addUpdDel(SQL4);
												DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
						    	 	        }	
						    			}
						    			else		//推迟后面其他人的预约时间
						    			{
						            		DBconn.addUpdDel("update timetable set state=1 where time_part=" +tt1);
						            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
						            		DBconn.addUpdDel("update timetable set time_part=time_part+" +inter+" where machine_number=" +number+" and time_part>"+t);
						            		request.setAttribute("xiaoxi", "插取卡成功，你未迟到，但推迟了你后面其他人的预约时间，属于插队，记入你的插队违规信息"); //向request域中放置信息
						            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
						            		
											String weigui="('"+id+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason1+"')";
											String SQL2= "insert into vlinformation values "+weigui;
											DBconn.addUpdDel(SQL2);
											DBconn.addUpdDel("update scoretable set score=score+10 where id=" +id);  //更新积分表
						            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
						            		
						            		//临时表rstemp记录当前者前面所有迟到者的信息
						    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + tt1);
						    	 	        
						    	 	        while(rstemp.next())
						    	 	        {
						    	 	        	
							            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
												String weigui4="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
												String SQL4= "insert into vlinformation values "+weigui4;
												DBconn.addUpdDel(SQL4);
												DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
						    	 	        }	
						    			}
						    				
						    		
						    		}
						    		else		//甲0已迟到
						    		{
						    			if(idd2.equals(id))		//取消后面自己的预约
						    			{					    				
						            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
						    				
						            		//临时表rstemp记录当前者前面所有迟到者的信息
						    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + t);
						    	 	        while(rstemp.next())
						    	 	        {
						    	 	        	
							            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
												String weigui="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
												String SQL2= "insert into vlinformation values "+weigui;
												DBconn.addUpdDel(SQL2);
												DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
						    	 	        }	
						    				
						    				
						            		request.setAttribute("xiaoxi", "插取卡成功，你已迟到，取消了你后面的预约，记入所有人的迟到信息"); //向request域中放置信息
						            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
						            		
						            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
						            		DBconn.addUpdDel("delete from timetable where time_part="+tt2);   //删除甲2的预约记录
											
											
						    	 	        

						    			}
						    			else  //推迟后面所有人的预约时间
						    			{
						            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);						    				
						    				
						            		//临时表rstemp记录当前者前面所有迟到者的信息
						    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + t);
						    	 	        while(rstemp.next())
						    	 	        {
						    	 	        	
							            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
												String weigui="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
												String SQL2= "insert into vlinformation values "+weigui;
												DBconn.addUpdDel(SQL2);
												DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
						    	 	        }	
						    				
						    				
						            		DBconn.addUpdDel("update timetable set time_part=time_part+" +inter+" where machine_number=" +number+" and time_part>"+t);
						            		request.setAttribute("xiaoxi", "插取卡成功，你已迟到，推迟了你后面其他人的预约时间，属于插队，记入你的插队违规信息和所有人的迟到信息"); //向request域中放置信息
						            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
						            		
						            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
						            		
											String weigui3="('"+id+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason1+"')";
											String SQL3= "insert into vlinformation values "+weigui3;
											DBconn.addUpdDel(SQL3);		
											DBconn.addUpdDel("update scoretable set score=score+10 where id=" +id);  //更新积分表

						    			}
						    		}
						    	}
						    }
						    else		//甲0前面的是其他人丙
						    {
						    	if(t-tt1<=5)  //丙未迟到
						    	{
				            		request.setAttribute("xiaoxi", "插取卡失败，已有人预约"); //向request域中放置信息
				            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
				            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
						    	}
						    	else		//丙已经迟到
						    	{
						    		if(inter<=0)  //不会导致后面的人推迟预约时间
						    		{
					            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
					            		
					            		//临时表rstemp记录当前者前面所有迟到者的信息
					    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + t);
					    	 	        while(rstemp.next())
					    	 	        {
					    	 	        	
						            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
											String weigui="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
											String SQL2= "insert into vlinformation values "+weigui;
											DBconn.addUpdDel(SQL2);
											DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
					    	 	        }	
					    	 	        
					            		request.setAttribute("xiaoxi", "插卡成功，你前面的人已迟到，记入所有人的迟到信息"); //向request域中放置信息
					            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
					            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录

						    		}
						    		else	//导致后面的人推迟预约时间或者取消预约(后面的人就是甲)
						    		{
						    			if(idd2.equals(id))		//取消后面自己的预约
						    			{					    				
						            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
						            		
						            		//临时表rstemp记录当前者前面所有迟到者的信息
						    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + t);
						    	 	        while(rstemp.next())
						    	 	        {
						    	 	        	
							            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
												String weigui="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
												String SQL2= "insert into vlinformation values "+weigui;
												DBconn.addUpdDel(SQL2);
												DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
						    	 	        }	
						    	 	        
						            		request.setAttribute("xiaoxi", "插卡成功，你前面的人已迟到，取消了你后面的预约，并记入所有人的迟到信息"); //向request域中放置信息
						            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
						            		
						            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录
						            		DBconn.addUpdDel("delete from timetable where time_part="+tt2);   //删除甲2的预约记录				            		
						    			}
						    			else
						    			{
						            		DBconn.addUpdDel("update usecondition set usestate=1,id= "+id+",overtime="+ overtime+" where machine_number=" +number);
						            		
						            		//临时表rstemp记录当前者前面所有迟到者的信息
						    	 	        ResultSet rstemp = DBconn.selectSql("select * from timetable where  state =0 and time_part<" + t);
						    	 	        while(rstemp.next())
						    	 	        {
						    	 	        	
							            		DBconn.addUpdDel("update timetable set state=4 where time_part=" +Integer.parseInt(rstemp.getString("time_part")));
												String weigui="('"+rstemp.getString("id")+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason2+"')";
												String SQL2= "insert into vlinformation values "+weigui;
												DBconn.addUpdDel(SQL2);
												DBconn.addUpdDel("update scoretable set score=score+10 where id=" +rstemp.getString("id"));  //更新积分表
						    	 	        }	
						    	 	        
						            		DBconn.addUpdDel("update timetable set time_part=time_part+" +inter+" where machine_number=" +number+" and time_part>"+t);
						            		request.setAttribute("xiaoxi", "插卡成功，你前面的人已迟到，推迟了你后面其他人的预约时间，属于插队，记入你的插队违规信息和所有人的迟到信息"); //向request域中放置信息
						            		request.getRequestDispatcher("/operatecard.jsp").forward(request, response);
						            		
						            		DBconn.addUpdDel("delete from timetable where time_part="+t);   //删除甲0的临时预约记录	            								            		
											String weigui3="('"+id+"','"+year+"','"+month+"','"+day+"','"+t+"','"+reason1+"')";
											String SQL3= "insert into vlinformation values "+weigui3;
											DBconn.addUpdDel(SQL3);	
											DBconn.addUpdDel("update scoretable set score=score+10 where id=" +id);  //更新积分表
						    			}
						    		}
						    	}
						    }
				        }
	            	} 
	 	       }
            }
	 	        
	 	        
	            
	
            

            DBconn.closeConn();
        }catch (SQLException e){
            e.printStackTrace();
        }
	}

}
