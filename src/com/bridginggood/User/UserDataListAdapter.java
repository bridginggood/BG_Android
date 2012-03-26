package com.bridginggood.User;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bridginggood.R;
import com.bridginggood.ValuePair;

public class UserDataListAdapter extends ArrayAdapter<ValuePair<String, String>>{

	Activity mActivity;
	int mLayoutResourceId;    
	ArrayList<ValuePair<String, String>> mData = null;

	public UserDataListAdapter(Activity activity, int layoutResourceId, ArrayList<ValuePair<String, String>> data) {
		super(activity, layoutResourceId, data);
		this.mLayoutResourceId = layoutResourceId;
		this.mData = data;
		this.mActivity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ValuePairHolder holder = null;

		if(row == null)
		{
			LayoutInflater inflater = mActivity.getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);

			//create temporary holder
			holder = new ValuePairHolder();
			holder.setLeftTextView((TextView)row.findViewById(R.id.profile_datalist_left_textview));
			holder.setRightTextView((TextView)row.findViewById(R.id.profile_datalist_right_textview));
			row.setTag(holder);
		}
		else
		{
			holder = (ValuePairHolder)row.getTag();

			final ValuePair<String, String> biz = mData.get(position);
			if(biz != null){
				holder.updateLeftTextView(biz.getKey());
				holder.updateRightTextView(biz.getValue());
			}
		}

		//Get current bizCell
		ValuePair<String, String> vpCell = mData.get(position);

		//Update the content of the cell
		holder.updateLeftTextView(vpCell.getKey());
		holder.updateRightTextView(vpCell.getValue());


		return row;
	}


	//Use this holder class for optimization.
	static class ValuePairHolder
	{
		TextView txtLeft, txtRight;

		public void setLeftTextView(TextView txtLeft){
			this.txtLeft = txtLeft;
		}
		public void setRightTextView(TextView txtRight){
			this.txtRight = txtRight;
		}

		public void updateLeftTextView(String txtLeft){
			this.txtLeft.setText(txtLeft);
		}

		public void updateRightTextView(String txtRight){
			this.txtRight.setText(txtRight);
		}
	}
}