/**
 * 
 */
package top.lmoon.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author PTY
 *
 */
public interface RowMapper<T> {
	
	/**
	 * 行处理，只处理ResultSet的一行数据
	 * 
	 * @param rs
	 * @param rowIndex
	 * @return Object
	 * @throws SQLException
	 */
	public T mapRow(ResultSet rs, int rowIndex) throws SQLException;

}
