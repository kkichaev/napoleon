package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.annotation.SuppressLint;

import com.grsoft.dataobjects.DocHandleStatus;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderResult;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class OrderResultHitching extends Hitching {
	
	static String errorMessage = "";
	
	SimpleDateFormat parser;
	OrderImplEx oi = new OrderImplEx();
	ReturnImplEx ri = new ReturnImplEx();
	
	DeliveryImpl di = new DeliveryImpl();
	OrgImpl orgi = new OrgImpl();
	
//	HashMap<String, Long> orgs = new HashMap<String, Long>();
//	static boolean updateDebet = true;
	
	@SuppressLint("SimpleDateFormat")
	public OrderResultHitching() {
		super(OrderResult.class, "OrderResult");
		
		parser = new SimpleDateFormat("yyyyMMddHHmmss");
		oi = new OrderImplEx();
		di = new DeliveryImpl();
		orgi = new OrgImpl();
	}
	
	@Override
	public void onStart() {
//		orgs.clear();
//		updateDebet = true;
		errorMessage = "";
	}
	
	public static String getErrorMessage() { 
		String ret = errorMessage;
		errorMessage = "";
		return ret;
	}
	
//	/**
//	 * ≈сли работает DeliveryHitchingEx - нам ничего не надо делать 
//	 * @param newVal
//	 */
//	public static void setUpdateDebet(boolean newVal) { updateDebet = newVal; }
	
	@Override
	public void onEnd() {
		oi.close();
		ri.close();
		orgi.close();
		di.close();
		
//		if(updateDebet) {
//			// remove all unchanged
//			DataTraveler.travel(OrgSum.class, new DataTraveler.Travel<OrgSum>() {
//
//				@Override
//				public boolean travel(DataTraveler<OrgSum> item) {
//					Long val = orgs.get(item.data.id);
//					if( val != null && val == item.data.sum )
//						orgs.remove(item.data.id);
//					return true;
//				}
//			}, "type='" + DebtDoc.instance().getName() + "'");
			
//			DebtDocEx doc = (DebtDocEx)DebtDoc.instance();
//			DbReader r = new DbReader();
//			Delivery d = new Delivery();
//			
//			doc.clearCache();
//			for(Entry<String, Long> entry : orgs.entrySet()) {
//				doc.putBalance(entry.getKey(), entry.getValue());
//			
//				String where = "id='" + entry.getKey() + "'";
//				boolean bdo = r.select(d, d.getTableName(), where);
//				while(bdo) {
//					doc.putDelivery(d);
//					bdo = r.selectNext(d);
//				}
//			}
//			r.close();
//			
//			try {
//				doc.updateFromCache(false);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		try {
			OrderResult ord = (OrderResult)rawObject.createDataObject(dataObject);
//			if(ord.orgid != null && ord.orgid.length() > 0)
//				orgs.put(ord.orgid, ord.balance);
			int status = DocHandleStatus.getStatus(ord.ordstatus);
			if( status == DocHandleStatus.FAIL ) {
				errorMessage = ord.message;
				return;
			}
			
			if( ord.created.length() == 0 || ord.created.toUpperCase(Locale.getDefault()).equals("NONE"))
				return;
			
			Date created = parser.parse(ord.created);
			if( ord.doctype.equals(ReturnDoc.instance().getObjectName())) {
				ReturnEx re = (ReturnEx)ri.getData();
				re.created = created;
				if(ri.read()) {
					re.retNumber = ord.ordnumber;
					re.docStatus = status;					
					re.docMessage = ord.message;
					ri.write();
				}
				return;
			}
			
			if( status != DocHandleStatus.REPEATED) {
				
				OrderEx oe = (OrderEx)oi.getData();
				
				oe.created = created;
				if(oi.read()) {
					oe.ordNumber = ord.ordnumber;
					oe.docStatus = status;					
					oe.docMessage = ord.message;

					status = DocHandleStatus.getStatus(ord.dlvstatus);
					oe.dlvStatus = status;
//					if( status == DocHandleStatus.HANDLED ) {
//						makeDelivery(oe, ord);
//						if( ord.dlvnumber.length() > 0 )
//							oe.number = ord.dlvnumber;
//					}
					oi.write();
					
//					orgs.put(oe.id, ord.balance);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private void makeDelivery(OrderEx oe, OrderResult ord) {
//		if( ord.dlvnumber.length() == 0 )
//			return;
//		
//		OrgEx orge = (OrgEx) orgi.getData();
//		orge.id = oe.id;
//		orgi.read();
//		
//		Delivery d = di.getData();
//		d.id = oe.id;
//		d.number = ord.dlvnumber;
//		d.date = ord.dlvdate;
//		d.payDate = ord.dlvpaydate; //new Date(d.date.getTime() + orge.delay * 24 * 3600 * 1000);
//		//d.payDate = new Date(d.date.getTime() + orge.delay * 24 * 3600 * 1000);
//		
//		d.sumD = 0;
//		d.items = new ArrayList<DeliveryItem>();
//		
//		for(OrderResultItem ori : ord.items) {
//			DeliveryItem ditem = new DeliveryItem();
//			ditem.id = ori.id;
//			ditem.qty = ori.qty;
//			ditem.sum = ori.sum;
//			
//			d.items.add(ditem);
//		}
//		
//		di.insert();
//	}
}
