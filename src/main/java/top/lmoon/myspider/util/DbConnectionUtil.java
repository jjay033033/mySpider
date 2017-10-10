package top.lmoon.myspider.util;

import java.sql.Connection;
import java.sql.SQLException;

import top.lmoon.myspider.h2db.H2DBConnectionPool;

public class DbConnectionUtil {
	
	public static Connection getConnection(){
		try {
			return H2DBConnectionPool.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
