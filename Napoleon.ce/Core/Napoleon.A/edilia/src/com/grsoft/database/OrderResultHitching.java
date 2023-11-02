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
		errorMessage = "";
	}
	
	public static String getErrorMessage() { 
		String ret = errorMessage;
		errorMessage = "";
		return ret;
	}
	
	@Override
	public void onEnd() {
		oi.close();
		ri.close();
		orgi.close();
		di.close();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		try {
			OrderResult ord = (OrderResult)rawObject.createDataObject(dataObject);
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
					//oe.ordNumber = ord.ordnumber;
					oe.docStatus = status;					
					oe.docMessage = ord.message;

					status = DocHandleStatus.getStatus(ord.dlvstatus);
					oe.dlvStatus = status;
					oi.write();
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
