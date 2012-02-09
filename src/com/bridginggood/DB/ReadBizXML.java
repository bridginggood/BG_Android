package com.bridginggood.DB;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bridginggood.Biz.Business;

import android.util.Log;


public class ReadBizXML {
	
	private String mUrlGetBizList = "http://api.bridginggood.com:8080/business_info/read.xml";
	private float mMyLat, mMyLng, mDistanceRadius;
	
	public ReadBizXML (float myLat, float myLng, float distanceRadius){
		this.mMyLat = myLat;
		this.mMyLng = myLng;
		this.mDistanceRadius = distanceRadius;		//in miles
		this.mUrlGetBizList = this.mUrlGetBizList+"?"+"lat="+this.mMyLat+"&"+"lng="+this.mMyLng+"&"+"dist="+this.mDistanceRadius;
		Log.d("BG", "DB URL: "+mUrlGetBizList);
	}
	
	public ArrayList<Business> getBizListFromXML(){
		ArrayList<Business> bizList = new ArrayList<Business>();
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new URL(mUrlGetBizList).openStream());

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("business-info");	//For each business node
			
			Business biz = null;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					//parse xml
					Element eElement = (Element) nNode;
					String bid = getTagValue("BusinessId", eElement);
					String name = getTagValue("BusinessName", eElement);
					String address = getTagValue("BusinessAddress", eElement);
					float lat = Float.parseFloat( getTagValue("Latitude", eElement) );
					float lng = Float.parseFloat( getTagValue("Longitude", eElement) );
					String cid = getTagValue("CharityId", eElement);
					float distanceAway = Float.parseFloat (getTagValue("distance", eElement));
					
					biz = new Business(bid, 0, name, address, lat, lng, cid, distanceAway);
					//biz.setDistanceAway(getDistanceAway(lat, lng)); //Calculate distance from myLocation
				}
				bizList.add(biz); //add to ArrayList
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bizList;
	}

	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		
		return nValue.getNodeValue();
	}
}
