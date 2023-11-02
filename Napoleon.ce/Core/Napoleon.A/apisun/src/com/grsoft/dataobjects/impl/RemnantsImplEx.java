package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class RemnantsImplEx extends RemnantsImpl {
	@Override
	public void editItem(final long itemRowid,final  Context context) {
		InputNumberDlg.open(context, new InputNumber() {
			
			@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
			@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (isExported())
					return;
				
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.read(itemRowid);
				
				boolean refresh = false;
				if( value == 0 && editValue.length() == 0) {
					refresh = deleteItem(priceImpl.getData());
				} else 
					if( Features.REST_IN_PACK )
						value = (int)((long)value * priceImpl.getData().qtyInPack / Consts.QTY_SCALE);
					refresh = updateQty(priceImpl, value, 0, false);
				if (refresh && context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
				
				priceImpl.close();
				
				RemnantsDoc.instance().refreshDocSum(data.id);
			}

			@Override
			public int getValue() {
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.read(itemRowid);
				priceImpl.close();
				RemnantItem ri = (RemnantItem)findItem(priceImpl.data.id);
				int qty = ri == null ? 0 : ri.qty;
				
				if( Features.REST_IN_PACK )
					qty = (int)((long)qty * Consts.QTY_SCALE / priceImpl.getData().qtyInPack);
				return qty;
			}
		},  Consts.QTY_SCALE, true, context.getString(R.string.input_value), false,
				new InputNumberDlg.Decorator() {
					
					@Override
					public int getContentView() {
						return R.layout.inputnumberdlgex;
					}
					
					@Override
					public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) {
						String where = String.format("created < %d and id=\"%s\"", 
								Util.resetTime(new Date()).getTime(),data.id);
						DocList list = new DocList(RemnantsImplEx.class, where, "created desc");
						
						int cnt = 0;
						int days[] = {R.id.tvDay1, R.id.tvDay2, R.id.tvDay3, R.id.tvDay4};
						int qties[] = {R.id.tvQty1, R.id.tvQty2, R.id.tvQty3, R.id.tvQty4};
						
						PriceImpl price = new PriceImpl();
						price.read(itemRowid);
						price.close();
						
						for (Document<?> d : list) {
							if (cnt == days.length)
								break;
							
							RemnantsImpl r = (RemnantsImpl)d;
							TextView tv = (TextView) view.findViewById(days[cnt]);
							tv.setText(Util.simpleDateFormat.format(r.getData().created));
							
							RemnantItem i = (RemnantItem) r.findItem(price.getData().id);
							tv = (TextView) view.findViewById(qties[cnt]);
							tv.setText(i == null ? "0" : Util.IntToScaleStr(i.qty, Consts.QTY_SCALE));
							
							cnt++;
						}
						
						if (cnt < days.length)
							for(int x=cnt; x < days.length; x++) {
								view.findViewById(days[x]).setVisibility(View.GONE);
								view.findViewById(qties[x]).setVisibility(View.GONE);
							}
					}
				});
	}
	
	@Override
	public int getItemColor() {
		return R.color.green;
	}

	public void reverseQty(String id, Context context) {
		PriceImpl pi = new PriceImpl();
		pi.read("id", id);
		
		int qty = (findItem(pi.getData().id) != null) ? 0 : 1 * Consts.QTY_SCALE;
		
		if( updateQty(pi, qty, 0, false) && context instanceof DataSetNotify )
			((DataSetNotify)context).notifyDataSetChanged();
		
		RemnantsDoc.instance().refreshDocSum(data.id);
		pi.close();
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {	
		Price price = priceImpl.getData();
		RemnantItem item = (RemnantItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty >= 0 )
			{
				Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");

				try {
					item = (RemnantItem) itemClass.newInstance();
					
					item.id = price.id;
					item.qty = qty;
					data.items.add(item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
				needUpdate = false;
		} else
		{
			if( qty == 0 )
				data.items.remove(item);
			else {
				if( item.qty != qty )
					item.qty = qty;
				else
					needUpdate = false;
			}
		}
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}
}
