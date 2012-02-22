package com.bridginggood.Charity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.bridginggood.R;
import com.viewpagerindicator.CirclePageIndicator;

public class CharityCurationController extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charity_layout);
		
		CharityPagerAdapter adapter = new CharityPagerAdapter( this );
		ViewPager pager = (ViewPager)findViewById( R.id.viewpagerCharity );
		pager.setAdapter( adapter );
		
		//Bind the title indicator to the adapter
		CirclePageIndicator circleIndicator = (CirclePageIndicator)findViewById(R.id.indicatorViewPagerCharity);
		circleIndicator.setViewPager(pager);
	}
}
