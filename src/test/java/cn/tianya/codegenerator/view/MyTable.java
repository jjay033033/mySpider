/**
 * @copyright 2017 tianya.cn
 */
package cn.tianya.codegenerator.view;

import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * @author guozy
 * @date 2017-6-20
 * 
 */
public class MyTable extends javax.swing.JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1103674273393823191L;

	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 */
	public MyTable() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param numRows
	 * @param numColumns
	 */
	public MyTable(int numRows, int numColumns) {
		super(numRows, numColumns);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rowData
	 * @param columnNames
	 */
	public MyTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dm
	 * @param cm
	 * @param sm
	 */
	public MyTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dm
	 * @param cm
	 */
	public MyTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dm
	 */
	public MyTable(TableModel dm) {
		super(dm);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rowData
	 * @param columnNames
	 */
	public MyTable(Vector rowData, Vector columnNames) {
		super(rowData, columnNames);
		// TODO Auto-generated constructor stub
	}
	
	

}
