/**
 * 
 */
package cn.tianya.codegenerator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import cn.tianya.codegenerator.BeanInfo;
import cn.tianya.codegenerator.db.DbTableVOCodeGenerator;
import cn.tianya.codegenerator.util.DbUtil;
import cn.tianya.codegenerator.util.JavaFileUtils;

/**
 * @author PTY
 * 
 */
public class TableWindow {

	private JComboBox typeComboBox;
	private JTextField serverField;
	private JTextField dbNameField;
	private JTextField portField;
	private JTextField userNameField;
	private JTextField passwordField;
	private JTextArea textArea;
	private JFrame frame;

	private JTable jTable;
	private DefaultTableModel tableModel;

	public TableWindow() {

		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		initWindow();
	}

	private void initWindow() {

		frame = new JFrame("数据库表");

		JMenuBar menuBar = menuBar();
		frame.setJMenuBar(menuBar);

		initCenterPanel();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		initFrame(frame);
	}
	
	private void initCenterPanel(){
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		JPanel centerPanel = centerPanel();
		contentPanel.add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = southPanel();
		contentPanel.add(southPanel, BorderLayout.SOUTH);

		frame.setContentPane(contentPanel);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * @return
	 */
	private JMenuBar menuBar() {

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorder(BorderFactory.createEmptyBorder(3, 2, 5, 2));

		JMenu dsMenu = new JMenu("数据源");
		JMenuItem createMenu = new JMenuItem("创建源");
		createMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				KeyEvent.CTRL_MASK));
		createMenu.addActionListener(new createMenuAction());
		dsMenu.add(createMenu);
		dsMenu.add(new JSeparator(SwingConstants.HORIZONTAL));

		JMenuItem menuItem = new JMenuItem("从文件导入源");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				KeyEvent.CTRL_MASK));
		menuItem.addActionListener(new openPropsAction());
		dsMenu.add(menuItem);

//		JMenuItem menuItem2 = new JMenuItem("源文件导出");
//		menuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
//				KeyEvent.CTRL_MASK));
//		dsMenu.add(menuItem2);
		menuBar.add(dsMenu);

		JMenu voMenu = new JMenu("VO生成");
		JMenuItem tablesMenu = new JMenuItem("选择数据库表");
		tablesMenu.addActionListener(new ViewTablesAction());
		voMenu.add(tablesMenu);
		menuBar.add(voMenu);

		// menuBar.setHelpMenu(new JMenu("帮助"));

		return menuBar;
	}

	/**
	 * @param frame
	 */
	private void initFrame(JFrame frame) {
		frame.pack();
		frame.setVisible(true);
		GraphicsEnvironment environment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice device = environment.getDefaultScreenDevice();
		GraphicsConfiguration configuration = device.getDefaultConfiguration();
		Rectangle bounds = configuration.getBounds();
		frame.setLocation(bounds.x + 350, bounds.y + 200);
		frame.setSize(new Dimension(700, 500));
		BorderLayout manager = new BorderLayout(5, 5);
		frame.setLayout(manager);
		// frame.setFont(new Font("SansSerif", Font.PLAIN, 2));
	}

	/**
	 * @return
	 */
	private JPanel southPanel() {
		JPanel southPanel = new JPanel(new GridLayout(1, 1));
		southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		textArea = new JTextArea(10, 60);
		textArea.setAutoscrolls(true);
		textArea.setEditable(false);

		southPanel.setAutoscrolls(true);
		southPanel.add(textArea);

		return southPanel;
	}

	/**
	 * @return
	 */
	private JPanel centerPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		GridLayout mgr = new GridLayout(3, 4);
		mgr.setHgap(8);
		topPanel.setLayout(mgr);

		JLabel typeLabel = new JLabel("数据库类型");
		typeComboBox = new JComboBox(new String[] { "Mysql数据库" });
		JLabel serverLabel = new JLabel("服务器主机");
		serverField = new JTextField("192.169.100.250");
		topPanel.add(typeLabel);
		topPanel.add(typeComboBox);
		topPanel.add(serverLabel);
		topPanel.add(serverField);

		JLabel dbNameLabel = new JLabel("数据库名称");
		dbNameField = new JTextField("tianya_");
		JLabel portLabel = new JLabel("服务器端口");
		portField = new JTextField("3306");
		topPanel.add(dbNameLabel);
		topPanel.add(dbNameField);
		topPanel.add(portLabel);
		topPanel.add(portField);

		JLabel userNameLabel = new JLabel("用户名");
		userNameField = new JTextField();
		JLabel passwordLabel = new JLabel("密码");
		passwordField = new JTextField();
		topPanel.add(userNameLabel);
		topPanel.add(userNameField);
		topPanel.add(passwordLabel);
		topPanel.add(passwordField);

		JPanel footPanel = new JPanel();
		GridLayout mgr2 = new GridLayout(1, 2);
		mgr2.setHgap(8);
		footPanel.setLayout(mgr2);
		JButton testButton = new JButton("测试连接");
		testButton.addActionListener(new TestConnectionAction());

		JButton saveButton = new JButton("保存属性到文件");
		saveButton.addActionListener(new SavePropsAction());
		footPanel.setSize(300, 50);
		footPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		footPanel.add(testButton);
		footPanel.add(saveButton);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(5, 5));
		centerPanel.add(topPanel, BorderLayout.CENTER);
		centerPanel.add(footPanel, BorderLayout.SOUTH);

		return centerPanel;
	}

	private class TestConnectionAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			try {
				ConnectionUtils.getConnection(getProperties());

				textArea.append("测试连接成功!\n");

			} catch (Exception e1) {
				textArea.append("测试连接失败!\n");
				textArea.append(e1.getMessage());
				textArea.append("\n");
			}
		}
	}

	private Properties getProperties() {
		Properties dbProps = new Properties();
		dbProps.put("dbType", typeComboBox.getSelectedItem().toString());
		dbProps.put("host", serverField.getText());
		dbProps.put("port", portField.getText());
		dbProps.put("dbName", dbNameField.getText());
		dbProps.put("user", userNameField.getText());
		dbProps.put("password", passwordField.getText());
		return dbProps;
	}

	private void setFieldFromProperties(Properties dbProps) {
		typeComboBox.setSelectedItem(dbProps.get("dbType"));
		serverField.setText(dbProps.get("host").toString());
		portField.setText(dbProps.get("port").toString());
		dbNameField.setText(dbProps.get("dbName").toString());
		userNameField.setText(dbProps.get("user").toString());
		passwordField.setText(dbProps.get("password").toString());
	}

	private class ViewTablesAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				JPanel contentPanel = new JPanel();
				contentPanel.setLayout(new BorderLayout());

				JPanel centerPanel = new JPanel();
				centerPanel.setLayout(new BorderLayout());
				Properties props = getProperties();
				Connection conn = ConnectionUtils.getConnection(props);
				List<String> tables = DbUtil.getTables(conn,
						props.getProperty("user"), props.getProperty("dbName"));
				Object[][] data = new Object[tables.size()][2];
				System.out.println(tables);
				for (int i = 0; i < tables.size(); i++) {
					data[i][0] = i+1;
					data[i][1] = tables.get(i);
				}
				tableModel = new DefaultTableModel(data, new String[] { "选择",
						"表名" });
				jTable = new JTable(tableModel);
				
				
