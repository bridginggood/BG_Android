package com.bridginggood.Biz;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridginggood.CONST;
import com.bridginggood.ImageManager;
import com.bridginggood.ImageManager.ImageManagerResult;
import com.bridginggood.R;
import com.bridginggood.UserInfo;

public class BizListAdapter extends ArrayAdapter<Business>{

    Activity mActivity;
    int mLayoutResourceId;    
    ArrayList<Business> mData = null;
    ImageManager mImageManager;
    
    public BizListAdapter(Activity activity, int layoutResourceId, ArrayList<Business> data) {
        super(activity, layoutResourceId, data);
        this.mLayoutResourceId = layoutResourceId;
        this.mData = data;
        this.mActivity = activity;
        mImageManager = new ImageManager(activity.getApplicationContext(), true, mImageDownloaded);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BusinessHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            
            //create temporary holder
            holder = new BusinessHolder();
            holder.setBizLogo((ImageView)row.findViewById( R.id.bizLogo ));
            holder.setBizName((TextView)row.findViewById(R.id.bizName));
            holder.setBizAddress((TextView)row.findViewById(R.id.bizAddress));
            holder.setDistanceAway((TextView)row.findViewById(R.id.distanceAway));
            holder.setCharityLogo((ImageView)row.findViewById(R.id.charityLogo));
            
            row.setTag(holder);
        }
        else
        {
            holder = (BusinessHolder)row.getTag();
            
            final Business biz = mData.get(position);
            if(biz != null){
            	holder.updateBizName(biz.getBizName());
            	holder.updateBizAddress(biz.getBizAddress());
            	mImageManager.displayImage(UserInfo.getQRCodeURL(), mActivity, holder.bizLogo);
            }
        }
        
        //Get current bizCell
        Business bizCell = mData.get(position);
       
        //Update the content of the cell
        holder.updateBizLogo(bizCell.getBizLogo());
        holder.updateBizName(bizCell.getBizName());
        holder.updateBizAddress(bizCell.getBizAddress());
        holder.updateDistanceAway(bizCell.getDistanceAwayStr("miles"));	
        holder.updateCharityLogo(bizCell.getCharityLogo());
        
        return row;
    }
    
    public ImageManagerResult mImageDownloaded = new ImageManagerResult()
	{
		@Override
		public void gotImage(final boolean isLoaded)
		{
			;
		}
	};
    
    //Use this holder class for optimization. (Does not load ImageView all the time)
    static class BusinessHolder
    {
        ImageView bizLogo, charityLogo;
        TextView bizName, bizAddress, distanceAway;
        
        public void setBizLogo(ImageView bizLogo){
        	this.bizLogo = bizLogo;
        }
        
        public void setBizName(TextView bizName){
        	this.bizName = bizName;
        }
        
        public void setBizAddress(TextView bizAddress){
        	this.bizAddress = bizAddress;
        }
        
        public void setDistanceAway(TextView distanceAway){
        	this.distanceAway = distanceAway;
        }
        
        public void setCharityLogo(ImageView charityLogo){
        	this.charityLogo = charityLogo;
        }
        
        public void updateBizLogo(int bizLogo){
        	this.bizLogo.setImageResource(bizLogo);
        }
        
        public void updateBizName(String bizName){
        	this.bizName.setText(bizName);
        }
        
        public void updateBizAddress(String bizAddress){
        	this.bizAddress.setText(bizAddress);
        }
        
        public void updateDistanceAway(String distanceAway){
        	this.distanceAway.setText(distanceAway);
        }
        
        public void updateCharityLogo(int charityLogo){
        	this.charityLogo.setImageResource(charityLogo);
        }
    }
}