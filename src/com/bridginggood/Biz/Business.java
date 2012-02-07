package com.bridginggood.Biz;

import java.io.Serializable;


public class Business implements Serializable {
	/**
	 * Make this class Serializable so that it can be passed onto another activity via 'intent'.
	 * serailVersionUID is generated automatically.
	 */
	private static final long serialVersionUID = -5579923920896416265L;
	private int bizLogo, charityLogo;						//bizLogo: Business Logo, charityLogo: Charity Logo
	private String bizId, bizName, bizAddress, bizCharity; 	//bizCharity: Charity index - used to match charityLogo
	private float bizLat, bizLng;
	private float distanceAway;								//distanceAway: distance from current location

	public Business(){
		setBizId("");
		setBizLogo(0);
		setBizName("");
		setBizAddress("");
		setBizCharity("");
		setBizLat(0.0f);
		setBizLng(0.0f);
		setDistanceAway(0.0f);
	}

	public Business(String bizId, int bizLogo, String bizName, String bizAddress, float bizLat, float bizLng, String bizCharity, float distanceAway){
		setBizId(bizId);
		setBizLogo(bizLogo);
		setBizName(bizName);
		setBizAddress(bizAddress);
		setBizCharity(bizCharity);
		setBizLat(bizLat);
		setBizLng(bizLng);
		setDistanceAway(distanceAway);
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}


	public String getBizAddress() {
		return bizAddress;
	}

	public void setBizAddress(String bizAddress) {
		this.bizAddress = bizAddress;
	}

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}

	public int getBizLogo() {
		return bizLogo;
	}

	public void setBizLogo(int bizLogo) {
		this.bizLogo = bizLogo;
	}

	public String getBizCharity() {
		return bizCharity;
	}

	public void setBizCharity(String bizCharity) {
		this.bizCharity = bizCharity;
	}

	public float getBizLat() {
		return bizLat;
	}

	public void setBizLat(float bizLat) {
		this.bizLat = bizLat;
	}

	public float getBizLng() {
		return bizLng;
	}

	public void setBizLng(float bizLng) {
		this.bizLng = bizLng;
	}

	public float getDistanceAway() {
		return distanceAway;
	}
	
	public String getDistanceAwayStr(String unit){
		return String.format("%.2f "+unit, distanceAway);
	}

	public void setDistanceAway(float distanceAway) {
		this.distanceAway = distanceAway;
	}

	public int getCharityLogo() {
		return charityLogo;
	}

	public void setCharityLogo(int charityLogo) {
		this.charityLogo = charityLogo;
	}
}
