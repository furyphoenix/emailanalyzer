package neos.tool.googlemap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import neos.app.gui.ProgressMornitor;
import neos.tool.googlemap.geocode.AddressComponent;
import neos.tool.googlemap.geocode.AddressType;
import neos.tool.googlemap.geocode.GeocodeResponse;
import neos.tool.googlemap.geocode.Geometry;
import neos.tool.googlemap.geocode.Location;
import neos.tool.googlemap.geocode.LocationType;
import neos.tool.googlemap.geocode.Result;
import neos.tool.googlemap.geocode.StatusCode;
import neos.tool.googlemap.geocode.Viewport;

public class NeosGeocodeTool {
	//private final static String DEFAULT_MAP_SERVER="maps.googleapis.com";
	private final static String URL_PREFIX="http://maps.googleapis.com/maps/api/geocode/xml?address=";
	private final static String URL_POSTFIX="&language=zh-CN&sensor=true";
	
	public static GeocodeResponse query(String addr, ProgressMornitor mor){
		try{
			String url=query2url(addr);
			String xml=getXml(url,mor);
			GeocodeResponse resp=xml2Response(xml, mor);
			return resp;
		}catch(Exception e){
			e.printStackTrace();
			mor.setMessage("    error: communication with Google Map server error!");
			return null;
		}
	}
	
	public static void main(String[] args){
		NeosStaticMapTool tool=new NeosStaticMapTool();
		GeocodeResponse resp=query("世纪锦绣",null);
		System.out.println(resp.getStatusCode().name());
		for(Result res:(resp.getResultList())){
			System.out.println(res.getFormattedAddress());
			tool.addMark(res.getGeometry().location.latitude, res.getGeometry().location.longitude, "A", "red");
		}
		resp=query("西工大",null);
		System.out.println(resp.getStatusCode().name());
		for(Result res:(resp.getResultList())){
			System.out.println(res.getFormattedAddress());
			tool.addMark(res.getGeometry().location.latitude, res.getGeometry().location.longitude, "B", "blue");
		}
		String url=tool.getUrl();
		System.out.print(url);
	}
	
	private static String query2url(String addr) throws UnsupportedEncodingException{
		return URL_PREFIX+URLEncoder.encode(addr, "UTF-8")+URL_POSTFIX;
	}
	
	private static String getXml(String url, ProgressMornitor mornitor){
		HttpGet      hg = new HttpGet(url);
        HttpResponse hr = null;

        try {
            hr = new DefaultHttpClient().execute(hg);
        } catch (ClientProtocolException e) {
            e.printStackTrace();

            if (mornitor != null) {
                mornitor.setMessage("    error: protocol exception occured!");
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();

            if (mornitor != null) {
                mornitor.setMessage("    error: io exception occured!");
            }

            return null;
        }

        if (hr.getStatusLine().getStatusCode() == 200) {
            try {
                String result = EntityUtils.toString(hr.getEntity());

                return result;
            } catch (ParseException e) {
                if (mornitor != null) {
                    mornitor.setMessage("    error: parser exception occured!");
                }

                e.printStackTrace();
            } catch (IOException e) {
                if (mornitor != null) {
                    mornitor.setMessage("    error: io exception occured!");
                }

                e.printStackTrace();
            }
        }

        return null;
	}
	
	private static GeocodeResponse xml2Response(String xml, ProgressMornitor mornitor){
		Document doc = null;
		
		try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            if (mornitor != null) {
                mornitor.setMessage("    error: parse esearch xml failed!");
            }

            e.printStackTrace();
            return null;
        }
        
        try{
        	Element responseElem=doc.getRootElement();
        	String status=responseElem.elementText("status");
        	GeocodeResponse resp=new GeocodeResponse(StatusCode.valueOf(status.toUpperCase()));
        	List<Element> resultList=responseElem.elements("result");
        	for(Element resultElem:resultList){
        		Result result=xml2Result(resultElem, mornitor);
        		resp.addResult(result);
        	}
        	
        	return resp;
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
	}
	
	private static Result xml2Result(Element elem, ProgressMornitor mornitor){
		try{
			Result result=new Result();
			
			List<Element> typeElemList=elem.elements("type");
			for(Element typeElem:typeElemList){
				result.addAddressType(AddressType.valueOf(typeElem.getText().toUpperCase()));
			}
			
			String addr=elem.elementText("formatted_address");
			result.setFormattedAddress(addr);
			
			List<Element> addrElemList=elem.elements("address_component");
			for(Element addrElem:addrElemList){
				AddressComponent comp=xml2AddressComponent(addrElem, mornitor);
				result.addAddressComponent(comp);
			}
			
			Element geoElem=elem.element("geometry");
			Geometry geo=xml2Geometry(geoElem, mornitor);
			result.setGeometry(geo);
			
			return result;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private static AddressComponent xml2AddressComponent(Element elem, ProgressMornitor mornitor){
		try{
			String ln=elem.elementText("long_name");
			String sn=elem.elementText("short_name");
			List<Element> tlst=elem.elements("type");
			AddressComponent ac=new AddressComponent(sn,ln);
			for(Element em:tlst){
				ac.addAddressType(AddressType.valueOf(em.getText().toUpperCase()));
			}
			
			return ac;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private static Geometry xml2Geometry(Element elem, ProgressMornitor mornitor){
		try{
			Element locationElem=elem.element("location");
			float lat=Float.parseFloat(locationElem.elementText("lat"));
			float lng=Float.parseFloat(locationElem.elementText("lng"));
			Location loc=new Location(lat,lng);
			
			String locTypeStr=elem.elementText("location_type");
			LocationType lt=LocationType.valueOf(locTypeStr.toUpperCase());
			
			Element viewportElem=elem.element("viewport");
			
			Element swElem=viewportElem.element("southwest");
			float swLat=Float.parseFloat(swElem.elementText("lat"));
			float swLng=Float.parseFloat(swElem.elementText("lng"));
			Location sw=new Location(swLat, swLng);
			
			Element neElem=viewportElem.element("northeast");
			float neLat=Float.parseFloat(neElem.elementText("lat"));
			float neLng=Float.parseFloat(neElem.elementText("lng"));
			Location ne=new Location(neLat, neLng);
			
			Viewport viewport=new Viewport(sw,ne);
			
			Geometry geo=new Geometry(loc, lt, viewport);
			
			return geo;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
