package com.bridginggood.Payment;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PaymentActivityGroup extends ActivityGroup {
	
	private ArrayList<View> historyPaymentActivityGroup; 		// ArrayList to manage Views.
	private PaymentActivityGroup paymentActivityGroup; 				// PaymentActivityGroup that Activity can access.
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	//Initialize global variables
    	historyPaymentActivityGroup = new ArrayList<View>();
    	paymentActivityGroup = this;
    	
    	// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity("PaymentCamera", new 
				Intent(this,PaymentCamera.class)		//First page
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		
		// Replace the view of this ActivityGroup
		replaceView(view);
    }
        
    public void changeView(View v)  { // Changing one activity to another on same level
    	historyPaymentActivityGroup.remove(historyPaymentActivityGroup.size()-1);
    	historyPaymentActivityGroup.add(v);
		setContentView(v);
	}

	public void replaceView(View v) {   // Changing to new activity level.
		//Log.d("BG","Replacing View...");
		historyPaymentActivityGroup.add(v);   
		setContentView(v); 
	}   

	public void back() { // On back key press
		if(historyPaymentActivityGroup.size() > 1) {   
			historyPaymentActivityGroup.remove(historyPaymentActivityGroup.size()-1);   
			setContentView(historyPaymentActivityGroup.get(historyPaymentActivityGroup.size()-1)); 
		} else {   
			finish(); // Finish tabactivity
		}   
	}  

	public void onBackPressed() { // Event Handler
		Log.d("BG", "Back Pressed from PaymentActivityGroup");
		paymentActivityGroup.back();   
		return;
	}
    
    //Accessor methods
    public PaymentActivityGroup getPaymentActivityGroup(){
    	return paymentActivityGroup;
    }
    
    public void setBizActivityGroup(PaymentActivityGroup paymentActivityGroup){
    	this.paymentActivityGroup = paymentActivityGroup;  
    }
    
    public ArrayList<View> getHistoryPaymentActivityGroup(){
    	return historyPaymentActivityGroup;
    }
    
    public void setHistoryPaymentActivityGroup(ArrayList<View> historyPaymentActivityGroup){
    	this.historyPaymentActivityGroup = historyPaymentActivityGroup;
    }
}