package com.bridginggood.Biz;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.bridginggood.BalloonItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class BizMapOverlay extends BalloonItemizedOverlay<OverlayItem>{

	private ArrayList<OverlayItem> mOverlayItemArrayList = new ArrayList<OverlayItem>();
	private ArrayList<Business> mBizArrayList = new ArrayList<Business>();
	private Context context;

	public BizMapOverlay(Drawable defaultMarker, MapView mapView, ArrayList<Business> bizList, Context context) {
		super(boundCenterBottom(defaultMarker), mapView);
		this.context = context;
		this.mBizArrayList = bizList;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlayItemArrayList.get(i);
	}

	@Override
	public int size() {
		return mOverlayItemArrayList.size();
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		//Toast.makeText(this.context, "onBalloonTap for overlay index " + bizList.get(index).getBizName(), Toast.LENGTH_LONG).show();
		Business biz = mBizArrayList.get(index);
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, BizDetailActivity.class);

		Bundle extra = new Bundle();
		extra.putSerializable("biz", (Serializable) biz);
		intent.putExtras(extra);
		
		BizActivityGroup bizActivityGroup = ((BizActivityGroup)context);
		View newView = bizActivityGroup.getBizActivityGroup().getLocalActivityManager()
				.startActivity("BizDetail", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		bizActivityGroup.getBizActivityGroup().replaceView(newView);	//Replace View
		return true;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlayItemArrayList.add(overlay);
		this.populate();
	}
}
