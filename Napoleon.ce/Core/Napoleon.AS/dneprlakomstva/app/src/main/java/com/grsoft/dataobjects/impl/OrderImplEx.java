package com.grsoft.dataobjects.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.OrgUtils;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class OrderImplEx extends OrderImpl {
	public boolean isEmpty() {
		
		OrderEx oe = (OrderEx)data;
		return super.isEmpty() && oe.incass == 0 && (oe.willPay == 0 || oe.willSum == 0); 
	}
	
	public boolean isGood(long outSum) {
		if( outSum > 0 ) {
			OrderEx oe = (OrderEx)data;
			return oe.incass > 0 || oe.willPay > 0;
		}
		return true;
	}
	
	@Override
	public boolean delete() {
		if(isExported())
			return false;
		return super.delete();
	}
	
	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		OrderEx oe = (OrderEx)copy.getData();
		
		oe.incass = 0;
		oe.willPay = 0;
		oe.willSum = 0;
		oe.docMessage = "";
		oe.docStatus = 0;
		oe.dlvStatus = 0;
		oe.ordNumber = "";
		super.postCopyProcess(copy);
	}

	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		oi.getData().id = data.id;
		oi.read();
		oi.close();
		
		data.sumType = oi.getData().costype;
	}
		
	@Override
	public boolean init(final Context context, final String orgId, final GpsCoord coord) {
		long outSum = OrgUtils.getOutDebt(orgId);		
		if( outSum > 0 ) {
			AlertDialog ad = new AlertDialog.Builder(context).create();
			ad.setTitle(R.string.alert);
			String message = context.getString(R.string.outDebt);
			message += "\n";
			message += Util.IntToScaleStr(outSum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			ad.setMessage(message);
			ad.setButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					initSilent(orgId, coord);
					Warehouse.open(context, OrderImplEx.this, false);
				}
			});
			ad.show();
			return false;
		}
		
		initSilent(orgId, coord);
		Warehouse.open(context, this, false);
		return false;
//		return super.init(context, orgId, coord);
	}
}
