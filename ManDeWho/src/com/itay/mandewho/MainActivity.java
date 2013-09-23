package com.itay.mandewho;

import java.util.ArrayList;
import java.util.Currency;

import com.facebook.Session;
import com.parse.*;

import android.os.Bundle;

import android.app.Activity;

import android.content.Context;
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
import android.widget.ScrollView;

public class MainActivity extends FragmentActivity {
	static final int ITEMS = 3;
	static final int ADD = 0;
	static final int LIVE = 1;
	static final int SETTINGS = 2;
	MyAdapter mAdapter;
	ViewPager mPager;
	Button liveButton, addButton,settingsButton;
	public ParseUser currentUser;
	public SettingsFragment mySettingFragment=null;
	public LiveFragment myLiveFragment=null;
	public AddFragment myAddFragment=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Parse.initialize(this, "4PgddgAXZjvMhpVJeILJ54dNH21NaRTpR4kTCVqQ", "1ur95H52EAwP6UVPNEHoHWEUYXGVmu5K1QwqZpyB");
		setContentView(R.layout.fragment_pager);
		initPages();
		currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {

		} else {
			//User is not logged in
			moveToLogin();
		}
		getUserDetails();
		mPager.setPageMargin(20);
		
		swipeListen();
	}

	private void getUserDetails() {
		System.out.println("fetching");
		currentUser.fetchInBackground(new GetCallback<ParseUser>() {
			  public void done(ParseUser newUser, ParseException e) {
			    if (e == null) {
			    	currentUser=newUser;
			    	System.out.println("fetch success");
			    } else {
			    	System.out.println("fetch failed");
			    	System.out.println(e.toString());
			    }
			  }
			});
		
	}
	
	public void onResume()
	{
		super.onResume();
		if(myLiveFragment!=null)
		{
			myLiveFragment.updateGui();
		}
	}
	
	public void moveToLogin() {
		final Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();	
	}

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

	public void changeBackground(Button pressed)
	{
		addButton.setBackgroundResource(R.drawable.tab_design);
		settingsButton.setBackgroundResource(R.drawable.tab_design);
		liveButton.setBackgroundResource(R.drawable.tab_design);
		pressed.setBackgroundResource(R.drawable.chosen_tab_design);
	}

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
	
	private String getFragmentTag(int pos){
	    return "android:switcher:"+R.id.pager+":"+pos;
	}
	
	
	public void setSettingFragment(SettingsFragment frag)
	{
		mySettingFragment=frag;
	}
	
	public void setLiveFragment(LiveFragment frag)
	{
		myLiveFragment=frag;
	}

	public void logOut() {
		System.out.println("you want to log out");
		ParseUser.logOut();
		Session session = Session.getActiveSession();
		if(session !=null)
		{
			session.closeAndClearTokenInformation();
		}
		currentUser = ParseUser.getCurrentUser();
		moveToLogin();
	}

}