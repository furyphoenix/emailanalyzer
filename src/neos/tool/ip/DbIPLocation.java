package neos.tool.ip;

public class DbIPLocation {
	public final String countryName;
	public final String regionName;
	public final String cityName;
	public final float latitude;
	public final float longitude;
	
	public DbIPLocation(String country, String region, String city, float lat, float lng){
		countryName=country;
		regionName=region;
		cityName=city;
		latitude=lat;
		longitude=lng;
	}
	
	@Override
	public String toString(){
		return countryName+" "+regionName+" "+cityName;
	}
}
