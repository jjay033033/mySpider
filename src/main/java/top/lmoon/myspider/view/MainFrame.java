/**
 * 
 */
package top.lmoon.myspider.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import cn.tianya.codegenerator.BeanInfo;
import cn.tianya.codegenerator.db.DbTableVOCodeGenerator;
import cn.tianya.codegenerator.util.JavaFileUtils;
import cn.tianya.codegenerator.view.ConnectionUtils;
import top.lmoon.myspider.apefile.FileReader;
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
	
	private void drawFrame(){
		MainFrame mFrame = new MainFrame();
		mFrame.setTitle("My songs");
		mFrame.setSize(800, 600);
		mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//界面关闭方式
		mFrame.setLocationRelativeTo(null);//显示的界面居中
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		List<ApeInfoVO> readAllFiles = FileReader.readAllFiles();
		Object[][] data = new Object[readAllFiles.size()][3];
		System.out.println(readAllFiles);
		for (int i = 0; i < readAllFiles.size(); i++) {
			ApeInfoVO vo = readAllFiles.get(i);
			data[i][0] = i+1;
			data[i][1] = vo.getSinger();
			data[i][2] = vo.getTitle();
		}
		DefaultTableModel tableModel = new DefaultTableModel(data, new String[] { "序号",
		"歌手","歌名" });
		JTable jTable = new JTable(tableModel);
		JScrollPane scrollpane = new JScrollPane(jTable);
		leftPanel.add(scrollpane, BorderLayout.CENTER);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());

		JButton testButton = new JButton("生成VO");
		testButton.addActionListener(new createVOAction());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 1));
		buttonPanel.add(testButton);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20,
				10, 20));

		contentPanel.add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = southPanel();
		contentPanel.add(southPanel, BorderLayout.SOUTH);

		frame.setContentPane(contentPanel);
		
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
