package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.napoleon.ReturnProperties;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Util;

public class ReturnImplEx extends ReturnImpl {

	@Override public CreatableDocument<Return> createInstance() { return new ReturnImplEx(); }

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		ReturnProperties.open(ctx, this, isOldOrder);
	}

	public void init(OrderImplBase<? extends Order> doc) {
		data.created = Util.getDateTime();
		data.date = Util.getDate();
		data.id = doc.getId();
		
		((ReturnEx)data).shedule = doc.getData().created;
		
		write();
	}
	
	@Override protected boolean checkPriceQty() { return false; }
	
	static public ReturnImpl getAssociated(OrderImplBase<? extends Order> doc) {
		return getAssociated(doc, true);
	}
	
	static public ReturnImpl getAssociated(OrderImplBase<? extends Order> doc, boolean create) {
		ReturnImplEx retDoc = null;
		
		if( doc != null ) {
			String table = DataObjectInfo.getInstance().getTableName(Return.class);
			Date crtd = doc.getData().created;
			if( crtd != null ) {
				String str = String.format("id='%s' and shedule=%s", 
					doc.getId(), ((Long)crtd.getTime()).toString());
				List<Long> ret = DbReader.readIds(table, str, "created");
				
				ReturnImplEx ri = new ReturnImplEx();
				for(Long rid : ret) {
					ri.read(rid);
					ri.close();
					return ri;
				}
				
				if( create ) {
					ri.init(doc);
					retDoc = ri;
				}
			}			
		}
		return retDoc;
	}

}
