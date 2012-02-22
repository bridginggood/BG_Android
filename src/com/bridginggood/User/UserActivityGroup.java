package com.bridginggood.User;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class UserActivityGroup extends ActivityGroup {
	
	private ArrayList<View> historyUserActivityGroup; 		// ArrayList to manage Views.
	private UserActivityGroup UserActivityGroup; 				// BgActivityGroup that Activity can access.
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	//Initialize global variables
    	historyUserActivityGroup = new ArrayList<View>();
    	UserActivityGroup = this;
    	
    	// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity("BgAboutController", new 
				Intent(this,UserController.class)		//First page
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		
		// Replace the view of this ActivityGroup
		replaceView(view);
    }
        
    public void changeView(View v)  { // Changing one activity to another on same level
    	historyUserActivityGroup.remove(historyUserActivityGroup.size()-1);
    	historyUserActivityGroup.add(v);
		setContentView(v);
	}

	public void replaceView(View v) {   // Changing to new activity level.
		//Log.d("BG","Replacing View...");
		historyUserActivityGroup.add(v);   
		setContentView(v); 
	}   

	public void back() { // On back key press
		if(historyUserActivityGroup.size() > 1) {   
			historyUserActivityGroup.remove(historyUserActivityGroup.size()-1);   
			setContentView(historyUserActivityGroup.get(historyUserActivityGroup.size()-1)); 
		} else {   
			finish(); // Finish tabactivity
			Log.d("BG", "onDestroy called from "+this.getClass().toString());
			System.exit(0);
		}   
	}  

	public void onBackPressed() { // Event Handler
		Log.d("BG", "Back Pressed from BgActivityGroup");
		UserActivityGroup.back();   
		return;
	}
    
    //Accessor methods
    public UserActivityGroup getBgActivityGroup(){
    	return UserActivityGroup;
    }
    
    public void setBizActivityGroup(UserActivityGroup BgActivityGroup){
    	this.UserActivityGroup = BgActivityGroup;  
    }
    
    public ArrayList<View> getHistoryBgActivityGroup(){
    	return historyUserActivityGroup;
    }
    
    public void setHistoryBgActivityGroup(ArrayList<View> historyBgActivityGroup){
    	this.historyUserActivityGroup = historyBgActivityGroup;
    }
    
    public void onDestory(){
		super.onDestroy();
		Log.d("BG", "onDestroy called from "+this.getClass().toString());
		System.exit(0);
	}
}