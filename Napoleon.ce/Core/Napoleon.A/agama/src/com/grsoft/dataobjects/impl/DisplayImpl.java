package com.grsoft.dataobjects.impl;

import java.io.File;

import android.content.Context;

import com.grsoft.dataobjects.Display;
import com.grsoft.dataobjects.DisplayItem;
import com.grsoft.napoleon.DisplayEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class DisplayImpl extends CreatableDocument<Display> implements PhotoDocument {

	@Override
	public void open(Context context) {
		DisplayEdit.open(context, this);
	}

	@Override
	public boolean delete() {
		deleteSrcItems();
		return super.delete();
	}
	
	public void deleteSrcItems(){
		Display visit = getData();
		
		if (visit.items == null)
			return;
		for(DisplayItem vi : visit.items){
			File file = new File(new String(vi.id));
			file.delete();
		}		
		visit.items.clear();
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		data.date = Util.getDateTime();
		data.created = Util.getDateTime();
		
		data.id = orgId;
		data.latitude = gpsCoord.latitude;
		data.longitude = gpsCoord.longitude;
		data.params = 0;
		
		return (write() != ExtrasConst.INVALID_ID);
	}
	
	
	@Override
	public long size() {
		long result = super.size();
		Display visit = getData();
		
		if (visit != null && visit.items != null && visit.items.size() > 0)
			for(DisplayItem vi : visit.items){
				File file = new File(new String(vi.id));
				result += file.length();
			}
		
		return result;
	}

	@Override
	public void addPhoto(byte[] photo) {
		DisplayItem di = new DisplayItem();
		di.id = photo;
		getData().items.add(di);
		write();
		close();
	}

	@Override public int count() { return data.items.size(); }
}
