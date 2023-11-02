package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.NewOrg;
import com.grsoft.napoleon.NewOrgEdit;
import com.grsoft.napoleon.OrgInfo;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.R;
import android.content.Context;

public class NewOrgImpl extends CreatableDocument<NewOrg> implements OrgInfo{

	@Override
	public void open(Context context) {
		NewOrgEdit.open(context, getRowid());
	}

	@Override
	public String getName() {
		return data.name;
	}

	@Override
	public int getTextColor() {
		return R.color.darker_gray;
	}

	@Override
	public String getAddress() {
		return String.format("%s %s %s %s %s %s", data.region, data.city, data.punkt, data.street, data.dom, data.kvartira);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		data.timeIn = 2;
		data.timeOut = 2;
		
		DataTraveler.travel(NewOrg.class, new DataTraveler.Travel<NewOrg>() {

			@Override
			public boolean travel(DataTraveler<NewOrg> item) {
				data.city = item.data.city;
				data.region = item.data.region;
				return false;
			}}, null, "created DESC");
	}
	
}
