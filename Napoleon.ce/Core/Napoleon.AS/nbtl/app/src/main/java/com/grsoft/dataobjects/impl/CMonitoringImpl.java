package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.CMonitoring;
import com.grsoft.dataobjects.CMonitoringItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.CMonitoringDetail;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;

import android.content.Context;


public class CMonitoringImpl extends CreatableDocument<CMonitoring> implements Itemsable{

	@Override
	public void open(Context context) { CMonitoringDetail.open(context, getRowid()); }
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		if (super.init(context, orgId, gpsCoord))
			Warehouse.open(context, this, false);
		return false;
	}

	@Override
	public void editItem(final long itemRowid, final Context context) {
		if( !isEditable() )
			return;

		PriceImpl priceImpl = new PriceImpl();
		priceImpl.read(itemRowid);
		priceImpl.close();
		
		final String pid = priceImpl.getData().id;

		boolean val = Features.INTEGER_INPUTS_QTY;
		
		Features.INTEGER_INPUTS_QTY = false;
		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (!isEditable())
					return;
								
				CMonitoringItem item = (CMonitoringItem) findItem(pid) ;
				
				if(item == null){
					item = new CMonitoringItem();
					item.id = pid;
					item.cost = value;
					data.items.add(item);
				}else
					item.cost = value;
				
				if(item.cost == 0)
					data.items.remove(item);
					
				write();
				
				((DataSetNotify)context).notifyDataSetChanged();
			}

			@Override
			public long getValue() {
				CMonitoringItem ri = (CMonitoringItem)findItem(pid);				
				return ri == null ? 0 : ri.cost;
			}
			
			@Override
			public boolean useComma() { return true; }
		}, Consts.SUM_SCALE,  false, context.getString(R.string.cost), false);
		
		Features.INTEGER_INPUTS_QTY = val;
	}

	@Override
	public DataObject findItem(String itemId) {
		for(CMonitoringItem ci : data.items)
			if(ci.id.equals(itemId))
				return ci;
		return null;
	}

	@Override
	public int getItemColor() { return R.color.green;	}

	@Override
	public int getItemQty(Price item) {		
		return 0;
	}

	@Override
	public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack) {
		return false;
	}

	@Override
	public long sum(){ 
		long result = 0;
		
		for(CMonitoringItem ci : data.items)
			result += ci.cost;
		
		return result;	
	}
	
	

	@Override
	public int getItemValue(Price item) {
		int result = 0;
		CMonitoringItem i = (CMonitoringItem) findItem(item.id);
		
		if( i != null)
			result = i.cost;
		
		return result; 
	}

}
