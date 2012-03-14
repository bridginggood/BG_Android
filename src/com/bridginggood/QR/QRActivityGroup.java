package com.bridginggood.QR;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class QRActivityGroup extends ActivityGroup {
	
	private ArrayList<View> QRActivityGroup; 		// ArrayList to manage Views.
	private QRActivityGroup qrActivityGroup; 				// qrActivityGroup that Activity can access.
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	//Initialize global variables
    	QRActivityGroup = new ArrayList<View>();
    	qrActivityGroup = this;
    	
    	// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity("BizList", new 
				Intent(this,QRMainActivity.class)		//First page
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		
		// Replace the view of this ActivityGroup
		replaceView(view);
    }
        
    public void changeView(View v)  { // Changing one activity to another on same level
		QRActivityGroup.remove(QRActivityGroup.size()-1);
		QRActivityGroup.add(v);
		setContentView(v);
	}

	public void replaceView(View v) {   // Changing to new activity level.
		//Log.d("BgBiz","Replacing View...");
		QRActivityGroup.add(v);   
		setContentView(v); 
	}   

	public void back() { // On back key press
		if(QRActivityGroup.size() > 1) {   
			QRActivityGroup.remove(QRActivityGroup.size()-1);   
			setContentView(QRActivityGroup.get(QRActivityGroup.size()-1)); 
		} else {   
			finish(); // Finish tabactivity
			Log.d("BgBiz", "onDestroy called from "+this.getClass().toString());
			System.exit(0);
		}   
	}  

	public void onBackPressed() { // Event Handler
		Log.d("BgBiz", "Back Pressed from qrActivityGroup");
		qrActivityGroup.back();   
		return;
	}
    
    //Accessor methods
    public QRActivityGroup getqrActivityGroup(){
    	return qrActivityGroup;
    }
    
    public void setqrActivityGroup(QRActivityGroup qrActivityGroup){
    	this.qrActivityGroup = qrActivityGroup;  
    }
    
    public ArrayList<View> getQRActivityGroup(){
    	return QRActivityGroup;
    }
    
    public void setQRActivityGroup(ArrayList<View> QRActivityGroup){
    	this.QRActivityGroup = QRActivityGroup;
    }
    
    public void onDestory(){
		super.onDestroy();
		Log.d("BgBiz", "onDestroy called from "+this.getClass().toString());
		System.exit(0);
	}
}