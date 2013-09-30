package com.itay.mandewho;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import com.parse.*;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class LiveFragment extends ListFragment  {
	int fragVal;
	boolean _firstLoad;
	View _fragView;
	ParseUser _currentUser;
	MainActivity _myActivity;
	List<ParseObject> _cars;
	List<ParseObject> _riders;
	TextView _emptyList;

	private enum ListKind {
		MANS, CARS
	};
	ListKind listKind;

	static LiveFragment init(int val) {
		LiveFragment myFrag = new LiveFragment();
		// Supply val input as an argument.
		Bundle args = new Bundle();
		args.putInt("val", val);
		myFrag.setArguments(args);
		return myFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
		_myActivity = (MainActivity)getActivity();
		_currentUser = ParseUser.getCurrentUser();
		_firstLoad=true;
		listKind=ListKind.CARS;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layoutView = inflater.inflate(R.layout.fragment_live, container,
				false);
		_fragView= layoutView;
		_cars = new ArrayList<ParseObject>();
		_riders = new ArrayList<ParseObject>();
		_emptyList = (TextView) layoutView.findViewById(android.R.id.empty);
		updateGui();
		buttonsListener listener = new buttonsListener();
		ImageButton carButton = (ImageButton) layoutView.findViewById(R.id.carListButton);
		carButton.setOnClickListener(listener);
		ImageButton manButton = (ImageButton) layoutView.findViewById(R.id.manListButton);
		manButton.setOnClickListener(listener);
		ImageButton refreshButton = (ImageButton) layoutView.findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(listener);
		return layoutView;
	}

	/*
	 * Updates the gui of this fragment mainly from the query
	 */
	public void updateGui() {
		_emptyList.setText(_myActivity.getResources().getString(R.string.loading));
		_currentUser = ParseUser.getCurrentUser();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Ride");
		ParseQuery.clearAllCachedResults();
		System.out.println(_currentUser.getList(UserFields.ACTIVEGROUPS).toString());
		query.whereContainedIn(Ride.GROUPS,_currentUser.getList(UserFields.ACTIVEGROUPS));
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				_emptyList.setText(_myActivity.getResources().getString(R.string.no_rides));
				if (e == null) {
					ParseObject ride;
					_cars = new ArrayList<ParseObject>();
					_riders = new ArrayList<ParseObject>();
					if(objects!=null && !objects.isEmpty()){
						for (int i = 0; i < objects.size(); i++) {
							ride = objects.get(i);
							Calendar rideTime = new GregorianCalendar ();
							rideTime.setTime(ride.getDate(Ride.DATE));
							Calendar now = Calendar.getInstance();
							if(rideTime.after(now))
							{
								if(ride.getBoolean(Ride.RIDEKIND)==Ride.CAR)
								{
									_cars.add(ride);
								}
								else
								{
									_riders.add(ride);
								}
							}
							Collections.sort(_cars,new Comparator<ParseObject>(){
								public int compare(ParseObject lhs,
										ParseObject rhs) {
									return lhs.getDate(Ride.DATE).compareTo(rhs.getDate(Ride.DATE));
								}});
							Collections.sort(_riders,new Comparator<ParseObject>(){
								public int compare(ParseObject lhs,
										ParseObject rhs) {
									return lhs.getDate(Ride.DATE).compareTo(rhs.getDate(Ride.DATE));
								}});
						}
					}
					if(_myActivity!=null && !_myActivity.activityCanceled)
					{
						updateButtons();
					}
				} else {
					System.out.println("object fail");
				}
			}
		});

	}

	//Listen to this fragment buttons
	private class buttonsListener implements View.OnClickListener 
	{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.carListButton:
				listKind=ListKind.CARS;
				updateButtons();
				break;
			case R.id.manListButton:
				listKind=ListKind.MANS;
				updateButtons();
				break;
			case R.id.refreshButton:
				updateGui();
				break;
			}
		}
	}

	/*
	 * Update the car and rider buttons and also
	 * the List itself. with animation
	 */
	public void updateButtons() {
		ImageButton carButton = (ImageButton) _fragView.findViewById(R.id.carListButton);
		ImageButton manButton = (ImageButton) _fragView.findViewById(R.id.manListButton);
		final ListView list = (ListView) _fragView.findViewById(android.R.id.list);
		if(listKind==ListKind.CARS)
		{
			carButton.setBackgroundResource(R.drawable.car_reg);
			manButton.setBackgroundResource(R.drawable.rider_dark);

		}
		else
		{
			carButton.setBackgroundResource(R.drawable.car_dark);
			manButton.setBackgroundResource(R.drawable.rider_reg);
		}
		Animation fadeOut = AnimationUtils.loadAnimation(_myActivity, R.anim.fade_out_list);
		final Animation fadeIn = AnimationUtils.loadAnimation(_myActivity, R.anim.fade_in_list);
		fadeOut.setAnimationListener(new Animation.AnimationListener() {

			public void onAnimationEnd(Animation animation) {
				setList();
				list.startAnimation(fadeIn);
			}
			public void onAnimationRepeat(Animation animation) {	}
			public void onAnimationStart(Animation animation) {		}
		});
		if(_firstLoad)
		{
			setList();
			_firstLoad=false;
		}
		else
		{
			list.startAnimation(fadeOut);
		}
	}

	/*
	 * Sets the list for the listview
	 */
	protected void setList() {
		ListView lv = (ListView) _fragView.findViewById(android.R.id.list);
		if(listKind==ListKind.CARS)
		{
			setListAdapter(new RideListAdapter(getActivity(),
					R.layout.live_list_line, _cars));
		}
		else
		{
			setListAdapter(new RideListAdapter(getActivity(),
					R.layout.live_list_line, _riders));
		}
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(_myActivity,RideActivity.class);
				Bundle b = new Bundle();
				if(listKind==ListKind.CARS)
				{
					b.putString("rideId", _cars.get(arg2).getObjectId());
				}
				else
				{
					b.putString("rideId", _riders.get(arg2).getObjectId());
				}

				intent.putExtras(b);
				startActivity(intent);
				_myActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}});
	}
}

