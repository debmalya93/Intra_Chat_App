package server;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageOperation {
	static int writeImage(byte[] fileContent,String id) throws IOException, SQLException {
		Connection con=null;
		PreparedStatement ps=null;
		int row=-1;
		//System.out.println(new String(fileContent,0,fileContent.length));
		try{
			con=ConnectionFactory.getConnection();
			if (con != null) {
				ps = con.prepareStatement("update users set dp=? where id='"+id+"'");
				ps.setBytes(1, fileContent);
				row = ps.executeUpdate();
			}

			
		}catch(Exception e){
			System.out.println("write image error");
		}finally{
			ps.close();
			con.close();
		}
		return row;

	}

	static byte[] readImage(String id) throws Exception {
		Connection con=null;
		PreparedStatement ps=null; 
		ResultSet rs=null;
		byte[] barr=null;
		try{
			con = ConnectionFactory.getConnection();
			ps = con.prepareStatement("select dp from users where id='"+id+"'");
			rs = ps.executeQuery();
			if (rs.next()) {// now on 1st row

				//Blob b = rs.getBlob("dp");
				//barr = b.getBytes(1, (int)b.length());// 1 means first image
				barr=rs.getBytes("dp");
			}
		}catch(Exception e){
			System.out.println("read image error");
		}finally{
			ps.close();
			con.close();	
		}

		return barr;
	}

}
