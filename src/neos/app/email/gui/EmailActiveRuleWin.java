package neos.app.email.gui;

//~--- non-JDK imports --------------------------------------------------------

import neos.app.gui.GuiUtil;

import org.jdesktop.swingx.JXDatePicker;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleInsets;

import phoenix.visualization.StandardGuiUtil;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.ResultSet;
import java.sql.Statement;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JEditorPane;

public class EmailActiveRuleWin extends NeosStandardFrame {
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
	private JXDatePicker dpEnd;
	private JXDatePicker dpStart;
	private EmailMainWin parent;
	private JTextField textFieldFrom;
	private JTextField textFieldTo;
	private ChartPanel chartPanelTrend;
	private ChartPanel chartPanelDayInMonth;
	private ChartPanel chartPanelDayInWeek;
	private ChartPanel chartPanelHourInDay;
	private JEditorPane editorPaneDate;
	private ChartPanel chartPanelGap;
	private ChartPanel panelSendChart;
	private ChartPanel panelRecvChart;

	/**
	 * Create the frame.
	 */
	public EmailActiveRuleWin(EmailMainWin parent) {
		setBounds(100, 100, 800, 600);
		setTitle("邮件活动时间规律");

		JPanel panel = new JPanel();

		getCenterPanel().add(panel, BorderLayout.NORTH);

		JLabel lblNewLabel = new JLabel("\u53D1\u4EF6\u4EBA");

		panel.add(lblNewLabel);
		textFieldFrom = new JTextField();
		StandardGuiUtil.addMouseMenu4TextComponent(textFieldFrom);
		panel.add(textFieldFrom);
		textFieldFrom.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("    ");

		panel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("\u6536\u4EF6\u4EBA");

		panel.add(lblNewLabel_2);
		textFieldTo = new JTextField();
		StandardGuiUtil.addMouseMenu4TextComponent(textFieldTo);
		panel.add(textFieldTo);
		textFieldTo.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("        ");

		panel.add(lblNewLabel_3);

		JLabel label = new JLabel("\u8D77\u59CB\u65E5\u671F");

		panel.add(label);
		dpStart = new JXDatePicker();
		dpStart.setFormats(fmt);
		dpStart.setDate(new Date());
		panel.add(dpStart);

		JLabel lblNewLabel_4 = new JLabel("    ");

		panel.add(lblNewLabel_4);

		JLabel lblNewLabel_5 = new JLabel("\u7ED3\u675F\u65E5\u671F");

		panel.add(lblNewLabel_5);
		dpEnd = new JXDatePicker();
		dpEnd.setFormats(fmt);
		dpEnd.setDate(new Date());
		panel.add(dpEnd);

		JLabel lblNewLabel_6 = new JLabel("        ");

		panel.add(lblNewLabel_6);

		JButton btnNewButton = new JButton("\u5206\u6790");

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					new Thread() {
						public void run() {
							computeActive();
						}
					}.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		panel.add(btnNewButton);

		JPanel panel_1 = new JPanel();

		getCenterPanel().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		panel_1.add(tabbedPane, BorderLayout.CENTER);

		JPanel panel_6 = new JPanel();

		tabbedPane.addTab("\u65F6\u95F4\u66F2\u7EBF", null, panel_6, null);
		panel_6.setLayout(new BorderLayout(0, 0));

		chartPanelTrend = new ChartPanel(null);
		panel_6.add(chartPanelTrend, BorderLayout.CENTER);

		JPanel panel_7 = new JPanel();
		tabbedPane.addTab("\u65E5\u671F\u95F4\u9694", null, panel_7, null);
		panel_7.setLayout(new BorderLayout(0, 0));

		chartPanelGap = new ChartPanel(null);
		panel_7.add(chartPanelGap, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();

		tabbedPane.addTab("\u6708\u5185\u5206\u5E03", null, panel_2, null);
		panel_2.setLayout(new BorderLayout(0, 0));

		chartPanelDayInMonth = new ChartPanel(null);
		panel_2.add(chartPanelDayInMonth, BorderLayout.CENTER);

		JPanel panel_3 = new JPanel();

		tabbedPane.addTab("\u5468\u5185\u5206\u5E03", null, panel_3, null);
		panel_3.setLayout(new BorderLayout(0, 0));

		chartPanelDayInWeek = new ChartPanel(null);
		panel_3.add(chartPanelDayInWeek, BorderLayout.CENTER);

		JPanel panel_4 = new JPanel();

		tabbedPane.addTab("\u65E5\u5185\u5206\u5E03", null, panel_4, null);
		panel_4.setLayout(new BorderLayout(0, 0));

		chartPanelHourInDay = new ChartPanel(null);
		panel_4.add(chartPanelHourInDay, BorderLayout.CENTER);

		JPanel panel_9 = new JPanel();
		tabbedPane.addTab("\u53D1\u4EF6\u6392\u540D", null, panel_9, null);
		panel_9.setLayout(new BorderLayout(0, 0));

		panelSendChart = new ChartPanel(null);
		panel_9.add(panelSendChart, BorderLayout.CENTER);

		JPanel panel_10 = new JPanel();
		tabbedPane.addTab("\u6536\u4EF6\u6392\u540D", null, panel_10, null);
		panel_10.setLayout(new BorderLayout(0, 0));

		panelRecvChart = new ChartPanel(null);
		panel_10.add(panelRecvChart, BorderLayout.CENTER);

		JPanel panel_5 = new JPanel();

		panel_5.setPreferredSize(new Dimension(200, 10));
		panel_1.add(panel_5, BorderLayout.EAST);
		panel_5.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();

		panel_5.add(scrollPane, BorderLayout.CENTER);

		editorPaneDate = new JEditorPane();
		editorPaneDate.setEditable(false);
		StandardGuiUtil.addMouseMenu4TextComponent(editorPaneDate);
		scrollPane.setViewportView(editorPaneDate);

		JPanel panel_8 = new JPanel();
		panel_5.add(panel_8, BorderLayout.NORTH);

		JLabel label_1 = new JLabel("\u7EDF\u8BA1\u6570\u636E");
		panel_8.add(label_1);

		// custom code start here
		this.parent = parent;
	}
	

	public void computeWithInfo(String from, String to, Date start, Date end) {
		textFieldFrom.setText(from);
		textFieldTo.setText(to);
		dpStart.setDate(start);
		dpEnd.setDate(end);
		computeActive();
	}

	private void computeActive() {
		String from = textFieldFrom.getText();
		String to = textFieldTo.getText();
		Date start = dpStart.getDate();
		Date end = dpEnd.getDate();

		if (start.after(end)) {
			setMessage(" 错误：起始日期晚于结束日期。");

			return;
		}

		int[] dayInMonth = new int[31];
		int[] dayInWeek = new int[7];
		int[] hourInDay = new int[24];
		Hashtable<String, Integer> dateCntTab = new Hashtable<String, Integer>();
		Hashtable<Long, Integer> dateGapTab = new Hashtable<Long, Integer>();
		String sql = buildSql(from, to, start, end);
		Calendar cal = Calendar.getInstance();

		try {
			Statement st = parent.getEmailDbConnect().createStatement();
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

			StringBuilder sb = new StringBuilder();

			sb.append("邮件数量随日期变化曲线数据：\r\n");
			Map.Entry[] entries = GuiUtil.sortMap(dateCntTab, true);
			Date prevEntryDate = null;
			if (entries.length > 0) {
				prevEntryDate = fmt.parse(entries[0].getKey().toString());
			}
			TimeSeries ts = new TimeSeries("邮件数量");
			for (int i = 0; i < entries.length; i++) {
				Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) entries[i];
				Date date = fmt.parse(entry.getKey());
				int cnt = entry.getValue();
				ts.add(new Day(date), cnt);
				sb.append(entry.getKey());
				sb.append("\t");
				sb.append(cnt);
				sb.append("\r\n");
				int gapCnt = 0;
				long gap = (date.getTime() - prevEntryDate.getTime())
						/ (1000l * 3600l * 24l);
				if (dateGapTab.containsKey(gap)) {
					gapCnt = dateGapTab.get(gap);
				}
				dateGapTab.put(gap, gapCnt + 1);
				prevEntryDate = date;
			}
			sb.append("\r\n\r\n");
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			dataset.addSeries(ts);
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
					"邮件数量随日期变化曲线", "", "", dataset, false, true, false);
			setXYChart(chartPanelTrend, chart);

			sb.append("邮件日期间隔曲线数据：\r\n");
			Map.Entry[] gapEntries = GuiUtil.sortMap(dateGapTab, true);
			double[][] gapData = new double[2][gapEntries.length];
			for (int i = 0; i < gapEntries.length; i++) {
				Map.Entry<Long, Integer> gapEntry = (Map.Entry<Long, Integer>) gapEntries[i];
				gapData[0][i] = gapEntry.getKey();
				gapData[1][i] = gapEntry.getValue();
				sb.append(gapEntry.getKey());
				sb.append("天\t");
				sb.append(gapEntry.getValue());
				sb.append("\r\n");
			}
			sb.append("\r\n\r\n");
			DefaultXYDataset gapDataset = new DefaultXYDataset();
			gapDataset.addSeries("日期间隔分布", gapData);
			JFreeChart gapChart = ChartFactory.createXYLineChart("邮件日期间隔曲线",
					"间隔天数", "邮件数量", gapDataset, PlotOrientation.VERTICAL,
					false, false, false);
			setXYChart(chartPanelGap, gapChart);

			sb.append("邮件在每月内按日期分布数据：\r\n");
			DefaultCategoryDataset dayInMonthData = new DefaultCategoryDataset();
			for (int i = 0; i < dayInMonth.length; i++) {
				dayInMonthData.addValue(dayInMonth[i], "数量", monthdays[i]);
				sb.append(monthdays[i]);
				sb.append("\t");
				sb.append(dayInMonth[i]);
				sb.append("\r\n");
			}
			sb.append("\r\n\r\n");
			JFreeChart dayInMonthChart = ChartFactory.createBarChart(
					"邮件往来月内活动规律", "日期", "邮件数量", dayInMonthData,
					PlotOrientation.VERTICAL, false, true, false);
			setBarChart(chartPanelDayInMonth, dayInMonthChart);

			sb.append("邮件在每周内按日分布数据：\r\n");
			DefaultCategoryDataset dayInWeekData = new DefaultCategoryDataset();
			for (int i = 0; i < dayInWeek.length; i++) {
				dayInWeekData.addValue(dayInWeek[i], "数量", weekdays[i]);
				sb.append(weekdays[i]);
				sb.append("\t");
				sb.append(dayInWeek[i]);
				sb.append("\r\n");
			}
			sb.append("\r\n\r\n");
			JFreeChart dayInWeekChart = ChartFactory.createBarChart(
					"邮件往来周内活动规律", "日期", "邮件数量", dayInWeekData,
					PlotOrientation.VERTICAL, false, true, false);
			setBarChart(chartPanelDayInWeek, dayInWeekChart);

			sb.append("邮件在每日内按小时分布数据：\r\n");
			DefaultCategoryDataset hourInDayData = new DefaultCategoryDataset();
			for (int i = 0; i < hourInDay.length; i++) {
				hourInDayData.addValue(hourInDay[i], "数量", hours[i]);
				sb.append(hours[i]);
				sb.append("\t");
				sb.append(hourInDay[i]);
				sb.append("\r\n");
			}
			sb.append("\r\n\r\n");
			JFreeChart hourInDayChart = ChartFactory.createBarChart(
					"邮件往来日内活动规律", "小时", "邮件数量", hourInDayData,
					PlotOrientation.VERTICAL, false, true, false);
			setBarChart(chartPanelHourInDay, hourInDayChart);

			sb.append("发件数量排名数据: \r\n");
			DefaultCategoryDataset sendData = new DefaultCategoryDataset();
			String sendSql = buildSendSql(from, to, start, end);
			st = parent.getEmailDbConnect().createStatement();
			rs = st.executeQuery(sendSql);
			while (rs.next()) {
				String fromAddr = rs.getString("FromAddr");
				int cnt = rs.getInt("CNT");
				sendData.addValue(cnt, "数量", fromAddr);
				sb.append(fromAddr);
				sb.append("\t");
				sb.append(cnt);
				sb.append("\r\n");
			}
			sb.append("\r\n\r\n");
			JFreeChart sendChart = ChartFactory.createBarChart("发件数量排名",
					"Email", "邮件数量", sendData, PlotOrientation.VERTICAL, false,
					true, false);
			setBarChart(panelSendChart, sendChart);

			sb.append("收件数量排名数据: \r\n");
			DefaultCategoryDataset recvData = new DefaultCategoryDataset();
			String recvSql = buildRecvSql(from, to, start, end);
			st = parent.getEmailDbConnect().createStatement();
			rs = st.executeQuery(recvSql);
			while (rs.next()) {
				String toAddr = rs.getString("ToAddr");
				int cnt = rs.getInt("CNT");
				recvData.addValue(cnt, "数量", toAddr);
				sb.append(toAddr);
				sb.append("\t");
				sb.append(cnt);
				sb.append("\r\n");
			}
			sb.append("\r\n\r\n");
			JFreeChart recvChart = ChartFactory.createBarChart("收件数量排名",
					"Email", "邮件数量", recvData, PlotOrientation.VERTICAL, false,
					true, false);
			setBarChart(panelRecvChart, recvChart);

			editorPaneDate.setText(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String buildSql(String from, String to, Date start, Date end) {
		StringBuilder sb = new StringBuilder();

		sb.append("Select Distinct(`SendDate`) From `");
		sb.append(parent.getEmailDbName());
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
		sb.append(parent.getEmailDbName());
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
		sb.append(parent.getEmailDbName());
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

	private void setBarChart(ChartPanel panel, JFreeChart chart) {
		chart.setBackgroundPaint(Color.white);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(
				"{1}={2}", new DecimalFormat("#0")));

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));

		configBarFont(chart);

		panel.setChart(chart);
	}

