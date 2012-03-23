package com.bridginggood.User;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bridginggood.LoginActivity;
import com.bridginggood.R;
import com.bridginggood.UserInfo;
import com.bridginggood.UserInfoStore;
import com.bridginggood.DB.UserLoginJSON;

public class UserPreferencesActivity extends Activity{

	private ListView mMenuListView;
	private UserPreferencesAdapter mAdapter;
	private ArrayList<String> mProfilePreferencesArrayList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_preferences_layout);
		//TextView txtHello = (TextView) findViewById(R.id.txtHello);
		//txtHello.setText("Hello "+UserInfo.getUserFirstName()+" "+UserInfo.getUserLastName()+"\n"+
		//UserInfo.getUserEmail()+" | Type: "+UserInfo.getUserType()+" | DeviceId: "+UserInfo.getDeviceId()
		//	+" | c2dm: "+UserInfo.getC2DMRegistrationId());

		initMenu();
	}

	private void initMenu(){
		mProfilePreferencesArrayList = new ArrayList<String>();
		mProfilePreferencesArrayList.add(getString(R.string.profile_preferences_menu_youaccount));
		mProfilePreferencesArrayList.add(getString(R.string.profile_preferences_menu_donationpriorities));
		mProfilePreferencesArrayList.add(getString(R.string.profile_preferences_menu_pushnotification));
		mProfilePreferencesArrayList.add(getString(R.string.profile_preferences_menu_socialnetwork));
		mProfilePreferencesArrayList.add(getString(R.string.profile_preferences_menu_mapandgeosettings));
		mProfilePreferencesArrayList.add(getString(R.string.profile_preferences_menu_clearimagecache));
		mProfilePreferencesArrayList.add(getString(R.string.profile_preferences_menu_logout));


		mAdapter = new UserPreferencesAdapter(this, R.layout.profile_preferences_menu_cell, mProfilePreferencesArrayList);
		mMenuListView = (ListView)findViewById(R.id.profile_preferences_menu_listview);

		mMenuListView.setAdapter(mAdapter);
		mMenuListView.setOnItemClickListener(mItemClickListener);
	}

	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			if(position < 0 )	//Clicked header
				return;
			if(position == mAdapter.getCount()) //Clicked footer
				return;

			String item = mProfilePreferencesArrayList.get(position);

			//Clear cache - TEMP code!
			if(item.equalsIgnoreCase(getString(R.string.profile_preferences_menu_clearimagecache))){
				handleClearImageCache();
			}
			//Logout
			else if(item.equalsIgnoreCase(getString(R.string.profile_preferences_menu_logout))){
				handleLogout();
			}
		}
	};

	private void handleClearImageCache(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(UserPreferencesActivity.this);
		builder.setMessage("Hello "+UserInfo.getUserFirstName()+" "+UserInfo.getUserLastName()+"\n"+
				UserInfo.getUserEmail()+" | Type: "+UserInfo.getUserType()+" | DeviceId: "+UserInfo.getDeviceId()
				+" | c2dm: "+UserInfo.getC2DMRegistrationId())
				.setTitle("handleClearImageCache")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void handleLogout(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(UserPreferencesActivity.this);
		builder.setMessage("Are you sure?")
		.setTitle("Logout")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {

				boolean isSucc = UserLoginJSON.logoutUser();
				if(isSucc){
					UserInfoStore.clearSession(getApplicationContext());
					startActivity(new Intent().setClass(UserPreferencesActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				} else {
					//Logout failed
					Toast.makeText(UserPreferencesActivity.this, "An error has occured while logging out.", Toast.LENGTH_SHORT).show();
				}
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}
