package com.bridginggood.User;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.bridginggood.R;

public class UserActivityGroup extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView txtHello = (TextView) findViewById(R.id.txtHello);
        txtHello.setText("User Tab");
    }
}