	private void setXYChart(ChartPanel panel, JFreeChart chart) {
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;

			// renderer.setBaseToolTipGenerator(new
			// StandardXYToolTipGenerator("{1} {2}", fmt,
			// NumberFormat.getInstance()));

			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}

		ValueAxis axis = plot.getDomainAxis();
		if (axis instanceof DateAxis) {
			DateAxis dateAxis = (DateAxis) axis;
			dateAxis.setDateFormatOverride(fmt);
			// axis.setAutoTickUnitSelection(false);
			// axis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
		}
		configXYFont(chart);

		panel.setChart(chart);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	}

	private void configBarFont(JFreeChart chart) {
		// 配置字体
		Font xfont = new Font("宋体", Font.PLAIN, 12);// X轴
		Font yfont = new Font("宋体", Font.PLAIN, 12);// Y轴
		// Font kfont = new Font("宋体",Font.PLAIN,12) ;// 底部
		Font titleFont = new Font("隶书", Font.BOLD, 25); // 图片标题
		CategoryPlot plot = chart.getCategoryPlot();// 图形的绘制结构对象

		// 图片标题
		chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));

		// 底部
		// chart.getLegend().setItemFont(kfont);

		// X 轴
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLabelFont(xfont);// 轴标题
		domainAxis.setTickLabelFont(xfont);// 轴数值

		// Y 轴
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setLabelFont(yfont);
		rangeAxis.setTickLabelFont(yfont);

	}

	private void configXYFont(JFreeChart chart) {
		// 配置字体
		Font xfont = new Font("宋体", Font.PLAIN, 12);// X轴
		Font yfont = new Font("宋体", Font.PLAIN, 12);// Y轴
		Font kfont = new Font("宋体", Font.PLAIN, 12);// 底部
		Font titleFont = new Font("隶书", Font.BOLD, 25); // 图片标题
		XYPlot plot = chart.getXYPlot();// 图形的绘制结构对象

		// 图片标题
		chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));

		// 底部
		if (chart.getLegend() != null) {
			chart.getLegend().setItemFont(kfont);
		}

		plot.getDomainAxis().setLabelFont(xfont);
		plot.getDomainAxis().setTickLabelFont(xfont);
		plot.getRangeAxis().setLabelFont(yfont);
	}

}
