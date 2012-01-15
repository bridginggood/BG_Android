package com.bridginggood.Biz;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.location.Location;


public class BizDBController {
	
	private String mUrlGetBizList = "http://api.bridginggood.com/business/read.xml";
	private float mMyLat, mMyLng, mDistanceRadius;
	
	public BizDBController (float myLat, float myLng, float distanceRadius){
		this.mMyLat = myLat;
		this.mMyLng = myLng;
		this.mDistanceRadius = distanceRadius;		//in miles
		this.mUrlGetBizList = this.mUrlGetBizList+"?"+"lat="+this.mMyLat+"&"+"lng="+this.mMyLng+"&"+"dist="+this.mDistanceRadius;
	}
	
	public ArrayList<Business> getBizListFromXML(){
		ArrayList<Business> bizList = new ArrayList<Business>();
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new URL(mUrlGetBizList).openStream());

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("business");	//For each business node
			
			Business biz = null;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					//parse xml
					Element eElement = (Element) nNode;
					String bid = getTagValue("bid", eElement);
					String name = getTagValue("name", eElement);
					String address = getTagValue("address", eElement);
					float lat = Float.parseFloat( getTagValue("latitude", eElement) );
					float lng = Float.parseFloat( getTagValue("longitude", eElement) );
					String cid = getTagValue("cid", eElement);
					
					biz = new Business(bid, 0, name, address, lat, lng, cid);
					biz.setDistanceAway(getDistanceAway(lat, lng)); //Calculate distance from myLocation
				}
				bizList.add(biz); //add to ArrayList
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bizList;
	}
	
	public float getDistanceAway(float bizLat, float bizLng){
		/*float distanceAway = (float) (3958.755864232 * 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((myLat - bizLat) * Math.PI / 180 / 2), 2) + 
				Math.cos(myLat * Math.PI / 180) * Math.cos(bizLat * Math.PI / 180) * Math.pow(Math.sin((myLng - bizLng) * Math.PI / 180 / 2), 2) )));
		*/
		
		//Android function. Returns in Meter 
		Location myLoc = new Location("myLoc");
		myLoc.setLatitude(mMyLat);
		myLoc.setLongitude(mMyLng);
		
		Location bizLoc = new Location("bizLoc");
		bizLoc.setLatitude(bizLat);
		bizLoc.setLongitude(bizLng);
		float distanceAway = myLoc.distanceTo(bizLoc); 
		
		//Convert distanceAway from meter to miles
		distanceAway = (float) ((float)(distanceAway/1000)/1.6);
		
		return distanceAway;
	}

	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		
		return nValue.getNodeValue();
	}
}
