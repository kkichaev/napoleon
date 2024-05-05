package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Actionable;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderAction;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.SimpleItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	private int client_cost;
	int minCost = 0;
	Boolean salesDoc = null;
	ActionAdapter actionAdapter;

	List<OrderAction> actions = new ArrayList<>();
	String selActId = "";

	ActionHelper actionHelper = new ActionHelper();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(isSalesDoc()) {
			((OrderImplBase) document).setUpdateQtyHandler(this);
		}
	}

	@Override
	protected boolean getStartInPack() {
		return ((PriceEx)price.getData()).unitType == PriceEx.UNIT_PACK;
	}

	@Override
	protected boolean canChangeCost() {
		if(isSalesDoc()) {
			minCost = ((PriceEx)price.getData()).minCost;
			return minCost != 0;
		}
		return super.canChangeCost();
	}

	@Override
	protected void onChangeCost(int newCost) {
		if(newCost < minCost) {
			Toast.makeText(this, "Цена ниже минимальной", Toast.LENGTH_LONG).show();
			return;
		}
		super.onChangeCost(newCost);
	}

	boolean isSalesDoc() {
		if(salesDoc == null)
			salesDoc = (document instanceof OrderImplEx || document instanceof SalesImplEx);
		return salesDoc;
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		PriceEx pe = (PriceEx)price.getData();
		cbPackets.setEnabled(pe.unitType == PriceEx.UNIT_NONE);
		if(isSalesDoc()) {
			client_cost = (int)CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass()).getItemCost(price.getData(), (Document<?>) document);;

			View v = findViewById(R.id.trMinCost);
			if(minCost != 0) {
				v.setVisibility(View.VISIBLE);
				OrderItem sip = (OrderItem)((OrderImplBase<?>) document).findItem(price.getData().id);
				if(sip != null && sip.cost != priceVal) {
					super.onChangeCost(sip.cost);
				}
			} else {
				v.setVisibility(View.GONE);
			}
			((TextView)findViewById(R.id.tvMinCost)).setText(Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

			actions.clear();
			actions = OrderAction.getActions(pe, document.getId());
			DataObject di = ((OrderImplBase)document).findItem(pe.id);
			selActId = "";
			if(di instanceof OrderItemEx) {
				selActId = ((OrderItemEx) di).action;
			} else if(di instanceof SalesItemEx) {
				selActId = ((SalesItemEx) di).action;
			}

			actionAdapter = new ActionAdapter(this, actions, selActId,
					(Actionable) document, actionId -> selActId = actionId);
			((ListView)findViewById(R.id.actions)).setAdapter(actionAdapter);
		}
	}

	protected boolean isInputValid(Runnable r) { return priceVal > 0; }
	
	@Override
	protected void invalidInputValueHandler() { Toast.makeText(this, R.string.empty_price_error, Toast.LENGTH_SHORT).show(); }
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		if(item instanceof SalesItemEx) {
			SalesItemEx se = (SalesItemEx) item;
			se.costWOD = client_cost;
		} else {
			OrderItemEx oe = (OrderItemEx) item;
			oe.costWOD = client_cost;
		}
		item.countSum();
	}

	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		if(actionAdapter != null)
			actionHelper.applyActions((Actionable) document, actionAdapter.selectedAction(), (PriceEx) price.getData());
		return ret;
	}
}
