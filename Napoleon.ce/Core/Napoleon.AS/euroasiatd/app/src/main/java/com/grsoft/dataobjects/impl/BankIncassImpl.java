package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.BankIncass;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.BankIncassEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class BankIncassImpl extends CreatableDocument<BankIncass> implements PhotoDocument {
	public VisitImpl refVisit = new VisitImpl();

	@Override
	public void open(Context context) {
		BankIncassEdit.open(context, this);
	}

	@Override
	public void addPhoto(byte[] photo) {
		if (refVisit.getData().items.size() > 0)
			refVisit.getData().items.clear();

		refVisit.addPhoto(photo);
	}

	@Override public int count() { return refVisit.getData().items.size(); }

	public String getPhoto() {
		if (refVisit.getData().items.size() > 0)
			return new String(refVisit.getData().items.get(0).id);

		return "";
	}

	public void delPhoto() {
		refVisit.getData().items.clear();
		write();
		close();
	}
}
