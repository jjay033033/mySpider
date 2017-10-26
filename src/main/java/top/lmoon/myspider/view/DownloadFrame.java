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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.lmoon.myspider.service.BaiduCloudService;
import top.lmoon.myspider.service.BaiduCloudService.BaiduCloudInfo;
import top.lmoon.myspider.service.BaiduCloudService.BaiduCloudVcode;
import top.lmoon.myspider.util.DownloadUtil.downloadType;
import top.lmoon.myspider.vo.ApeInfoVO;

/**
 * @author LMoon
 * @date 2017年10月25日
 * 
 */
public class DownloadFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(DownloadFrame.class);
	
	private JTextField text;
	
	private JLabel vcodePic;
	
	private BaiduCloudInfo baiduCloudInfo;
	
	private ApeInfoVO apeInfoVO;

	public DownloadFrame(Component c, BaiduCloudInfo baiduCloudInfo,ApeInfoVO apeInfoVO) {
		if (StringUtils.isBlank(baiduCloudInfo.getVcodeUrl())) {
			return;
		}
		this.baiduCloudInfo = baiduCloudInfo;
		this.apeInfoVO = apeInfoVO;
		this.setTitle("VCode input");
		this.setIconImage(new ImageIcon("./res/img/title.png").getImage());
		this.setSize(250, 150);
		// this.addWindowListener(new CloseWindowListener());
		// mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//界面关闭方式
		// this.setLocationRelativeTo(null);// 显示的界面居中
		this.setLocationRelativeTo(c);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridLayout(2, 2, 10, 10));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		// contentPanel.setPreferredSize(new Dimension(100, 200));

		text = new JTextField();
		
		vcodePic = new JLabel();
		if(!setVCodePic()){
			return;
		}

		JButton changeButton = new JButton("换个");
		changeButton.addActionListener(new changeAction());	

		JButton checkButton = new JButton("验证");
		checkButton.addActionListener(new checkAction());

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

//		private JLabel vcodePic;
//		private String picUrl;
//		private JTextField text;
//		private BaiduCloudInfo info;

//		public changeAction(JLabel vcodePic,JTextField text,BaiduCloudInfo info) {
//			this.vcodePic = vcodePic;
//			this.picUrl = picUrl;
//			this.text = text;
//			this.info = info;
//		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			getAndSetVcodePic();
		}

	}
	
	private boolean setVCodePic(){
		try {
			vcodePic.setIcon(new ImageIcon(new URL(baiduCloudInfo.getVcodeUrl())));
			text.setText("");
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error("",e);
			return false;
		}
	}

	/**
	 * 
	 */
	public void getAndSetVcodePic() {
		BaiduCloudVcode vcodeInfo = BaiduCloudService.getVcodeInfo(baiduCloudInfo.getBdstoken(), baiduCloudInfo.getAppId());
		baiduCloudInfo.setVcodeStr(vcodeInfo.getVcode_str());
		baiduCloudInfo.setVcodeUrl(vcodeInfo.getVcode_url());
		setVCodePic();
	}

	/**
	 * @author LMoon
	 * @date 2017年10月25日
	 * 
	 */
	public class checkAction implements ActionListener {
		
//		private DownloadFrame frame;
		
//		private BaiduCloudInfo info;		
//		private JLabel vcodePic;
//		private String picUrl;
//		private JTextField text;		
//		private ApeInfoVO infoVo;
		
//		public checkAction(DownloadFrame frame,BaiduCloudInfo info,JLabel vcodePic, String picUrl,JTextField text,ApeInfoVO infoVo) {
//			this.frame = frame;
//			this.info = info;
//			this.vcodePic = vcodePic;
//			this.picUrl = picUrl;
//			this.text = text;
//			this.infoVo = infoVo;
//		}
		
//		public checkAction(DownloadFrame frame) {
//			this.frame = frame;
//		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String vcode = text.getText();
			baiduCloudInfo.setVcodeInput(vcode);
			BaiduCloudInfo fileUrlInfo = BaiduCloudService.download(apeInfoVO,baiduCloudInfo.getBdstoken(), baiduCloudInfo.getAppId(), baiduCloudInfo.getParams(), baiduCloudInfo.getFormParams());
			if(fileUrlInfo.getHasDownload()==downloadType.ONGOING){
				setVisible(false);
			}else{
				getAndSetVcodePic();
			}
		}

	}

}
