package neos.app.email.bean;

import java.util.List;

/**
 * 邮件基础信息Bean
 * @author phoenix
 *
 */
public class EmailInfoBean extends EmailHeaderBean {
	private List<String> recieved;
	private String textBody;
	private String htmlBody;
	private List<String> attachmentFileNames;
	
	public EmailInfoBean(){
		super();
	}

	/**
	 * 获取邮件路由信息表
	 * @return 邮件路由信息列表
	 */
	public List<String> getRecieved() {
		return recieved;
	}

	/**
	 * 设置邮件路由信息表
	 * @param recieved 邮件路由信息表
	 */
	public void setRecieved(List<String> recieved) {
		this.recieved = recieved;
	}

	/**
	 * 获取邮件体纯文本部分
	 * @return 邮件体纯文本部分
	 */
	public String getTextBody() {
		return textBody;
	}

	/**
	 * 设置邮件体的纯文本部分
	 * @param textBody 邮件体纯文本部分
	 */
	public void setTextBody(String textBody) {
		this.textBody = textBody;
	}

	/**
	 * 获取邮件体的HTML部分
	 * @return 邮件体HTML部分
	 */
	public String getHtmlBody() {
		return htmlBody;
	}

	/**
	 * 设置邮件体的HTML部分
	 * @param htmlBody 邮件体饿的HTML部分
	 */
	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}

	/**
	 * 获取邮件的附件文件名列表
	 * @return 邮件附件文件名列表
	 */
	public List<String> getAttachmentFileNames() {
		return attachmentFileNames;
	}

	/**
	 * 设置邮件附件文件名列表
	 * @param attachmentFileNames 邮件附件文件名列表
	 */
	public void setAttachments(List<String> attachmentFileNames) {
		this.attachmentFileNames = attachmentFileNames;
	}
	
	
}
