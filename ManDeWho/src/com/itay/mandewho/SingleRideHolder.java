package com.itay.mandewho;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.parse.ParseObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleRideHolder
{
	TextView from;
	TextView to;
	TextView time;
	TextView date;
	TextView group;
	TextView publisherName;
	ImageView publisherImage;
	
	public SingleRideHolder(Activity activity,View rideView,ParseObject ride) {
		from  = (TextView) rideView.findViewById(R.id.from_view);
		to = (TextView) rideView.findViewById(R.id.to_view);
		time = (TextView) rideView.findViewById(R.id.time_of_ride);
		date = (TextView) rideView.findViewById(R.id.date_of_ride);
		group = (TextView) rideView.findViewById(R.id.groupNameRideView);
		publisherName = (TextView) rideView.findViewById(R.id.publisherNameRideView);
		publisherImage = (ImageView) rideView.findViewById(R.id.publisherImageRideView);
		Typeface type=Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Light.ttf");
		time.setTypeface(type);
		date.setTypeface(type);
		putData(ride);
	}

	public SingleRideHolder(RideActivity rideActivity, ParseObject ride) {
		from  = (TextView) rideActivity.findViewById(R.id.from_view);
		to = (TextView) rideActivity.findViewById(R.id.to_view);
		time = (TextView) rideActivity.findViewById(R.id.time_of_ride);
		date = (TextView) rideActivity.findViewById(R.id.date_of_ride);
		group = (TextView) rideActivity.findViewById(R.id.groupNameRideView);
		publisherName = (TextView) rideActivity.findViewById(R.id.publisherNameRideView);
		publisherImage = (ImageView) rideActivity.findViewById(R.id.publisherImageRideView);
		Typeface type=Typeface.createFromAsset(rideActivity.getAssets(), "fonts/Roboto-Light.ttf");
		time.setTypeface(type);
		date.setTypeface(type);
		putData(ride);
	}
	
	private void putData(ParseObject ride)
	{
		
		String url = ride.getString(Ride.PUBLISHERIMG);
		if(url!=null)
		{
			new DownloadImage(publisherImage).execute(url);
		}
		if(ride.getString(Ride.FROM)!=null)
		{
			from.setText(ride.getString(Ride.FROM));
		}
		if(ride.getString(Ride.TO)!=null)
		{
			to.setText(ride.getString(Ride.TO));
		}
		
		
		Calendar calendarRideDate = new GregorianCalendar ();
		calendarRideDate.setTime(ride.getDate(Ride.DATE));

		String chosenTimeString = new SimpleDateFormat("HH:mm").format(calendarRideDate.getTime());
		time.setText(chosenTimeString);
		String currentDateString = new SimpleDateFormat("dd-MM-yyyy").format(calendarRideDate.getTime());
		date.setText(currentDateString);
		publisherName.setText(ride.getString(Ride.PUBLISHERNAME));	
	}
}