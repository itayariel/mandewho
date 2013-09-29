package com.itay.mandewho;

import java.util.ArrayList;
import java.util.List;

import com.parse.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsGroupsViewAdapter extends ArrayAdapter<Group> {

	private ArrayList<Group> _groups;
	private ParseUser _currentUser;
	private Context context;
	public SettingsGroupsViewAdapter(Context context, int textViewResourceId,
			ArrayList<Group> groups, ParseUser currentUser) {
		super(context, textViewResourceId,groups);
		_currentUser=currentUser;
		_groups=groups;
		this.context=context;
	}

	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View lineView;
		if(position==0)
		{
			lineView = inflater.inflate(R.layout.settings_first_line, parent,false);
			TextView userNameView = (TextView) lineView.findViewById(R.id.userName);
			userNameView.setText(_currentUser.getString(UserFields.FULLNAME));
			String url = _currentUser.getString(UserFields.PICTURE);
			Button logoutButton  = (Button) lineView.findViewById(R.id.logout_button);
			logoutButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					((MainActivity)context).logOut();
				}
			});
			if(url!=null)
			{
				ImageView imageView = (ImageView) lineView.findViewById(R.id.userPicture);
				new DownloadImage(imageView).execute(url);
			}
			return lineView;
		}
	
		final Group group = _groups.get(position);
		lineView = inflater.inflate(R.layout.group_view_in_groups_list, parent,false);
		SingleGroupHolder singleGroupHolder = new SingleGroupHolder();
		singleGroupHolder.nameHolder = (TextView) lineView.findViewById(R.id.settingsGroupName);
		singleGroupHolder.nameHolder.setText(group.getName());
		singleGroupHolder.checkboxHolder = (CheckBox) lineView.findViewById(R.id.settingsGroupCheckBox);
		singleGroupHolder.checkboxHolder.setChecked(group.isChecked());
		singleGroupHolder.checkboxHolder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				List<String> activeGroups = _currentUser.getList(UserFields.ACTIVEGROUPS);
				group.setChecked(isChecked);
				if(isChecked)
				{
					activeGroups.add(group.getId());
				}
				else
				{
					activeGroups.remove(group.getId());
				}
				if(((MainActivity)context)!=null && ((MainActivity)context).myAddFragment!=null)
				{
					((MainActivity)context).myAddFragment.updateGui();
				}
				_currentUser.put(UserFields.ACTIVEGROUPS, activeGroups);
				_currentUser.saveInBackground();
			}
		});
		return lineView;	
	}

	private class SingleGroupHolder
	{
		TextView nameHolder;
		CheckBox checkboxHolder;
	}

}
