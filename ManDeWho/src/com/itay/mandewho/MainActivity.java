package com.itay.mandewho;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.parse.*;

import android.os.Bundle;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;

public class MainActivity extends FragmentActivity {
	static final int ITEMS = 3;
	static final int ADD = 0;
	static final int LIVE = 1;
	static final int SETTINGS = 2;
	MyAdapter mAdapter;
	ViewPager mPager;
	Button liveButton, addButton,settingsButton;
	public ParseUser currentUser;
	public AddFragment myAddFragment=null;
	public SettingsFragment mySettingsFragment=null;
	public boolean activityCanceled =false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Parse.initialize(this, "4PgddgAXZjvMhpVJeILJ54dNH21NaRTpR4kTCVqQ", "1ur95H52EAwP6UVPNEHoHWEUYXGVmu5K1QwqZpyB");
		setContentView(R.layout.fragment_pager);
		currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {//User is not logged in
			moveToLogin();
		} 
		initPages();
		getUserDetails();
		mPager.setPageMargin(20);
		swipeListen();
	}

	/*
	 * Updates the user from the cloud and from facebook
	 */
	private void getUserDetails() {
		currentUser.fetchInBackground(new GetCallback<ParseObject>() {
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					getFacebookGroupsInBackground();
				} else {
				}
			}
		});

	}
	
	/*
	 * Sends request to facebook graph for updated user details
	 */
	private void getFacebookGroupsInBackground() {
		Request.Callback C = new Request.Callback() {
			public void onCompleted(Response response) {
				if (response != null) {
					try {
						saveFaceBookData(response.getGraphObject().getInnerJSONObject());
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		};
		Bundle params = new Bundle();
		params.putString("fields", "id,name,groups,email,picture");
		Request request = new Request(ParseFacebookUtils.getSession(),
				"me", params, HttpMethod.GET, C);

		RequestAsyncTask task = new RequestAsyncTask(request);
		task.execute();
	}

	/*
	 * Saves the user facebook details inside parse
	 */
	protected void saveFaceBookData(JSONObject user) {
		try {
			currentUser.put(UserFields.FACEBOOKID, user.get("id"));
			currentUser.put(UserFields.FULLNAME, user.get("name"));
			currentUser.put(UserFields.EMAIL, user.get("email"));
			JSONObject pictureObject = new JSONObject(user.get("picture").toString());
			JSONObject pictureInnerObject = pictureObject.getJSONObject("data");
			if(pictureInnerObject.isNull("url"))
			{
				currentUser.put(UserFields.PICTURE, null);
			}
			else
			{
				currentUser.put(UserFields.PICTURE, pictureInnerObject.get("url"));
			}
			JSONObject facebookGroups = new JSONObject(user.get("groups").toString());
			currentUser.put(UserFields.GROUPS, facebookGroups.get("data"));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		updateAll();
		currentUser.saveInBackground();
	}

	/*
	 * Moves the user to the login activity if he choose to logout
	 */
	public void moveToLogin() {
		final Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();	
	}

	/*
	 * initiates the fragments pages
	 */
	private void initPages()
	{
		mAdapter = new MyAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(LIVE);
		mPager.setOnPageChangeListener(null);

		liveButton = (Button) findViewById(R.id.Live);
		addButton= (Button) findViewById(R.id.NewRide);
		settingsButton = (Button) findViewById(R.id.MySettings);
		liveButton.setBackgroundResource(R.drawable.chosen_tab_design);
		liveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mPager.setCurrentItem(LIVE);
				swipeToTab(LIVE);
			}

		});
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mPager.setCurrentItem(ADD);
				swipeToTab(ADD);
			}
		});


		settingsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mPager.setCurrentItem(SETTINGS);
				swipeToTab(SETTINGS);
			}
		});
	}

	/*
	 * Swipes user to the chosen tab from the bar on top
	 */
	private void swipeToTab(int tab)
	{
		HorizontalScrollView sv = (HorizontalScrollView)findViewById(R.id.tabscroller);
		switch(tab){
		case ADD:
			changeBackground(addButton);
			sv.scrollTo(0, 0);
			break;
		case LIVE:
			changeBackground(liveButton);
			sv.scrollTo(40, 0);
			break;
		case SETTINGS:
			changeBackground(settingsButton);
			sv.scrollTo(180, 0);
			break;
		}
	}

	/*
	 * Listens to user swipes
	 */
	private void swipeListen(){
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int position) {}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageSelected(int position) {
				swipeToTab(position);
			}

		});

	}

	/*
	 * changes the background of the tabs buttons
	 */
	public void changeBackground(Button pressed)
	{
		addButton.setBackgroundResource(R.drawable.tab_design);
		settingsButton.setBackgroundResource(R.drawable.tab_design);
		liveButton.setBackgroundResource(R.drawable.tab_design);
		pressed.setBackgroundResource(R.drawable.chosen_tab_design);
	}

	/*
	 * Page Adapter for the activity fragments
	 */
	public static class MyAdapter extends FragmentPagerAdapter {
		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return ITEMS;
		}
		@Override 
		public void destroyItem(ViewGroup container, int position, Object object) {
		} 

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case ADD: // Fragment # 0 - add ride frag
			return AddFragment.init(position);
			case LIVE: // Fragment # 1 - live rides frag
				return LiveFragment.init(position);
			case SETTINGS: // Fragment # 2 - settings frag
				return SettingsFragment.init(position);
			default:
				return null;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * Called by fragment for keeping connection to current fragment
	 */
	public void setSettingFragment(SettingsFragment frag)
	{
		mySettingsFragment=frag;
	}
	
	/*
	 * Called by fragment for keeping connection to current fragment
	 */
	public void setAddFragment(AddFragment frag)
	{
		myAddFragment=frag;
	}

	/*
	 * Deletes all user data before login out
	 */
	public void logOut() {
		ParseUser.logOut();
		Session session = Session.getActiveSession();
		if(session !=null)
		{
			session.closeAndClearTokenInformation();
		}
		currentUser = ParseUser.getCurrentUser();
		moveToLogin();
	}

	/*
	 * Update fragments after fetching new data
	 */
	private void updateAll()
	{
		if(myAddFragment!=null)
		{
			myAddFragment.updateGui();
		}
		if(mySettingsFragment!=null)
		{
			mySettingsFragment.updateGui();
		}
	}
	
	public void onBackPressed() {
	    super.onBackPressed();   
	    activityCanceled =true;
	}
}