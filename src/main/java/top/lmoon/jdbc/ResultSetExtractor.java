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
public interface ResultSetExtractor<T> {
	
	/**
	 * ResultSet抽取，需要在方法内部处理ResultSet所有数据
	 * 
	 * @param rs
	 * @param rowIndex
	 * @return Object
	 * @throws SQLException
	 */
	public T extract(ResultSet rs) throws SQLException;

}
