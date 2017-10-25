/**
 * 
 */
package top.lmoon.myspider.view;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.util.BaiduCloudUtil;
import top.lmoon.myspider.util.BaiduCloudUtil.BaiduCloudInfo;

/**
 * @author LMoon
 * @date 2017年10月25日
 * 
 */
public class DownloadFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(DownloadFrame.class);
	
	private JTextField text;

	public DownloadFrame(Component c, BaiduCloudInfo info) {
		if (StringUtils.isBlank(info.getVcodeUrl())) {
			return;
		}
		this.setTitle("VCode input");
		this.setIconImage(new ImageIcon("./res/img/title.png").getImage());
		this.setSize(200, 100);
		// this.addWindowListener(new CloseWindowListener());
		// mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//界面关闭方式
		// this.setLocationRelativeTo(null);// 显示的界面居中
		this.setLocationRelativeTo(c);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridLayout(2, 2));
		// contentPanel.setPreferredSize(new Dimension(100, 200));

		JLabel vcodePic = new JLabel();
		if(!setVCodePic(vcodePic, info.getVcodeUrl(),text)){
			return;
		}

		JButton changeButton = new JButton("换个");
		changeButton.addActionListener(new changeAction(vcodePic, info.getVcodeUrl(),text));

		text = new JTextField();

		JButton checkButton = new JButton("验证");
		checkButton.addActionListener(new checkAction(this,info,vcodePic, info.getVcodeUrl(),text));

		contentPanel.add(vcodePic);
		contentPanel.add(changeButton);
		contentPanel.add(text);
		contentPanel.add(checkButton);
		
		this.setContentPane(contentPanel);
		this.setVisible(true);

	}

	/**
	 * @author LMoon
	 * @date 2017年10月25日
	 * 
	 */
	public class changeAction implements ActionListener {

		private JLabel vcodePic;
		private String picUrl;
		private JTextField text;

		/**
		 * 
		 */
		public changeAction(JLabel vcodePic, String picUrl,JTextField text) {
			this.vcodePic = vcodePic;
			this.picUrl = picUrl;
			this.text = text;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			setVCodePic(vcodePic, picUrl,text);
		}

	}
	
	private static boolean setVCodePic(JLabel vcodePic, String picUrl,JTextField text){
		try {
			vcodePic.setIcon(new ImageIcon(new URL(picUrl)));
			text.setText("");
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error("",e);
			return false;
		}
	}

	/**
	 * @author LMoon
	 * @date 2017年10月25日
	 * 
	 */
	public class checkAction implements ActionListener {
		
		private DownloadFrame frame;
		
		private BaiduCloudInfo info;
		
		private JLabel vcodePic;
		private String picUrl;
		private JTextField text;
		
		/**
		 * 
		 */
		public checkAction(DownloadFrame frame,BaiduCloudInfo info,JLabel vcodePic, String picUrl,JTextField text) {
			this.frame = frame;
			this.info = info;
			this.vcodePic = vcodePic;
			this.picUrl = picUrl;
			this.text = text;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String vcode = text.getText();
			info.setVcodeInput(vcode);
			BaiduCloudInfo fileUrlInfo = BaiduCloudUtil.download(info.getBdstoken(), info.getAppId(), info.getParams(), info.getFormParams());
			if(fileUrlInfo.getHasDownload()){
				frame.setVisible(false);
			}else{
				setVCodePic(vcodePic, picUrl, text);
			}
		}

	}

}