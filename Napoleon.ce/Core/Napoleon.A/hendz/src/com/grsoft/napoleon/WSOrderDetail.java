package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class WSOrderDetail extends OrderDetail {
	Adapter adapter;
	
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, WSOrderDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	private DocType docType = OrderDoc.instance();
	protected void onCreate(android.os.Bundle savedInstanceState) {
		docType = DocType.getCurDoc();
		DocType.setCurDoc(WSOrderDoc.instance());
		super.onCreate(savedInstanceState);
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbInPack);
		cb.setChecked(((CfgNpl)ConfigManager.getConfig()).isPackView);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				((CfgNpl)ConfigManager.getConfig()).isPackView = arg1;
				ConfigManager.save();
				adapter.notifyDataSetChanged();
			}
		});
	};
	
	@Override
	protected void onDestroy() {
		DocType.setCurDoc(docType);
		super.onDestroy();
	}
	 
	@Override protected void setAdapter() { 
		adapter = new Adapter();
		lvItems.setAdapter(adapter);
		lvItems.setDividerHeight(0);
	}
	
	@Override
	protected boolean haveFocusedGroup() {
		return false;
	}
	
	protected void setContentView(){
		setContentView(R.layout.wsorderdetail);
	}
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		boolean showPack = ((CfgNpl)ConfigManager.getConfig()).isPackView;
		String qtyText;
		if( !showPack )
			qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
		else {
			Price p = price.getData();
			int inPack = p.qtyInPack;
			if( inPack == 0 )
				inPack = Consts.QTY_SCALE;
			int qty = ((int)((long)item.qty * Consts.QTY_SCALE / inPack) + 50) / 100;
			qtyText = Util.IntToScaleStr(qty, 10) + " " + getString(R.string.pack_lbl);
		}
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.wsorderdetail_list_row; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			super.drawInternal(view, name, color, item);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvWhQty);
			int qty = doc.getItemValue(price.getData());
			boolean showPack = (((CfgNpl)ConfigManager.getConfig()).isPackView);
			String qtyStr;
			if( showPack) {
				Price p = price.getData();
				int inPack = p.qtyInPack;
				if( inPack == 0 )
					inPack = Consts.QTY_SCALE;
				qty = ((int)((long)qty * Consts.QTY_SCALE / inPack) + 50) / 100;
				qtyStr = Util.IntToScaleStr(qty, 10) + " " + getString(R.string.pack_lbl);
			} else
				qtyStr = Util.IntToScaleStr(qty, Consts.QTY_SCALE);
			tv.setText(qtyStr);
			
			if( price.getData().color == NapoleonApp.MONEY_COLOR ) {
				view.setBackgroundResource(R.drawable.money_color);
			} else {
				int index = doc.getData().items.indexOf(item);
				view.setBackgroundResource((index % 2) != 0 ? R.drawable.even_row_selector
						: R.drawable.list_selector);
			}
		}
	}
}
