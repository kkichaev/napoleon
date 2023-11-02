package com.grsoft.napoleon;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.Unitable;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	boolean haveCompleeteFocusedItems() {
		boolean ret = true;
		OrgImpl oi = new OrgImpl();
		oi.getData().id = doc.getId();
		
		OrgEx oe = (OrgEx) oi.getData();
		List<MatrixItem> focusedItems = oe.focusedItems; 
		if( oi.read() &&  focusedItems != null && focusedItems.size() > 0 ) {
			for(MatrixItem mi : focusedItems) {
				if(doc.findItem(mi.id) == null) {
					ret = false;
					break;
				}
			}
		}
		oi.close();
		return ret;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK &&
				doc.isExported() == false && !haveCompleeteFocusedItems() ) {
			showDialog(FOCUS_WARNING_DLG);
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected Dialog createFocusWarningDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Ошибка");
		builder.setMessage("Не проставлен вес по позициям:..");
		builder.setPositiveButton("OK", null);
		builder.setNegativeButton("Изменить заказ", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( doc.getData().items.size() == 0 )
					doc.delete();
				finish();
			}
		});
		
		return builder.create();
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapterEx());
	}
	
	@Override
	protected void updateTotalSum() {
		updateTotalSum(doc.sum(), doc.weight(), countItems());
	}
	
	private int countItems() {
		if( doc.getData().items == null )
			return 0;
		
		int count = 0;
		PriceImpl pi = new PriceImpl();
		PriceEx pe = (PriceEx) pi.getData();
		for(OrderItem i : doc.getData().items ) {
			((Price)pe).id = i.id;
			pi.read();
		
			int inPack = 0;
			for(UnitItem unitItem : pe.units)
				if (unitItem.id.equals(((Unitable)i).getUnit())) {
					if( unitItem.name.equals("шт")) {
						inPack = unitItem.inpack;
						break;
					}
				}
			
			if( inPack > 0 ) {
				int qty = (int)((long)i.qty * Consts.QTY_SCALE / inPack);
				count += qty;
			}
		}
			
		pi.close();
		
		return count / Consts.QTY_SCALE;
	}

	class OrderItemsAdapterEx extends OrderItemsAdapter{
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			super.drawInternal(view, name, color, item);
						
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.getData().id = item.id;
			
			if(priceImpl.read()){
				String unitName = "";
				
				int inPack = 0;
				for(UnitItem unitItem : ((PriceEx)priceImpl.getData()).units)
					if (unitItem.id.equals(((Unitable)item).getUnit())) {
						unitName = unitItem.name;
						inPack = unitItem.inpack;
					}
				
				TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
				boolean showPack = ((CfgNpl)ConfigManager.getConfig()).isPackView;
				String qtyText;
				if( !showPack )
					qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
				else {
					if( inPack == 0 )
						inPack = Consts.QTY_SCALE;
					if( inPack != Consts.QTY_SCALE) {
						int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
						qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + unitName;
					} else
						qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE) + " " + unitName;
				}
				tvQty.setText(qtyText);
			}
			
			priceImpl.close();
		}
		
		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			view = super.getView(arg0, view, arg2);
			
			view.setBackgroundResource(
					arg0 % 2 != 0 ? R.drawable.pink_row_selector:  
									R.drawable.list_selector);
			
			return view;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		int minWeight = ((OrgEx)org.getData()).minWeight;
		TextView tvWeightWarning = (TextView)findViewById(R.id.tvWeightWarning);
		
		if(minWeight > 0 && minWeight > doc.weight()){
			tvWeightWarning.setVisibility(View.VISIBLE);
			btnSend.setVisibility(View.GONE);
		}else{
			tvWeightWarning.setVisibility(View.GONE);
			btnSend.setVisibility(View.VISIBLE);
		}
	}
}
