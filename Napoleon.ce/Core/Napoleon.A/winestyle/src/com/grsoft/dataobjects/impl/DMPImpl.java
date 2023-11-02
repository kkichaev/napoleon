package com.grsoft.dataobjects.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DMP;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.napoleon.DMPEdit;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

public class DMPImpl extends CreatableDocument<DMP> {

	VisitImpl refVisit = null;
	
	public VisitImpl getRefVisit() {
		if(refVisit == null) {
			refVisit = new VisitImpl();
			Visit v = refVisit.getData();
			v.created = data.created;
			v.date = data.created;
			if( !refVisit.read() ) {
				v.date = data.created;
				v.created = data.created;				
				v.id = data.id;
				v.latitude = data.latitude;
				v.longitude = data.longitude;
				v.params = 0;				
				v.timeZone = data.timeZone;
				refVisit.write();
			}
		}else
			refVisit.read();
		
		return refVisit;
	}
	
	@Override
	public void open(Context context) {
		DMPEdit.open(context, getRowid());
	}
	
	@Override
	public void close() {
		if(refVisit != null)
			refVisit.close();
		super.close();
	}
	
	@Override
	public boolean delete() {
		boolean ret = super.delete();
		if(ret)
			getRefVisit().delete();
		return ret;
	}
	
	@Override
	public boolean init(Context context, final String orgId, GpsCoord gpsCoord) {
		DbWriter.checkDBTable(OrgMatrix.class);
		SQLiteDatabase db = DataBaseManager.getDataBase();
		SQLiteStatement stm = db.compileStatement("select count(id) from " + 
				DataObjectInfo.getInstance().getTableName(OrgMatrix.class) + " where id = ?");
		stm.bindString(1, orgId);
		
		boolean result = stm.simpleQueryForLong() > 0;
			
		if (result)
			result = super.init(context, orgId, gpsCoord); 
		else
			Toast.makeText(context, R.string.matrix_not_found, Toast.LENGTH_SHORT).show();
		
		return result;
	}

	public void addPhoto(String id, String dmpID, byte[] bytes) {
		VisitItemEx di = new VisitItemEx();
		di.itemId = id;
		di.dmpId = dmpID;
		di.id = bytes;
		di.key = UUID.randomUUID().toString().replace("-", "");
		
		VisitImpl v = getRefVisit();
		v.getData().items.add(di);
		v.write();
		v.close();
		
		write();
		close();
	}

	public boolean emptyItems() {
		// TODO Auto-generated method stub
		return true;
	}

	public void removeDMP(String priceID, String dmpID) {
		List<VisitItem> newItems = new ArrayList<VisitItem>();
		VisitImpl v = getRefVisit();
		
		for(VisitItem i : v.getData().items) { 
			VisitItemEx e = (VisitItemEx)i; 
			
			if(!(e.dmpId.equals(dmpID) && e.itemId.endsWith(priceID)))
				newItems.add(i);
			else {
				File file = new File(new String(i.id));
				file.delete();
			}
		}
		
		v.getData().items = newItems;
		v.write();
		v.close();
	}
}
