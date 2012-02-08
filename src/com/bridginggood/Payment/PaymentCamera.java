package com.bridginggood.Payment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bridginggood.R;

public class PaymentCamera extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView txtHello = (TextView) findViewById(R.id.txtHello);
		txtHello.setText("성주 까꾸꾸꾸꾸꾸꿍!");

		initButton();
	}

	private void initButton(){
		Button btnGoBizMap = (Button) findViewById(R.id.btnTestButton);
		btnGoBizMap.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "Button Clicked");
			}
		});
	}
}