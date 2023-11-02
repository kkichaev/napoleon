package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderBundle;
import com.grsoft.dataobjects.OrderBundleItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.OrderBundleEdit;
import com.grsoft.napoleon.OrderDocEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class OrderBundleImpl extends CreatableDocument<OrderBundle> {

	@Override
	public void open(Context context) {
		OrderBundleEdit.open(context, this);
	}

	@Override
	public void postInit() {
		initChildOrder();
	}
	
	public void initChildOrder() {
		OrderImplEx ord = new OrderImplEx();
		OrderEx o = (OrderEx) ord.getData();
		ord.initSilent(data.id, new GpsCoord(data.latitude, data.longitude, data.stltime));
		o.linked = data.created.getTime();
		ord.write();
		ord.close();
		
		data.items.add(new OrderBundleItem(o));
	}
	
	public boolean isEmpty() { return data.items.size() == 0; }
	
	public void refreshDocs() {
		try {
			if(isEditable() && rowid != ExtrasConst.INVALID_ROWID) {
				data.items.clear();
				data.sum = 0;
				String where = "linked=" + Long.toString(data.created.getTime());
				DataTraveler.travel(OrderEx.class, new DataTraveler.Travel<OrderEx>() {

					@Override
					public boolean travel(DataTraveler<OrderEx> item) {
						data.items.add(new OrderBundleItem(item.data));
						data.sum += item.data.sum(); 
						return true;
					}
				}, where); 
				write();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	String makeSendWhere(Set<String> disabledFirms) {
		String where = "linked=" + Long.toString(data.created.getTime()) + " and ((params & " + Integer.toString(ParamState.ofExported) +  ")=0)";
		if(disabledFirms != null && disabledFirms.size() > 0) {
			where += " and (not firmCode in (";
			
			boolean first = true;
			for(String fc : disabledFirms) {
				if(first) first = false;
				else {
					where += ",";
				}
				where += "'" + fc + "'";
			}
			where += "))";
		}
		return where;
	}
	
	public DocSendListner getSendedDocs(Set<String> disabledFirms) {
		return new DocSendListner(OrderDoc.instance().getObjectName(), OrderImplEx.class, makeSendWhere(disabledFirms));
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		DocType.setCurDoc(OrderDoc.instance());
		open(context);
		OrderDocEdit.open(context, data.created.getTime());
		return false;
	}
	
	@Override public long sum() { return data.sum;}
	
	@Override
	public boolean delete() {
		if(!super.delete())
			return false;
		try {
			String sql = "DELETE FROM \"" + (new Order()).getTableName() + "\" WHERE linked=" + Long.toString(data.created.getTime()); 
			DataBaseManager.getDataBase().execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void addDocumentsToSend(Map<String, List<Long>> docs, Set<String> disabledFirms) {
		List<Long> ids = DbReader.readIds((new Order()).getTableName(), makeSendWhere(disabledFirms), "");
		if(ids.size() > 0) {
			String key = OrderDoc.instance().getObjectName();
			List<Long> ods = docs.get(key);
			if(ods == null) {
				ods = new ArrayList<Long>();
				docs.put(key, ods);
			}
			ods.addAll(ids);
		}
	}
}
