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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashController extends Activity {
	protected final static long SPLASH_DELAY = 1500;	//Minimum time to show Splash in milliseconds

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);

		Thread splashTread = new Thread() {
			@Override
			public void run() {
				long startTime = android.os.SystemClock.uptimeMillis();

				/*
				 *===============START Loading Job==================== 
				 */

				//Check if saved user token exists or not
				boolean userWithValidUserToken = hasValidUserToken();

				/*
				 * Decide where to redirect user depending on userWithValidUserToken
				 */
				Log.d("BG", "userWithValidUserToken "+userWithValidUserToken);

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
				Log.d("BG", "Should redirect soon "+durationTime);
				if (durationTime <=SPLASH_DELAY) {
					try {
						synchronized(this){
							wait(SPLASH_DELAY-durationTime);			//Pause the application for remaining time to meet SPLASH_DELAY
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				/*
				 *===============END Loading Job==================== 
				 */

				finish();
				startActivity(new Intent().setClass(SplashController.this, targetClass));
			}
		};
		splashTread.start();
	}

	private boolean hasValidUserToken(){
		UserSession userSession = SessionStore.loadUserSession(getApplicationContext());
		if(userSession.isSessionTokenEmpty())
			return false;
		else{
			//TODO: check user authentication with userSession
			return true;
		}
	}
}
