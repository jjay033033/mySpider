/**
 * 
 */
package top.lmoon.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.lmoon.jdbc.CloseUtil;
import top.lmoon.jdbc.RowMapper;

/**
 * JDBC的普通常用操作的模板类, 普通修改、删除、查询 可获支持，旨在减少生成PreparedStatement, ResultSet,
 * 一般格式的结果解析，及最后的释放资源等等重复操作，使 工程师可专注于SQL编写及数据处理
 * 
 * @author PTY
 * 
 */
public class JdbcTemplate {
	
	/**
	 * 批量修改
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return int
	 */
	public static int[] batchUpdate(Connection conn, String sql, List<Object[]> batchParams) {

		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			
			for (Object[] params : batchParams) {
				setPsParams(ps, params);
				ps.addBatch();
			}
			return ps.executeBatch();

		} catch (Exception e) {
			throw new RuntimeException("JdbcTemplate批量修改失败！", e);
		} finally {
			CloseUtil.closeSilently(ps);
			CloseUtil.closeSilently(conn);
		}
	}

	/**
	 * 修改模板方法 执行无参数SQL时建议第三个参数为new Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return int
	 */
	public static int executeUpdate(Connection conn, String sql, Object... params) {

		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			setPsParams(ps, params);

			return ps.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException("JdbcTemplate执行修改失败！", e);
		} finally {
			CloseUtil.closeSilently(ps);
			CloseUtil.closeSilently(conn);
		}
	}

	/**
	 * 插入模板方法，执行数据插入并返回自增加字段的值 执行无参数SQL时建议第三个参数为new Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return int
	 */
	public static int insertReturnAutoGenerateKey(Connection conn, String insertSql, Object... params) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
			setPsParams(ps, params);

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs != null && rs.next()) {
				return rs.getInt(1);
			}

			return 0;

		} catch (Exception e) {
			throw new RuntimeException("JdbcTemplate执行插入失败！", e);
		} finally {
			CloseUtil.closeSilently(rs);
			CloseUtil.closeSilently(ps);
			CloseUtil.closeSilently(conn);
		}
	}

	/**
	 * 查询模板方法，接收ResultSetHandler循环处理每行数据 执行无参数SQL时建议第三个参数为new Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return int
	 */
	public static <T> List<T> queryForList(Connection conn, String sql, RowMapper<T> rowMapper, Object... params) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		List<T> dataList = new ArrayList<T>();
		try {
			ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			setPsParams(ps, params);

			rs = ps.executeQuery();
			int rowIndex = 0;
			while (rs.next()) {
				dataList.add(rowMapper.mapRow(rs, rowIndex++));
			}

		} catch (Exception e) {
			throw new RuntimeException("JdbcTemplate执行查询失败！", e);
		} finally {
			CloseUtil.closeSilently(rs);
			CloseUtil.closeSilently(ps);
			CloseUtil.closeSilently(conn);
		}
		return dataList;
	}

	/**
	 * 查询模板方法，查询结果返回int 执行无参数SQL时建议第三个参数为new Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return int
	 */
	public static int queryForInt(Connection conn, String sql, Object... params) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			setPsParams(ps, params);

			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			throw new RuntimeException("JdbcTemplate执行查询失败！", e);
		} finally {
			CloseUtil.closeSilently(rs);
			CloseUtil.closeSilently(ps);
			CloseUtil.closeSilently(conn);
		}
		return 0;
	}
	
	/**
	 * 查询模板方法，查询结果返回String 执行无参数SQL时建议第三个参数为new Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return String
	 */
	public static String queryForString(Connection conn, String sql, Object... params) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			setPsParams(ps, params);

			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}

		} catch (Exception e) {
			throw new RuntimeException("JdbcTemplate执行查询失败！", e);
		} finally {
			CloseUtil.closeSilently(rs);
			CloseUtil.closeSilently(ps);
			CloseUtil.closeSilently(conn);
		}
		return null;
	}

	/**
	 * 查询模板方法，接收ResultSetExtractor处理所有行的数据 执行无参数SQL时建议第三个参数为new Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return int
	 */
	public static <T> T queryForT(Connection conn, String sql, ResultSetExtractor<T> rse, Object... params) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			setPsParams(ps, params);

			// 处理ResultSet结果
			rs = ps.executeQuery();

			return rse.extract(rs);

		} catch (Exception e) {
			throw new RuntimeException("JdbcTemplate执行查询失败！", e);
		} finally {
			CloseUtil.closeSilently(rs);
			CloseUtil.closeSilently(ps);
			CloseUtil.closeSilently(conn);
		}
	}

	/**
	 * 查询并返回每行为Object数组的数据集合(ArrayList) 执行无参数SQL时建议第三个参数为new Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return List<Object[]>
	 */
	public static List<Object[]> queryForArrayRow(Connection conn, String sql, Object... params) {

		ResultSetExtractor<List<Object[]>> rse = new ResultSetExtractor<List<Object[]>>() {

			public List<Object[]> extract(ResultSet rs) throws SQLException {

				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();

				List<Object[]> dataList = new ArrayList<Object[]>();
				while (rs.next()) {

					Object[] row = new Object[columnCount];
					for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
						row[columnIndex] = rs.getObject(columnIndex + 1);
					}

					dataList.add(row);
				}

				return dataList;
			}
		};
		return queryForT(conn, sql, rse, params);
	}
	
	/**
	 * 查询并返回每行为Object的数据集合(ArrayList) 执行无参数SQL时建议第三个参数为new Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return List<Object>
	 */
	public static List<Object> queryForObjectArrayRow(Connection conn, String sql, Object... params) {

		ResultSetExtractor<List<Object>> rse = new ResultSetExtractor<List<Object>>() {

			public List<Object> extract(ResultSet rs) throws SQLException {

				List<Object> dataList = new ArrayList<Object>();
				while (rs.next()) {
					dataList.add(rs.getObject(1));
				}

				return dataList;
			}
		};
		return queryForT(conn, sql, rse, params);
	}
	
	/**
	 * 查询并返回每行为Integer的数据集合(ArrayList) 执行无参数SQL时建议第三个参数为new Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return List<Object>
	 */
	public static List<Integer> queryForIntegerArrayRow(Connection conn, String sql, Object... params) {

		ResultSetExtractor<List<Integer>> rse = new ResultSetExtractor<List<Integer>>() {

			public List<Integer> extract(ResultSet rs) throws SQLException {

				List<Integer> dataList = new ArrayList<Integer>();
				while (rs.next()) {
					dataList.add(rs.getInt(1));
				}

				return dataList;
			}
		};
		return queryForT(conn, sql, rse, params);
	}

	/**
	 * 查询并返回每行为Map<String, Object>的数据集合(ArrayList) 执行无参数SQL时建议第三个参数为new
	 * Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return List<Object[]>
	 */
	public static List<Map<String, Object>> queryForMapRow(Connection conn, String sql, Object... params) {

		ResultSetExtractor<List<Map<String, Object>>> rse = new ResultSetExtractor<List<Map<String, Object>>>() {

			public List<Map<String, Object>> extract(ResultSet rs) throws SQLException {

				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				String[] columnNames = new String[columnCount];
				for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
					// 不用getColumnName而是getColumnLabel可兼容SQL语句中的字段别名
					columnNames[columnIndex] = rsmd.getColumnLabel(columnIndex + 1);
				}

				List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
				while (rs.next()) {

					Map<String, Object> row = new HashMap<String, Object>();
					for (String columnLabel : columnNames) {
						row.put(columnLabel, rs.getObject(columnLabel));
					}

					dataList.add(row);
				}

				return dataList;
			}
		};
		return queryForT(conn, sql, rse, params);
	}

	/**
	 * 查询并返回每行为Map<String, Object>的数据集合(ArrayList) 执行无参数SQL时建议第三个参数为new
	 * Object[]
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return List<Object[]>
	 */
	public static <T> T queryForSingleRow(Connection conn, String sql, RowMapper<T> rm, Object... params) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			setPsParams(ps, params);

			rs = ps.executeQuery();
			if (rs.next()) {
				return rm.mapRow(rs, 0);
			}

			return null;

		} catch (Exception e) {
			throw new RuntimeException("JdbcTemplate执行查询失败！", e);
		} finally {
			CloseUtil.closeSilently(rs);
			CloseUtil.closeSilently(ps);
			CloseUtil.closeSilently(conn);
		}
	}
	
	/**
	 * 查询并返回Map<String, Object>的数据 执行无参数SQL时建议第三个参数为new
	 * Object[]
	 * @param conn
	 * @param sql
	 * @param params
	 * @return Map<String,Object>
	 */
	public static Map<String,Object> queryForSingleRow(Connection conn, String sql, Object... params){
		return queryForSingleRow(conn, sql, new RowMapper<Map<String,Object>>() {

			@Override
			public Map<String,Object> mapRow(ResultSet rs,
					int rowIndex) throws SQLException {
				Map<String,Object> map = new HashMap<String, Object>();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for(int columnIndex=0;columnIndex<columnCount;columnIndex++){
					map.put(rsmd.getColumnLabel(columnIndex + 1),rs.getObject(columnIndex + 1));					
				}
				return map;
			}
		}, params);
	}

	/**
	 * 设置SQL的参数
	 * 
	 * @param ps
	 * @param params
	 * @throws SQLException
	 */
	private static void setPsParams(PreparedStatement ps, Object... params) throws SQLException {
		if (params != null) {
			for (int index = 0; index < params.length; index++) {
				if (params[index] != null) {
					ps.setObject(index + 1, params[index]);
				} else {
					ps.setNull(index + 1, Types.NULL);
				}
			}
		}
	}
	
	/**
	 * 批量更新（事务处理）
	 * @param conn
	 * @param sqls
	 * @return
	 * @throws SQLException
	 */
	public static int[] executeUpdateBatchCommit(Connection conn,String... sqls) throws SQLException {		
		if(sqls==null||sqls.length==0){
			return null;
		}
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			for(String sql:sqls){
				stmt.addBatch(sql);
			}			
			conn.setAutoCommit(false);
			int[] result = stmt.executeBatch();
			conn.commit();
			return result;
		} catch(Exception e){
			try{
				conn.rollback();
			}catch(Exception ex){}			
			throw new SQLException("批量更新出错！",e);
		}finally{
			conn.setAutoCommit(true);
			CloseUtil.closeSilently(stmt);
			CloseUtil.closeSilently(conn);				
		}		
	}
	
}
