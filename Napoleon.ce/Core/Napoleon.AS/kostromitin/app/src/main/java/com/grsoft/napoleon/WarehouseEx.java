package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {
	static int isLukoil = -1;

	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	@Override protected BaseAdapter createListAdapter() { 
		int islk = -1;
//		int islk = 0;
//		if(document instanceof OrderImplEx) {
//			if(document.getRowid() == ExtrasConst.INVALID_ROWID) {
//				islk = -1;
//			} else {
//				OrderEx oe = (OrderEx)document.getData();
//				if((oe.params & ParamState.ofCash) != 0)
//					islk = -1;
//				else {
//					if(oe.lukoil > 0)
//						islk = 1;
//				}
//			}
//		}
		if( islk != isLukoil ) {
			isLukoil = islk;
			FoldersAdapter.resetCache();
		}
		return new Adapter(this, islk); 
	}
	
//	@Override
//	protected void setTextColumnValue(TextView textView, int type, Price price) {
//		if( type == COLUMN_QTY_WH) {
//			PriceEx pe = (PriceEx) price;
//			String text = Util.IntToScaleStr(pe.qty, Consts.QTY_SCALE) + " / " + Util.IntToScaleStr(pe.qty2, Consts.QTY_SCALE);
//			textView.setText(text);
//			return;
//		}
//		super.setTextColumnValue(textView, type, price);
//	}
	
	@Override protected Filter createZeroPositionFilter() { return null; }
	
	class Adapter extends FoldersAdapter {
		
		int isLukoil;

		public Adapter(WarehouseManager warehouse, int isLukoil) {
			super(warehouse);
			this.isLukoil = isLukoil;
		}
		
		@Override
		public String getWhereStr() {
			String ret = super.getWhereStr(); 
			if(isLukoil >= 0) {
				if(ret.length() > 0)
					ret += " AND ";
				ret += "isLukoil = " + Integer.toString(isLukoil);
			}
			return ret;
		}
		
		@Override
		protected void postUpdateView(View v, TreeNode node) {
			if(node instanceof PriceTreeNode && v != null){
				PriceEx pe = (PriceEx)price.getData();
				TextView tv = (TextView)v.findViewById(R.id.tvMark);
				tv.setBackgroundColor(pe.getPriorityColor());
			}
		}
	}
}
