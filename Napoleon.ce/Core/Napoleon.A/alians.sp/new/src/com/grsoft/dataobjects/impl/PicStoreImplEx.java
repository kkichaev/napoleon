package com.grsoft.dataobjects.impl;

import java.io.File;

import com.grsoft.dataobjects.PicStoreEx;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class PicStoreImplEx extends CreatableDocument<PicStoreEx> {

	public long size() {
		File file = new File(new String(data.picture));
		return file.length();
	}

	@Override
	public void open(Context context) {
	}
	
}
