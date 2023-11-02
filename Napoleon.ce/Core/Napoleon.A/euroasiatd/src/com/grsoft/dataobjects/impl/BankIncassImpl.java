package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.BankIncass;
import com.grsoft.napoleon.BankIncassEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;

import android.content.Context;

public class BankIncassImpl extends CreatableDocument<BankIncass> implements PhotoDocument {

	@Override
	public void open(Context context) {
		BankIncassEdit.open(context, this);
	}

	@Override
	public void addPhoto(byte[] photo) {
		data.photo = photo;
		write();
		close();
	}

	@Override public int count() { return data.photo.length > 0 ? 1 : 0; }

}
