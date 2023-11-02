package com.grsoft.napoleon;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	int costWODiscount;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void refreshData() {
		cbPackets.setEnabled(false);
		super.refreshData();
		
		int bonusVisible = View.GONE;
		if( document instanceof OrderImpl ) {

			HashMap<String, BonusDef> bonuses = BonusDefImpl.getActiveBonuses(document.getDate());
			BonusDef bd = bonuses.get(price.getData().id);
			if( bd != null ) {
				TextView tvBonus = (TextView)findViewById(R.id.tvBonus);
				String text = Util.IntToScaleStr(bd.qty, Consts.QTY_SCALE);
				tvBonus.setText(text);
				bonusVisible = View.VISIBLE;				
			}
			
			OrderImplEx doc = (OrderImplEx)document;
			int vsbl = View.GONE;
			if(((OrderEx)document.getData()).canDiscount > 0) {
				vsbl = View.VISIBLE;
				
				doc.setUpdateQtyHandler(this);
				costWODiscount = priceVal;
				OrderItemEx oie = (OrderItemEx) doc.findItem(price.getData().id);
				if( oie != null && oie.discount != 0)
					onChangeCost(oie.cost);
				
				List<DiscountItem> values = new ArrayList<DiscountItem>();				
				int selected = loadDiscounts(values, (oie != null) ? oie.discount : 0, (PriceEx)price.getData());

				Spinner sp = (Spinner) findViewById(R.id.spDiscount);
				ArrayAdapter<DiscountItem> aa = new ArrayAdapter<DiscountItem>(this, R.layout.simple_spinner_layout, values);
				aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
				sp.setAdapter(aa);
				if( selected >= 0)
					sp.setSelection(selected);
				sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						DiscountItem di = (DiscountItem)arg0.getSelectedItem();
						if(di != null)
							updateCostFromDiscount(di.getDiscoount());
					}

					@Override public void onNothingSelected(AdapterView<?> arg0) {}
				});
				
			}
			findViewById(R.id.tvDiscount).setVisibility(vsbl);
			findViewById(R.id.spDiscount).setVisibility(vsbl);			
		}
		findViewById(R.id.trBonus).setVisibility(bonusVisible);
	}

	protected void updateCostFromDiscount(int discoount) {
		int newCost = CostStrategy.costWithDiscount(costWODiscount, discoount, Consts.SUM_SCALE);
		onChangeCost(newCost);
	}

	private int loadDiscounts(List<DiscountItem> values, int discount, PriceEx data) {
		int sel = -1;
		int sp = 0, delta = data.dscQuant * 10;
		int ep = data.discount;
		if( delta > 0 ) {
			for( ; sp <= ep; sp += delta ) {
				if( sp == discount )
					sel = values.size();
				values.add(new DiscountItem(sp));
			}
		}
		return sel;
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		if(costWODiscount != 0) {
			OrderItemEx oie =  (OrderItemEx)item;
			oie.costWODiscount = costWODiscount;
			oie.cost = priceVal;
			Spinner sp = (Spinner) findViewById(R.id.spDiscount);
			DiscountItem di = (DiscountItem)sp.getSelectedItem();
			if( di != null )
				oie.discount = di.getDiscoount();
		}
	}
	
}

class DiscountItem {
	int discount;
	
	public DiscountItem(int dsc) {
		this.discount = dsc / 10;
	}
	
	public int getDiscoount() { return discount * 10; }
	
	@Override
	public String toString() {
		return Util.IntToScaleStr(discount, 10, Util.DEC_DELIM, false) + " %";
	}
}
