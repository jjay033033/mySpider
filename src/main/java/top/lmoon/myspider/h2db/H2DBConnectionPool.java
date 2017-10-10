package top.lmoon.myspider.h2db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.util.CommonUtil;

public class H2DBConnectionPool {

	private static final Logger logger = LoggerFactory.getLogger(H2DBConnectionPool.class);

	private static final String JDBC_URL = "jdbc:h2:tcp://localhost/./res/db/apelist";

	private static final String USER = "lmoon";

	private static final String PASSWORD = "f_4j^+KL_@*f(ioO9P2d";

	private static JdbcConnectionPool pool = null;

	static {
		try {
			pool = JdbcConnectionPool.create(JDBC_URL, USER, PASSWORD);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public static Connection getConnection() throws SQLException{
		return pool.getConnection();
	}

	public static void main(String[] args) {

		try {
			Connection conn = pool.getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute("create table test(id varchar(36) primary key,name varchar(100))");
			stmt.executeUpdate("insert into test values ('" + CommonUtil.getUUID() + "','大f日如来')");
			ResultSet rs = stmt.executeQuery("select * from test");
			while (rs.next()) {
				System.out.println(rs.getString("id") + "," + rs.getString("name"));
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
