package com.bridginggood.Charity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.bridginggood.R;

public class CharityCurationController extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charity_layout);
		
		CharityPagerAdapter adapter = new CharityPagerAdapter( this );
		ViewPager pager = (ViewPager)findViewById( R.id.viewpager );
		pager.setAdapter( adapter );
	}
}
