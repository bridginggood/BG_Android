package com.bridginggood.QR;

import java.util.ArrayList;

import com.bridginggood.R;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class QRActivityGroup extends ActivityGroup {
	
	private ArrayList<View> historyQRActivityGroup; 		// ArrayList to manage Views.
	private QRActivityGroup qrActivityGroup; 				// BizActivityGroup that Activity can access.
	private boolean mExitApplication;
	
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
		mExitApplication = false;
		historyQRActivityGroup.add(v);   
		setContentView(v); 
	}   

	public void back() { // On back key press
		if(historyQRActivityGroup.size() > 1) {   
			historyQRActivityGroup.remove(historyQRActivityGroup.size()-1);   
			setContentView(historyQRActivityGroup.get(historyQRActivityGroup.size()-1)); 
		} else {   
			if(mExitApplication){
				finish(); // Finish tabactivity
				Log.d("BG", "onDestroy called from "+this.getClass().toString());
				System.exit(0);
			}else{
				Toast.makeText(this, R.string.application_backtoexit, Toast.LENGTH_SHORT).show();
				mExitApplication = true;
			}
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