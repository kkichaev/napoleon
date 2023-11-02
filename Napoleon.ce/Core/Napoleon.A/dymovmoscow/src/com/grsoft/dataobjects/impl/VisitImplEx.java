package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;

public class VisitImplEx extends VisitImpl {

	static String photoTag = "";
	static String orgId = "";
	
	public List<VisitItem> getItems(String id) {
		List<VisitItem> ret = new ArrayList<VisitItem>();
		for(VisitItem vi : data.items) {
			VisitItemEx ve = (VisitItemEx)vi;
			if(ve.itemId != null && ve.itemId.equals(id)) {
				ret.add(vi);
			}
		}
		return ret;
	}
	
	public boolean openAssociatedVisit(Return src) {
		long created = src.created.getTime() + 1000;
		Date dt = new Date(created);
		data.created = dt;
		
		return read();
	}

	public void addPhoto(byte[] bytes, String id, String oid) {
		try{
			VisitItemEx visitItem = new VisitItemEx();
			visitItem.id = bytes;
			visitItem.date = new Date();
			visitItem.itemId = id;
			visitItem.orgId = oid;
			data.items.add(visitItem);
			data.sendedPhotos = 0;
			write();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void addPhoto(byte[] photo) {
		addPhoto(photo, photoTag, orgId);
		photoTag = "";
		orgId = "";
	}

	public static void setPhotoTag(String itemId, String orgId) { 
		photoTag = itemId;
		VisitImplEx.orgId = orgId;
	}

}
