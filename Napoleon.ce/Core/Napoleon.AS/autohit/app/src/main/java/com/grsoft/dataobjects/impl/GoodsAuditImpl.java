package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ActiveOrgActions;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.GoodsAudit;
import com.grsoft.dataobjects.GoodsAuditItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.AuditDetail;
import com.grsoft.napoleon.AuditGoods;
import com.grsoft.napoleon.AuditItemEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.GoodsAuditDoc;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class GoodsAuditImpl extends CreatableDocument<GoodsAudit> implements Itemsable {

	@Override
	public void open(Context context) {
		AuditDetail.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		AuditItemEdit.open(context, this, itemRowid);
	}
	
	@Override
	public long write() {
		long res = super.write();
		GoodsAuditDoc.instance().refreshDocSum(data.id);
		return res;
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		Date dt = Util.getDateTime();
		long r = find(orgId, dt);
		
		if( r != ExtrasConst.INVALID_ID ) {
			read(r);
			return true;
		}
		if( super.init(context, orgId, gpsCoord) ) {
			data.date = data.created;
			write();
			AuditGoods.open(context, this, false);
		}
		return false;
	}

	static public long find(String orgId, Date d) {
		long ret = ExtrasConst.INVALID_ID;
		long from, to;
		from = d.getTime();
		
		// перейдем на начало дня
		from -= (from % (1000 * 3600 * 24));
		
		// начало следующего дня
		to = from + (1000 * 3600 * 24);
		String tn = DataObjectInfo.getInstance().getTableName(GoodsAudit.class);
		String condition = "id='" + orgId + "' AND date >= " + Long.toString(from) + " AND date < " + Long.toString(to);
		DbWriter.checkDBTable(getDataType(ActiveOrgActions.class));
		List<Long> ids = DbReader.readIds(tn, condition, null);
		
		if( ids.size() > 0 )
			ret = ids.get(0);
		return ret;
	}
	
	@Override
	public DataObject findItem(String itemId) {
		if( data.items != null )
			for(GoodsAuditItem oi : data.items) {
				if( oi.id.compareTo(itemId) == 0 )
					return oi;
			}
		
		return null;
	}

	@Override public int getItemColor() { return com.grsoft.napoleon.R.color.green; }
	@Override public int getItemValue(Price item) { return 0; }

	@Override
	public int getItemQty(Price item) {
		GoodsAuditItem i = (GoodsAuditItem) findItem(item.id);
		return i == null ? 0 : i.scuOur;
	}

	@Override public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		return true;
	}

	public void deleteItem(GoodsAuditItem orderItem) {
		if(data.items != null) {
			if( data.items.remove(orderItem) )
				write();
		}
	}

}
