package com.grsoft.dataobjects.impl;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WhData;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderImplEx extends OrderImpl {
	HashMap<String, Integer> qtys = null;
	
	public String getWhId() { return ((OrderEx)data).whCode; }

	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		super.postCopyProcess(copy);
		OrderEx dest = (OrderEx) copy.getData();
		dest.locChecked = 0;
	}

	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		oi.read("id", data.id);

		OrderEx o = (OrderEx) data;
		OrgEx org = (OrgEx) oi.getData();

		o.prcType = org.prcType;
		o.whCode = org.whCode;
		o.firmCode = org.firmCode;

		if(org.delivery != 0 && (org.delivery & 0x7f) < 0x7f) {
			Calendar c = Calendar.getInstance();

			while (true) {
				c.add(Calendar.DAY_OF_MONTH, 1);
				int dw = c.get(Calendar.DAY_OF_WEEK);
				if (dw != 0) {
					int f = 1 << (dw - 1);
					if ((org.delivery & f) != 0) {
						o.date = Util.getDayStart(c.getTime());
						break;
					}
				}
			}
		}
	}

	public int getWhIndex() {
		int index = ((OrderEx)data).whIndex;
		
		if( index < 0) {
			ConfigImpl ci = new ConfigImpl();
			Config c = ci.getData();
			c.key = "Склады";
			if(ci.read()) {
				ArrayList<KeyValue> values = new ArrayList<KeyValue>();
				index = DialogHelper.makeListWithKey(c.value, values, getWhId());
			}
			ci.close();
			
			if( index < 0 )
				index = 0;
			
			((OrderEx)data).whIndex = index;
			write();
		}
		
		return index;
	}
	
	@Override
	public int getItemValue(Price item) {
		int index = getWhIndex();
		if( index == 0 )
			return item.qty;
		
		if(qtys == null) {
			qtys = new HashMap<String, Integer>();
			DataTraveler.travel(WhData.class, new DataTraveler.Travel<WhData>() {

				@Override
				public boolean travel(DataTraveler<WhData> item) {
					qtys.put(item.data.id, item.data.qty);
					return true;
				}
			}, "whCode = '"+ getWhId() + "'");
		}
		
		Integer val = qtys.get(item.id);
		return val == null ? 0 : val;
	}

	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int index = getWhIndex();
		if( index > 0 ) {
			WhDataImpl wd = new WhDataImpl();
			WhData wddata = wd.getData();
			
			wddata.id = price.getData().id;
			wddata.whCode = getWhId();
			
			wd.read();
			
			wddata.qty += qty;
			wd.write();
			wd.close();
			
		} else
			super.updatePrice(price, qty);
	}
}
