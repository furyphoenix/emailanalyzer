package neos.component.ner;

public class NeosLocationNameEntity implements NeosNamedEntity {
	private String country=null;
	private String administrative_area_level_1=null;
	private String administrative_area_level_2=null;
	private String administrative_area_level_3=null;
	private String locality=null;
	private String sublocality=null;
	private String premise=null;
	private String subpremise=null;
	private int flag;
	public final static int CountryFlag=0x0001;
	public final static int Administrative_Area_Level_1_Flag=0x0002;
	public final static int Administrative_Area_Level_2_Flag=0x0004;
	public final static int Administrative_Area_Level_3_Flag=0x0008;
	public final static int LocalityFlag=0x0010;
	public final static int SublocalityFlag=0x0020;
	public final static int PremiseFlag=0x0040;
	public final static int SubpremiseFlag=0x0080;
	public final static int DefaultFlag=CountryFlag|Administrative_Area_Level_1_Flag|LocalityFlag;
	
	
	public NeosLocationNameEntity(){
		
	}
	
	public NeosLocationNameEntity(String country, String province, String city){
		this.country=country;
		this.administrative_area_level_1=province;
		this.locality=city;
		this.flag=DefaultFlag;
	}
	
	
	public NeosLocationNameEntity(String country, String province, String city, String premise){
		this.country=country;
		this.administrative_area_level_1=province;
		this.locality=city;
		this.premise=premise;
		this.flag=CountryFlag|Administrative_Area_Level_1_Flag|LocalityFlag|PremiseFlag;
	}
	
	public void setFlag(int flag){
		this.flag=flag;
	}
	
	public void setCountry(String country){
		this.country=country;
		flag=(flag|this.CountryFlag);
	}
	
	public String getCountry(){
		return this.country;
	}
	
	public void setAdministrative_area_level_1(String ad){
		this.administrative_area_level_1=ad;
		flag=(flag|this.Administrative_Area_Level_1_Flag);
	}
	
	public String getAdministrative_area_level_1(){
		return this.administrative_area_level_1;
	}
	
	public void setAdministrative_area_level_2(String ad){
		this.administrative_area_level_2=ad;
		flag=(flag|this.Administrative_Area_Level_2_Flag);
	}
	
	public String getAdministrative_area_level_2(){
		return this.administrative_area_level_2;
	}
	
	public void setAdministrative_area_level_3(String ad){
		this.administrative_area_level_3=ad;
		flag=(flag|this.Administrative_Area_Level_3_Flag);
	}
	
	public String getAdministrative_area_level_3(){
		return this.administrative_area_level_3;
	}
	
	public void setLocality(String locality){
		this.locality=locality;
		flag=(flag|this.LocalityFlag);
	}
	
	public String getLocality(){
		return this.locality;
	}
	
	public void setSubLocality(String sublocality){
		this.sublocality=sublocality;
		flag=(flag|this.SublocalityFlag);
	}
	
	public String getSubLocality(){
		return this.sublocality;
	}
	
	public void setPremise(String premise){
		this.premise=premise;
		flag=(flag|this.PremiseFlag);
	}
	
	public String getPremise(){
		return this.premise;
	}
	
	public void setSubPremise(String subpremise){
		this.subpremise=subpremise;
		flag=(flag|this.SubpremiseFlag);
	}
	
	public String getSubPremise(){
		return this.subpremise;
	}
	
	@Override
	public NamedEntityType getType() {
		return NamedEntityType.LocationName;
	}

	@Override
	public int getFieldValidFlag() {
		return flag;
	}

}
