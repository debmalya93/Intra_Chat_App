package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageDaoImpl{
	
	public static String register(String name,String id,String pass){
		
		pass=Encryption.encrypt(pass);
		String sql="insert into users (id,name,password,uniqueid,last_online)values(?,?,?,?,systimestamp)";
		String status=null;
		PreparedStatement pst=null;
		Connection con=null;
		try{
			con=ConnectionFactory.getConnection();
			pst=con.prepareStatement(sql);
			pst.setString(1,id);
			pst.setString(2,name);
			pst.setString(3,pass);
			status=UniqueGen.getID();
			pst.setString(4,status);
			int row=pst.executeUpdate();
			if(row>0){
				createTable(con,status);
				status="/r/"+status;
			}else{
				//name="/nr/";//registration unsuccessful
				status="/nr/";
			}
		}catch(Exception e){
			//e.printStackTrace();
			status="/nr/";
			return status;
		}
		finally{
			try {
				pst.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}
	
	public static void updateTime(String id){
		Connection con=null;
		PreparedStatement pst=null;
		String sql="update users set last_online = systimestamp where id='"+id+"'";
		try{
			con=ConnectionFactory.getConnection();
			pst=con.prepareStatement(sql);
			pst.executeUpdate();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				pst.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private static void createTable(Connection con,String unique){
		PreparedStatement pst=null;
		String sql="create table a"+unique+" (chatwith varchar2(30), msg clob, time timestamp)";
		try {
			pst=con.prepareStatement(sql);
			pst.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				pst.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		
	}
	
	public static String authenticate(String id,String password){
		password=Encryption.encrypt(password);
		String sql="select * from users where id=? and password=?";
		String name_id=null;
		PreparedStatement pst=null;
		Connection con=null;
		ResultSet rs;
		try{
			con=ConnectionFactory.getConnection();
			pst=con.prepareStatement(sql);
			pst.setString(1,id);
			pst.setString(2,password);
			rs=pst.executeQuery();
			if(rs.next()){
				name_id=rs.getString("name")+"/n/"+rs.getString("uniqueid");
			}else{
				name_id="invalid";
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}
		finally{
			try {
				pst.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return name_id;
	}
	
	public static void writeMessage(long id, long chatWithID, String message) 
	{
		Connection con;
		PreparedStatement ps;
		message=Encryption.encrypt(message);
		try
		{
			con=(Connection)ConnectionFactory.getConnection();
			
			String tableName="A"+id;
			String chatWith="A"+chatWithID;
			
			String sql="insert into "+tableName+"(chatWith,Msg,Time)values(?,?,systimestamp)";
			ps=con.prepareStatement(sql);
			
			ps.setString(1, chatWith);
			ps.setString(2,	message);
		
			int row=ps.executeUpdate();
			
			if(row>0)
				return;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static String readMessage(long id1, long id2) 
	{
		Connection con=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		String allChats="Start your conversation...";
		
		try
		{
			con=(Connection)ConnectionFactory.getConnection();
			String searchQuery="select msg,time from A"+id1+" where chatwith='A"+id2+"' union all select msg,time from A"+id2+" where chatwith='A"+id1+"' order by time asc";
			ps=con.prepareStatement(searchQuery);
			rs=ps.executeQuery();
			while(rs.next())
			{
				allChats=allChats+"\n"+Encryption.decrypt(rs.getString("msg"));
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				ps.close();
				rs.close();
				con.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return allChats;
	}

	public static String lastSeen(String id){
		int time=0;
		String status=null;
		Connection con=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		String sql="select extract(minute from (systimestamp-last_online)) + extract(hour from (systimestamp-last_online))*60 + extract(day from (systimestamp-last_online))*24*60 as time from users where id='"+id+"'";
		try{
			con=ConnectionFactory.getConnection();
			pst=con.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()){
				time=rs.getInt("time");//minute
			}
			if(time==0){
				status="/seen/last seen few seconds ago";
			}else if(time>60){
				time=time/60;//hour
				status="/seen/last seen "+time+" hours ago";
				if(time>24){
					time=time/24;//day
					status="/seen/last seen "+time+" days ago";
				}
			}else{
				status="/seen/last seen "+time+" minutes ago";
			}
		}catch(Exception e){
			
		}finally{
			try {
				pst.close();
				rs.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return status;
	}
}
