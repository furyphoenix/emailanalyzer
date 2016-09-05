package neos.app.email.bean;

import java.util.List;
import neos.component.ner.NeosNamedEntity.NamedEntityType;

/**
 * 要素信息分类统计
 * @author phoenix
 *
 */
public class EntityCatalogueBean {
	List<EntityCountBean> dateTimeList;
	List<EntityCountBean> locationNameList;
	List<EntityCountBean> personNameList;
	List<EntityCountBean> orgnizationNameList;
	List<EntityCountBean> emailAddressList;
	List<EntityCountBean> urlList;
	List<EntityCountBean> phoneNumberList;
	List<EntityCountBean> mobilePhoneNumberList;
	List<EntityCountBean> idCardNumberList;
	List<EntityCountBean> postalCodeList;
	List<EntityCountBean> generalNumberList;
	
	public EntityCatalogueBean(){
		
	}

	public List<EntityCountBean> getDateTimeList() {
		return dateTimeList;
	}

	public void setDateTimeList(List<EntityCountBean> dateTimeList) {
		this.dateTimeList = dateTimeList;
	}

	public List<EntityCountBean> getLocationNameList() {
		return locationNameList;
	}

	public void setLocationNameList(List<EntityCountBean> locationNameList) {
		this.locationNameList = locationNameList;
	}

	public List<EntityCountBean> getPersonNameList() {
		return personNameList;
	}

	public void setPersonNameList(List<EntityCountBean> personNameList) {
		this.personNameList = personNameList;
	}

	public List<EntityCountBean> getOrgnizationNameList() {
		return orgnizationNameList;
	}

	public void setOrgnizationNameList(List<EntityCountBean> orgnizationNameList) {
		this.orgnizationNameList = orgnizationNameList;
	}

	public List<EntityCountBean> getEmailAddressList() {
		return emailAddressList;
	}

	public void setEmailAddressList(List<EntityCountBean> emailAddressList) {
		this.emailAddressList = emailAddressList;
	}

	public List<EntityCountBean> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<EntityCountBean> urlList) {
		this.urlList = urlList;
	}

	public List<EntityCountBean> getPhoneNumberList() {
		return phoneNumberList;
	}

	public void setPhoneNumberList(List<EntityCountBean> phoneNumberList) {
		this.phoneNumberList = phoneNumberList;
	}

	public List<EntityCountBean> getMobilePhoneNumberList() {
		return mobilePhoneNumberList;
	}

	public void setMobilePhoneNumberList(List<EntityCountBean> mobilePhoneNumberList) {
		this.mobilePhoneNumberList = mobilePhoneNumberList;
	}

	public List<EntityCountBean> getIdCardNumberList() {
		return idCardNumberList;
	}

	public void setIdCardNumberList(List<EntityCountBean> idCardNumberList) {
		this.idCardNumberList = idCardNumberList;
	}

	public List<EntityCountBean> getPostalCodeList() {
		return postalCodeList;
	}

	public void setPostalCodeList(List<EntityCountBean> postalCodeList) {
		this.postalCodeList = postalCodeList;
	}

	public List<EntityCountBean> getGeneralNumberList() {
		return generalNumberList;
	}

	public void setGeneralNumberList(List<EntityCountBean> generalNumberList) {
		this.generalNumberList = generalNumberList;
	}
	
	public void increaseEntityList(EntityCountBean entity, NamedEntityType type){
		List<EntityCountBean> list=null;
		switch(type){
		case DateTime:
			list=this.dateTimeList;
			break;
		case LocationName:
			list=this.locationNameList;
			break;
		case PersonName:
			list=this.personNameList;
			break;
		case OrgnizationName:
			list=this.orgnizationNameList;
			break;
		case EmailAddress:
			list=this.emailAddressList;
			break;
		case URL:
			list=this.urlList;
			break;
		case PhoneNumber:
			list=this.phoneNumberList;
			break;
		case MobilePhoneNumber:
			list=this.getMobilePhoneNumberList();
			break;
		case IDCardNumber:
			list=this.getIdCardNumberList();
			break;
		case PostalCode:
			list=this.getPostalCodeList();
			break;
		case GeneralNumber:
			list=this.getGeneralNumberList();
			break;
		default:
			list=this.getGeneralNumberList();
		}
		
		list.add(entity);
	}
	
}
