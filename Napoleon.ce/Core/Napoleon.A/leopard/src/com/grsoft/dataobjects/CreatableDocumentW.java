package com.grsoft.dataobjects;

import android.content.Context;

import com.grsoft.napoleon.documents.CreatableDocument;

public class CreatableDocumentW<T extends CreateDocDataObject> extends CreatableDocument<T> {
	@SuppressWarnings("unchecked")
	public void setData(DataObject data){
		this.data = (T) data;
	}

	@Override
	public void open(Context context) {
	}
}
