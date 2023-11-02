package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;

public class VisitImplEx extends VisitImpl {
	static String photoTag = "";

	public static void setPhotoTag(String newTag) { photoTag = newTag; }
	
	@Override
	public void addPhoto(byte[] photo) {
		addPhoto(photo, photoTag);
		photoTag= "";
	}
	
	public void addPhoto(byte[] photo, String tag) {
		VisitItemEx ve = new VisitItemEx();
		ve.id = photo;
		ve.tag = tag;
		ve.date = new Date();
		data.items.add(ve);
		write();
		close();
	}
	
	public VisitItemEx findPhoto(String tag) {
		for(VisitItem vi : data.items) {
			VisitItemEx ve = (VisitItemEx)vi;
			if(ve.tag.equals(tag))
				return ve;
		}
		
		return null;
	}
}
