package com.bridginggood;

public class PushMessage {
	private String mBusinessId;
	private float mDonationAmount;
	private String mBusinessName;

	public PushMessage(String businessId, float donationAmount, String businessName){
		this.setBusinessId(businessId);
		this.setDonationAmount(donationAmount);
		this.setBusinessName(businessName);
	}

	public float getDonationAmount() {
		return mDonationAmount;
	}

	public void setDonationAmount(float mDonationAmount) {
		this.mDonationAmount = mDonationAmount;
	}

	public String getBusinessName() {
		return mBusinessName;
	}

	public void setBusinessName(String mBusinessName) {
		this.mBusinessName = mBusinessName;
	}

	public String getBusinessId() {
		return mBusinessId;
	}

	public void setBusinessId(String mBusinessId) {
		this.mBusinessId = mBusinessId;
	}

}
