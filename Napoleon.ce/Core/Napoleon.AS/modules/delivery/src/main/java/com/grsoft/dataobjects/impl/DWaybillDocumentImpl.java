package com.grsoft.dataobjects.impl;

import java.util.Collections;
import java.util.Comparator;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DWaybillDocument;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
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
			Class<? extends DataObject> ic = DataObjectInfo.getInstance().getListType(data.getClass(), "items");
			for(DeliveryItem di : sr.items){
				try {
					DWaybillDocumentItem i = (DWaybillDocumentItem) ic.newInstance();//new DWaybillDocumentItem();
					initWaybillItem(di, i);

					data.items.add(i);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		Collections.sort(data.items, new Comparator<DWaybillDocumentItem>() {
			@Override public int compare(DWaybillDocumentItem lhs, DWaybillDocumentItem rhs) {
				return lhs.pos - rhs.pos;
			}});
		
		return super.init(context, doc, item, loc);
	}

	protected boolean superInit(Context context, DispatchImpl doc, DispatchItem item, GpsCoord loc) {
		return super.init(context, doc, item, loc);
	}

	public void initWaybillItem(DeliveryItem di, DWaybillDocumentItem i) {
		i.id = di.id;
		i.inqty = di.qty;
		i.outqty = di.qty;
		i.cost = ((DeliveryItemEx) di).cost;
		DeliveryItemEx dix = (DeliveryItemEx) di;
		i.pos = dix.pos;
	}

	public boolean isDirty() {
		if(isReadyToSend())
			return true;

		for(DWaybillDocumentItem i : data.items) {
			if(i.inqty != i.outqty) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public long sum() {
		long result = 0;
		
		for(DWaybillDocumentItem i : data.items)
			result += FPOperation.itemMul(i.cost, i.outqty, Consts.QTY_SCALE);
		
		return result;
	}
}
