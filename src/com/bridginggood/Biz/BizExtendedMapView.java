package com.bridginggood.Biz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class BizExtendedMapView extends MapView{
	public interface OnTouchEventListener {
		public void onTouchEvent(MapView view, int newZoom, int oldZoom, GeoPoint newMapCenter, GeoPoint oldMapCenter);
	}

	private BizExtendedMapView _this;
	
	private GeoPoint mPrevMapCenter;
	private int mPrevZoom;

	private BizExtendedMapView.OnTouchEventListener mOnTouchListener;

	public BizExtendedMapView(Context context, String apiKey) {
		super(context, apiKey);
		_this = this;
		mPrevMapCenter = this.getMapCenter();
		mPrevZoom = this.getZoomLevel();
	}

	public BizExtendedMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BizExtendedMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnTouchEventListener(BizExtendedMapView.OnTouchEventListener l) {
		mOnTouchListener = l;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//User has finished touching the view
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			//Change detected
			if(getZoomLevel() != mPrevZoom || getMapCenter() != mPrevMapCenter){
				mOnTouchListener.onTouchEvent(_this, getZoomLevel(), mPrevZoom, getMapCenter(), mPrevMapCenter);
				mPrevZoom = getZoomLevel();
			}
		}
		return super.onTouchEvent(ev);
	}
}
