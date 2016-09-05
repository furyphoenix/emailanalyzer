package neos.app.email.control;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import neos.app.email.bean.EmailAttachmentBean;
import neos.app.email.bean.EmailAttachmentMD5Bean;

public class EmailAttachmentSearchController extends AbstractEmailController {
	private final static SimpleDateFormat fmt = new SimpleDateFormat(
			"yyyy-MM-dd");
	private final static SimpleDateFormat fullFmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public EmailAttachmentSearchController(Connection conn, String dbName) {
		super(conn, dbName);
	}

	List<EmailAttachmentMD5Bean> listAttachmentByAddr(String from, String to,
			Date start, Date end) {
		List<EmailAttachmentMD5Bean> list = new ArrayList<EmailAttachmentMD5Bean>();
		String sql = buildAddrSql(from, to, start, end);
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				String fileName = rs.getString("FileName");
				String fileMd5 = rs.getString("FileMD5");
				long fileLen = rs.getLong("FileLen");
				int cnt = rs.getInt("CNT");
				EmailAttachmentMD5Bean bean = new EmailAttachmentMD5Bean(
						fileName, fileMd5, fileLen, cnt);
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	private String buildAddrSql(String from, String to, Date start, Date end) {
		StringBuffer sb = new StringBuffer();
		sb.append("Select Count(Distinct (`emailattach`.`EmailID`)) AS `CNT`, `FileMD5`, `FileLen`, `FileName` From `");
		sb.append(dbName);
		sb.append("`.`emailattach` Inner Join (");
		sb.append("Select Distinct(`EmailID`) From `");
		sb.append(dbName);
		sb.append("`.`emailfromto` Where `SendDate`>=\"");
		sb.append(fmt.format(start));
		sb.append(" 00:00:00\" AND `SendDate`<\"");
		sb.append(fmt.format(end));
		sb.append(" 23:59:59\" ");
		if ((from != null) && (from.length() > 0)) {
			sb.append("AND `FromAddr`='");
			sb.append(from);
			sb.append("' ");
		}
		if ((to != null) && (to.length() > 0)) {
			sb.append("AND `ToAddr`='");
			sb.append(to);
			sb.append("' ");
		}
		sb.append(") AS `tab` ON `emailattach`.`EmailID`=`tab`.`EmailID` Group By `FileMD5` Having `CNT`>1");

		String sql = sb.toString();
		return sql;
	}

	List<EmailAttachmentBean> listAttachmentByMd5(String fileMd5, int fileLen) {
		List<EmailAttachmentBean> list = new ArrayList<EmailAttachmentBean>();

		String sql = buildMd5Sql(fileMd5, fileLen);

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				EmailAttachmentBean bean = new EmailAttachmentBean();

				int emailId = rs.getInt("EmailID");
				Date sendDate = rs.getTimestamp("SendDate");
				String fromAddr = rs.getString("FromAddr");
				String mailSubject = rs.getString("MailSubject");
				String fileName = rs.getString("FileName");
				String storePath = rs.getString("StorePath");

				bean.setEmailId(emailId);
				bean.setSendDate(sendDate);
				bean.setFileName(fromAddr);
				bean.setMailSubject(mailSubject);
				bean.setFileName(fileName);
				bean.setStorePath(storePath);

				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	private String buildMd5Sql(String md5, long len) {
		StringBuffer sb = new StringBuffer();
		sb.append("Select `EmailID`, `SendDate`, `FromAddr`, `MailSubject`, `FileName`, `StorePath` From `");
		sb.append(dbName);
		sb.append("`.`emailheader` inner join (");
		sb.append("Select `EmailID`,`FileName`,`StorePath` From `");
		sb.append(dbName);
		sb.append("`.`emailattach` where `FileMD5`='");
		sb.append(md5);
		sb.append("' AND `FileLen`=");
		sb.append(len);
		sb.append(" ) AS `Tab` ON `emailheader`.`id`=`tab`.`emailid` Order By `SendDate` DESC");

		String sql = sb.toString();

		return sql;
	}

}
