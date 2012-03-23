package com.bridginggood;

import android.app.Activity;
import android.os.Bundle;

public class ThankyouActivity extends Activity{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thankyou_layout);
		UserInfo.setLoadThankyouActivity(false);
	}
}
