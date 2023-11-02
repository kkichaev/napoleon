package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.NewClient;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.NewClientEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;

import android.content.Context;

public class NewClientImpl extends CreatableDocument<NewClient> {

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
	public String getDescription(Context context) {
		String ret = data.name + " " + super.getDescription(context);;
		return ret;
	}
	
	public boolean isCompleete(VisitImpl refDoc) {
		return isValid(refDoc);
	}

	public boolean isValid(VisitImpl refDoc) {
		if(data.inn.length() == 0 || data.name.length() == 0 || data.address.length() == 0 || data.phone.length() == 0)
			return false;
		if(data.isBlack == 0 && refDoc.getData().items.size() == 0)
			return false;
//		if(data.delay == 0)
//			return false;
		return true;
	}

	@Override
	public boolean delete() {
		boolean ret = super.delete();
		if(ret) {
			VisitImplEx vi = new VisitImplEx();
			vi.getData().created = data.visitDoc;
			if(vi.read()) {
				vi.delete();
			}
			vi.close();
		}
		return ret;
	}
}
