/**
 * 
 */
package top.lmoon.myspider.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class SqlUtil {
	private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static ThreadLocal threadlocal = new ThreadLocal() {
		protected Object initialValue() {
			return new SimpleDateFormat(SqlUtil.DATE_FORMAT);
		}
	};

	public static DateFormat getDateFormat() {
		return (DateFormat) threadlocal.get();
	}

	public static void setDateFormat(String format) {
		DATE_FORMAT = format;
	}

	@Deprecated
	public static String makeSelectAllSql(String tableName,
			Map<String, Object> where) {
		int ws = where != null ? where.size() : 0;
		StringBuilder sql = new StringBuilder(64 + ws * 32);
		sql.append("\n select * from ").append(tableName);
		sql.append("\n where ");
		int index = 0;
		Iterator arg5 = where.keySet().iterator();

		while (arg5.hasNext()) {
			String key = (String) arg5.next();
			Object v = where.get(key);
			sql.append(key).append("=").append(sqlValue(v));
			++index;
			if (index < ws) {
				sql.append("\n   and ");
			}
		}

		return sql.toString();
	}

	@Deprecated
	public static String makeInsertSql(String tableName,
			Map<String, Object> columns) {
		return getInsertSql(tableName, columns);
	}

	@Deprecated
	public static String makeUpdateSql(String tableName,
			Map<String, Object> set, Map<String, Object> where) {
		if (set == null) {
			throw new IllegalArgumentException("update的字段集合不能为null");
		} else {
			int ss = set.size();
			int ws = where != null ? where.size() : 0;
			StringBuilder sql = new StringBuilder(64 + ss * 32 + ws * 32);
			sql.append("\n update ").append(tableName).append("\n set ");
			int index = 0;
			Iterator arg7 = set.keySet().iterator();

			String key;
			Object v;
			while (arg7.hasNext()) {
				key = (String) arg7.next();
				v = set.get(key);
				sql.append("\t").append(key).append("=").append(sqlValue(v));
				++index;
				if (index < ss) {
					sql.append(",\n");
				}
			}

			if (ws == 0) {
				return sql.toString();
			} else {
				sql.append("\n where ");
				index = 0;
				arg7 = where.keySet().iterator();

				while (arg7.hasNext()) {
					key = (String) arg7.next();
					v = where.get(key);
					sql.append(key).append("=").append(sqlValue(v));
					++index;
					if (index < ws) {
						sql.append("\n   and ");
					}
				}

				return sql.toString();
			}
		}
	}

	@Deprecated
	public static String makeDeleteSql(String tableName,
			Map<String, Object> where) {
		int ws = where != null ? where.size() : 0;
		StringBuilder sql = new StringBuilder(64 + ws * 32);
		sql.append("\n delete from ").append(tableName);
		if (ws == 0) {
			return sql.toString();
		} else {
			sql.append("\n where ");
			int index = 0;
			Iterator arg5 = where.keySet().iterator();

			while (arg5.hasNext()) {
				String key = (String) arg5.next();
				Object v = where.get(key);
				sql.append(key).append("=").append(sqlValue(v));
				++index;
				if (index < ws) {
					sql.append("\n   and ");
				}
			}

			return sql.toString();
		}
	}

	@Deprecated
	public static String makeDynamicSql(String dynamicSql,
			Map<String, Object> params) {
		int ps = dynamicSql.length();
		StringBuilder sql = new StringBuilder(128 + ps * 2);
		StringBuilder item = new StringBuilder(128);
		boolean isDynamicStart = false;
		boolean c = false;

		for (int ii = 0; ii < ps; ++ii) {
			char arg8 = dynamicSql.charAt(ii);
			if (isDynamicStart) {
				if (125 == arg8) {
					isDynamicStart = false;
					String ii1 = item.toString();
					item.setLength(0);
					sql.append(makeDynamicItem(ii1, params));
				} else {
					item.append(arg8);
				}
			} else if (123 == arg8) {
				isDynamicStart = true;
			} else {
				sql.append(arg8);
			}
		}

		if (item.length() > 0) {
			String arg9 = item.toString();
			sql.append(makeDynamicItem(arg9, params));
		}

		return sql.toString();
	}

	@Deprecated
	private static String makeDynamicItem(String dynamicItem,
			Map<String, Object> params) {
		int ps = dynamicItem.length();
		StringBuilder sqlItem = new StringBuilder(64 + ps * 2);
		StringBuilder param = new StringBuilder(64);
		boolean isParamStart = false;
		boolean c = false;
		char flag = 0;

		for (int p = 0; p < ps; ++p) {
			char arg10 = dynamicItem.charAt(p);
			if (isParamStart) {
				if (32 != arg10 && 10 != arg10 && 9 != arg10 && 13 != arg10) {
					param.append(arg10);
				} else {
					isParamStart = false;
					String v = param.toString();
					param.setLength(0);
					Object v1 = params.get(v);
					if (36 == flag) {
						if (v1 == null) {
							return "";
						}

						sqlItem.append(sqlValue(v1));
					} else if (35 == flag) {
						sqlItem.append(sqlValue(v1));
					} else if (38 == flag) {
						if (v1 == null) {
							return "";
						}

						sqlItem.append(v1);
					}
				}
			} else if (36 != arg10 && 35 != arg10 && 38 != arg10 && 63 != arg10
					&& 64 != arg10) {
				sqlItem.append(arg10);
			} else {
				flag = arg10;
				isParamStart = true;
			}
		}

		if (param.length() > 0) {
			String arg11 = param.toString();
			Object arg12 = params.get(arg11);
			if (36 == flag) {
				if (arg12 == null) {
					return "";
				}

				sqlItem.append(sqlValue(arg12));
			} else if (35 == flag) {
				sqlItem.append(sqlValue(arg12));
			}
		}

		return sqlItem.toString();
	}

	private static String sqlValue(String value) {
		if (value == null) {
			return "\'\'";
		} else {
			String v = value.trim();
			int vs = v.length();
			StringBuilder sb = new StringBuilder(2 + vs * 2);
			boolean c = false;
			sb.append('\'');

			for (int i = 0; i < vs; ++i) {
				char arg5 = v.charAt(i);
				if (39 == arg5) {
					sb.append('\'');
					sb.append('\'');
				} else if (92 == arg5) {
					sb.append('\\');
					sb.append('\\');
				} else {
					sb.append(arg5);
				}
			}

			sb.append('\'');
			return sb.toString();
		}
	}

	private static String sqlValue(Date value) {
		return "\'" + getDateFormat().format(value) + "\'";
	}

	public static Date parseObject(String value) {
		try {
			return (Date) getDateFormat().parseObject(value);
		} catch (ParseException arg1) {
			arg1.printStackTrace();
			return null;
		}
	}

	public static String sqlValue(Date value, SimpleDateFormat simpleDateFormat) {
		return "\'" + simpleDateFormat.format(value) + "\'";
	}

	private static String sqlValue(Timestamp value) {
		return "\'" + value + "\'";
	}

	private static <T> String sqlValuePrimitive(T value) {
		return value.toString();
	}

	private static <T> String sqlValueArray(T[] value) {
		if (value == null) {
			return "\'\'";
		} else {
			StringBuilder sql = new StringBuilder(64 + value.length * 32);

			for (int i = 0; i < value.length; ++i) {
				sql.append(sqlValue(value[i]));
				if (i < value.length - 1) {
					sql.append(",");
				}
			}

			return sql.toString();
		}
	}

	public static String sqlValue(Object value) {
		if (value == null) {
			return "\'\'";
		} else if (value instanceof String) {
			return sqlValue((String) value);
		} else if (value instanceof Date) {
			return sqlValue((Date) value);
		} else if (value instanceof Timestamp) {
			return sqlValue((Timestamp) value);
		} else if (!(value instanceof Integer) && !(value instanceof Long)
				&& !(value instanceof Short) && !(value instanceof Float)
				&& !(value instanceof Double)) {
			if (value instanceof List) {
				return sqlValueArray(((List) value).toArray());
			} else if (value.getClass().isArray()) {
				Class ct = value.getClass().getComponentType();
				return ct == String.class ? sqlValueArray((String[]) String[].class
						.cast(value))
						: (ct == Integer.TYPE ? sqlValueArray(boxedPrimitiveArray((int[]) value))
								: (ct == Long.TYPE ? sqlValueArray(boxedPrimitiveArray((long[]) value))
										: (ct == Short.TYPE ? sqlValueArray(boxedPrimitiveArray((short[]) value))
												: (ct == Float.TYPE ? sqlValueArray(boxedPrimitiveArray((float[]) value))
														: (ct == Double.TYPE ? sqlValueArray(boxedPrimitiveArray((double[]) value))
																: sqlValueArray((Object[]) value))))));
			} else {
				return "\'" + value.toString() + "\'";
			}
		} else {
			return sqlValuePrimitive(value);
		}
	}

	private static Integer[] boxedPrimitiveArray(int[] array) {
		Integer[] result = new Integer[array.length];

		for (int i = 0; i < array.length; ++i) {
			result[i] = Integer.valueOf(array[i]);
		}

		return result;
	}

	private static Short[] boxedPrimitiveArray(short[] array) {
		Short[] result = new Short[array.length];

		for (int i = 0; i < array.length; ++i) {
			result[i] = Short.valueOf(array[i]);
		}

		return result;
	}

	private static Long[] boxedPrimitiveArray(long[] array) {
		Long[] result = new Long[array.length];

		for (int i = 0; i < array.length; ++i) {
			result[i] = Long.valueOf(array[i]);
		}

		return result;
	}

	private static Float[] boxedPrimitiveArray(float[] array) {
		Float[] result = new Float[array.length];

		for (int i = 0; i < array.length; ++i) {
			result[i] = Float.valueOf(array[i]);
		}

		return result;
	}

	private static Double[] boxedPrimitiveArray(double[] array) {
		Double[] result = new Double[array.length];

		for (int i = 0; i < array.length; ++i) {
			result[i] = Double.valueOf(array[i]);
		}

		return result;
	}

	public static String getSql(String prepareSql, Object... params) {
		if (params != null) {
			int length = prepareSql.length();
			StringBuilder result = new StringBuilder(2 + length * 2);
			int paramIndex = 0;

			for (int i = 0; i < length; ++i) {
				char c = prepareSql.charAt(i);
				if (c == 63) {
					result.append(sqlValue(params[paramIndex]));
					++paramIndex;
				} else {
					result.append(c);
				}
			}

			return result.toString();
		} else {
			return prepareSql;
		}
	}

	public static String getSqlByList(String prepareSql, List<Object> params) {
		if (params != null) {
			int length = prepareSql.length();
			StringBuilder result = new StringBuilder(2 + length * 2);
			int paramIndex = 0;

			for (int i = 0; i < length; ++i) {
				char c = prepareSql.charAt(i);
				if (c == 63) {
					result.append(sqlValue(params.get(paramIndex)));
					++paramIndex;
				} else {
					result.append(c);
				}
			}

			return result.toString();
		} else {
			return prepareSql;
		}
	}

	public static String getInsertSql(String tableName,
			Map<String, Object> columns) {
		int columnSize = columns.size();
		StringBuilder sql = new StringBuilder(64 + columnSize * 32);
		sql.append("\n insert into ").append(tableName);
		sql.append(" ( ");
		int index = 0;
		Iterator arg5 = columns.keySet().iterator();

		String item;
		while (arg5.hasNext()) {
			item = (String) arg5.next();
			sql.append(item);
			++index;
			if (index != columnSize) {
				sql.append(",");
			}
		}

		sql.append(" )\n");
		sql.append(" values ( ");
		index = 0;
		arg5 = columns.keySet().iterator();

		while (arg5.hasNext()) {
			item = (String) arg5.next();
			Object value = columns.get(item);
			sql.append(sqlValue(value));
			++index;
			if (index != columnSize) {
				sql.append(",");
			}
		}

		sql.append(" )");
		return sql.toString();
	}
}