package com.bridginggood;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class LoginController extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView txtHello = (TextView) findViewById(R.id.txtHello);
		txtHello.setText("성주 까꾸꾸꾸꾸꾸꿍!");
	}
}
