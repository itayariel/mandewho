package com.itay.mandewho;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {


	private ArrayList<View> myScreens;
	private ParseUser currentUser;
	private enum Screen {
		PROGRESS,FACEBOOK
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_login);
		Parse.initialize(this, "4PgddgAXZjvMhpVJeILJ54dNH21NaRTpR4kTCVqQ", "1ur95H52EAwP6UVPNEHoHWEUYXGVmu5K1QwqZpyB");
		ParseFacebookUtils.initialize("537020433036560");
		currentUser = ParseUser.getCurrentUser();
		myScreens = new ArrayList<View>();
		View v = findViewById(R.id.login_status);
		myScreens.add(v);
		v = findViewById(R.id.facebookpage);
		myScreens.add(v);
		buttonsListener listener = new buttonsListener();
		findViewById(R.id.login_button).setOnClickListener(listener);
		/*
		 * Kill user saved login
		ParseUser.logOut();
		Session session = Session.getActiveSession();
		if(session !=null)
		{
			session.closeAndClearTokenInformation();
		}
		currentUser = ParseUser.getCurrentUser();
		//*/
		/*
		 * Check if user is logged in 
		 */
		if (currentUser == null) {			// Set up the register form.
			
			currentUser = new ParseUser();
			showScreen(Screen.FACEBOOK);
		}else
		{
			moveToMain();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	/*
	 * A button listener class
	 */
	private class buttonsListener implements View.OnClickListener 
	{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.login_button:
				showScreen(Screen.PROGRESS);
				startFacebookLogin();
				break;
			}
		}
	}

	/*
	 * When user click sign in with facebook
	 */
	public void startFacebookLogin() {
		ParseFacebookUtils.logIn(Arrays.asList("email", ParseFacebookUtils.Permissions.User.GROUPS),
				this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if(err==null)
				{
					currentUser=user;
					
					getFacebookIdInBackground();
				}
			}
		});
	}

	/*
	 * After we got user we request for some data from facebook
	 */
	private void getFacebookIdInBackground() {
		Request.Callback C = new Request.Callback() {

			//When we got user data
			public void onCompleted(Response response) {
				if (response != null) {
			//TODO it fails here when the response is error but not null
					try {
						saveFaceBookDataAndMoveToMain(response.getGraphObject().getInnerJSONObject());
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

	protected void saveFaceBookDataAndMoveToMain(JSONObject user) {
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

			ActiveGroups list = new ActiveGroups(null,(JSONArray)facebookGroups.get("data"));
			currentUser.addAllUnique(UserFields.ACTIVEGROUPS, list.getActiveGroupsList());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		currentUser.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {
					moveToMain();
				} else {
					System.out.println("error"+e);
					
				}
			}
		});
	}

	/*
	 * Closes this activity and sends user to main activity
	 */
	public void moveToMain() {
		final Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();	
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}


	/**
	 * Shows the requested screen
	 */
	private void showScreen(Screen screen) {
		for(Screen s :Screen.values())
		{
			if(s==screen)
			{
				myScreens.get(s.ordinal()).setVisibility(View.VISIBLE);
			}
			else
			{
				myScreens.get(s.ordinal()).setVisibility(View.GONE);
			}

		}
	}
}
