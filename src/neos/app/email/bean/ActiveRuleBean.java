package neos.app.email.bean;

import java.util.List;

/**
 * 活动规律Bean
 * @author phoenix
 *
 */
public class ActiveRuleBean {
	private List<DateCountBean> countByDate;
	private List<CatalogueCountBean> dateGap;
	private List<CatalogueCountBean> countByDayInMonth;
	private List<CatalogueCountBean> countByDayInWeek;
	private List<CatalogueCountBean> countByHourInDay;
	private List<CatalogueCountBean> rankBySend;
	private List<CatalogueCountBean> rankByRecv;

	public ActiveRuleBean() {

	}

	public List<DateCountBean> getCountByDate() {
		return countByDate;
	}

	public void setCountByDate(List<DateCountBean> countByDate) {
		this.countByDate = countByDate;
	}

	public List<CatalogueCountBean> getDateGap() {
		return dateGap;
	}

	public void setDateGap(List<CatalogueCountBean> dateGap) {
		this.dateGap = dateGap;
	}

	public List<CatalogueCountBean> getCountByDayInMonth() {
		return countByDayInMonth;
	}

	public void setCountByDayInMonth(List<CatalogueCountBean> countByDayInMonth) {
		this.countByDayInMonth = countByDayInMonth;
	}

	public List<CatalogueCountBean> getCountByDayInWeek() {
		return countByDayInWeek;
	}

	public void setCountByDayInWeek(List<CatalogueCountBean> countByDayInWeek) {
		this.countByDayInWeek = countByDayInWeek;
	}

	public List<CatalogueCountBean> getCountByHourInDay() {
		return countByHourInDay;
	}

	public void setCountByHourInDay(List<CatalogueCountBean> countByHourInDay) {
		this.countByHourInDay = countByHourInDay;
	}

	public List<CatalogueCountBean> getRankBySend() {
		return rankBySend;
	}

	public void setRankBySend(List<CatalogueCountBean> rankBySend) {
		this.rankBySend = rankBySend;
	}

	public List<CatalogueCountBean> getRankByRecv() {
		return rankByRecv;
	}

	public void setRankByRecv(List<CatalogueCountBean> rankByRecv) {
		this.rankByRecv = rankByRecv;
	}

}
