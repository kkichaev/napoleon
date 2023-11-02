package com.grsoft.napoleon;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.napoleon.util.QtyEditor;
import com.grsoft.util.Consts;

public class OrderDetailEx extends OrderDetail {
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// remove empty order
			if (doc.getData().items.size() == 0) {
				doc.delete();
//			} else if (!doc.isExported()) {
//				doc.editProperties(this);
			}
		}

		return super.onKeyDown(keyCode, event);
	};
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}
	
	void editQty(final OrderItem orderItem) {
		InputNumberDlg.open(OrderDetailEx.this, 
				new QtyEditor(doc, orderItem, (BaseAdapter) lvItems.getAdapter()),
				Consts.QTY_SCALE, true, getString(R.string.input_new_qty), true);
	}

//	@Override
//	protected ItemsOnClickListener createItemsCL() {
//		return new ItemsOnClickListener() {
//			@Override
//			public void onItemClick(final AdapterView<?> adapterView,
//					View arg1, int arg2, long arg3) {
//
//				if (!doc.isExported() || doc.isProceeded() && 
//						!((doc.getData().params & OrderProceededEx.APPROVED) ==  OrderProceededEx.APPROVED)) {
//					final OrderItem orderItem = (OrderItem) arg1.getTag();
//
//					InputNumberDlg.open(OrderDetailEx.this, new InputNumber() {
//
//						@Override
//						public int getValue() {
//							return orderItem.qty;
//						}
//
//						@Override
//						public boolean isInpack() {
//							return orderItem.inPack();
//						}
//
//						@Override
//						public boolean isPackCanChange() {
//							boolean result = true;
//							PriceImpl priceImpl = new PriceImpl();
//							priceImpl.getData().id = orderItem.id;
//
//							if (priceImpl.read()) {
//								result = ((PriceEx) price.getData()).pack == 0;
//							}
//
//							priceImpl.close();
//
//							return result;
//						}
//
//						@Override
//						public void applayInput(final int value,
//								Object... params) {
//							PriceImpl priceImpl = new PriceImpl();
//							priceImpl.getData().id = orderItem.id;
//
//							if (priceImpl.read()) {
//								if (doc.updateQty(priceImpl, value,
//										orderItem.cost, (Boolean) params[0]))
//									((BaseAdapter) adapterView.getAdapter())
//											.notifyDataSetChanged();
//							}
//
//							priceImpl.close();
//							
//							doc.setExported(false);
//							doc.unsetProceeded();
//							doc.write();
//							doc.close();
//						}
//					}, Consts.QTY_SCALE, true,
//							getString(R.string.input_new_qty), true);
//				}
//			}
//		};
//	}
//
//	@Override
//	public OnItemLongClickListener createLongItemsCL() {
//		return new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				editItem((OrderItem) arg1.getTag());
//				return false;
//			}
//		};
//	}
	
	OnClickListener opencard = new OnClickListener() {		
		@Override
		public void onClick(View arg0) {
			OrderItem item = (OrderItem)arg0.getTag();
			editItem(item);
		}
	};
	
	OnClickListener openqty = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (!doc.isExported() || doc.isProceeded() && !((doc.getData().params & OrderProceededEx.APPROVED) ==  OrderProceededEx.APPROVED)) {			
				OrderItem item = (OrderItem)arg0.getTag();
				editQty(item);
			}
		}
	};
	
	class Adapter extends OrderItemsAdapter {
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = super.getView(arg0, arg1, arg2);
			
			OrderItem item = (OrderItem) getItem(arg0);
			TextView tv;
			tv = (TextView)v.findViewById(R.id.tvName);
			if( tv != null ) {
				tv.setTag(item);
				tv.setOnClickListener(opencard);
			}
			
			tv = (TextView)v.findViewById(R.id.tvQty);
			if( tv != null ) {
				tv.setTag(item);
				tv.setOnClickListener(openqty);
			}

			tv = (TextView)v.findViewById(R.id.tvSum);
			if( tv != null ) {
				tv.setTag(item);
				tv.setOnClickListener(openqty);
			}
			return v;
		}
	}
}
