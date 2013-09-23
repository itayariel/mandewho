package com.itay.mandewho;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class AddFragGroupsAdapter extends ArrayAdapter<Group>{
	private ArrayList<Group> groupsList;
	private Context context;
	
	public AddFragGroupsAdapter(Context context,
			int textViewResourceId, ArrayList<Group> groupsList) {
		super(context, textViewResourceId, groupsList);
		this.groupsList = new ArrayList<Group>();
		this.groupsList.addAll(groupsList);
		this.context=context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View groupView = inflater.inflate(R.layout.group_view, parent,false);
		SingleGroupHolder singleGroupHolder = new SingleGroupHolder();
		singleGroupHolder.nameHolder = (TextView) groupView.findViewById(R.id.groupName);
		singleGroupHolder.checkboxHolder = (CheckBox) groupView.findViewById(R.id.groupCheckBox);
		final Group group = groupsList.get(position);
		singleGroupHolder.nameHolder.setText(group.getName());
		singleGroupHolder.checkboxHolder.setChecked(group.isChecked());
		singleGroupHolder.checkboxHolder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				group.setChecked(isChecked);
				System.out.println(group.getName()+" is now "+ group.isChecked());
			}
		});
		return groupView;	
	}
	
	private class SingleGroupHolder
	{
		TextView nameHolder;
		CheckBox checkboxHolder;
	}

}
