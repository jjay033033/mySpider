/**
 * 
 */
package top.lmoon.myspider.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.constant.SysConstants;
import top.lmoon.myspider.dao.ApeInfoDAO;
import top.lmoon.myspider.dao.ApeInfoDAOH2DBImpl;
import top.lmoon.myspider.h2db.H2DBServer;
import top.lmoon.myspider.service.BaiduCloudService;
import top.lmoon.myspider.service.BaiduCloudService.BaiduCloudInfo;
import top.lmoon.myspider.service.CacheService;
import top.lmoon.myspider.service.DownloadService;
import top.lmoon.myspider.service.ThreadPool;
import top.lmoon.myspider.util.CommonUtil;
import top.lmoon.myspider.util.DownloadUtil.downloadType;
import top.lmoon.myspider.vo.ApeInfoVO;

/**
 * @author LMoon
 * @date 2017年9月30日
 * 
 */
public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8826403470545143680L;

	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	private JTextArea textArea;

	private static JTable jTable;

	private static DefaultTableModel tableModel;

	private static JScrollPane scrollpane;

	private static JLabel totalLabel;

	private static JTextField singerTf = new JTextField();
	private static JTextField titleTf = new JTextField();

	private static int pageNo = 1;

	private static int songsCount = 0;

	private static int pageTotal = 0;

	// private Desktop desktop = Desktop.getDesktop();

	// private static Toolkit defaultToolkit = Toolkit.getDefaultToolkit();

	private static final int VO_COLUMN_INDEX = 6;

	private static final String[] COLUMN_NAME = { "序号", "歌手", "歌名", "大小", "状态", "", "" };

	private static final int PAGE_SIZE = 20;

	private static ApeInfoDAO dao = new ApeInfoDAOH2DBImpl();

	// private static ApeFileDAO fileDao = new ApeFileDAOH2DBImpl();

	private static MainFrame instance = new MainFrame();

	private MainFrame() {

	}

	public static MainFrame getInstance() {
		return instance;
	}

	public void drawFrame() {
		// MainFrame mFrame = getInstance();
		this.setTitle("My songs");
		this.setIconImage(new ImageIcon(SysConstants.TITLE_IMG).getImage());
		this.setSize(690, 567);
		this.addWindowListener(new CloseWindowListener());
		// mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//界面关闭方式
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);// 显示的界面居中

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(westPanel(), BorderLayout.CENTER);
		contentPanel.add(eastPanel(), BorderLayout.EAST);
		contentPanel.add(southPanel(), BorderLayout.SOUTH);
		this.setContentPane(contentPanel);
		this.setVisible(true);
	}

	private class downloadAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						int row = jTable.getSelectedRow();
						if (row == -1) {
							textArea.setText("请选中数据!");
							return;
						}
						ApeInfoVO vo = (ApeInfoVO) tableModel.getValueAt(row, VO_COLUMN_INDEX);
						if (DownloadService.isDownloading(vo.getSongId())) {
							JOptionPane.showMessageDialog(getInstance(), "正在下载中哦！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						// ApeFileVO fileVo = fileDao.select(vo.getSongId());
						// ApeInfoVO fileVo = dao.select(vo.getSongId());
						// if (fileVo != null) {
						// if(fileVo.getDownType() == downloadType.FINISHED){
						// JOptionPane.showMessageDialog(getInstance(),
						// "已经下载过了哦！", "提示", JOptionPane.INFORMATION_MESSAGE);
						// return;
						// }
						//
						// }

						if (vo.getDownType() == downloadType.FINISHED) {
							// JOptionPane.showMessageDialog(getInstance(),
							// "已经下载过了哦！", "提示",
							// JOptionPane.INFORMATION_MESSAGE);
							// return;
							int showConfirmDialog = JOptionPane.showConfirmDialog(getInstance(), "已经下载过了哦，是否重新下载？",
									"提示", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
							if (showConfirmDialog == JOptionPane.NO_OPTION) {
								return;
							}
						}

						// fileVo = new ApeInfoVO();
						// long currentTimeMillis = System.currentTimeMillis();
						// fileVo.setCreateTime(currentTimeMillis);
						// vo.setDownType(downloadType.NOTSTART);
						// fileVo.setSize(vo.getSize());
						// fileVo.setSongId(vo.getSongId());
						// fileVo.setTitle(vo.getTitle());
						// fileVo.setName("");
						// fileVo.setUpdateTime(currentTimeMillis);
						// if(dao.update(fileVo)<1){
						// dao.insert(fileVo);
						// }
						dao.update(vo.getSongId(), downloadType.NOTSTART);

						if (!CommonUtil.isBaiduCloudUrl(
								vo.getLink())/*
												 * ||StringUtils.isNotBlank(vo.
												 * getPw())
												 */) {
							// setSysClipboardText(vo.getPw());
							JOptionPane.showMessageDialog(getInstance(), "暂时无法下载这个文件哦！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						// desktop.browse(new URI(vo.getLink()));
						BaiduCloudInfo fileUrlInfo = BaiduCloudService.downloadAndGetFile(vo.getLink(), vo);
						if(fileUrlInfo==null){
							dao.update(vo.getSongId(), downloadType.UNFINISHED);
							JOptionPane.showMessageDialog(getInstance(), "下载失败！资源不存在或解析错误哦！\n（"+vo.getTitle()+"）", "提示",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						if (fileUrlInfo.getHasDownload() == downloadType.NOTSTART) {
							DownloadFrame downloadFrame = new DownloadFrame(getInstance(), fileUrlInfo, vo);
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						logger.error("", e1);
					}
				}
			};
			ThreadPool.submit(runnable);

		}

	}

	private class searchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			pageNo = 1;
			Object[][] data = getData(pageNo);
			if (data == null) {
				JOptionPane.showMessageDialog(getInstance(), "没有数据哦！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			setTableData(data);
		}

	}

	private class pageUpAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (--pageNo < 1) {
				pageNo++;
				JOptionPane.showMessageDialog(getInstance(), "上页没有数据了哦！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			Object[][] data = getData(pageNo);
			if (data == null) {
				pageNo++;
				JOptionPane.showMessageDialog(getInstance(), "上页没有数据了哦！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			setTableData(data);
		}

	}

	private class pageDownAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object[][] data = getData(++pageNo);
			if (data == null) {
				pageNo--;
				JOptionPane.showMessageDialog(getInstance(), "下页没有数据了哦！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			setTableData(data);
		}

	}

	private static void setTableData(Object[][] data) {
		tableModel.setDataVector(data, COLUMN_NAME);
		formatTable();
		jTable.scrollRectToVisible(jTable.getCellRect(0, 0, true));
		// scrollpane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));;
		updateTotal();
	}

	private static Object[][] getData(int pageNo) {
		return getData(pageNo, singerTf.getText(), titleTf.getText());
	}

	private static Object[][] getData(int pageNo, String singer, String title) {
		List<ApeInfoVO> readAllFiles = dao.select(pageNo, PAGE_SIZE, singer, title);
		if (readAllFiles == null || readAllFiles.isEmpty()) {
			return null;
		}
		Object[][] data = new Object[readAllFiles.size()][VO_COLUMN_INDEX + 1];
		for (int i = 0; i < readAllFiles.size(); i++) {
			ApeInfoVO vo = readAllFiles.get(i);
			data[i][0] = i + 1;
			data[i][1] = vo.getSinger();
			data[i][2] = vo.getTitle();
			data[i][3] = vo.getSize();
			data[i][4] = vo.getDownType().getName();
			data[i][5] = vo.getDownType().getImg();
			data[i][VO_COLUMN_INDEX] = vo;
		}
		return data;
	}

	private static void formatTable() {
		jTable.getColumnModel().getColumn(0).setMaxWidth(40);
		jTable.getColumnModel().getColumn(1).setMaxWidth(150);
		jTable.getColumnModel().getColumn(3).setMaxWidth(80);
		jTable.getColumnModel().getColumn(4).setMaxWidth(60);
		jTable.getColumnModel().getColumn(5).setMaxWidth(20);
		jTable.getColumnModel().getColumn(VO_COLUMN_INDEX).setMinWidth(0);
		jTable.getTableHeader().getColumnModel().getColumn(VO_COLUMN_INDEX).setMaxWidth(0);
	}

	private static void updateTotal() {
		songsCount = CacheService.getSearchTotal(singerTf.getText(), titleTf.getText());
		pageTotal = (songsCount - 1) / PAGE_SIZE + 1;
		totalLabel.setText(pageNo + "/" + pageTotal + "(" + songsCount + ")");
	}

	private JPanel westPanel() {
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BorderLayout());
		// List<ApeInfoVO> readAllFiles = FileReader.readAllFiles();
		Object[][] data = getData(1);
		if (data == null) {
			JOptionPane.showMessageDialog(getInstance(), "没有数据哦！", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
		tableModel = new DefaultTableModel(data, COLUMN_NAME) {

			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int arg0) {
				if (arg0 == 5) {
					return ImageIcon.class;
				}
				// TODO Auto-generated method stub
				return super.getColumnClass(arg0);
			}

		};
		jTable = new JTable(tableModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}

		};
		formatTable();
		ListSelectionModel selectionModel = jTable.getSelectionModel();
		selectionModel.addListSelectionListener(listSelectionListener);
		scrollpane = new JScrollPane(jTable);
		westPanel.add(scrollpane, BorderLayout.CENTER);
		westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));

		return westPanel;
	}

	private ListSelectionListener listSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			try {
				// int row = e.getLastIndex();
				int row = jTable.getSelectedRow();
				if (row == -1) {
					// textArea.setText("请选中数据!");
					textArea.setText("");
					return;
				}
				// .getValueAt(row, 3)
				ApeInfoVO vo = (ApeInfoVO) tableModel.getValueAt(row, VO_COLUMN_INDEX);
				StringBuffer sb = new StringBuffer();
				sb.append("歌手：").append(vo.getSinger()).append(SysConstants.LINE_SYMBOL);
				sb.append("歌曲：").append(vo.getTitle()).append(SysConstants.LINE_SYMBOL);
				sb.append("专辑：").append(vo.getAlbum()).append(SysConstants.LINE_SYMBOL);
				sb.append("大小：").append(vo.getSize()).append(SysConstants.LINE_SYMBOL);
				sb.append("语言：").append(vo.getLanguage()).append(SysConstants.LINE_SYMBOL);
				sb.append("备注：").append(vo.getRemark()).append(SysConstants.LINE_SYMBOL);
				sb.append("网盘链接：").append(vo.getLink()).append(SysConstants.LINE_SYMBOL);
				sb.append("网盘密码：").append(vo.getPw()).append(SysConstants.LINE_SYMBOL);
				sb.append("网页url：").append(vo.getUrl());
				textArea.setText(sb.toString());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.error("", e1);
			}
		}

	};

	private JPanel eastPanel() {
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BorderLayout());
		eastPanel.setPreferredSize(new Dimension(100, 200));

		JLabel singerLabel = new JLabel("歌手:");
		JLabel titleLabel = new JLabel("歌名:");

		JButton searchButton = new JButton("搜索");
		searchButton.addActionListener(new searchAction());

		JButton pageUpButton = new JButton("上页");
		pageUpButton.addActionListener(new pageUpAction());

		JButton pageDownButton = new JButton("下页");
		pageDownButton.addActionListener(new pageDownAction());

		JButton downloadButton = new JButton("下载");
		downloadButton.addActionListener(new downloadAction());

		totalLabel = new JLabel();
		updateTotal();

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(14, 1, 0, 5));
		buttonPanel.add(singerLabel);
		buttonPanel.add(singerTf);
		buttonPanel.add(titleLabel);
		buttonPanel.add(titleTf);
		buttonPanel.add(searchButton);
		buttonPanel.add(downloadButton);

		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());

		buttonPanel.add(pageUpButton);
		buttonPanel.add(pageDownButton);
		buttonPanel.add(totalLabel);

		eastPanel.add(buttonPanel, BorderLayout.CENTER);
		eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 10));
		return eastPanel;
	}

	private JPanel southPanel() {
		JPanel southPanel = new JPanel(new GridLayout(1, 1));
		southPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

		// textArea = new JTextArea(10, 60);
		// textArea.setAutoscrolls(true);
		// textArea.setEditable(false);
		// JTextPane textPane = new JTextPane();

		textArea = new JTextArea(4, 30);
		// textField.setText("fdfdfdf");
		textArea.setEditable(false);
		// textArea.setColumns(30);
		// textArea.setAutoscrolls(true);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		// southPanel.setMinimumSize(new Dimension(500, 200));

		southPanel.setPreferredSize(new Dimension(550, 170));
		// southPanel.setAutoscrolls(true);
		southPanel.add(new JScrollPane(textArea));

		return southPanel;
	}

	private static class CloseWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			int downloadThreadCounter = DownloadService.getDownloadThreadCounter();
			if (downloadThreadCounter > 0) {
				int showConfirmDialog = JOptionPane.showConfirmDialog(getInstance(),
						"还有" + downloadThreadCounter + "个任务正在下载哦，确定关闭吗？", "提示", JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				if (showConfirmDialog == JOptionPane.NO_OPTION) {
					return;
				}

				Set<Integer> downloadSet = DownloadService.getDownloadSet();
				for (Integer songId : downloadSet) {
					dao.update(songId, downloadType.UNFINISHED);
				}
			}

			H2DBServer.stop();
			System.exit(0);
		}
	}

	/**
	 * 将字符串复制到剪切板。
	 */
	// private static void setSysClipboardText(String writeMe) {
	// Clipboard clip = defaultToolkit.getSystemClipboard();
	// Transferable tText = new StringSelection(writeMe);
	// clip.setContents(tText, null);
	// }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		H2DBServer.start();
		getInstance().drawFrame();
	}

}
