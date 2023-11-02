package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount implements UpdateQtyHandler{
	TextView tvBaseCost;
	long basePrice = 0;
	View trCostWithDiscount;
	
	@Override protected int getContentViewId() { return R.layout.pricecount_newex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		trCostWithDiscount = findViewById(R.id.trCostWithDiscount);
		basePrice = ((CostStrategyEx)CostStrategy.defaultInstance).getBasePrice(price.getData(), document); 
		tvBaseCost = (TextView)findViewById(R.id.tvBaseCost);
		tvBaseCost.setText(Util.IntToScaleStr(basePrice, Consts.SUM_SCALE));
		
		
		if( document instanceof OrderImpl )
			((OrderImpl)document).setUpdateQtyHandler(this);
		
		int disc = 0;
		OrgImpl org = new OrgImpl();

		if(document != null && org.read("id", document.getId()))
			disc = ((OrgEx)org.getData()).disc;
		;

		disc = Math.min(disc, ((PriceEx)price.getData()).disc);
		trCostWithDiscount.setVisibility(disc > 0 ? View.VISIBLE : View.GONE);

		btnOK.setOnClickListener(okClick);
	}

	OKClick okClick = new OKClick();

	class OKClick extends BtnOKClickListenet{
		@Override
		public void onClick(View v) {
			if (((PriceEx)price.getData()).expdate == 1)
				showDialog(R.id.exp_date_dlg);
			else
				super.onClick(v);
		}

		public void baseClick(View v){
			super.onClick(v);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id ==R.id.exp_date_dlg)
			return createDateDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createDateDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setMessage(getString(R.string.exp_date_text, price.getData().name));
		builder.setPositiveButton(R.string.cancel, (d,v)->finish());
		builder.setNegativeButton(R.string.to_add, (d,v)->okClick.baseClick(btnOK));
		return builder.create();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx ie = (OrderItemEx)item;
		ie.disc = basePrice - priceVal;
	}
}
