package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceSklads;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceSkladsImpl;
import com.grsoft.dataobjects.impl.SkladsImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	int discount = 0;
	PriceSkladsImpl psk = new PriceSkladsImpl();
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.cbPackets).setVisibility(View.INVISIBLE);
		findViewById(R.id.trInPack).setVisibility(View.GONE);
		
		if(document instanceof OrderImpl)
			((OrderImpl)document).setUpdateQtyHandler(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		psk.close();
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		((CheckBox)findViewById(R.id.cbPackets)).setChecked(((PriceEx)price.getData()).isWeight > 0);
		
		TextView tv;
		String text;
		CostStrategyEx cs = (CostStrategyEx)CostStrategy.defaultInstance;
		Price p = price.getData();

		text = OrderHelper.getSumType(document);
		tv = (TextView)findViewById(R.id.tvCostType);
		tv.setText(text);
		
		tv = (TextView)findViewById(R.id.tvCostWODisc);
		tv.setText(Util.IntToScaleStr(cs.getCostWODiscount(p, document), Consts.SUM_SCALE, Util.DEC_DELIM, false));

		discount = cs.getDiscount(p, document);
		tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setText(Util.IntToScaleStr(discount, Consts.SUM_SCALE));
		
		text = "";
		PriceSklads ps = psk.getData();
		ps.id = p.id;
		ps.idwh = "";
		if( psk.read() ) {
			SkladsImpl si = new SkladsImpl();
			Sklads skl = si.getData();
			skl.id = ps.idwh;
			if(si.read())
				text = skl.name;
			si.close();
		}
		
		tv = (TextView)findViewById(R.id.tvSklad);
		tv.setText(text);
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oi = (OrderItemEx)item;
		oi.discount = discount;
		long cs = ((CostStrategyEx)CostStrategy.defaultInstance).getCostWODiscount(price.getData(), document);
		oi.sumWODiscount = cs * item.qty / Consts.QTY_SCALE; 
		oi.idwh = psk.getData().idwh;
	}
}
