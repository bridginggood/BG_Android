package com.bridginggood;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.EditText;

public class SignupController extends Activity{
	private EditText mEdtDOB;
	private Calendar mCalendar;
	private DatePickerDialog mDatePickerDialog; 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_layout);

		mCalendar = Calendar.getInstance();
		mEdtDOB = (EditText)findViewById(R.id.signup_edtDOB);
		mDatePickerDialog = new DatePickerDialog(SignupController.this, mDateSetListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

		initEdtDOB();
		
	}

	private void initEdtDOB(){
		mEdtDOB.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mDatePickerDialog.show();
				return true;
			}
		});

		mEdtDOB.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mDatePickerDialog.show();
				} else {
					mDatePickerDialog.dismiss();
				}
			}
		});
	}

	DatePickerDialog.OnDateSetListener mDateSetListener = 
			new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mCalendar.set(year, monthOfYear, dayOfMonth);
			mEdtDOB.setText((monthOfYear+1)+"."+dayOfMonth+"."+year);
		}
	};

}
