package com.bridginggood.Charity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bridginggood.R;

public class CharityActivityGroup extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView txtHello = (TextView) findViewById(R.id.txtHello);
        txtHello.setText("Charity Tab");
    }
    
    public void onDestory(){
		super.onDestroy();
		Log.d("BG", "onDestroy called from "+this.getClass().toString());
		System.exit(0);
	}
}