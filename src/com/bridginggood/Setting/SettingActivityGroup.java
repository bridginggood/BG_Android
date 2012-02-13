package com.bridginggood.Setting;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SettingActivityGroup extends ActivityGroup {
	
	private ArrayList<View> historyBgActivityGroup; 		// ArrayList to manage Views.
	private SettingActivityGroup BgActivityGroup; 				// BgActivityGroup that Activity can access.
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	//Initialize global variables
    	historyBgActivityGroup = new ArrayList<View>();
    	BgActivityGroup = this;
    	
    	// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity("BgAboutController", new 
				Intent(this,SettingController.class)		//First page
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		
		// Replace the view of this ActivityGroup
		replaceView(view);
    }
        
    public void changeView(View v)  { // Changing one activity to another on same level
    	historyBgActivityGroup.remove(historyBgActivityGroup.size()-1);
    	historyBgActivityGroup.add(v);
		setContentView(v);
	}

	public void replaceView(View v) {   // Changing to new activity level.
		//Log.d("BG","Replacing View...");
		historyBgActivityGroup.add(v);   
		setContentView(v); 
	}   

	public void back() { // On back key press
		if(historyBgActivityGroup.size() > 1) {   
			historyBgActivityGroup.remove(historyBgActivityGroup.size()-1);   
			setContentView(historyBgActivityGroup.get(historyBgActivityGroup.size()-1)); 
		} else {   
			finish(); // Finish tabactivity
			Log.d("BG", "onDestroy called from "+this.getClass().toString());
			System.exit(0);
		}   
	}  

	public void onBackPressed() { // Event Handler
		Log.d("BG", "Back Pressed from BgActivityGroup");
		BgActivityGroup.back();   
		return;
	}
    
    //Accessor methods
    public SettingActivityGroup getBgActivityGroup(){
    	return BgActivityGroup;
    }
    
    public void setBizActivityGroup(SettingActivityGroup BgActivityGroup){
    	this.BgActivityGroup = BgActivityGroup;  
    }
    
    public ArrayList<View> getHistoryBgActivityGroup(){
    	return historyBgActivityGroup;
    }
    
    public void setHistoryBgActivityGroup(ArrayList<View> historyBgActivityGroup){
    	this.historyBgActivityGroup = historyBgActivityGroup;
    }
    
    public void onDestory(){
		super.onDestroy();
		Log.d("BG", "onDestroy called from "+this.getClass().toString());
		System.exit(0);
	}
}