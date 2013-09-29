package com.itay.mandewho;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.MatrixCursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class AddFragment extends Fragment{
	int fragVal;
	ParseUser _currentUser;
	View _fragView;
	MainActivity _myActivity;
	static Calendar _date;
	static ParseObject _rideObject;
	static ArrayList<Group> _groupsList;





	static AddFragment init(int val) {
		AddFragment myFrag = new AddFragment();
		// Supply val input as an argument.
		Bundle args = new Bundle();
		args.putInt("val", val);
		myFrag.setArguments(args);
		return myFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_myActivity = (MainActivity)getActivity();
		_currentUser  = _myActivity.currentUser;
		fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
		_myActivity.setAddFragment(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layoutView = inflater.inflate(R.layout.fragment_add, container,
				false);
		_fragView = layoutView;

		updateGui();
		return layoutView;
	}

	/*
	 * Updates the screen, refresh all data in form
	 */
	public void updateGui() {
		buttonsListener listener = new buttonsListener();
		final View publishButton = _fragView.findViewById(R.id.publishButton);
		publishButton.setOnClickListener(listener);
		final Button timeButton = (Button) _fragView.findViewById(R.id.timeButton);
		timeButton.setOnClickListener(listener);
		Typeface type=Typeface.createFromAsset(_myActivity.getAssets(), "fonts/Roboto-Light.ttf");
		timeButton.setTypeface(type);
		final Button dateButton = (Button) _fragView.findViewById(R.id.dateButton);
		dateButton.setOnClickListener(listener);
		final Button groupsButton = (Button) _fragView.findViewById(R.id.groupsButton);
		groupsButton.setOnClickListener(listener);
		String currentDateString = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		dateButton.setText(currentDateString);
		String currentTimeString = new SimpleDateFormat("HH:mm").format(new Date());
		timeButton.setText(currentTimeString);
		generateGroupsList();
		_rideObject = new ParseObject(Ride.RIDE);
		_date = Calendar.getInstance();
		EditText fromHolder = (EditText) _fragView.findViewById(R.id.fromField);
		fromHolder.setText("");
		EditText toHolder = (EditText) _fragView.findViewById(R.id.toField);
		toHolder.setText("");
		EditText detailsHolder = (EditText) _fragView.findViewById(R.id.rideDetails);
		detailsHolder.setText("");	
		EditText emailHolder = (EditText) _fragView.findViewById(R.id.emailAddress);
		emailHolder.setText(_currentUser.getString(UserFields.EMAIL));	
		String phoneNumber =_currentUser.getString(UserFields.PHONENUMBER);
		EditText phoneHolder = (EditText) _fragView.findViewById(R.id.phoneNumber);
		if(phoneNumber==null)
		{
			phoneHolder.setText("");	
		}
		else
		{
			phoneHolder.setText(phoneNumber);	
		}
		((CheckBox) _fragView.findViewById(R.id.publishMail)).setChecked(true);
		((CheckBox)_fragView.findViewById(R.id.publishPhoneNumber)).setChecked(true);
		((CheckBox)_fragView.findViewById(R.id.publishSMS)).setChecked(true);

	}

	/*
	 * Generates the list of groups that should appear
	 */
	private void generateGroupsList()
	{
		if(_fragView==null || _currentUser==null)
		{
			return ;
		}

		_groupsList = new ArrayList<Group>();
		List<String> activeGroups =  _currentUser.getList(UserFields.ACTIVEGROUPS);
		Group group=null;
		try {
			JSONArray groupsParseList = _currentUser.getJSONArray(UserFields.GROUPS);
			if(groupsParseList==null)
			{
				return ;
			}
			for (int i = 0; i < groupsParseList.length(); ++i) {
				group  = new Group(groupsParseList.getJSONObject(i));
				if(activeGroups.contains(group.getId()))
				{
					group.setChecked(true);
					_groupsList.add(group);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	/*
	 * The user is set by Activity
	 */
	public void setUser(ParseUser user)
	{
		_currentUser=user;
	}


	/*
	 * Listens to the screen buttons
	 */
	private class buttonsListener implements View.OnClickListener 
	{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.publishButton:
				publishRide();
				break;
			case R.id.timeButton:
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
				break;
			case R.id.dateButton:
				DialogFragment newFragment2 = new DatePickerFragment();
				newFragment2.show(getActivity().getSupportFragmentManager(), "datePicker");
				break;
			case R.id.groupsButton:
				DialogFragment newFragment3 = new GroupPickerFragment();
				newFragment3.show(getActivity().getSupportFragmentManager(), "groupsPicker");
				break;
			}

		}
	}

	/*
	 * When publish button is pressed
	 * This should send it to outer space
	 */
	public void publishRide()
	{
		_rideObject.put(Ride.DATE,_date.getTime());
		EditText fromHolder = (EditText) _fragView.findViewById(R.id.fromField);
		EditText toHolder = (EditText) _fragView.findViewById(R.id.toField);
		String from = fromHolder.getText().toString();
		String to = toHolder.getText().toString();
		if(TextUtils.isEmpty(from))
		{
			fromHolder.setError(_myActivity.getResources().getString(R.string.youMustChooseFrom));
			fromHolder.requestFocus();
			return ;
		}
		if(TextUtils.isEmpty(to))
		{
			toHolder.setError(_myActivity.getResources().getString(R.string.youMustChooseTo));
			toHolder.requestFocus();
			return ;
		}
		_rideObject.put(Ride.FROM,from);
		_rideObject.put(Ride.TO,to);
		EditText detailsHolder = (EditText) _fragView.findViewById(R.id.rideDetails);
		_rideObject.put(Ride.COMMENTS,detailsHolder.getText().toString());
		RadioButton Istart= (RadioButton)  _fragView.findViewById(R.id.radioIstart);
		_rideObject.put(Ride.RIDEKIND,Istart.isChecked());

		EditText phoneHolder = (EditText) _fragView.findViewById(R.id.phoneNumber); 
		String phoneNumber = phoneHolder.getText().toString();
		if(((CheckBox) _fragView.findViewById(R.id.publishMail)).isChecked())
		{
			_rideObject.put(Ride.BYMAIL,((EditText) _fragView.findViewById(R.id.emailAddress)).getText().toString());
		}
		if(((CheckBox) _fragView.findViewById(R.id.publishPhoneNumber)).isChecked())
		{

			_rideObject.put(Ride.BYPHONE,phoneNumber);
		}
		if(((CheckBox) _fragView.findViewById(R.id.publishSMS)).isChecked())
		{
			_rideObject.put(Ride.BYSMS,phoneNumber);
		}

		if(phoneNumber!=null && phoneNumber!="")
		{
			_currentUser.put(UserFields.PHONENUMBER, phoneNumber);
			_currentUser.saveInBackground();
		}

		Group g;
		if(_currentUser!=null)
		{
			_rideObject.put(Ride.PUBLISHERNAME, _currentUser.getString(UserFields.FULLNAME));
			_rideObject.put(Ride.PUBLISHERIMG, _currentUser.getString(UserFields.PICTURE));
			_rideObject.put(Ride.PUBLISHERID, _currentUser.getObjectId());

		}
		List<String> groupsIDs=new ArrayList<String>();
		for (int i = 0; i < _groupsList.size(); ++i) {
			g = _groupsList.get(i);
			if(g.isChecked())
				groupsIDs.add(g.getId());
		}

		_rideObject.addAllUnique(Ride.GROUPS,groupsIDs);
		//showStatus(true);
		//TODO show error in many options!!!!
		//
		//

		_rideObject.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				Context context = _myActivity;
				CharSequence text;
				int duration = Toast.LENGTH_SHORT;
				//showStatus(false);
				if (e == null) {
					text = "Ride added successfuly!";
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
					updateGui();
				} else {
					text = "Ride adding failed";
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
			}
		});

	}


	/*
	 * A time picker class
	 */
	public static class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			int hour = _date.get(Calendar.HOUR_OF_DAY);
			int minute = _date.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Button timeButton = (Button) getActivity().findViewById(R.id.timeButton);
			_date.set(Calendar.HOUR, hourOfDay);
			_date.set(Calendar.MINUTE, minute);
			String chosenTimeString = new SimpleDateFormat("HH:mm").format(_date.getTime());
			timeButton.setText(chosenTimeString);
		}
	}

	/*
	 * Date picker Class
	 */
	public static class DatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker

			int year = _date.get(Calendar.YEAR);
			int month = _date.get(Calendar.MONTH);
			int day = _date.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			Button dateButton = (Button) getActivity().findViewById(R.id.dateButton);
			_date.set(year, month, day);
			String currentDateString = new SimpleDateFormat("dd-MM-yyyy").format(_date.getTime());
			dateButton.setText(currentDateString);
		}
	}

	public static class GroupPickerFragment extends DialogFragment{
		String [] namesCursor;
		boolean [] checkedCursor;

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			createCursor();

			// Set the dialog title
			builder.setTitle(R.string.groupsToSend)
			// Specify the list array, the items to be selected by default (null for none),
			// and the listener through which to receive callbacks when items are selected
			.setMultiChoiceItems(namesCursor, checkedCursor,
					new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which,
						boolean isChecked) {
					try {
						_groupsList.get(which).setChecked(isChecked);
						checkedCursor[which]=isChecked;
					} catch (Exception e) {
						System.out.println(e.toString());
					}
				}
			})
			// Set the action buttons
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK, so save the mSelectedItems results somewhere
					// or return them to the component that opened the dialog
					//...
				}
			});

			return builder.create();
		}

		public void createCursor()
		{
			namesCursor = new String[_groupsList.size()];
			checkedCursor = new boolean[_groupsList.size()];

			for (int i = 0; i < _groupsList.size(); ++i) {
				Group group  = _groupsList.get(i);
				namesCursor[i]=group.getName();
				checkedCursor[i]=group.isChecked();
			}
		}
	}
}

