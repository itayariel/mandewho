package com.itay.mandewho;

import org.json.JSONException;
import org.json.JSONObject;

public class Group {
	private String name = null;
	private String id = null;
	private boolean isChecked;
	
	public Group(String headline)
	{
		name=headline;
		id="-1";
		isChecked=false;
	}
	public Group(JSONObject thegroup)
	{
		try {
			name = thegroup.getString("name");
			id =  thegroup.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		isChecked=false;
	}
	
	public JSONObject jsonMeForMetaData(String source)
	{
		JSONObject groupInJson = new JSONObject();
		try {
			groupInJson.put("id", id);
			groupInJson.put("name", name);
			groupInJson.put("origin", source);
			if(isChecked)
			{
				groupInJson.put("status", 1);
			}
			else
			{
				groupInJson.put("status", 0);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return groupInJson;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

		
}
