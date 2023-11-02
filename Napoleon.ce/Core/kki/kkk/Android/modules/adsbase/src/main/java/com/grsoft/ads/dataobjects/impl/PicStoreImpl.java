package com.grsoft.ads.dataobjects.impl;

import java.io.File;

import com.grsoft.ads.dataobjects.PicStore;
import com.grsoft.dataobjects.impl.DbObject;

public class PicStoreImpl extends DbObject<PicStore> {

	public long size() {
		File file = new File(new String(data.picture));
		return file.length();
	}
	
	@Override
	public boolean delete() {
		File file = new File(new String(data.picture));
		file.delete();
		return super.delete();
	}
}
