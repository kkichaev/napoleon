package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.NewClient;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.NewClientEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;

import android.content.Context;

public class NewClientImpl extends CreatableDocument<NewClient> implements PhotoDocument {

	@Override
	public void open(Context context) {
		NewClientEdit.open(context, this);
	}

	@Override
	public void postInit() {
		super.postInit();
		data.date = data.created;
	}
	
	@Override
	public void addPhoto(byte[] photo) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends VisitItem> itemType = (Class<? extends VisitItem>) DataObjectInfo.getInstance().getListType(data.getClass(), "items");
			VisitItem visitItem = itemType.newInstance();
			
			visitItem.id = photo;
			visitItem.date = new Date();
			NewClient v = getData();
			v.items.add(visitItem);
			write();
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getDescription(Context context) {
		String ret = data.name + " " + super.getDescription(context);;
		return ret;
	}
	
	public boolean isCompleete() {
		return isValid();
	}

	public boolean isValid() {
		if(data.inn.length() == 0 || data.name.length() == 0 || data.address.length() == 0 || data.phone.length() == 0)
			return false;
		if(data.isBlack == 0 && data.items.size() == 0)
			return false;
		
		if(data.delay == 0)
			return false;
		return true;
	}
	
	@Override
	public int count() { return data.items.size();	}
}
