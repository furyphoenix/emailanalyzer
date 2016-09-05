package neos.app.email.dao;

import java.util.Date;

import neos.app.email.bean.EmailHeaderBean;

/**
 * 邮件头数据库的数据访问接口
 * @author phoenix
 *
 */
public interface EmailHeaderDao {
	/**
	 * 检查指定邮件是否已经存在
	 * @param from 发送人
	 * @param date 发送日期时间
	 * @return 是否存在
	 */
	boolean isEmailExisted(String from, Date date);
	
	EmailHeaderBean getEmailHeader(int internalId);
}
