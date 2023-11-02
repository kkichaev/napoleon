package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.CellsAudit;
import com.grsoft.dataobjects.CellsAuditItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.VandReload;
import com.grsoft.dataobjects.VandReloadItem;
import com.grsoft.napoleon.CellData;
import com.grsoft.napoleon.CellsAuditDetail;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CellsAuditDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class CellsAuditImpl extends CreatableDocument<CellsAudit> implements Itemsable {

	static public CellsAuditImpl getLastDoc(String orgId) {
		CellsAuditImpl ret = null;
		
		try {
			String table = DataObjectInfo.getInstance().getTableName(CellsAudit.class);
			String sql = "select rowid from '" + table + "' where id='" + orgId + "' order by created desc";
			
			android.database.Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
			if( c.moveToNext() ) {
				long rid = c.getLong(0);
				ret = new CellsAuditImpl();
				ret.read(rid);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	static public long find(String orgId, Date d) {
		long ret = ExtrasConst.INVALID_ID;
		long from, to;
		from = d.getTime();
		
		// перейдем на начало дня
		from -= (from % (1000 * 3600 * 24));
		
		// начало следующего дня
		to = from + (1000 * 3600 * 24);
		String table = DataObjectInfo.getInstance().getTableName(CellsAudit.class);
		String where = "id='" + orgId + "' AND date >= " + Long.toString(from) + " AND date < " + Long.toString(to);
		DbWriter.checkDBTable(getDataType(CellsAudit.class));
		List<Long> ids = DbReader.readIds(table, where, null);
		
		if( ids.size() > 0 )
			ret = ids.get(0);
		return ret;
	}
	
	@Override
	public long write() {
		long res = super.write();
		CellsAuditDoc.instance().refreshDocSum(data.id);
		return res;
	}
	
	@Override
	public CreatableDocument<CellsAudit> copy() {
		CellsAuditImpl result = null;
		
		if (rowid != ExtrasConst.INVALID_ID) {
			result = new CellsAuditImpl();
			result.read(rowid);
			result.data.created = Util.getDateTime();
			result.data.date = result.data.created;
			result.data.params = 0;
			result.rowid = ExtrasConst.INVALID_ID;
//			List<CellData> cd = CellData.getVandData(data.id);
//			initItemsQty(cd);
			result.write();
		}
		
		return result;
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		Date dt = Util.getDateTime();
		long r = find(orgId, dt);
		
		if( r != ExtrasConst.INVALID_ID )
			read(r);
		else {
			List<CellData> cd = CellData.getVandData(orgId);
			if( super.init(context, orgId, coord) ) {
				data.date = data.created;
				initItemsQty(cd);
				write();
			}
		}
		return true;
	}

	private void initItemsQty(List<CellData> cd) {
		data.items = new ArrayList<CellsAuditItem>();
		for(CellData ci : cd) {
			CellsAuditItem cai = new CellsAuditItem();
			cai.id = ci.id;
			cai.cell = ci.cell;
			cai.cost = ci.cost;
			cai.limit = ci.limit;
			cai.qty = ci.rest;
			
			data.items.add(cai);
		}
	}


	@Override
	public Date getDate() { return data.date; }

	@Override
	public String getDescription(Context context) {
		return (isExported()) ? context.getString(R.string.sent) : ""; 
	}

	@Override
	public String getId() {return data.id; }

	@Override
	public void open(Context context) {
		CellsAuditDetail.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		
	}

	@Override
	public DataObject findItem(String itemId) {
		if( data.items != null ) {
			for(CellsAuditItem i : data.items)
				if( i.id.equals(itemId) )
					return i;
		}
		return null;
	}

	@Override
	public int getItemColor() {
		return 0;
	}

	@Override
	public int getItemValue(Price item) {
		return 0;
	}

	@Override
	public int getItemQty(Price item) {
		return 0;
	}

	@Override
	public long getItemSum(Price item) {
		return 0;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		return false;
	}

	public void createAudit(VandReload src, List<CellData> items) {
		data.id = src.id;
		data.created = Util.getDateTime();
		data.date = data.created;
		data.latitude = src.latitude;
		data.longitude = src.longitude;
		data.remark = "Автоматичекий документ ревизии";
		
		data.items = new ArrayList<CellsAuditItem>();
		for( CellData item : items)
			data.items.add(new CellsAuditItem(item));
		
		updateItems(src);

		setExported(true);
	}
	
	public void updateItems(VandReload src) {
		for(CellsAuditItem cai : data.items) {
			for(VandReloadItem vri : src.items) {
				if( cai.cell == vri.cell ) {
					cai.id = vri.id;
					cai.qty = vri.qty;
					cai.cost = vri.cost;
					break;
				}
			}
		}
	}

}
