/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект данных Visit для работы с базой
 *
 * kki   26/10/2010   creating
 */
package com.grsoft.dataobjects.impl;

import java.io.File;
import java.util.Date;

import android.content.Context;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.VisitEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.util.Util;

public class VisitImpl extends CreatableDocument<Visit>
	implements PhotoDocument
{
	@SuppressWarnings("deprecation")
	@Override
	public boolean isExported() { 
		if ((data.flags & ParamState.ofExported) == ParamState.ofExported)
			return true;
		else return super.isExported();
	}
	
	@Override
	public void open(Context context) { 
		VisitEdit.open(context, this); 
	}
	
	@Override
	public boolean isEmpty() {
		return Features.DEL_VISIT_WITHOUT_PHOTO ? data.items.size() == 0 : data.remark.length() == 0 && (data.items.size() == 0);
	}

	@Override
	public boolean delete() {
		deleteSrcItems();
		return super.delete();
	}
	
	public void deleteSrcItems(){
		Visit visit = getData();
		
		if (visit.items == null)
			return;
		for(VisitItem vi : visit.items){
			vi.deletePhoto();
		}
		
		visit.items.clear();
		visit.sendedPhotos = 0;
	}

//	@Override
//	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
//		data.date = Util.getDateTime();
//		data.created = Util.getDateTime();
//		
//		data.id = orgId;
//		data.latitude = gpsCoord.latitude;
//		data.longitude = gpsCoord.longitude;
//		data.params = 0;
//		
//		return (write() != ExtrasConst.INVALID_ID);
//	}
	
	@Override
	public void postInit() {
		data.date = Util.getDateTime();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addPhoto(byte[] photo) {
		try{
			Class<? extends VisitItem> itemType = (Class<? extends VisitItem>) DataObjectInfo.getInstance().getListType(Visit.class, "items");
			VisitItem visitItem = itemType.newInstance();
			visitItem.setImageFileName(photo);
			visitItem.date = new Date();
			Visit v = getData();
			v.items.add(visitItem);
			v.sendedPhotos = 0;
			write();
			close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public long size() {
		long result = super.size();
		if(!Features.UNLIMIT_VISIT_ITEMS) {
			Visit visit = getData();
			
			if (visit != null && visit.items != null && visit.items.size() > 0)
				for(VisitItem vi : visit.items){
					File file = new File(vi.getImageFileName());
					result += file.length();
				}
			
		}
		return result;
	}

	@Override
	public int count() { return data.items.size();	}
}
