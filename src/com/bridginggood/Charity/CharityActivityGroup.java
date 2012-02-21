package com.bridginggood.Charity;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class CharityActivityGroup extends ActivityGroup {
	
	private ArrayList<View> historyCharityActivityGroup; 		// ArrayList to manage Views.
	private CharityActivityGroup CharityActivityGroup; 				// BgActivityGroup that Activity can access.
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	//Initialize global variables
    	historyCharityActivityGroup = new ArrayList<View>();
    	CharityActivityGroup = this;
    	
    	// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity("CharityCurationController", new 
				Intent(this,CharityCurationController.class)		//First page
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		
		// Replace the view of this ActivityGroup
		replaceView(view);
    }
        
    public void changeView(View v)  { // Changing one activity to another on same level
    	historyCharityActivityGroup.remove(historyCharityActivityGroup.size()-1);
    	historyCharityActivityGroup.add(v);
		setContentView(v);
	}

	public void replaceView(View v) {   // Changing to new activity level.
		//Log.d("BG","Replacing View...");
		historyCharityActivityGroup.add(v);   
		setContentView(v); 
	}   

	public void back() { // On back key press
		if(historyCharityActivityGroup.size() > 1) {   
			historyCharityActivityGroup.remove(historyCharityActivityGroup.size()-1);   
			setContentView(historyCharityActivityGroup.get(historyCharityActivityGroup.size()-1)); 
		} else {   
			finish(); // Finish tabactivity
			Log.d("BG", "onDestroy called from "+this.getClass().toString());
			System.exit(0);
		}   
	}  

	public void onBackPressed() { // Event Handler
		Log.d("BG", "Back Pressed from BgActivityGroup");
		CharityActivityGroup.back();   
		return;
	}
    
    //Accessor methods
    public CharityActivityGroup getBgActivityGroup(){
    	return CharityActivityGroup;
    }
    
    public void setBizActivityGroup(CharityActivityGroup BgActivityGroup){
    	this.CharityActivityGroup = BgActivityGroup;  
    }
    
    public ArrayList<View> getHistoryBgActivityGroup(){
    	return historyCharityActivityGroup;
    }
    
    public void setHistoryBgActivityGroup(ArrayList<View> historyBgActivityGroup){
    	this.historyCharityActivityGroup = historyBgActivityGroup;
    }
    
    public void onDestory(){
		super.onDestroy();
		Log.d("BG", "onDestroy called from "+this.getClass().toString());
		System.exit(0);
	}
}