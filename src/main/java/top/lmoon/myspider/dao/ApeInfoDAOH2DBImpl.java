package top.lmoon.myspider.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.jdbc.JdbcTemplate;
import top.lmoon.jdbc.ResultSetExtractor;
import top.lmoon.jdbc.RowMapper;
import top.lmoon.myspider.util.DbConnectionUtil;
import top.lmoon.myspider.util.DownloadUtil;
import top.lmoon.myspider.util.DownloadUtil.downloadType;
import top.lmoon.myspider.util.SqlUtil;
import top.lmoon.myspider.vo.ApeInfoVO;

/**
 * apeInfo数据库操作类（mysql语法）
 * 
 * @author LMoon
 * @date 2017年10月4日
 *
 */
public class ApeInfoDAOH2DBImpl implements ApeInfoDAO {

	private static final Logger logger = LoggerFactory.getLogger(ApeInfoDAOH2DBImpl.class);

	public int insert(ApeInfoVO vo) {
		String sql = "insert into apeinfo(songId,singerId,songIdForSinger,singer,"
				+ "title,link,pw,album,size,language,remark,url,name,downloadType,createTime,updateTime) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = new Object[] { vo.getSongId(), vo.getSingerId(), vo.getSongIdForSinger(), vo.getSinger(),
				vo.getTitle(), vo.getLink(), vo.getPw(), vo.getAlbum(), vo.getSize(), vo.getLanguage(), vo.getRemark(),
				vo.getUrl(),vo.getName(), vo.getDownType().get(), new Timestamp(vo.getCreateTime()), new Timestamp(vo.getUpdateTime()) };
		return JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
	}

	public List<ApeInfoVO> select(int pageNo, int pageSize, String singer, String title) {
		List<Object> paramsList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select * from apeinfo where 1=1 ");
		if (StringUtils.isNotBlank(singer)) {
			sql.append(" and singer like ? ");
			paramsList.add("%" + singer + "%");
		}
		if (StringUtils.isNotBlank(title)) {
			sql.append(" and title like ? ");
			paramsList.add("%" + title + "%");
		}
		sql.append(" order by songId limit ?,? ");
		paramsList.add((pageNo - 1) * pageSize);
		paramsList.add(pageSize);
		return JdbcTemplate.queryForList(DbConnectionUtil.getConnection(), sql.toString(), new RowMapper<ApeInfoVO>() {

			@Override
			public ApeInfoVO mapRow(ResultSet rs, int rowIndex) throws SQLException {
				ApeInfoVO vo = new ApeInfoVO();
				vo.setAlbum(rs.getString("album"));
				vo.setLanguage(rs.getString("language"));
				vo.setLink(rs.getString("link"));
				vo.setPw(rs.getString("pw"));
				vo.setRemark(rs.getString("remark"));
				vo.setSinger(rs.getString("singer"));
				vo.setSingerId(rs.getInt("singerId"));
				vo.setSize(rs.getString("size"));
				vo.setSongId(rs.getInt("songId"));
				vo.setSongIdForSinger(rs.getInt("songIdForSinger"));
				vo.setTitle(rs.getString("title"));
				vo.setUrl(rs.getString("url"));
				vo.setName(rs.getString("name"));
				vo.setDownType(DownloadUtil.getDownloadType(rs.getInt("downloadType")));
				vo.setCreateTime(rs.getTimestamp("createTime").getTime());
				vo.setUpdateTime(rs.getTimestamp("updateTime").getTime());
				return vo;
			}
		}, paramsList.toArray());
	}

	public int createTable() {
		Connection connection = DbConnectionUtil.getConnection();
		String sql = "CREATE TABLE IF NOT EXISTS apeinfo (songId int PRIMARY KEY NOT NULL,singerId int,songIdForSinger int,"
				+ "singer varchar(64),title varchar(64),link varchar(64),pw varchar(8),"
				+ "album varchar(64),size varchar(16),language varchar(16),remark varchar(1024),"
				+ "url varchar(64),name varchar(64),downloadType int,createTime timestamp,updateTime timestamp)";
		return JdbcTemplate.executeUpdate(connection, sql, new Object[0]);
	}

	@Override
	public int count(String singer, String title) {
		List<Object> paramsList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select count(1) from apeinfo where 1=1 ");
		if (StringUtils.isNotBlank(singer)) {
			sql.append(" and singer like ? ");
			paramsList.add("%" + singer + "%");
		}
		if (StringUtils.isNotBlank(title)) {
			sql.append(" and title like ? ");
			paramsList.add("%" + title + "%");
		}
		return JdbcTemplate.queryForInt(DbConnectionUtil.getConnection(), sql.toString(), paramsList.toArray());
	}

	@Override
	public int selectMaxSingerId() {
		String sql = "select max(singerId) from apeinfo";
		return JdbcTemplate.queryForInt(DbConnectionUtil.getConnection(), sql, new Object[0]);
	}

	@Override
	public ConcurrentHashMap<String, Integer> selectSingerIdMap() {
		String sql = "select singerId,singer from apeinfo group by singerId";
		return JdbcTemplate.queryForT(DbConnectionUtil.getConnection(), sql,
				new ResultSetExtractor<ConcurrentHashMap<String, Integer>>() {

					@Override
					public ConcurrentHashMap<String, Integer> extract(ResultSet rs) throws SQLException {
						ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
						while (rs != null && rs.next()) {
							String singer = rs.getString("singer");
							int singerId = rs.getInt("singerId");
							if (!StringUtils.isBlank(singer) && singerId != 0) {
								map.put(singer, singerId);
							}
						}
						return map;
					}
				}, new Object[0]);
	}

	@Override
	public int selectMaxSongId() {
		String sql = "select max(songId) from apeinfo";
		return JdbcTemplate.queryForInt(DbConnectionUtil.getConnection(), sql, new Object[0]);
	}

	@Override
	public ConcurrentHashMap<Integer, Integer> selectSongIdForSingerMap() {
		String sql = "select singerId,max(songIdForSinger) songIdForSinger from apeinfo group by singerId";
		return JdbcTemplate.queryForT(DbConnectionUtil.getConnection(), sql,
				new ResultSetExtractor<ConcurrentHashMap<Integer, Integer>>() {

					@Override
					public ConcurrentHashMap<Integer, Integer> extract(ResultSet rs) throws SQLException {
						ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
						while (rs != null && rs.next()) {
							int songIdForSinger = rs.getInt("songIdForSinger");
							int singerId = rs.getInt("singerId");
							if (songIdForSinger != 0 && singerId != 0) {
								map.put(singerId, songIdForSinger);
							}
						}
						return map;
					}
				}, new Object[0]);
	}

	@Override
	public int update(ApeInfoVO vo) {
		String sql = "update apeinfo set name=?, downloadType = ?,updateTime = ? where songId=?";
		Object[] params = new Object[] { vo.getName(),vo.getDownType().get(), new Timestamp(vo.getUpdateTime()),vo.getSongId()};
		return JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
	}

	@Override
	public ApeInfoVO select(int songId) {
		String sql = "select * from apeinfo where songId = ? limit 1";
		Object[] params = new Object[] {songId};
		return JdbcTemplate.queryForT(DbConnectionUtil.getConnection(), sql, new ResultSetExtractor<ApeInfoVO>() {

			@Override
			public ApeInfoVO extract(ResultSet rs) throws SQLException {
				ApeInfoVO vo = new ApeInfoVO();
				vo.setAlbum(rs.getString("album"));
				vo.setLanguage(rs.getString("language"));
				vo.setLink(rs.getString("link"));
				vo.setPw(rs.getString("pw"));
				vo.setRemark(rs.getString("remark"));
				vo.setSinger(rs.getString("singer"));
				vo.setSingerId(rs.getInt("singerId"));
				vo.setSize(rs.getString("size"));
				vo.setSongId(rs.getInt("songId"));
				vo.setSongIdForSinger(rs.getInt("songIdForSinger"));
				vo.setTitle(rs.getString("title"));
				vo.setUrl(rs.getString("url"));
				vo.setName(rs.getString("name"));
				vo.setDownType(DownloadUtil.getDownloadType(rs.getInt("downloadType")));
				vo.setCreateTime(rs.getTimestamp("createTime").getTime());
				vo.setUpdateTime(rs.getTimestamp("updateTime").getTime());
				return vo;
			}
		}, params);
	}

	@Override
	public int update(int songId, downloadType type) {
		String sql = "update apeinfo set downloadType = ?,updateTime = ? where songId=?";
		Object[] params = new Object[] { type.get(), new Timestamp(System.currentTimeMillis()),songId};
		return JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
	}
	
	//name varchar(64),downloadType int,createTime timestamp,updateTime timestamp
	public void alterTable(){
		String sql = "alter table apeinfo add name varchar(64)";
		Object[] params = new Object[0];
		JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
		
		sql = "alter table apeinfo add downloadType int";
		JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
		
		sql = "alter table apeinfo add createTime timestamp";
		JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
		
		sql = "alter table apeinfo add updateTime timestamp ";
		JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
		
	}
	
	public int init() {
		String sql = "update apeinfo set name=?, downloadType = ?,updateTime = ?,createTime=?";
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Object[] params = new Object[] { "",downloadType.INITIAL.get(), timestamp,timestamp};
		return JdbcTemplate.executeUpdate(DbConnectionUtil.getConnection(), sql, params);
	}
	
	public int dropApefileTable() {
		Connection connection = DbConnectionUtil.getConnection();
		String sql = "DROP TABLE apefile";
		return JdbcTemplate.executeUpdate(connection, sql, new Object[0]);
	}

}
