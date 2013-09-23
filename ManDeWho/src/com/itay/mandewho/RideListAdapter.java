package com.itay.mandewho;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.parse.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RideListAdapter extends ArrayAdapter<ParseObject> {

	private ArrayList<ParseObject> ridesList;
	private Context context;
	public RideListAdapter(Context context, int textViewResourceId,
			List<ParseObject> objects) {
		super(context, textViewResourceId, objects);
		this.ridesList = new ArrayList<ParseObject>();
		if(objects!=null && !objects.isEmpty()){
			this.ridesList.addAll(objects);
		}
		this.context=context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rideView = inflater.inflate(R.layout.live_list_line, parent,false);
		final ParseObject ride = ridesList.get(position);
		SingleRideHolder singleRideHolder = new SingleRideHolder((MainActivity)context,rideView,ride);

		return rideView;	
	}

}
