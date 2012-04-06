package com.bridginggood.Biz;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class BizMapOverlay<Item extends OverlayItem> extends BalloonItemizedOverlay<BizMapOverlayItem>{

	private ArrayList<BizMapOverlayItem> mOverlayItemArrayList = new ArrayList<BizMapOverlayItem>();
	private ArrayList<Business> mBizArrayList = new ArrayList<Business>();
	private Context context;

	public BizMapOverlay(Drawable defaultMarker, MapView mapView, Context context) {
		super(boundCenterBottom(defaultMarker), mapView);
		this.context = context;
	}

	public void setBizArrayList(ArrayList<Business> bizList){
		this.mBizArrayList = bizList;
	}

	@Override
	protected BizMapOverlayItem createItem(int i) {
		return mOverlayItemArrayList.get(i);
	}

	@Override
	public int size() {
		return mOverlayItemArrayList.size();
	}

	@Override
	protected boolean onBalloonTap(int index, BizMapOverlayItem item) {
		try{
			Business biz = null;
			//Search for the business using the Overlay Item BizId
			for(Business business : mBizArrayList){
				if(business.getBizId().equals(item.getBizId())){
					biz = business;
					break;
				}
			}

			//Toast.makeText(this.context, "onBalloonTap for overlay index " + bizList.get(index).getBizName(), Toast.LENGTH_LONG).show();

			// TODO Auto-generated method stub
			Intent intent = new Intent(context, BizDetailActivity.class);

			Bundle extra = new Bundle();
			extra.putSerializable("biz", (Serializable) biz);
			intent.putExtras(extra);

			context.startActivity(intent);
		} catch(Exception e){
			Log.d("BG", "Exception on balloon tap:"+e.getLocalizedMessage());
		}

		return true;
	}

	public void addOverlay(BizMapOverlayItem overlay) {
		mOverlayItemArrayList.add(overlay);
		this.populate();
	}

	@Override
	protected BalloonOverlayView<BizMapOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new BalloonOverlayView<BizMapOverlayItem>(getMapView().getContext(), getBalloonBottomOffset());
	}
	
	
	@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow)
    {
		//Remove shadow
        if(!shadow)
        {
            super.draw(canvas, mapView, false);
        }
    }
}
