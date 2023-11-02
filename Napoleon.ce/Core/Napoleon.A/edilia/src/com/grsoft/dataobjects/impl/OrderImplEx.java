package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.OrgUtils;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class OrderImplEx extends OrderImpl {
	public boolean isEmpty() {
		
		OrderEx oe = (OrderEx)data;
		return super.isEmpty() && oe.incass == 0; 
	}
	
	public boolean isGood(long outSum) {
		if( outSum > 0 ) {
			OrderEx oe = (OrderEx)data;
			return oe.incass > 0 || oe.thinkInOffice > 0;
		}
		return true;
	}
	
//	@Override
//	public int getItemValue(Price item) {
//		int qip = item.qtyInPack > 0 ? item.qtyInPack : Consts.QTY_SCALE;
//		return (int)((long)item.qty * Consts.QTY_SCALE / qip);
//	}
	
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
		oe.docMessage = "";
		oe.docStatus = 0;
		oe.dlvStatus = 0;
		oe.ordNumber = "";
		
//		oe.date = oe.created;
		Calendar c = Calendar.getInstance();
		c.setTime(oe.created);
		c.add(Calendar.DAY_OF_MONTH, 1);
		oe.date = c.getTime();	

		super.postCopyProcess(copy);
	}

	@Override
	public void postInit() {
		ConfigImpl config = new ConfigImpl();
		config.read("key", "¬ид÷ены");
		List<KeyValue> pc = new ArrayList<KeyValue>(); 
		DialogHelper.makeListWithKey(config.getData().value, pc, "");
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = data.id;
		oi.read();
		oi.close();
		
		data.sumType = oi.getData().costype;
		if(pc.size() > data.sumType)
			data.prcType = pc.get(data.sumType).key.toString();

		Calendar c = Calendar.getInstance();
		c.setTime(data.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		data.date = c.getTime();	
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
