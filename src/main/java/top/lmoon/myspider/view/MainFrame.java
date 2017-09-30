/**
 * 
 */
package top.lmoon.myspider.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.apefile.FileReader;
import top.lmoon.myspider.constant.SysConstants;
import top.lmoon.myspider.vo.ApeInfoVO;

/**
 * @author LMoon
 * @date 2017年9月30日
 * 
 */
public class MainFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8826403470545143680L;
	
	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
	
	private JTextArea textArea;
	
	private JTable jTable;
	
	private DefaultTableModel tableModel;
	
	private Desktop desktop = Desktop.getDesktop();
	
	private static final int VO_COLUMN_INDEX = 4;
	
	private void drawFrame(){
		MainFrame mFrame = new MainFrame();
		mFrame.setTitle("My songs");
		mFrame.setIconImage(new ImageIcon("./res/img/title.png").getImage());
		mFrame.setSize(690, 600);
		mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//界面关闭方式
		mFrame.setLocationRelativeTo(null);//显示的界面居中

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(westPanel(), BorderLayout.CENTER);
		contentPanel.add(eastPanel(), BorderLayout.EAST);
		contentPanel.add(southPanel(), BorderLayout.SOUTH);
		mFrame.setContentPane(contentPanel);		
		mFrame.setVisible(true);
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
				int row = jTable.getSelectedRow();
				if(row==-1){
					textArea.setText("没有选中任何表!");
					return;
				}
				ApeInfoVO vo = (ApeInfoVO) tableModel.getValueAt(row, VO_COLUMN_INDEX);
				desktop.browse(new URI(vo.getLink()));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.error("",e1);
			}
//			try {
//				// TODO Auto-generated method stub
//				int[] selectedRows = jTable.getSelectedRows();
//				if(selectedRows.length==0){
//					textArea.append("没有选中任何表!\n");
//					return;
//				}
//				List<String> list = new ArrayList<String>();
//				for (int i : selectedRows) {
//					list.add(tableModel.getValueAt(i, 1).toString());
//				}
//				System.out.println(list);
//				Connection conn = ConnectionUtils
//						.getConnection(getProperties());
//				DbTableVOCodeGenerator codeGenerator = new DbTableVOCodeGenerator(
//						conn);
//				String[] a = new String[]{};
//				BeanInfo[] beanInfos = codeGenerator.generate(
//						"vo1", list.toArray(a));
//				JavaFileUtils.createJavaFile(null, beanInfos);
//				textArea.append("生成VO"+list+"完毕!\n");
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}

		}

	}
	
	private JPanel westPanel() {
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BorderLayout());
		List<ApeInfoVO> readAllFiles = FileReader.readAllFiles();
		Object[][] data = new Object[readAllFiles.size()][VO_COLUMN_INDEX+1];
//		System.out.println(readAllFiles);
		for (int i = 0; i < readAllFiles.size(); i++) {
			ApeInfoVO vo = readAllFiles.get(i);
			data[i][0] = i+1;
			data[i][1] = vo.getSinger();
			data[i][2] = vo.getTitle();
			data[i][3] = vo.getSize();
			data[i][VO_COLUMN_INDEX] = vo;
		}
		tableModel = new DefaultTableModel(data, new String[] { "序号",
		"歌手","歌名","大小","" });
		jTable = new JTable(tableModel){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		jTable.getColumnModel().getColumn(0).setMaxWidth(40);
		jTable.getColumnModel().getColumn(1).setMaxWidth(150);
		jTable.getColumnModel().getColumn(3).setMaxWidth(80);
		jTable.getColumnModel().getColumn(4).setMinWidth(0);
		jTable.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(0);
		ListSelectionModel selectionModel = jTable.getSelectionModel();
		selectionModel.addListSelectionListener(listSelectionListener);
		JScrollPane scrollpane = new JScrollPane(jTable);
		westPanel.add(scrollpane, BorderLayout.CENTER);
		westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));

		return westPanel;
	}
	
	private ListSelectionListener listSelectionListener = new ListSelectionListener(){

		@Override
		public void valueChanged(ListSelectionEvent e) {
			try {
//				int row = e.getLastIndex();
				int row = jTable.getSelectedRow();
				if(row==-1){
					textArea.setText("没有选中任何表!");
					return;
				}
				//.getValueAt(row, 3)
				ApeInfoVO vo = (ApeInfoVO) tableModel.getValueAt(row, VO_COLUMN_INDEX);
				StringBuffer sb = new StringBuffer();
				sb.append("歌手：").append(vo.getSinger()).append(SysConstants.COLUMN_SYMBOL).append(SysConstants.LINE_SYMBOL);
				sb.append("歌曲：").append(vo.getTitle()).append(SysConstants.COLUMN_SYMBOL).append(SysConstants.LINE_SYMBOL);
				sb.append("专辑：").append(vo.getAlbum()).append(SysConstants.COLUMN_SYMBOL).append(SysConstants.LINE_SYMBOL);
				sb.append("大小：").append(vo.getSize()).append(SysConstants.COLUMN_SYMBOL).append(SysConstants.LINE_SYMBOL);
				sb.append("语言：").append(vo.getLanguage()).append(SysConstants.COLUMN_SYMBOL).append(SysConstants.LINE_SYMBOL);
				sb.append("备注：").append(vo.getRemark()).append(SysConstants.COLUMN_SYMBOL).append(SysConstants.LINE_SYMBOL);
				sb.append("网盘链接：").append(vo.getLink()).append(SysConstants.COLUMN_SYMBOL).append(SysConstants.LINE_SYMBOL);
				sb.append("网盘密码：").append(vo.getPw());
				textArea.setText(sb.toString());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.error("",e1);
			}
		}
		
	};
	
	private JPanel eastPanel() {
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BorderLayout());

		JButton testButton = new JButton("下载");
		testButton.addActionListener(new createVOAction());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(18, 1));
		buttonPanel.add(testButton);		
		
		eastPanel.add(buttonPanel, BorderLayout.CENTER);
		eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 5,
				5, 10));
		return eastPanel;
	}
	
	private JPanel southPanel() {
		JPanel southPanel = new JPanel(new GridLayout(1, 1));
		southPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

//		textArea = new JTextArea(10, 60);
//		textArea.setAutoscrolls(true);
//		textArea.setEditable(false);
//		JTextPane textPane = new JTextPane();

		textArea = new JTextArea(4, 30);
//		textField.setText("fdfdfdf");
		textArea.setEditable(false);
//		textArea.setColumns(30);
//		textArea.setAutoscrolls(true);
		textArea.setLineWrap(true);    
		textArea.setWrapStyleWord(true);
//		southPanel.setMinimumSize(new Dimension(500, 200));
		
		southPanel.setPreferredSize(new Dimension(550, 150));
//		southPanel.setAutoscrolls(true);
		southPanel.add(new JScrollPane(textArea));

		return southPanel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MainFrame().drawFrame();
	}

}
