package com.grsoft.dataobjects.impl;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.GpsCoord;

public class OrderImplEx extends OrderImpl {
	
	public static OrderImpl autoorder(String orgId, GpsCoord coord) {		
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = orgId;
		oi.read();
		oi.close();
		
		HashMap<String, Integer> items = new HashMap<String, Integer>();
		
		for(OrgMatrix item : oe.orgMatrix) {
			items.put(item.id, item.qty);
		}
		
		OrderImpl ret = (OrderImpl)OrderDoc.instance().create();
		ret.autoorder(orgId, coord, items);
		return ret;
	}
	
	@Override
	public boolean init(final Context context, final String orgId, final GpsCoord coord) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Выберите вариант");
		CharSequence[] items = new CharSequence[] {"Автозаказ", "Обычный заказ"};
		b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( which == 1 ) {
					OrderImplEx.super.init(context, orgId, coord);
					dialog.dismiss();
				} else {
					OrderImpl o = autoorder(orgId, coord);
					if( o != null )
						o.open(context);
					dialog.dismiss();
				}
			}
		});
		b.create().show();
		
		return false;		
	}
}
