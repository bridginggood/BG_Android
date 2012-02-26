package com.bridginggood.Charity;

import java.util.List;
import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.bridginggood.R;
import com.viewpagerindicator.CirclePageIndicator;

public class CharityCurationController extends FragmentActivity{
	/** maintains the pager adapter*/
	private PagerAdapter mPagerAdapter;
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.charity_layout);
		//initialsie the pager
		this.initialisePaging();
	}

	/**
	 * Initialise the fragments to be paged
	 */
	private void initialisePaging() {

		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, CharityPage1.class.getName()));
		fragments.add(Fragment.instantiate(this, CharityPage2.class.getName()));
		fragments.add(Fragment.instantiate(this, CharityPage3.class.getName()));
		this.mPagerAdapter  = new CharityPagerAdapter(super.getSupportFragmentManager(), fragments);
		//
		ViewPager pager = (ViewPager)super.findViewById(R.id.viewpagerCharity);
		pager.setAdapter(this.mPagerAdapter);

		//Bind the title indicator to the adapter
		CirclePageIndicator circleIndicator = (CirclePageIndicator)findViewById(R.id.indicatorViewPagerCharity);
		circleIndicator.setViewPager(pager);
	}
}