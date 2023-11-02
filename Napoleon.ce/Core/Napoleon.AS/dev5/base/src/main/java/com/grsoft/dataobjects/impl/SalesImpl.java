package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.SalesDetail;
import com.grsoft.napoleon.SalesPropertiesEditor;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

import android.content.Context;

public class SalesImpl extends SalesBaseImpl<Sales> implements Itemsable{

	public void initFromOrder(OrderImplBase<?> src, GpsCoord location) {
		Order s = (Order) src.getData();

		data.id = s.id;
		data.latitude = location.latitude;
		data.longitude = location.longitude;
		data.params = 0;
		data.created = Util.getDateTime();
		data.date = Util.getDate();
		data.supplyer = s.supplyer;
		data.sumType = s.sumType;
		data.remark = s.remark;
		data.supplyercode = s.firmCode;

		if(data.supplyercode == null || data.supplyercode.length() == 0) {
			// if code not filled, find firm in list
			Firm f = new Firm();
			int count = s.supplyer;
			DbReader r = new DbReader();
			boolean bdo = r.select(f, f.getTableName(), "");
			while(bdo) {
				if(count == 0) {
					data.supplyercode = f.id;
					break;
				}
				count--;
				bdo = r.selectNext(f);
			}
		}

		processInit(src);
		initDocNumber();
		DocHelper.saveDocNumber(getTableName(), data.number);

		PriceImpl pi = new PriceImpl();
		for(OrderItem oi : s.items) {
			pi.getData().id = oi.id;
			if( pi.read() ){
				updateQty(pi, oi.qty, oi.cost, oi.inPack());
				initItem(oi);
			}
		}
		pi.close();
	}
	public static SalesImpl fromOrder(OrderImpl src, GpsCoord location) {
		SalesImpl res = (SalesImpl) SalesDoc.instance().create();
		res.initFromOrder(src, location);
		res.write();
		return res;
	}

}
