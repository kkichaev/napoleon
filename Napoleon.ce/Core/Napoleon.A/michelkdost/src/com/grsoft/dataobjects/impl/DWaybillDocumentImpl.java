package com.grsoft.dataobjects.impl;

import java.util.Collections;
import java.util.Comparator;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DWaybillDocument;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.Waybill;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.GpsCoord;
import android.content.Context;

public abstract class DWaybillDocumentImpl<T extends DWaybillDocument> extends DispatchDocImpl<T> {
	@Override
	public boolean init(Context context, DispatchImpl doc, DispatchItem item, GpsCoord loc) {
		Waybill sr = new Waybill();
		DbReader r = new DbReader();
		
		if (r.select(sr, sr.getTableName(), String.format("number='%s'", item.number))){
			for(DeliveryItem di : sr.items){
				DWaybillDocumentItem i = new DWaybillDocumentItem();
				i.id = di.id;
				i.inqty = di.qty;
				i.outqty = di.qty;
				i.cost = di.sum * Consts.QTY_SCALE / di.qty;
				DeliveryItemEx dix = (DeliveryItemEx)di;
				i.pos = dix.pos;
						
				data.items.add(i);
			}
		}
		
		Collections.sort(data.items, new Comparator<DWaybillDocumentItem>() {
			@Override public int compare(DWaybillDocumentItem lhs, DWaybillDocumentItem rhs) {
				return lhs.pos - rhs.pos;
			}});
		
		return super.init(context, doc, item, loc);
	}
	
	@Override
	public long sum() {
		long result = 0;
		
		for(DWaybillDocumentItem i : data.items)
			result += FPOperation.itemMul(i.cost, i.outqty, Consts.QTY_SCALE);
		
		return result;
	}
}
