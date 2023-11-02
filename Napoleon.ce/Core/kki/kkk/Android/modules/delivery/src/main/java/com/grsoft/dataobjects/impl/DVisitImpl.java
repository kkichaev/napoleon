package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.DVisit;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.dostavka.VisitEditDelivery;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import android.content.Context;


public class DVisitImpl extends CreatableDocument<DVisit> 
	implements PhotoDocument{

	@Override public void open(Context context) { VisitEditDelivery.open(context, getRowid());}

	public boolean readOrCreate(Context context, DispatchImpl dispatch) {
		boolean result = false;
		data.created = dispatch.getData().visit;
		
		result = read();
		
		if(!result && dispatch.isInWork()){
			result = init(context, dispatch, null, GPSUtilNew.getLastKnownLocation());
			
			if(result){
				dispatch.getData().visit = data.created;
				dispatch.write();
				dispatch.close();
			}
		}
		
		close();
		
		return result;
	}
	
	public boolean init(Context context, DispatchImpl doc, DispatchItem i, GpsCoord loc){
		data.routeItemId = doc.getData().itemid;
//		data.dispatch = doc.getData().created;
//		
//		if(i != null){
//			data.disprem = i.remark;
//			data.number = i.number;
//		}
		
		return super.init(context, doc.getId(), loc);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		data.date = Util.getDateTime();
	}

	@Override
	public void addPhoto(byte[] photo) {
		try{
			@SuppressWarnings("unchecked")
			Class<? extends VisitItem> itemType = (Class<? extends VisitItem>) DataObjectInfo.getInstance().getListType(Visit.class, "items");
			VisitItem visitItem = itemType.newInstance();
			visitItem.id = photo;
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
	public int count() {
		return data.items.size();
	}

	public void setReadyToSend() {	
		data.params &= ~Dispatch.NOT_READY_TO_SEND;
		setExported(false);
	}
}
