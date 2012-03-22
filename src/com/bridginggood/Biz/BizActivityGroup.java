package com.bridginggood.Biz;

import java.util.ArrayList;

import com.bridginggood.R;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class BizActivityGroup extends ActivityGroup {

	private ArrayList<View> historyBizActivityGroup; 		// ArrayList to manage Views.
	private BizActivityGroup bizActivityGroup; 				// BizActivityGroup that Activity can access.
	private boolean mExitApplication = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Initialize global variables
		historyBizActivityGroup = new ArrayList<View>();
		bizActivityGroup = this;

		// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity("BizList", new 
				Intent(this,BizListActivity.class)		//First page
		.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
		.getDecorView();

		// Replace the view of this ActivityGroup
		replaceView(view);
	}

	public void changeView(View v)  { // Changing one activity to another on same level
		historyBizActivityGroup.remove(historyBizActivityGroup.size()-1);
		historyBizActivityGroup.add(v);
		setContentView(v);
	}

	public void replaceView(View v) {   // Changing to new activity level.
		mExitApplication = false;
		historyBizActivityGroup.add(v);   
		setContentView(v); 
	}   

	public void back() { // On back key press
		if(historyBizActivityGroup.size() > 1) {   
			historyBizActivityGroup.remove(historyBizActivityGroup.size()-1);   
			setContentView(historyBizActivityGroup.get(historyBizActivityGroup.size()-1)); 
		} else {   
			if(mExitApplication){
				finish(); // Finish tabactivity
				Log.d("BgBiz", "onDestroy called from "+this.getClass().toString());
				System.exit(0);
			}else{
				Toast.makeText(this,  R.string.application_backtoexit, Toast.LENGTH_SHORT).show();
				mExitApplication = true;
			}
		}   
	}  

	public void onBackPressed() { // Event Handler
		Log.d("BgBiz", "Back Pressed from BizActivityGroup");
		bizActivityGroup.back();   
		return;
	}

	//Accessor methods
	public BizActivityGroup getBizActivityGroup(){
		return bizActivityGroup;
	}

	public void setBizActivityGroup(BizActivityGroup bizActivityGroup){
		this.bizActivityGroup = bizActivityGroup;  
	}

	public ArrayList<View> getHistoryBizActivityGroup(){
		return historyBizActivityGroup;
	}

	public void setHistoryBizActivityGroup(ArrayList<View> historyBizActivityGroup){
		this.historyBizActivityGroup = historyBizActivityGroup;
	}

	public void onDestory(){
		super.onDestroy();
		Log.d("BgBiz", "onDestroy called from "+this.getClass().toString());
		System.exit(0);
	}
}