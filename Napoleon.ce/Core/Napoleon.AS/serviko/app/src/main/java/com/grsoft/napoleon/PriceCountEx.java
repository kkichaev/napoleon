package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.ItemActionData;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ServikoAction;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	int orgCost = 0;
	int priceCost = 0;
	List<CheckBox> actionCB = new ArrayList<CheckBox>();
	ItemActionData iad;
	
	@Override protected int getStartValue() { return 0; }
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(document instanceof OrderImpl)
			((OrderImpl)document).setUpdateQtyHandler(this);
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(document instanceof OrderImplEx && ((OrderEx)document.getData()).retDoc == 0) {
			int qty = qtyItems;
			qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
			int quant = ((PriceEx) price.getData()).quant * Consts.QTY_SCALE;
			if (quant != 0 && (qty % quant != 0)) {
				Toast.makeText(this, "Заказ должен быть кратен " + Integer.toString(quant / Consts.QTY_SCALE), Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return super.isInputValid(r);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		if(document instanceof OrderImpl) {
			Price p = price.getData(); 
			orgCost = (int)((CostStrategyEx)CostStrategy.defaultInstance).getOrgCost(p, document);
			priceCost = (int)((CostStrategyEx)CostStrategy.defaultInstance).getPriceCost(p, document);
			iad = ((CostStrategyEx)CostStrategy.defaultInstance).getActionData(p, document);
			if(iad != null && iad.actions.size() > 0) {
				String actionID = "";
				OrderItemEx oie = (OrderItemEx) ((OrderImpl)document).findItem(p.id);
				if(oie != null) actionID = oie.promoId;
				LinearLayout ll =(LinearLayout) findViewById(R.id.llActions);
				
				int idx = 1;
				
				for(ServikoAction sab : iad.actions) {
					if(sab.isBaseAction())
						continue;
					
					LinearLayout.LayoutParams lp  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					View v = View.inflate(this, R.layout.action_row, null);
					ll.addView(v, lp);
					
					CheckBox cb = (CheckBox) v.findViewById(R.id.cbAction);
					if(document.isEditable()) {
						cb.setOnCheckedChangeListener(actionChecked);
						v.setOnClickListener(clickView);
					} else {
						cb.setEnabled(false);
					}
					cb.setTag(sab);
					if(actionID.equals(sab.id))
						cb.setChecked(true);
					
					int actCost = iad.count(priceCost, sab.id).cost;
					TextView tv = (TextView)v.findViewById(R.id.tvInfo);
					tv.setText(Html.fromHtml(sab.actionText(actCost)));
					
					actionCB.add(cb);
					v.setBackgroundResource( ((idx % 2) == 1) ? R.drawable.list_selector : R.drawable.even_row_selector);
					idx++;
				}
			}
		}
	}
	
	View.OnClickListener clickView = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			CheckBox cb = (CheckBox) arg0.findViewById(R.id.cbAction);
			cb.setChecked(!cb.isChecked());
		}
	};
	
	boolean doCheck = false; 
	CompoundButton.OnCheckedChangeListener actionChecked = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if(doCheck)
				return;
			
			doCheck = true;
			String actionId = "";
			if(arg1) {
				ServikoAction sab = (ServikoAction) arg0.getTag();
				actionId = sab.id;
				
				for(CheckBox cb : actionCB) {
					if(cb != arg0)
						cb.setChecked(false);
				}
			}
			int newCost = iad.count(priceCost, actionId).cost;
			if(document instanceof OrderImpl)
				newCost = (int)CostStrategy.costWithDiscount(newCost, -((OrderEx)document.getData()).nac, Consts.SUM_SCALE);
			
			onChangeCost(newCost);
			doCheck = false;
		}
	};

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		String actionId = "";
		for(CheckBox cb : actionCB) {
			if(cb.isChecked()) {
				ServikoAction sab = (ServikoAction) cb.getTag();
				actionId = sab.id;
				break;
			}
		}
		
		((OrderItemEx)item).promoId = actionId;
	}
}
