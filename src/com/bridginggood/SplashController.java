/**
 * Created By: Junsung Lim
 * 
 * Description:
 * 	Starting point of the BridgingGood application.
 * 	
 * 	Checks whether saved user token exists or not. If not, directs user to the login page.
 * 	Otherwise, directs the user to the MainController
 */
package com.bridginggood;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashController extends Activity {
	protected final static long SPLASH_DELAY = 1500;	//Minimum time to show Splash in milliseconds

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				long startTime = android.os.SystemClock.uptimeMillis();

				//Check if saved user token exists or not
				boolean userWithValidUserToken = hasValidUserToken();
				
				/*
				 * Decide where to redirect user depending on userWithValidUserToken
				 */
				Class<?> targetClass = null;
				if(userWithValidUserToken){
					//Go to main tabhost view
					 targetClass = MainController.class;
				}
				else{
					//Go to login view
					targetClass = LoginController.class;
				}

				long durationTime = android.os.SystemClock.uptimeMillis() - startTime;
				if (durationTime <=SPLASH_DELAY) {
					try {
						wait(SPLASH_DELAY-durationTime);			//Pause the application for remaining time to meet SPLASH_DELAY
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				finish();
				startActivity(new Intent().setClass(SplashController.this, targetClass));
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, 0);
	}

	private boolean hasValidUserToken(){
		UserSession userSession = SessionStore.loadUserSession(getApplicationContext());
		if(userSession.isEmptySession())
			return false;
		else{
			//TODO: check user authentication with userSession
			return true;
		}
	}
}
