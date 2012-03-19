package com.bridginggood.Charity;

import android.app.Activity;
import android.os.Bundle;

import com.bridginggood.R;

public class CharityCurationActivity extends Activity{
	/** maintains the pager adapter*/

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.charity_layout);
	}
}