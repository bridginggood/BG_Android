package com.bridginggood.Biz;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bridginggood.CustomBalloonOverlayView;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class BizMapOverlay<Item extends OverlayItem> extends BalloonItemizedOverlay<CustomOverlayItem>{

	private ArrayList<CustomOverlayItem> mOverlayItemArrayList = new ArrayList<CustomOverlayItem>();
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
	protected CustomOverlayItem createItem(int i) {
		return mOverlayItemArrayList.get(i);
	}

	@Override
	public int size() {
		return mOverlayItemArrayList.size();
	}

	@Override
	protected boolean onBalloonTap(int index, CustomOverlayItem item) {
		//Toast.makeText(this.context, "onBalloonTap for overlay index " + bizList.get(index).getBizName(), Toast.LENGTH_LONG).show();
		Business biz = mBizArrayList.get(index);
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, BizDetailActivity.class);

		Bundle extra = new Bundle();
		extra.putSerializable("biz", (Serializable) biz);
		intent.putExtras(extra);

		context.startActivity(intent);
		return true;
	}

	public void addOverlay(CustomOverlayItem overlay) {
		mOverlayItemArrayList.add(overlay);
		this.populate();
	}
	
	@Override
	protected BalloonOverlayView<CustomOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new CustomBalloonOverlayView<CustomOverlayItem>(getMapView().getContext(), getBalloonBottomOffset());
	}
}
