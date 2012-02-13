package com.bridginggood.Biz;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class BizExtendedMapView extends MapView{
	// ------------------------------------------------------------------------
	// LISTENER DEFINITIONS
	// ------------------------------------------------------------------------

	// Change listener
	public interface OnChangeListener
	{
		public void onChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom);
	}

	// ------------------------------------------------------------------------
	// MEMBERS
	// ------------------------------------------------------------------------

	private BizExtendedMapView mThis;
	private long mEventsTimeout = 500L;     // Set this variable to your preferred timeout
	private boolean mIsTouched = false;
	private GeoPoint mLastCenterPosition;
	private int mLastZoomLevel;
	private Timer mChangeDelayTimer = new Timer();
	private BizExtendedMapView.OnChangeListener mChangeListener = null;
	private boolean mLock = false;

	// ------------------------------------------------------------------------
	// CONSTRUCTORS
	// ------------------------------------------------------------------------

	public BizExtendedMapView(Context context, String apiKey)
	{
		super(context, apiKey);
		init();
	}

	public BizExtendedMapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public BizExtendedMapView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		mThis = this;
		mLastCenterPosition = this.getMapCenter();
		mLastZoomLevel = this.getZoomLevel();
		mLock = false;
	}

	// ------------------------------------------------------------------------
	// GETTERS / SETTERS
	// ------------------------------------------------------------------------

	public void setOnChangeListener(BizExtendedMapView.OnChangeListener l)
	{
		mChangeListener = l;
	}

	// ------------------------------------------------------------------------
	// EVENT HANDLERS
	// ------------------------------------------------------------------------

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		// Set touch internal
		mIsTouched = (ev.getAction() != MotionEvent.ACTION_UP);

		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll()
	{
		super.computeScroll();

		// Check for change
		if (isSpanChange() || isZoomChange())
		{
			// If computeScroll called before timer counts down we should drop it and
			// start counter over again
			resetMapChangeTimer();
		}
	}

	// ------------------------------------------------------------------------
	// TIMER RESETS
	// ------------------------------------------------------------------------

	private void resetMapChangeTimer()
	{
		mChangeDelayTimer.cancel();
		mChangeDelayTimer = new Timer();
		mChangeDelayTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				if(mLock)
					return;
				mLock = true;
				if (mChangeListener != null) mChangeListener.onChange(mThis, getMapCenter(), mLastCenterPosition, getZoomLevel(), mLastZoomLevel);
				mLastCenterPosition = getMapCenter();
				mLastZoomLevel = getZoomLevel();
				mLock = false;
			}
		}, mEventsTimeout);
	}

	// ------------------------------------------------------------------------
	// CHANGE FUNCTIONS
	// ------------------------------------------------------------------------

	private boolean isSpanChange()
	{
		return !mIsTouched && !getMapCenter().equals(mLastCenterPosition);
	}

	private boolean isZoomChange()
	{
		return (getZoomLevel() != mLastZoomLevel);
	}
}
