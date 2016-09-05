package neos.app.email.bean;

import java.util.Date;

/**
 * 按照日期的统计值Bean
 * 
 * @author phoenix
 * 
 */
public class DateCountBean {
	private Date date;
	private int count;

	public DateCountBean() {

	}
	
	public DateCountBean(Date date, int count){
		this.date=date;
		this.count=count;
	}

	/**
	 * 获取统计值对应的日期
	 * 
	 * @return 日期
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * 设置统计值对应的日期
	 * @param date 日期
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * 获取统计值
	 * @return 统计值
	 */
	public int getCount() {
		return count;
	}

	/**
	 * 设置统计值
	 * @param count 统计值
	 */
	public void setCount(int count) {
		this.count = count;
	}

}
