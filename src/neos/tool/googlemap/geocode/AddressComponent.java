package neos.tool.googlemap.geocode;

import java.util.ArrayList;
import java.util.List;

public class AddressComponent{
	public final String shortName;
	public final String longName;
	public final List<AddressType> typeList;
	
	public AddressComponent(String sn, String ln){
		shortName=sn;
		longName=ln;
		typeList=new ArrayList<AddressType> ();
	}
	
	public void addAddressType(AddressType type){
		typeList.add(type);
	}
}
