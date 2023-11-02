package com.grsoft.dataobjects.impl;

import java.io.File;

import com.grsoft.dataobjects.PicStore;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class PicStoreImpl extends CreatableDocument<PicStore> {

	public long size() {
		File file = new File(new String(data.picture));
		return file.length();
	}

	@Override
	public void open(Context context) {
	}
	
}
