package com.grsoft.dataobjects.impl;

import java.util.HashSet;
import java.util.Set;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Distrib;
import com.grsoft.dataobjects.DistribRemark;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.napoleon.DistribEdit;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

public class DistribImpl extends CreatableDocument<Distrib> {

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
		}
		return refVisit;
	}
	
	@Override
	public void open(Context context) {
		DistribEdit.open(context, getRowid());
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
	
	public void setRemark(String id, String remark) {
		DistribRemark i = getRemarkItem(id);
		i.remark = remark;
		write();
		close();
	}

	public VisitItemEx findPhotoItem(String id) {
		VisitItemEx result = null;
		
		VisitImpl vi = getRefVisit();
		for(VisitItem i : vi.getData().items){
			if (((VisitItemEx)i).itemId.equals(id)) {
				result = (VisitItemEx)i;
				break;
			}
		}
		
		return result;
	}
	
	public DistribRemark findRemarkItem(String id) {
		DistribRemark result = null;
		
		for(DistribRemark i : data.items){
			if (i.id.equals(id)) {
				result = i;
				break;
			}
		}
		
		return result;
	}
	
	public VisitItemEx getPhotoItem(String id) {
		VisitItemEx result = findPhotoItem(id);
		
		if (result == null) {
			result = new VisitItemEx();
			result.itemId = id;
			getRefVisit().getData().items.add(result);
		}
		
		return result;
	}
	
	public DistribRemark getRemarkItem(String id) {
		DistribRemark result = findRemarkItem(id);
		
		if (result == null) {
			result = new DistribRemark();
			result.id = id;
			data.items.add(result);
		}
		
		return result;
	}

	public void addPhoto(String id, byte[] bytes) {
		VisitItemEx di = getPhotoItem(id);
		di.id = bytes;
		getRefVisit().write();
		
		DistribRemark r = findRemarkItem(id);
		if(r != null)
			data.items.remove(r);
		
		write();
		close();
	}

	public void deletePhotoItem(String id) {
		VisitItemEx di = findPhotoItem(id);
		
		if (di != null) {
			getRefVisit().getData().items.remove(di);
			write();
			close();
		}
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		final Set<String> ids = new HashSet<String>();
		
		Distrib prev = new Distrib();
		DbReader r = new DbReader();
		if (r.select(prev, prev.getTableName(), String.format("created = (select max(created) from distrib where id='%s')", getId()))) {
			for(DistribRemark dr : prev.items) {
				if(dr.remark.trim().length() > 0) {
					data.items.add(dr);
					ids.add(dr.id);
				}
			}
		}
		
		DataTraveler.travel(OrgMatrix.class, new DataTraveler.Travel<OrgMatrix>(true) {

			@Override
			public boolean travel(DataTraveler<OrgMatrix> item) {
				if(!ids.contains(item.data.id_i)) {
					DistribRemark dr = new DistribRemark();
					dr.id = item.data.id_i;
					data.items.add(dr);
				}
				return true;
			}
		}, "id='" + data.id + "'");
			
		r.close();
	}

	public boolean emptyItems() {
		return !hasRemarkItem();
	}

	protected boolean hasRemarkItem() {
		boolean val = false;
		
		for(DistribRemark dr : data.items) {
			val = dr.remark.trim().length() > 0;
			
			if(val)
				break;
		}
		
		return val;
	}
}
