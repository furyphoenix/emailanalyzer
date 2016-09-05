package neos.app.email.control;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import neos.app.email.bean.ActiveRuleBean;
import neos.app.email.bean.CatalogueCountBean;
import neos.app.email.bean.DateCountBean;
import neos.app.gui.GuiUtil;

public class EmailActiveRuleController extends AbstractEmailController {
	private final static SimpleDateFormat fmt = new SimpleDateFormat(
			"yyyy-MM-dd");
	private final static SimpleDateFormat fullFmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private final static String[] hours = { "00时", "01时", "02时", "03时", "04时",
			"05时", "06时", "07时", "08时", "09时", "10时", "11时", "12时", "13时",
			"14时", "15时", "16时", "17时", "18时", "19时", "20时", "21时", "22时",
			"23时" };
	private final static String[] monthdays = { "01日", "02日", "03日", "04日",
			"05日", "06日", "07日", "08日", "09日", "10日", "11日", "12日", "13日",
			"14日", "15日", "16日", "17日", "18日", "19日", "20日", "21日", "22日",
			"23日", "24日", "25日", "26日", "27日", "28日", "29日", "30日", "31日" };
	private final static String[] weekdays = { "星期日", "星期一", "星期二", "星期三",
			"星期四", "星期五", "星期六" };

	public EmailActiveRuleController(Connection conn, String dbName) {
		super(conn, dbName);

	}

	public ActiveRuleBean getActiveRuleBean(String from, String to, Date start,
			Date end) {
		ActiveRuleBean bean = new ActiveRuleBean();

		List<DateCountBean> countByDate = new ArrayList<DateCountBean>();
		List<CatalogueCountBean> dateGap = new ArrayList<CatalogueCountBean>();
		List<CatalogueCountBean> countByDayInMonth = new ArrayList<CatalogueCountBean>();
		List<CatalogueCountBean> countByDayInWeek = new ArrayList<CatalogueCountBean>();
		List<CatalogueCountBean> countByHourInDay = new ArrayList<CatalogueCountBean>();
		List<CatalogueCountBean> rankBySend = new ArrayList<CatalogueCountBean>();
		List<CatalogueCountBean> rankByRecv = new ArrayList<CatalogueCountBean>();

		int[] dayInMonth = new int[31];
		int[] dayInWeek = new int[7];
		int[] hourInDay = new int[24];
		Hashtable<String, Integer> dateCntTab = new Hashtable<String, Integer>();
		Hashtable<Long, Integer> dateGapTab = new Hashtable<Long, Integer>();
		String sql = buildSql(from, to, start, end);
		Calendar cal = Calendar.getInstance();

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				Date date = fullFmt.parse(rs.getString("SendDate"));
				cal.setTime(date);
				int monthday = cal.get(Calendar.DAY_OF_MONTH);
				int weekday = cal.get(Calendar.DAY_OF_WEEK);
				int dayhour = cal.get(Calendar.HOUR_OF_DAY);

				dayInMonth[monthday - 1]++;
				dayInWeek[weekday - 1]++;
				hourInDay[dayhour]++;

				String day = fmt.format(date);
				int cnt = 0;

				if (dateCntTab.containsKey(day)) {
					cnt = dateCntTab.get(day);
				}

				dateCntTab.put(day, cnt + 1);

			}

			Map.Entry[] entries = GuiUtil.sortMap(dateCntTab, true);
			Date prevEntryDate = null;
			if (entries.length > 0) {
				prevEntryDate = fmt.parse(entries[0].getKey().toString());
			}
			for (Map.Entry entry : entries) {
				Date date = fmt.parse(entry.getKey().toString());
				Integer cnt = (Integer) entry.getValue();
				countByDate.add(new DateCountBean(date, cnt));

				int gapCnt = 0;
				long gap = (date.getTime() - prevEntryDate.getTime())
						/ (1000l * 3600l * 24l);
				if (dateGapTab.containsKey(gap)) {
					gapCnt = dateGapTab.get(gap);
				}
				dateGapTab.put(gap, gapCnt + 1);
				prevEntryDate = date;
			}

			Map.Entry[] gapEntries = GuiUtil.sortMap(dateGapTab, true);
			for (int i = 0; i < gapEntries.length; i++) {
				Map.Entry<Long, Integer> gapEntry = (Map.Entry<Long, Integer>) gapEntries[i];
				Long gap = gapEntry.getKey();
				Integer cnt = gapEntry.getValue();
				dateGap.add(new CatalogueCountBean(gap + "天", cnt));
			}

