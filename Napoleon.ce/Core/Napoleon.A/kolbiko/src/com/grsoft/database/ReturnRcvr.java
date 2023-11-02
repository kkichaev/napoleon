package com.grsoft.database;

import java.util.Date;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

public class ReturnRcvr extends Hitching {
	
	public ReturnRcvr() { super(Return.class, "RetSend"); }
	
	@Override
	public void onEnd() {
		super.onEnd();
		pi.close();
		oi.close();
		
		try {
			ReturnDoc.instance().refreshDocSum();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Return dobj = (Return) rawObject.createDataObject(dataObject);
		
		if( dobj.created.before(checkdate)) {
			long val = dobj.date.getTime();
	         
			// перейдем в начало дня
	         val -= (val % ((long)24 * 3600 * 1000));
	         
	         // добавим остаток от деления номера на число секунд в дне
	         long add = ((long)(Integer.parseInt(dobj.number) % (24 * 3600))) * 1000;
	         val += add;
	         
	         dobj.created = new Date(val);
		}
		
		dobj.params |= (ParamState.ofExported | ParamState.ofProceeded);
		oi.getData().id = dobj.id;
		oi.read();
		int coef = ((OrgEx)oi.getData()).coef;
		if( coef == 0 ) coef = Consts.SUM_SCALE;
		
		Price prc = pi.getData();
		for(OrderItem item : dobj.items) {
			prc.id = item.id;
			if( pi.read() ) {
				int cost = prc.cost.get(0).cost;
				item.cost = (coef == Consts.SUM_SCALE) ? cost : cost * coef / Consts.SUM_SCALE;
			}
		}
		
		dbProxy.insertRecord(dobj);
	}
	
	OrgImpl oi = new OrgImpl();
	PriceImpl pi = new PriceImpl();
	Date checkdate = new Date(2000, 1, 1);
}
