package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import java.io.File;

import com.grsoft.dataobjects.PicStore;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class PicStoreImpl extends CreatableDocument<PicStore> {

	public long size() {
		File file = new File(data.getImageFileName());
		return file.length();
	}

	@Override
	public void open(Context context) {
	}

	public static void delete(String id) {
		PicStoreImpl pi = new PicStoreImpl();
		pi.getData().id = id;
		if(pi.read()) {
			pi.delete();
			pi.close();
			new File(pi.getData().getImageFileName()).delete();
		}
	}
}