//				TableColumnModel tcm = jTable.getColumnModel();
//				tcm.getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
				
				// jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				JScrollPane scrollpane = new JScrollPane(jTable);
				centerPanel.add(scrollpane, BorderLayout.CENTER);

				JButton testButton = new JButton("生成VO");
				testButton.addActionListener(new createVOAction());
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new GridLayout(1, 1));
				buttonPanel.add(testButton);
				centerPanel.add(buttonPanel, BorderLayout.SOUTH);

				buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20,
						10, 20));

				contentPanel.add(centerPanel, BorderLayout.CENTER);

				JPanel southPanel = southPanel();
				contentPanel.add(southPanel, BorderLayout.SOUTH);

				frame.setContentPane(contentPanel);
				frame.setVisible(true);
			} catch (Exception e1) {
				textArea.append("连接失败!\n");
				textArea.append(e1.getMessage());
				textArea.append("\n");
			}
		}
	}
	
	private class createMenuAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			initCenterPanel();
		}

	}

	private class createVOAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// TODO Auto-generated method stub
				int[] selectedRows = jTable.getSelectedRows();
				if(selectedRows.length==0){
					textArea.append("没有选中任何表!\n");
					return;
				}
				List<String> list = new ArrayList<String>();
				for (int i : selectedRows) {
					list.add(tableModel.getValueAt(i, 1).toString());
				}
				System.out.println(list);
				Connection conn = ConnectionUtils
						.getConnection(getProperties());
				DbTableVOCodeGenerator codeGenerator = new DbTableVOCodeGenerator(
						conn);
				String[] a = new String[]{};
				BeanInfo[] beanInfos = codeGenerator.generate(
						"vo1", list.toArray(a));
				JavaFileUtils.createJavaFile(null, beanInfos);
				textArea.append("生成VO"+list+"完毕!\n");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	private class openPropsAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@SuppressWarnings("deprecation")
		@Override
		public void actionPerformed(ActionEvent e) {
			// 新建文件对话框，并设置为打开的方式
			FileDialog filedlg = new FileDialog(frame, "打开");
			// 设置文件对话框的标题
			filedlg.setTitle("文件选择");
//			filedlg.setFilenameFilter(new FilenameFilter() {
//
//				@Override
//				public boolean accept(File dir, String name) {
//					// TODO Auto-generated method stub
//					return name.endsWith(".properties");
//				}
//			});
			filedlg.setFile("*.properties");
			// 设置初始路径
//			filedlg.setDirectory("./conf/common/");
			filedlg.setDirectory(".\\conf\\common\\");
			System.out.println(filedlg.getDirectory());
			filedlg.setVisible(true);
			// filedlg.setFilterPath("SystemRoot");
			// 打开文件对话框，返回选中文件的绝对路径
			String file = filedlg.getFile();
			if(file==null)return;
			String selected = filedlg.getDirectory() + file;
			System.out.println("您选中的文件路径为：" + selected);
			initCenterPanel();
			Properties props = new Properties();
			try {
				props.load(new FileInputStream(new File(selected)));
				setFieldFromProperties(props);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	private class SavePropsAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			URL url = Thread.currentThread().getContextClassLoader()
					.getResource("./");
			String basePath = new File(url.getFile()).getParent()
					+ "/conf/common/";
			File dir = new File(basePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String fileName = basePath + "db_" + dbNameField.getText()
					+ ".properties";

			OutputStream outputStream = null;
			BufferedOutputStream bos = null;
			try {
				outputStream = new FileOutputStream(fileName);
				bos = new BufferedOutputStream(outputStream);
				getProperties().store(bos, "");

				textArea.append("保存成功! 文件名为" + fileName + "\n");

			} catch (Exception e1) {
				textArea.append("保存失败!\n");
				textArea.append(e1.getMessage());
				textArea.append("\n");
			}finally{
				try {
					bos.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					outputStream.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		new TableWindow();
	}

}
