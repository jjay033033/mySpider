package top.lmoon.myspider;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import top.lmoon.myspider.apefile.FileReader;
import top.lmoon.myspider.dao.ApeFileDAOH2DBImpl;
import top.lmoon.myspider.dao.ApeInfoDAO;
import top.lmoon.myspider.dao.ApeInfoDAOH2DBImpl;
import top.lmoon.myspider.h2db.H2DBServer;
import top.lmoon.myspider.idfactory.SongIdFactory;
import top.lmoon.myspider.vo.ApeInfoVO;

/**
 * Unit test for simple App.
 */
public class AppTest {
	
	private static ApeInfoDAOH2DBImpl dao = new ApeInfoDAOH2DBImpl();
	
	private static ApeFileDAOH2DBImpl fileDao = new ApeFileDAOH2DBImpl();
	
	private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
	
	public static void main(String[] args) {
		H2DBServer.start();
//		select();
		int dropTable = fileDao.dropTable();
		int createTable = fileDao.createTable();
		System.out.println(dropTable);
		System.out.println(createTable);
		H2DBServer.stop();
//		createdb();
	}
	
	private static void select(){
		List<ApeInfoVO> select = dao.select(1, 100, null, null);
//		int select = dao.count(null, null);
		System.out.println(select);
	}

	private static void createdb() {
		dao.createTable();
		List<ApeInfoVO> readAllFiles = FileReader.readAllFiles();
		for(ApeInfoVO vo:readAllFiles){
			threadPool.submit(new Runnable() {
				
				@Override
				public void run() {
					vo.setSongId(SongIdFactory.getInstance().getSongId());
					dao.insert(vo);
				}
			});
			
		}
	}
}
