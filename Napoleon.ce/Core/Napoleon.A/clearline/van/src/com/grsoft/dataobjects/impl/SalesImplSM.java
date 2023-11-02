package com.grsoft.dataobjects.impl;

public class SalesImplSM extends SalesImplEx {
//	@Override
//	public void editItem(long itemRowid, final Context context) {
//		if( !isEditable() )
//			return;
//
//		final PriceImpl priceImpl = new PriceImpl();
//		priceImpl.read(itemRowid);
//		priceImpl.close();
//		
//		InputNumberDlg.open(context, new InputNumber() {
//			
//			@Override
//			public void applayInput(int value, Object... params) {
//				
//				if (!isEditable())
//					return;
//				
//				value = value * priceImpl.getData().qtyInPack / Consts.QTY_SCALE;
//				CostStrategy cs = CostStrategy.getInstance(SalesImpl.class);
//				int cost = cs.getItemCost(priceImpl.getData(), SalesImplSM.this);
//				if (updateQty(priceImpl, value, cost, true) && context instanceof DataSetNotify)
//					((DataSetNotify)context).notifyDataSetChanged();
//								
//				SalesDoc.instance().refreshDocSum(data.id);
//			}
//
//			@Override
//			public int getValue() {				
//				OrderItem ri = (OrderItem)findItem(priceImpl.data.id);				
//				return ri == null ? 0 : ri.qty * Consts.QTY_SCALE / priceImpl.getData().qtyInPack;
//			}
//		});
//	}
}
