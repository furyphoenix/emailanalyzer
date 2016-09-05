package neos.app.email.control;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import neos.app.email.bean.EmailBriefBean;
import neos.app.email.bean.EmailEntityBean;
import neos.app.email.bean.EntityCatalogueBean;
import neos.app.email.bean.EntityCountBean;
import neos.component.ner.NeosNamedEntity;
import neos.component.ner.NeosNamedEntity.NamedEntityType;

public class EmailEntitySearchController extends AbstractEmailController {
	private final static SimpleDateFormat fmt = new SimpleDateFormat(
			"yyyy-MM-dd");
	private final static SimpleDateFormat fullFmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public EmailEntitySearchController(Connection conn, String dbName) {
		super(conn, dbName);
	}

	public EmailEntityBean findRelevantEntity(Date start, Date end,
			String info, boolean isStrictMatch, int range) {
		EmailEntityBean entityBean = new EmailEntityBean();

		Map<Integer, Map<String, Integer>> idEntityMap = new Hashtable<Integer, Map<String, Integer>>();
		Map<Integer, EmailBriefBean> idBriefMap = new Hashtable<Integer, EmailBriefBean>();
		Map<String, NamedEntityType> entityTypeMap = new Hashtable<String, NamedEntityType>();
		Map<NamedEntityType, Map<String, Integer>> typeEntityCntMap = new Hashtable<NamedEntityType, Map<String, Integer>>();

		prepareIdBriefMap(start, end, info, isStrictMatch, idBriefMap);
		prepareIdEntityMap(start, end, info, range, isStrictMatch, idEntityMap,
				entityTypeMap, typeEntityCntMap);

		return entityBean;
	}
	
