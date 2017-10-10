package top.lmoon.myspider.h2db;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * H2数据库工具类
 * @author LMoon
 * @date 2017年10月2日
 *
 */
public class H2DBServer {
	
	private static final Logger logger = LoggerFactory.getLogger(H2DBServer.class);
	
	private static Server server;
	
	public static void start(){		
		try {
			server = Server.createTcpServer().start();
			System.out.println("h2数据库启动成功！");
			logger.info("h2数据库启动成功！");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("h2数据库启动失败：",e);
		}		
	}
	
	public static void stop(){
		if(server!=null){
			server.stop();
			server = null;
		}
	}
	
	public static void main(String[] args) {
		start();
	}

}
