package com.grsoft.napoleon;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	boolean canChangeCost = false;
	boolean showMinCost = false;
	int discount;
	int orgDiscount;
	int minCost;
	int priceCost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ConfigImpl cfg = new ConfigImpl();
		Config c = cfg.getData();
		
		c.key = "ћожетћен€ть÷ену";
		if(cfg.read()) {
			try {
				canChangeCost = (Integer.parseInt(c.value) > 0);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		if( canChangeCost ) {
			c.key = "ѕоказыватьћин÷ену";
			if(cfg.read()) {
				try {
					showMinCost = (Integer.parseInt(c.value) > 0);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		
		cfg.close();
		
		super.onCreate(savedInstanceState);
		findViewById(R.id.tvDiscount).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { changeDiscount(); }
		});
		
		if(document instanceof OrderImpl)
			((OrderImpl) document).setUpdateQtyHandler(this);
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	protected void changeDiscount() {
		if(document.isEditable() == false)
			return;
		
		InputNumberDlg.open(this, new InputNumber() {
			
			@Override public int getValue() { return discount; }
			
			@Override
			public void applayInput(int value, Object... params) {
				CostStrategyEx cs = (CostStrategyEx) CostStrategy.defaultInstance;
				int newCost = cs.costWithDiscount(priceCost, value, Consts.SUM_SCALE);
				if(newCost < minCost) {
					Toast.makeText(PriceCountEx.this, "÷ена меньше минимальной", Toast.LENGTH_SHORT).show();
				} else {
					discount = value; 
					updateDiscount();
					onChangeCost(newCost);
				}
			}
		}, Consts.SUM_SCALE, false, "¬ведите скидку");
	}

	@Override
	protected void refreshData() {
		super.refreshData();
		PriceEx p = (PriceEx) price.getData();
		minCost = p.minCost;
		
		((TextView)findViewById(R.id.tvPlanQty)).setText(Util.IntToScaleStr(p.planArrive, Consts.QTY_SCALE));
		
		TextView tv;
		CostStrategyEx cs = (CostStrategyEx) CostStrategy.defaultInstance;
		priceCost = cs.getCostWODiscount(p, document);		
		orgDiscount = cs.getDiscount(p, document);
		if( document instanceof OrderImpl) {
			OrderItemEx oie = (OrderItemEx) ((OrderImpl)document).findItem(p.id);
			if(oie != null) {
				discount = oie.discount;
				orgDiscount = oie.orgDiscount;
				onChangeCost(oie.cost);
			}
		}
		tv = (TextView)findViewById(R.id.tvOrgDiscount);
		tv.setText(Util.IntToScaleStr(orgDiscount, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		if( orgDiscount != 0)
			priceCost = cs.costWithDiscount(priceCost, orgDiscount, Consts.SUM_SCALE);

		tv = (TextView)findViewById(R.id.tvPriceWODiscount);
		tv.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		tv = (TextView)findViewById(R.id.tvMinCost);
		tv.setText(Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		findViewById(R.id.trMinCost).setVisibility(showMinCost ? View.VISIBLE : View.GONE);
		
		findViewById(R.id.trDiscount).setVisibility(canChangeCost ? View.VISIBLE : View.GONE);
		
		updateDiscount();
	}
	
	void updateDiscount() {
		String value = Util.IntToScaleStr(discount, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		if(canChangeCost)
			value = "<u><font color='blue'>" + value + "</font><u>";
		tv.setText(Html.fromHtml(value));
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx)item; 
		oie.discount = discount;
		oie.orgDiscount = orgDiscount;
	}
}
