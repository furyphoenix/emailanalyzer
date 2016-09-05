package neos.component.ner;

public class NeosPersonNameEntity implements NeosNamedEntity {
	private String familyName;
	private String givenName;
	private String title;
	public enum Gender{Male, Female};
	
	
	@Override
	public NamedEntityType getType() {
		return NamedEntityType.PersonName;
	}

	@Override
	public int getFieldValidFlag() {
		// TODO Auto-generated method stub
		return 0;
	}

}
