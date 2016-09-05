package neos.component.ner;

public interface NeosNamedEntity {
	enum NamedEntityType{
		DateTime, 
		LocationName, 
		PersonName, 
		OrgnizationName, 
		EmailAddress, 
		URL, 
		PhoneNumber,
		MobilePhoneNumber,
		IDCardNumber,
		PostalCode,
		GeneralNumber};
	
	NamedEntityType getType();
	int getFieldValidFlag();
}
