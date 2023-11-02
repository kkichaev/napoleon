package com.grsoft.dataobjects.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Merch;
import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.InputNumberDlg.Decorator;
import com.grsoft.napoleon.MerchDetail;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;


public class MerchImpl extends CreatableDocument<Merch>
	implements Itemsable{

	@Override
	public void open(Context context) { MerchDetail.open(context, this); }
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		Warehouse.open(context, this, false);
		return false;
	}
	
	
	@Override
	public void editItem(final long itemRowid, final Context context) { 
		PriceImpl priceImpl = new PriceImpl();
		priceImpl.read(itemRowid);
		priceImpl.close();
		MerchItem ri = (MerchItem)findItem(priceImpl.data.id);
		
		final int start = ri != null ? ri.start : 0;
		final int finish = ri != null ? ri.finish : 0;
		
		InputNumberDlg.open(context, new InputNumber() {
			
		@Override public boolean useComma() { return false; }
		@Override public boolean replaceCommaToPlus() { return false; }
		
		@Override
		public void applayInput(int value, Object... params) {
			if (isExported())
				return;
			value = apply(value, params);
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			
			if (updateQty(priceImpl, value, 0, false) && context instanceof DataSetNotify)
				((DataSetNotify)context).notifyDataSetChanged();
			
			priceImpl.close();
		}

		
		@Override
		public int getValue() { return start; }
		
	}, Consts.QTY_SCALE, true, context.getString(R.string.Faces), false, new Decorator() {
		@Override public int getContentView() { return R.layout.inputnumberdlgex;	}
		@Override public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) { adjView(dialog, view, nh, finish); }
	});}

	protected int apply(int value, Object... params) { return value; }
	
	protected void adjView(AlertDialog arg0, View view, KeypadHelper kh, int finish) {
		View v = view.findViewById(R.id.edCount);
		
		if(v != null)
			kh.setTargetID(v.getId());
		
		v = view.findViewById(R.id.edFinish);
		
		if(v != null){
			((EditText)v).setText(Util.IntToScaleStr(finish, Consts.QTY_SCALE));
			v.setEnabled(false);
		}
	}

	@Override
	public DataObject findItem(String itemId) {
		
		if( data.items != null )
			for(MerchItem ri : data.items) {
				if( ri.id.compareTo(itemId) == 0 )
					return ri;
			}
		
		return null;
	}

	@Override
	public int getItemColor() {	return R.color.magneta; }

	@Override
	public int getItemValue(Price item) { return item.qty;	}

	@Override
	public int getItemQty(Price item) {
		MerchItem ri = (MerchItem) findItem(item.id);		
		return ri == null ? 0 : ri.start;
	}

	@Override
	public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		Price price = priceImpl.getData();
		MerchItem item = (MerchItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) // new item
		{
			item = new MerchItem();
			item.id = price.id;
			item.start = qty;
			data.items.add(item);
		} else {
			if( item.start != qty ){
				item.start = qty;
			}else
				needUpdate = false;
		}
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}

	public void removeItem(String id) {
		DataObject i =  findItem(id);
		data.items.remove(i);
		write();
	}

}
