package top.lmoon.myspider.h2db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import top.lmoon.myspider.util.CommonUtil;

public class test {
	
	private static final String JDBC_URL = "jdbc:h2:tcp://localhost/./res/db/test";
	
	private static final String USER = "test";
	
	private static final String PASSWORD = "123";
	
	private static final String DRIVER_CLASS = "org.h2.Driver";

	public static void main(String[] args) {
		
		try {
			Class.forName(DRIVER_CLASS);			
			Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);			
			Statement stmt = conn.createStatement();
			stmt.execute("create table user_info(id varchar(36) primary key,name varchar(100))");
			stmt.executeUpdate("insert into user_info values ('"+CommonUtil.getUUID()+"','大日如来')");
			ResultSet rs = stmt.executeQuery("select * from user_info");
			while(rs.next()){
				System.out.println(rs.getString("id")+","+rs.getString("name"));
			}
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
