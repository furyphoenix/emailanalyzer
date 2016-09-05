package neos.tool.googlemap.geocode;

import java.util.ArrayList;
import java.util.List;

public class Result {
	private List<AddressType> typeList;
	private String formattedAddress;
	private List<AddressComponent> compList;
	private Geometry geometry;
	
	public Result(){
		typeList=new ArrayList<AddressType> ();
		compList=new ArrayList<AddressComponent> ();
	}
	
	public void setFormattedAddress(String addr){
		this.formattedAddress=addr;
	}
	
	public void setGeometry(Geometry geo){
		this.geometry=geo;
	}
	
	public void addAddressType(AddressType type){
		this.typeList.add(type);
	}
	
	public void addAddressComponent(AddressComponent comp){
		this.compList.add(comp);
	}
	
	public List<AddressType> getAddressTypeList(){
		return typeList;
	}
	
	public String getFormattedAddress(){
		return formattedAddress;
	}
	
	public List<AddressComponent> getAddressComponentList(){
		return compList;
	}
	
	public Geometry getGeometry(){
		return geometry;
	}
}
