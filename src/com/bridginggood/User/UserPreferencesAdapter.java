package com.bridginggood.User;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bridginggood.R;

public class UserPreferencesAdapter extends ArrayAdapter<String>{  
	Activity mActivity;
	int mLayoutResourceId;    
	ArrayList<String> mData = null;
	
	public UserPreferencesAdapter(Activity activity, int layoutResourceId, ArrayList<String> data) {
		super(activity, layoutResourceId, data);
		this.mLayoutResourceId = layoutResourceId;
		this.mData = data;
		this.mActivity = activity;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}
		String item = mData.get(position);
		if (item != null) {
			TextView textview = (TextView) row.findViewById(R.id.profile_preferences_menu_textview);
			if (textview != null){
				textview.setText(item);                            
			}
		}
		return row;
	}
}