	private EntityCatalogueBean getEntityCatalogueBean(Date start, Date end, String info,
			boolean match, int range){
		EntityCatalogueBean cbean=new EntityCatalogueBean();
		StringBuffer sb=new StringBuffer();
		
		sb.append("Select `Entity`, `EntittType`, Count(*) AS CNT, Count(Distinct `emailentity`.`EmailId`) as emailcnt FROM `");
		sb.append(dbName);
		sb.append("`.`emailentity` INNER JOIN (");
		sb.append("Select `EmailId`, `Offset` From `emailentity` Where `Entity` ");
		
		if (match) {
			sb.append("='");
		} else {
			sb.append("Like '%");
		}		
		sb.append(info);
		if (match) {
			sb.append("'");
		} else {
			sb.append("%'");
		}
		
		sb.append(" AND `emailentity`.`SendDate`>=\"");
		sb.append(fmt.format(start));
		sb.append(" 00:00:00\" AND `emailheader`.`SendDate`<=\"");
		sb.append(fmt.format(end));
		sb.append(" 23:59:59\" ) AS `TAB` ON `emailentity`.`EmailId`=`TAB`.`EmailId` ");
		
		if(range>0){
			sb.append("AND abs(`emailentity`.`offset`-`tab`.`offset`)<= ");
			sb.append(range);
		}
		
		sb.append(" Group by Entity Order By EntityType, EmailCnt Desc");
		
		String sql=sb.toString();
		
		try{
			Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(sql);
			
			while(rs.next()){
				String entity=rs.getString("Entity");
				NamedEntityType entityType=NamedEntityType.valueOf(rs.getString("EntityType"));
				int cnt=rs.getInt("CNT");
				int emailCnt=rs.getInt("EmailCnt");
				EntityCountBean bean=new EntityCountBean(entity, cnt, emailCnt);
				cbean.increaseEntityList(bean, entityType);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return cbean;
	}

	private void prepareIdBriefMap(Date start, Date end, String info,
			boolean match, Map<Integer, EmailBriefBean> idBriefMap) {
		StringBuffer sb = new StringBuffer();

		sb.append("Select `ID`, `emailheader`.`SendDate`, `emailheader`.`FromAddr`, `MailSubject` From `");
		sb.append(dbName);
		sb.append("`.`emailheader` Inner Join (");
		sb.append("Select Distinct `EmailID` From `emailentity` Where `Entity` ");

		if (match) {
			sb.append("='");
		} else {
			sb.append("Like '%");
		}

		sb.append(info);

		if (match) {
			sb.append("'");
		} else {
			sb.append("%'");
		}

		sb.append(" AND `emailentity`.`SendDate`>=\"");
		sb.append(fmt.format(start));
		sb.append(" 00:00:00\" AND `emailheader`.`SendDate`<=\"");
		sb.append(fmt.format(end));
		sb.append(" 23:59:59\" ) AS `tab` ON `emailheader`.`id`=`tab`.`EmailID`");

		String sql = sb.toString();

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				int id = rs.getInt("ID");
				Date date = rs.getTimestamp("SendDate");
				String fromAddr = rs.getString("FromAddr");
				String subject = rs.getString("MailSubject");
				EmailBriefBean brief = new EmailBriefBean();
				brief.setEmailId(id);
				brief.setFromAddr(fromAddr);
				brief.setSendDate(date);
				brief.setMailSubject(subject);
				idBriefMap.put(id, brief);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void prepareIdEntityMap(Date start, Date end, String info,
			int range, boolean match,
			Map<Integer, Map<String, Integer>> idBriefMap,
			Map<String, NamedEntityType> entityTypeMap,
			Map<NamedEntityType, Map<String, Integer>> typeEntityCntMap) {
		StringBuffer sb = new StringBuffer();

		sb.append("Select `emailentity`.`EmailID`, `Entity`, `EntityType` From `");
		sb.append(dbName);
		sb.append("`.`emailentity` Inner Join (");
		sb.append("Select `EmailID`,`Offset` From `");
		sb.append(dbName);
		sb.append("`.`emailentity` Where `Entity` ");

		if (match) {
			sb.append("='");
		} else {
			sb.append("Like '%");
		}

		sb.append(info);

		if (match) {
			sb.append("'");
		} else {
			sb.append("%'");
		}

		sb.append(" AND `emailentity`.`SendDate`>=\"");
		sb.append(fmt.format(start));
		sb.append(" 00:00:00\" AND `emailheader`.`SendDate`<=\"");
		sb.append(fmt.format(end));
		sb.append(" 23:59:59\") AS TAB ON (`emailentity`.`emailid`=`tab`.`emailid` AND abs(`emailentity`.`offset`-`tab`.`offset`)<=");
		sb.append(range);
		sb.append(")");

		String sql = sb.toString();

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				int id = rs.getInt("EmailID");
				String entity = rs.getString("Entity");
				
				NamedEntityType entityType=NamedEntityType.valueOf(rs.getString("EntityType").toUpperCase()); 

				entityTypeMap.put(entity, entityType);
				if(typeEntityCntMap.containsKey(entityType)){
					
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// public EmailEntityBean findRelavantEntity(Date start, Date end,
	// String info, boolean isStrictMatch) {
	// EmailEntityBean entityBean = new EmailEntityBean();
	//
	// Map<Integer, String> idTitleMap = new Hashtable<Integer, String>();
	// Map<Integer, Set<String>> idEntitySetMap = new Hashtable<Integer,
	// Set<String>>();
	// Map<String, NeosNamedEntity.NamedEntityType> entityTypeMap = new
	// Hashtable<String, NeosNamedEntity.NamedEntityType>();
	// Set<String> topEntitySet = new HashSet<String>();
	//
	// return entityBean;
	// }

	// private void prepareIdTitleMap(String info, boolean match, Map<Integer,
	// String> idTitleMap) {
	// StringBuffer sb = new StringBuffer();
	//
	// sb.append("Select `ID`, `emailheader`.`SendDate`, `emailheader`.`FromAddr`, `MailSubject` From `");
	// sb.append(dbName);
	// sb.append("`.`emailheader` Inner Join (");
	// sb.append("Select Distinct `EmailID` From `emailentity` Where `Entity` ");
	//
	// if (match) {
	// sb.append("='");
	// } else {
	// sb.append("Like '%");
	// }
	//
	// sb.append(info);
	//
	// if (match) {
	// sb.append("'");
	// } else {
	// sb.append("%'");
	// }
	//
	// sb.append(") AS `tab` ON `emailheader`.`id`=`tab`.`EmailID`");
	//
	// String sql = sb.toString();
	//
	// try {
	// Statement st = conn.createStatement();
	// ResultSet rs = st.executeQuery(sql);
	//
	// while (rs.next()) {
	// int id = rs.getInt("ID");
	// Date date = rs.getTimestamp("SendDate");
	// String fromAddr = rs.getString("FromAddr");
	// String subject = rs.getString("MailSubject");
	// StringBuffer tsb = new StringBuffer();
	//
	// tsb.append("[#");
	// tsb.append(id);
	// tsb.append("]");
	// tsb.append("[");
	// tsb.append(fullFmt.format(date));
	// tsb.append("] [");
	// tsb.append(fromAddr);
	// tsb.append("] ");
	// tsb.append(subject);
	// idTitleMap.put(id, tsb.toString());
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private void prepareData(String info, int range, boolean match,
	// Map<String, NeosNamedEntity.NamedEntityType> entityTypeMap, Map<Integer,
	// Set<String>> idEntitySetMap) {
	// StringBuffer sb = new StringBuffer();
	//
	// sb.append("Select `emailentity`.`EmailID`, `Entity`, `EntityType` From `");
	// sb.append(dbName);
	// sb.append("`.`emailentity` Inner Join (");
	// sb.append("Select `EmailID`,`Offset` From `");
	// sb.append(dbName);
	// sb.append("`.`emailentity` Where `Entity` ");
	//
	// if (match) {
	// sb.append("='");
	// } else {
	// sb.append("Like '%");
	// }
	//
	// sb.append(info);
	//
	// if (match) {
	// sb.append("'");
	// } else {
	// sb.append("%'");
	// }
	//
	// sb.append(") AS TAB ON (`emailentity`.`emailid`=`tab`.`emailid` AND abs(`emailentity`.`offset`-`tab`.`offset`)<=");
	// sb.append(range);
	// sb.append(")");
	//
	// String sql = sb.toString();
	//
	// try {
	// Statement st = conn.createStatement();
	// ResultSet rs = st.executeQuery(sql);
	//
	// while (rs.next()) {
	// int id = rs.getInt("EmailID");
	// String entity = rs.getString("Entity").trim();
	// String entityType = rs.getString("EntityType");
	// NamedEntityType type = null;
	//
	// if (entityType.equals(NamedEntityType.DateTime.toString())) {
	// type = NamedEntityType.DateTime;
	//
	// } else if (entityType.equals(NamedEntityType.LocationName
	// .toString())) {
	// type = NamedEntityType.LocationName;
	//
	// } else if (entityType.equals(NamedEntityType.PersonName
	// .toString())) {
	// type = NamedEntityType.PersonName;
	//
	// } else if (entityType.equals(NamedEntityType.OrgnizationName
	// .toString())) {
	// type = NamedEntityType.OrgnizationName;
	//
	// } else if (entityType.equals(NamedEntityType.PhoneNumber
	// .toString())) {
	// type = NamedEntityType.PhoneNumber;
	//
	// } else if (entityType.equals(NamedEntityType.MobilePhoneNumber
	// .toString())) {
	// type = NamedEntityType.MobilePhoneNumber;
	//
	// } else if (entityType.equals(NamedEntityType.EmailAddress
	// .toString())) {
	// type = NamedEntityType.EmailAddress;
	//
	// } else if (entityType.equals(NamedEntityType.URL.toString())) {
	// type = NamedEntityType.URL;
	//
	// } else if (entityType.equals(NamedEntityType.PostalCode
	// .toString())) {
	// type = NamedEntityType.PostalCode;
	//
	// } else if (entityType.equals(NamedEntityType.IDCardNumber
	// .toString())) {
	// type = NamedEntityType.IDCardNumber;
	//
	// } else if (entityType.equals(NamedEntityType.GeneralNumber
	// .toString())) {
	// type = NamedEntityType.GeneralNumber;
	//
	// } else {
	// type = NamedEntityType.GeneralNumber;
	//
	// }
	//
	// entityTypeMap.put(entity, type);
	//
	// Set<String> entitySet;
	//
	// if (idEntitySetMap.containsKey(id)) {
	// entitySet = idEntitySetMap.get(id);
	// } else {
	// entitySet = new HashSet<String>();
	// idEntitySetMap.put(id, entitySet);
	// }
	//
	// entitySet.add(entity);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}
