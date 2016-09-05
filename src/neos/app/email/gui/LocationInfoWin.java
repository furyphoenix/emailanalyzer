package neos.app.email.gui;

import java.sql.Connection;
import java.text.Format;
import java.util.List;

import neos.tool.googlemap.NeosGeocodeTool;
import neos.tool.googlemap.NeosStaticMapTool;
import neos.tool.googlemap.geocode.GeocodeResponse;
import neos.tool.googlemap.geocode.Result;
import neos.tool.googlemap.geocode.StatusCode;

public class LocationInfoWin extends AbstractInfoWin {

	public LocationInfoWin(EmailMainWin parent){
		super(null);
		this.setTitle("地名地图查询");
		this.setLabelInfo("地名");
	}
	
	@Override
	public void onQuery() {
		String text=this.getInput();
		if((text!=null)&&(text.length()>0)){
			query(text);
		}else{
			this.setMessage("输入格式无效。请重新输入。");
		}
	}
	
	private void query(String text){
		GeocodeResponse resp=NeosGeocodeTool.query(text, this);
		if(resp.getStatusCode()==StatusCode.OK){
			StringBuffer sb=new StringBuffer();
			List<Result> resultList=resp.getResultList();
			NeosStaticMapTool tool=new NeosStaticMapTool();
			for(Result result:resultList){
				sb.append(result.getFormattedAddress());
				sb.append("\r\n");
				tool.addMark(result.getGeometry().location.latitude, result.getGeometry().location.longitude, "A", "red");
			}
			this.setText(sb.toString());
			MapWin mapWin=new MapWin();
			mapWin.setMapUrl(tool.getUrl());
			mapWin.setTitle(this.getInput());
			mapWin.setVisible(true);
		}
		
	}

}
