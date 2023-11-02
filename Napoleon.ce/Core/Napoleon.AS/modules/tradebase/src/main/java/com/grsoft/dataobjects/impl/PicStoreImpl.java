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

	public boolean isEmpty() {
		return data.picture == null || data.picture.length == 0;
	}

	public static void delete(String id) {
		PicStoreImpl pi = new PicStoreImpl();
		pi.getData().id = id;
		if(pi.read()) {
			pi.delete();
			pi.close();
			new File(new String(pi.getData().picture)).delete();
		}
	}
}
