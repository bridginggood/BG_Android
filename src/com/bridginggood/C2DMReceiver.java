package com.bridginggood;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.bridginggood.DB.UserLoginJSON;

public class C2DMReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
	        handleRegistration(context, intent);
	    } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	        handleMessage(context, intent);
	    }
	 }
	
	/**
	 * When C2DM registration is received
	 * @param context From MainController
	 * @param intent Intent with all the result code/messages
	 */
	private void handleRegistration(Context context, Intent intent) {
	    String registration = intent.getStringExtra("registration_id");
	    if (intent.getStringExtra("error") != null) {
	        // Registration failed, should try again later.
		    Log.d("BG_C2DM", "registration failed"+intent.toString()+" | "+intent.getStringExtra("error"));
		    String error = intent.getStringExtra("error");
		    if(error == "SERVICE_NOT_AVAILABLE"){
		    	Log.d("BG_C2DM", "SERVICE_NOT_AVAILABLE");
		    }else if(error == "ACCOUNT_MISSING"){
		    	Log.d("BG_C2DM", "ACCOUNT_MISSING");
		    }else if(error == "AUTHENTICATION_FAILED"){
		    	Log.d("BG_C2DM", "AUTHENTICATION_FAILED");
		    }else if(error == "TOO_MANY_REGISTRATIONS"){
		    	Log.d("BG_C2DM", "TOO_MANY_REGISTRATIONS");
		    }else if(error == "INVALID_SENDER"){
		    	Log.d("BG_C2DM", "INVALID_SENDER");
		    }else if(error == "PHONE_REGISTRATION_ERROR"){
		    	Log.d("BG_C2DM", "PHONE_REGISTRATION_ERROR");
		    }else if(error == "ACCOUNT_MISSING"){
		    	//When the phone does not have a google account
		    	Log.d("BG_C2DM", "ACCOUNT_MISSING");
		    }
	    } else if (intent.getStringExtra("unregistered") != null) {
	        // unregistration done, new messages from the authorized sender will be rejected
	    	Log.d("BG_C2DM", "unregistered");

	    } else if (registration != null) {
	    	Log.d("BG_C2DM", registration);
	    	//Update the userInfo and save it
	    	UserInfo.setC2DMRegistrationId(registration);
	    	//UserInfoStore.saveUserSessionC2DMOnly(context);
	    	
	       // Send the registration ID to the 3rd party site that is sending the messages.
	    	sendRegistrationIdToServer(context);
	    }
	}
	
	/**
	 * When C2DM message is received
	 * @param context From MainController
	 * @param intent Intent with all the result code/messages
	 */
	private void handleMessage(Context context, Intent intent)
	{
		//Do whatever you want with the message
		String message = intent.getStringExtra("message");
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		createNotification(context, message);
	}
	
	public void createNotification(Context context, String payload) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon,
				"Message received", System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, SplashActivity.class);
		intent.putExtra("payload", payload);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "Message",
				payload, pendingIntent);
		notificationManager.notify(0, notification);

	}
	
	/**
	 * Sends registration Id to the server
	 * @param context Got it from MainController
	 */
	private void sendRegistrationIdToServer(final Context context){
		Thread threadStartLogin = new Thread(new Runnable() {
			public void run() {
				boolean isSucc = UserLoginJSON.sendC2DMRegistrationId();
				if (isSucc){
					UserInfoStore.saveUserSessionC2DMOnly(context);
				}
				
				handlerRegistration.sendEmptyMessage(0);
			}
		});
		threadStartLogin.start();	
	}

	private Handler handlerRegistration = new Handler() {
		public void handleMessage(Message msg) {
			Log.d("BG_C2DM", "sendRegistrationIdToServer DONE");
		}
	};
}
