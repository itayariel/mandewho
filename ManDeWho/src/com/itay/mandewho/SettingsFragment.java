package com.itay.mandewho;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

public class SettingsFragment extends ListFragment {
	int fragVal;
	ParseUser _currentUser;
	View _fragView;
	MainActivity _myActivity;
	ArrayList<Group> _groups;
	SettingsGroupsViewAdapter _groupsAdapter;

	static SettingsFragment init(int val) {
		SettingsFragment myFrag = new SettingsFragment();
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
		_myActivity.setSettingFragment(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layoutView = inflater.inflate(R.layout.fragment_settings, container,
				false);
		_fragView = layoutView;
		if(_currentUser!=null)
		{
			updateGui();
		}

		return layoutView;
	}
	
	/*
	 * Updates the gui of this fragment
	 */
	public void updateGui()
	{
		createGroupsArray();
		setListAdapter(new SettingsGroupsViewAdapter(getActivity(), R.layout.group_view, _groups,_currentUser));
	}



	//Generates a list of groups for the list view 
	private void createGroupsArray() {
		_groups  = new ArrayList<Group>();
		_groups.add(new Group("headline"));
		List<String> activeGroups = _currentUser.getList(UserFields.ACTIVEGROUPS);
		JSONArray groupsList = _currentUser.getJSONArray(UserFields.GROUPS);
		if(groupsList!=null && groupsList.length()!=0)
		{
			try {
				System.out.println("building settings");
				for (int i = 0; i < groupsList.length(); i++) {
					Group group = new Group(groupsList.getJSONObject(i));
					if(activeGroups.contains(group.getId()))
					{
						group.setChecked(true);
						System.out.println("group number "+i);
						System.out.println(group.getName());
					}
					else
					{
						group.setChecked(false);
					}
					_groups.add(group);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

