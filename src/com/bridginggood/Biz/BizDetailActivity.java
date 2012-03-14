package com.bridginggood.Biz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.bridginggood.R;

public class BizDetailActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView txtHello = (TextView) findViewById(R.id.txtHello);
		txtHello.setText("Biz Data should be here!");
		
		Business biz = (Business) getIntent().getExtras().get("biz");
		txtHello.setText(biz.getBizName()+"\n"+biz.getBizAddress()+"\n"+biz.getBizLat()+" , "+biz.getBizLng());
	}
}
