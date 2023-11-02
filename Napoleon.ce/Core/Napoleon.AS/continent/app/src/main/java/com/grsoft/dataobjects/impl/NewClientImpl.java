package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.NewClient;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.NewClientEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.util.Util;

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

	String statusText() {
		switch(data.podStatus) {
			case 1: return "<b>на проверке</b> ";
			case 2: return "<b>на доработке</b> ";
			case 3: return "<b>на согласовании юристом</b> ";
			case 4: return "<b>согласовано</b> ";
			case 5: return "<b>отказать</b> ";
			default: return "";
		}
	}

	@Override
	public boolean isEditable() {
		return data.podStatus != 4 && data.podStatus != 5;
	}

	@Override
	public String getDescription(Context context) {
		String ret = data.name + " " + statusText() + super.getDescription(context);;
		return ret;
	}
	
	public boolean isCompleete(VisitImpl refDoc) {
		return isValid(refDoc);
	}

	public boolean isValid(VisitImpl refDoc) {
		if(data.isFact > 0) {
			boolean good = (data.inn.length() > 0 && data.address.length() > 0 &&
					data.phone.length() > 0 && data.firma.length() > 0);
			return good;
		}
		if(data.inn.length() == 0
				|| data.name.length() == 0
				|| data.address.length() == 0
				|| data.phone.length() == 0
				|| data.bik.length() == 0
				|| data.account.length() == 0
				|| data.email.length() == 0
//				|| data.remark.length() == 0
				|| data.firma.length() == 0
				|| data.amount.length() == 0
				|| data.depth.length() == 0
				|| data.costype.length() == 0)
			return false;

		if(refDoc.getData().items.size() == 0)
			return false;

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

	@Override
	public CreatableDocument<NewClient> copy() {
		NewClientImpl copy = (NewClientImpl) NewClientDoc.instance().create();

		copy.data = (NewClient) this.data.clone();
		copy.data.visitDoc = new Date(1000);

		NewClient nc = copy.getData();
		nc.created = Util.getDateTime();
		nc.date = copy.getData().created;
		nc.params = 0;
		nc.podStatus = 0;
		nc.podRemark = "";

		copy.write();
		copy.close();

		return copy;
	}
}