			for (int i = 0; i < dayInMonth.length; i++) {
				countByDayInMonth.add(new CatalogueCountBean(monthdays[i],
						dayInMonth[i]));
			}

			for (int i = 0; i < dayInWeek.length; i++) {
				countByDayInWeek.add(new CatalogueCountBean(weekdays[i],
						dayInWeek[i]));
			}

			for (int i = 0; i < hourInDay.length; i++) {
				countByHourInDay.add(new CatalogueCountBean(hours[i],
						hourInDay[i]));
			}

		}catch(SQLException e){
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		String sendSql = buildSendSql(from, to, start, end);
		try{
			Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(sendSql);
			
			while(rs.next()){
				rankBySend.add(new CatalogueCountBean(rs.getString("FromAddr"), rs.getInt("CNT")));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		String recvSql=buildRecvSql(from, to, start, end);
		try{
			Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(recvSql);
			
			while(rs.next()){
				rankByRecv.add(new CatalogueCountBean(rs.getString("ToAddr"), rs.getInt("CNT")));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

		bean.setCountByDate(countByDate);
		bean.setCountByDayInMonth(countByDayInMonth);
		bean.setCountByDayInWeek(countByDayInWeek);
		bean.setCountByHourInDay(countByHourInDay);
		bean.setDateGap(dateGap);
		bean.setRankByRecv(rankByRecv);
		bean.setRankBySend(rankBySend);

		return bean;
	}

	private String buildSql(String from, String to, Date start, Date end) {
		StringBuilder sb = new StringBuilder();

		sb.append("Select Distinct(`SendDate`) From `");
		sb.append(dbName);
		sb.append("`.`emailfromto` Where ");
		sb.append("`SendDate`>=\"");
		sb.append(fmt.format(start) + " 00:00:00\"");
		sb.append(" AND ");
		sb.append("`SendDate`<=\"");
		sb.append(fmt.format(end) + " 23:59:59\" ");

		if ((from != null) && (from.length() > 0)) {
			sb.append("AND `FromAddr`=\"");
			sb.append(from);
			sb.append("\" ");
		}

		if ((to != null) && (to.length() > 0)) {
			sb.append("AND `ToAddr`=\"");
			sb.append(to);
			sb.append("\" ");
		}

		sb.append(" Group By `EmailID`");

		return sb.toString();
	}

	private String buildSendSql(String from, String to, Date start, Date end) {
		StringBuilder sb = new StringBuilder();

		sb.append("Select `FromAddr`, COUNT(Distinct `EmailID`) AS CNT From `");
		sb.append(dbName);
		sb.append("`.`emailfromto` Where ");
		sb.append("`SendDate`>=\"");
		sb.append(fmt.format(start) + " 00:00:00\"");
		sb.append(" AND ");
		sb.append("`SendDate`<=\"");
		sb.append(fmt.format(end) + " 23:59:59\" ");

		if ((from != null) && (from.length() > 0)) {
			sb.append("AND `FromAddr`=\"");
			sb.append(from);
			sb.append("\" ");
		}

		if ((to != null) && (to.length() > 0)) {
			sb.append("AND `ToAddr`=\"");
			sb.append(to);
			sb.append("\" ");
		}

		sb.append(" Group By `FromAddr` Order By CNT Desc Limit 20");

		return sb.toString();
	}

	private String buildRecvSql(String from, String to, Date start, Date end) {
		StringBuilder sb = new StringBuilder();

		sb.append("Select `ToAddr`, COUNT(Distinct `EmailID`) AS CNT From `");
		sb.append(dbName);
		sb.append("`.`emailfromto` Where ");
		sb.append("`SendDate`>=\"");
		sb.append(fmt.format(start) + " 00:00:00\"");
		sb.append(" AND ");
		sb.append("`SendDate`<=\"");
		sb.append(fmt.format(end) + " 23:59:59\" ");

		if ((from != null) && (from.length() > 0)) {
			sb.append("AND `FromAddr`=\"");
			sb.append(from);
			sb.append("\" ");
		}

		if ((to != null) && (to.length() > 0)) {
			sb.append("AND `ToAddr`=\"");
			sb.append(to);
			sb.append("\" ");
		}

		sb.append(" Group By `ToAddr` Order By CNT Desc Limit 20");

		return sb.toString();
	}

}
