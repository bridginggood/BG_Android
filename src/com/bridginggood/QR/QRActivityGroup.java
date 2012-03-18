package com.bridginggood.QR;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class QRActivityGroup extends ActivityGroup {
	
	private ArrayList<View> historyQRActivityGroup; 		// ArrayList to manage Views.
	private QRActivityGroup qrActivityGroup; 				// BizActivityGroup that Activity can access.
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	//Initialize global variables
    	historyQRActivityGroup = new ArrayList<View>();
    	qrActivityGroup = this;
    	
    	// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity("QRMain", new 
				Intent(this,QRMainActivity.class)		//First page
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		
		// Replace the view of this ActivityGroup
		replaceView(view);
    }
        
    public void changeView(View v)  { // Changing one activity to another on same level
		historyQRActivityGroup.remove(historyQRActivityGroup.size()-1);
		historyQRActivityGroup.add(v);
		setContentView(v);
	}

	public void replaceView(View v) {   // Changing to new activity level.
		//Log.d("BgBiz","Replacing View...");
		historyQRActivityGroup.add(v);   
		setContentView(v); 
	}   

	public void back() { // On back key press
		if(historyQRActivityGroup.size() > 1) {   
			historyQRActivityGroup.remove(historyQRActivityGroup.size()-1);   
			setContentView(historyQRActivityGroup.get(historyQRActivityGroup.size()-1)); 
		} else {   
			finish(); // Finish tabactivity
			Log.d("BgQR", "onDestroy called from "+this.getClass().toString());
			System.exit(0);
		}   
	}  

	public void onBackPressed() { // Event Handler
		Log.d("BgQR", "Back Pressed from BizActivityGroup");
		qrActivityGroup.back();   
		return;
	}
    
    //Accessor methods
    public QRActivityGroup getQRActivityGroup(){
    	return qrActivityGroup;
    }
    
    public void setQRActivityGroup(QRActivityGroup bizActivityGroup){
    	this.qrActivityGroup = bizActivityGroup;  
    }
    
    public ArrayList<View> getHistoryQRActivityGroup(){
    	return historyQRActivityGroup;
    }
    
    public void setHistoryQRActivityGroup(ArrayList<View> historyBizActivityGroup){
    	this.historyQRActivityGroup = historyBizActivityGroup;
    }
    
    public void onDestory(){
		super.onDestroy();
		Log.d("BgQR", "onDestroy called from "+this.getClass().toString());
		System.exit(0);
	}
}