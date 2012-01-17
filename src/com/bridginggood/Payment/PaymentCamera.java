package com.bridginggood.Payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bridginggood.R;
import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;

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
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, 0);
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.d("BG", "onActivityResult called: "+contents+" , "+format);
				// Handle successful scan
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// Get result
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		TextView txtHello = (TextView) findViewById(R.id.txtHello);
		txtHello.setText(result.getContents()+ "  "+result.getFormatName());
		Log.d("BG", "Payment onActivityResult called: "+result.getContents());
	}*/
}