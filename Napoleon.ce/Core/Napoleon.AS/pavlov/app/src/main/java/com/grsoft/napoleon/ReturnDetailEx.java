package com.grsoft.napoleon;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnCommit;
import com.grsoft.dataobjects.ReturnCommitItem;
import com.grsoft.dataobjects.impl.ReturnCommitImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class ReturnDetailEx extends ReturnDetail {
	ReturnCommit rc = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		long rid;
		if( savedInstanceState == null )
			rid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			rid = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		ReturnImplEx re = new ReturnImplEx();
		if( re.read(rid) ) {
			ReturnCommitImpl rci = new ReturnCommitImpl();
			if( rci.read("created", re.getData().created) ) {
				rc = rci.getData();
			}
		}

		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void setContentView() {
		if(rc != null) {
			setContentView(R.layout.return_detail_ex);
		} else
			super.setContentView();
	}
	
	@Override
	protected void updateTotalSum() {
		if( rc != null ) {
			updateTotalSum(rc.sum(), 0);
		} else
			super.updateTotalSum();
	}
	
	ReturnCommitItem getRCItem(String id) {
		for(ReturnCommitItem i : rc.items)
			if(i.id.equals(id))
				return i;
		return null;
	}
	
	@Override
	protected void setAdapter() {
		if(rc != null) {
			lvItems.setAdapter(new ReturnItemsAdapter());
		} else 
			super.setAdapter();
	}
	
	protected void drawRCItem(int color, ReturnCommitItem ditem, OrderItem oitem, TextView tvQty) {
		boolean showPack = (oitem.inPack() && ((CfgNplW)ConfigManager.getConfig()).isPackView);
		String qtyText;

		int qty = ditem == null ? 0 : ditem.qty;
		
		if( !showPack )
			qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE);
		else {
			Price p = price.getData();
			int inPack = p.qtyInPack;
			if( inPack == 0 )
				inPack = Consts.QTY_SCALE;
			qty = (int)((long)qty * Consts.QTY_SCALE / inPack);
			qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " ó.";
		}
		
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setText(qtyText);
		tvQty.setTextColor(color);
	}
	
	
	class ReturnItemsAdapter extends OrderItemsAdapter{ 
		ReturnCommitItem currentItem = null;
		
		@Override int getResourceID() { return R.layout.orderdeliverydetail_list_row; }
		
		@Override
		protected long getItemSum(OrderItem item) {
			return (currentItem == null) ? 0 : currentItem.sum;
		}
		
		protected int getItemColor(OrderItem item, int defaultColor) {
			int color = defaultColor;
			
			int qty = (currentItem == null) ? 0 : currentItem.qty; 
			if( qty != item.qty )
				color = Color.RED;
			
			return color;
		}
			
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			currentItem = getRCItem(item.id);
			
			TextView tvDispatch = (TextView) view.findViewById(R.id.tvDispatch);
			color = getItemColor(item, color);

			super.drawInternal(view, name, color, item, pos);
			drawRCItem(color, currentItem, item, tvDispatch);
		}
	}
}
