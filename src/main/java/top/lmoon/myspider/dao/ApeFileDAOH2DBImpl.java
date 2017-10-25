package top.lmoon.myspider.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.jdbc.JdbcTemplate;
import top.lmoon.jdbc.ResultSetExtractor;
import top.lmoon.jdbc.RowMapper;
import top.lmoon.myspider.util.DbConnectionUtil;
import top.lmoon.myspider.util.DownloadUtil;
import top.lmoon.myspider.vo.ApeFileVO;

public class ApeFileDAOH2DBImpl implements ApeFileDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(ApeFileDAOH2DBImpl.class);

	@Override
	public int createTable() {
		Connection connection = DbConnectionUtil.getConnection();
		String sql = "CREATE TABLE IF NOT EXISTS apefile (songId int PRIMARY KEY NOT NULL,"
				+ "name varchar(64),title varchar(64),size varchar(16),downloadType int,createTime timestamp,updateTime timestamp)";
		return JdbcTemplate.executeUpdate(connection, sql, new Object[0]);
	}
	
	public int dropTable() {
		Connection connection = DbConnectionUtil.getConnection();
		String sql = "DROP TABLE apefile";
		return JdbcTemplate.executeUpdate(connection, sql, new Object[0]);
	}

	@Override
	public int insert(ApeFileVO vo) {
		String sql = "insert into apefile(songId,name,title,size,downloadType,createTime,updateTime) values (?,?,?,?,?,?,?)";
		Object[] params = new Object[] { vo.getSongId(),vo.getName(),vo.getTitle(),vo.getSize(), vo.getDownType().get(), new Timestamp(vo.getCreateTime()), new Timestamp(vo.getUpdateTime())};
		return JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
	}

	@Override
	public List<ApeFileVO> select(int pageNo, int pageSize) {
		List<Object> paramsList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select * from apefile ");
		sql.append(" limit ?,?");
		paramsList.add((pageNo - 1) * pageSize);
		paramsList.add(pageSize);
		return JdbcTemplate.queryForList(DbConnectionUtil.getConnection(), sql.toString(), new RowMapper<ApeFileVO>() {

			@Override
			public ApeFileVO mapRow(ResultSet rs, int rowIndex) throws SQLException {
				ApeFileVO vo = new ApeFileVO();
				vo.setSize(rs.getString("size"));
				vo.setSongId(rs.getInt("songId"));
				vo.setName(rs.getString("name"));
				vo.setTitle(rs.getString("title"));
				vo.setDownType(DownloadUtil.getDownloadType(rs.getInt("downloadType")));
				vo.setCreateTime(rs.getTimestamp("createTime").getTime());
				vo.setUpdateTime(rs.getTimestamp("updateTime").getTime());
				return vo;
			}
		}, paramsList.toArray());
	}

	@Override
	public int count() {
		StringBuffer sql = new StringBuffer("select count(1) from apefile");
		return JdbcTemplate.queryForInt(DbConnectionUtil.getConnection(), sql.toString(), new Object[0]);
	}

	@Override
	public int update(ApeFileVO vo) {
		String sql = "update apefile set name=?, downloadType = ?,updateTime = ? where songId=?";
		Object[] params = new Object[] { vo.getName(),vo.getDownType().get(), new Timestamp(vo.getUpdateTime()),vo.getSongId()};
		return JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
	}

	@Override
	public ApeFileVO select(int songId) {
		String sql = "select * from apefile where songId = ? limit 1";
		Object[] params = new Object[] {songId};
		return JdbcTemplate.queryForT(DbConnectionUtil.getConnection(), sql, new ResultSetExtractor<ApeFileVO>() {

			@Override
			public ApeFileVO extract(ResultSet rs) throws SQLException {
				ApeFileVO vo = new ApeFileVO();
				vo.setSize(rs.getString("size"));
				vo.setSongId(rs.getInt("songId"));
				vo.setName(rs.getString("name"));
				vo.setTitle(rs.getString("title"));
				vo.setDownType(DownloadUtil.getDownloadType(rs.getInt("downloadType")));
				vo.setCreateTime(rs.getTimestamp("createTime").getTime());
				vo.setUpdateTime(rs.getTimestamp("updateTime").getTime());
				return vo;
			}
		}, params);
	}

}
