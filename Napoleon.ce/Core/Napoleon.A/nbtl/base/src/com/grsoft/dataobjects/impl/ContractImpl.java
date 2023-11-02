package com.grsoft.dataobjects.impl;

import java.util.HashSet;

import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.ContractItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.ContractDetail;
import com.grsoft.napoleon.ContractEdit;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;

import android.content.Context;

public class ContractImpl extends CreatableDocument<Contract> implements Itemsable {

	@Override
	public void open(Context context) { ContractDetail.open(context, getRowid()); }
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		if (super.init(context, orgId, gpsCoord))
			ContractEdit.open(context, getRowid(), false);
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

		
		InputNumberDlg.open(context, new InputNumber() {
			@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }

			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (!isEditable())
					return;
								
				ContractItem item = (ContractItem) findItem(pid) ;
				
				if(item == null){
					item = new ContractItem();
					item.id = pid;
					item.qty = value;
					data.items.add(item);
				}else
					item.qty = value;
				
				if(item.qty == 0)
					data.items.remove(item);
					
				write();
				
				((DataSetNotify)context).notifyDataSetChanged();
			}

			@Override
			public int getValue() {				
				ContractItem ri = (ContractItem)findItem(pid);				
				return ri == null ? 0 : ri.qty;
			}
		});
	}

	@Override
	public DataObject findItem(String itemId) {
		for(ContractItem ci : data.items)
			if(ci.id.equals(itemId))
				return ci;
		return null;
	}

	@Override
	public int getItemColor() { return R.color.green;	}

	@Override
	public int getItemQty(Price item) {
		ContractItem i = (ContractItem) findItem(item.id);
		
		if(i != null)
			return i.qty;
		
		return 0;
	}

	@Override
	public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		return false;
	}

	@Override
	public int qty(){
		int result = 0;
		
		for(ContractItem i : data.items)
			result += i.qty;
		
		return result;
	}

	@Override
	public int getItemValue(Price item) { return 0; }

	public long countQty(HashSet<String> nodes) {
		int result = 0;
		
		for(ContractItem i : data.items)
			if(nodes.contains(i.id))
					result += i.qty;
		
		return result;
	}
}
