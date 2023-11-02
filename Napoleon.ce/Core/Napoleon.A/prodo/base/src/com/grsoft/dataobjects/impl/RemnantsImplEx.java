package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgMargin;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
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
	
	Integer sumType = null;
	
	@Override
	public int getSumType() {
		if( sumType == null ) {
			OrgImpl oi = new OrgImpl();
			Org o = oi.getData();
			o.id = data.id;
			oi.read();
			oi.close();
			sumType = o.costype;
		}
		return sumType;
	}
	
	@Override
	public void editItem(long itemRowid, final Context context) {
		
		final PriceImpl priceImpl = new PriceImpl();
		priceImpl.read(itemRowid);
		priceImpl.close();

		InputNumberDlg.open(context, new InputNumber() {
			
			@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
			@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (isExported())
					return;
				
				if (updateQty(priceImpl, value, 0, false) && context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
				
				priceImpl.close();
				
				RemnantsDoc.instance().refreshDocSum(data.id);
			}
	
			@Override
			public int getValue() {
				RemnantItem ri = (RemnantItem)findItem(priceImpl.getData().id);
				return ri == null ? 0 : ri.qty;
			}
		}, Consts.QTY_SCALE, true, context.getString(R.string.value), false, new Decorator(priceImpl.getData()));
	}
	
	class Decorator implements InputNumberDlg.Decorator {
		
		int cost = 0;
		public Decorator(Price p) {
			int docCost = CostStrategy.getInstance(RemnantsImplEx.class).getItemCost(p, RemnantsImplEx.this);
			
			OrgMarginImpl oi = new OrgMarginImpl();
			OrgMargin o = oi.getData();
			o.id = getId();
			oi.read();
			oi.close();
		
			cost = docCost + (int)(((long) docCost * o.value + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		}
		
		@Override public int getContentView() { return R.layout.inputnumberdlgex; }

		@Override
		public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) {
			TextView tv = (TextView)view.findViewById(R.id.tvCost);
			tv.setText("–ек.цена: " + Util.IntToScaleStr(cost, Consts.SUM_SCALE));
			
		}
	}
}
