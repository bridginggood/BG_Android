package com.bridginggood;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
			// Send the registration ID to the 3rd party site that is sending the messages.
			sendRegistrationIdToServer(context, registration);
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
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName cn = taskInfo.get(0).topActivity;

		if (cn.getClassName().contains(CONST.PACKAGE_NAME)){
			//Intent to Thankyou page
			Intent nextIntent = new Intent(context, ThankyouActivity.class);
			nextIntent.putExtra(CONST.BUNDLE_C2DM_KEY, message);
			nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			//BridgingGood application is currently running!
	        context.startActivity(nextIntent);
		}
		else {
			//BridgingGood application is not running.
			//Intent to Splash page
			UserInfo.setLoadThankyouActivity(true);
			Intent nextIntent = new Intent(context, SplashActivity.class);
			nextIntent.putExtra(CONST.BUNDLE_C2DM_KEY, message);
			nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			createNotification(context, nextIntent);
		}
	}

	public void createNotification(Context context, Intent intent) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon, context.getResources().getString(R.string.c2dm_receive_message) , System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		//Notificatino configuration
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		notification.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), context.getResources().getString(R.string.c2dm_notification_message), pendingIntent);
		notificationManager.notify(0, notification);

	}

	/**
	 * Sends registration Id to the server
	 * @param context Got it from MainController
	 */
	private void sendRegistrationIdToServer(final Context context, final String regId){
		Thread threadStartLogin = new Thread(new Runnable() {
			public void run() {
				Log.d("BGB", "Sending C2dm registration to BG server");
				boolean isSucc = UserLoginJSON.sendC2DMRegistrationId(UserInfo.calcDeviceId(context), regId);
				if (isSucc){
					UserInfoStore.saveUserSessionC2DMOnly(context, regId);
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
