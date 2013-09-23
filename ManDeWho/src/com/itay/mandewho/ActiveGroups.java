package com.itay.mandewho;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseUser;

public class ActiveGroups {

	ArrayList<String> _activeGroups;
	ParseUser _currentUser;
	String _groupsName;

	String _metaDataName;


	

	/*
	 * Creates the list of groups to use
	 * If a group stops to exist it removes it
	 */
	public ActiveGroups(ArrayList<String> list,JSONArray groupsArray) {
		try {
			_activeGroups=list;
			if(groupsArray==null)
			{
				return ;
			}
			if(_activeGroups==null)
			{
				_activeGroups = new ArrayList<String>();
			}
			if(_activeGroups.size()==0)
			{
				CreateNewActiveGroups(groupsArray);
			}
			else
			{
				for (int i = 0; i < _activeGroups.size(); i++) {
					String id = _activeGroups.get(i);
					boolean hasGroup=false;
					for (int j = 0; j < groupsArray.length(); j++) {
						JSONObject tempJsonGroup = groupsArray.getJSONObject(j);
						String tempId= tempJsonGroup.getString("id");
						if(tempId.equals(id))
						{
							hasGroup=true;
							break;
						}
					}
					if(!hasGroup)
					{
						_activeGroups.remove(i);
					}
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Creates the list of groups to use for the first time
	 * It takes all groups!!
	 */
	public void CreateNewActiveGroups(JSONArray groupsArray)
	{
		try {
			_activeGroups = new ArrayList<String>();
			if(groupsArray==null || groupsArray.length()==0)
			{
				return ;
			}
			for (int i = 0; i < groupsArray.length(); i++) {
				JSONObject newJsonGroup = groupsArray.getJSONObject(i);
				if(!newJsonGroup.isNull("id"))
				{
					_activeGroups.add(newJsonGroup.getString("id"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> getActiveGroupsList()
	{
		return _activeGroups;
	}
}
