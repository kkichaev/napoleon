package com.grsoft.napoleon;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PresentList;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.AgentSalesPlanImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}
	
	@Override
	protected void onResume() {
		if( !starting )
			AgentSalesPlanImpl.refreshDocCache();
		super.onResume();
	}

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);
		Price p = price.getData();

		boolean isMinLines = linesController.isMinLines();
		TextView tvQty = (TextView) v.findViewById(R.id.tvQty);

		SalesDataItem sdi = AgentSalesPlanImpl.getItemQty(price.getData().id); 
		int qty = -1;
		OrderItem oi = null;
		if(document instanceof OrderImpl) {
			oi = (OrderItem) ((OrderImpl) document).findItem(p.id);
			qty = (oi != null) ? (int)((long)oi.qty * Consts.QTY_SCALE / p.qtyInPack) : 0;
		}
		if (qty < 0)
			tvQty.setVisibility(View.GONE);
		else {
			tvQty.setVisibility(View.VISIBLE);
			String text = Util.IntToScaleStr(qty, Consts.QTY_SCALE);

			TextView tvPlanQty = (TextView)v.findViewById(R.id.tvPlanQty);
			tvPlanQty.setVisibility(View.GONE);
			if( sdi != null ) {
				if( isMinLines == false) {
					text += "\n" + Util.IntToScaleStr(sdi.qty, Consts.QTY_SCALE);
				} else {
					tvPlanQty.setVisibility(View.VISIBLE);
					tvPlanQty.setText(Util.IntToScaleStr(sdi.qty, Consts.QTY_SCALE));
				}
			}
			tvQty.setText(text);
		}

		View tv = v.findViewById(R.id.tvPriceItemName);
		if( sdi != null) {
			tv.setBackgroundResource((oi == null) ? R.drawable.plan_selector : R.drawable.plan_gray_selector);
		} else {
			tv.setBackgroundColor(Color.TRANSPARENT);
		}
		return v;
	}

	@Override
	protected boolean hasPresentation() {
		int result = 0;

		try {
			DbWriter.checkDBTable(DbObject.getDataType(Present.class));
			DbWriter.checkDBTable(DbObject.getDataType(PresentList.class));
			SQLiteDatabase db = DataBaseManager.getDataBase();
			
			android.database.Cursor c = db.rawQuery(
					"SELECT COUNT(*) FROM "
							+ DataObjectInfo.getInstance().getTableName(
									PresentList.class), null);

			if (c.moveToFirst())
				result = c.getInt(0);

			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result > 0;
	}
	
	@Override
	protected void openPresentation() {
		PresentationView.open(this, docRowId);
		finish();
	}
}
