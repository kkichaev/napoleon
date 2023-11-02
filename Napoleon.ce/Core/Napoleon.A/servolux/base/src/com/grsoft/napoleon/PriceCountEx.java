package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import com.grsoft.dataobjects.ActionData;
import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DivisionPlan;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.CmpHistory;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.SalesHistory;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	boolean isAction = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cbPackets.setVisibility(View.GONE);
		
		if(document instanceof OrderImplEx)
			((OrderImplEx)document).setUpdateQtyHandler(this);
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected String getItemName(Price p) {
		PriceEx pe = (PriceEx)p;
		return super.getItemName(p) + " " + pe.thermalState + "/" + pe.packName;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		int divQty = 0;
		if(document instanceof OrderImplEx) {
			OrderEx oe = (OrderEx)document.getData();
			String where = "isMonthly=0 and date=" + Long.toString(Util.getDayStart(oe.date).getTime()) + " and firm='" + oe.firmCode + "'";
			GetDivisionPlanQty trvl = new GetDivisionPlanQty(price.getData().id);
			DataTraveler.travel(DivisionPlan.class, trvl, where);
			divQty = trvl.getQty();
			
			ActionData ad = ((CostStrategyEx)CostStrategy.defaultInstance).getActionData(document, price.getData().id);
			if(ad != null) {
				isAction = true;
				String text = String.format("<b>Акция цена %s</b><br/>Период действия скидки с %s по %s<br/><b>Период акции с %s по %s</b>",
						Util.IntToScaleStr(ad.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false),
						Util.simpleDateFormat.format(ad.start),
						Util.simpleDateFormat.format(ad.end),
						Util.simpleDateFormat.format(ad.startAction),
						Util.simpleDateFormat.format(ad.endAction)
						);
				
				((TextView)findViewById(R.id.tvActionInfo)).setText(Html.fromHtml(text));
			} else {
				isAction = false;
			}
		}
		
		TextView tv =(TextView)findViewById(R.id.tvDivisionQty);
		tv.setText(Util.IntToScaleStr(divQty, Consts.QTY_SCALE));
	}
	
	@Override
	protected void updateRest(boolean inPack, int rest, Editable txt) {
		
		String id = price.getData().id;
		
		RemnantItem ri = (RemnantItem) rdoc.findItem(id);
		int qty = (ri == null) ? 0 : ri.qty;
		
		super.updateRest(inPack, rest, txt);
		
		ri = (RemnantItem) rdoc.findItem(id);
		int newQty = (ri == null) ? 0 : ri.qty;
		if( newQty != qty) {
			OffTakeHistory.Item item = history.updateRest(id, newQty, null);
			WarehouseEx.autoOrder.put(id, item.qty);
		}
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		((OrderItemEx)item).hasAction = isAction ? 1 : 0;
	}
	
	@Override
	protected void createSimpleHistory(Price p, LinearLayout ll) {
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM", Locale.getDefault());
		SalesHistoryEx history = new SalesHistoryEx();
		history.create(document.getData().id, p.id, Features.SALES_FROM_ORDERS);
		
		ArrayList<Entry<Long, Integer>> saleHistory = new ArrayList<Entry<Long,Integer>>();
		saleHistory.addAll(history.entrySet());
		
		Collections.sort(saleHistory, new CmpHistory());
		
		for(Entry<Long, Integer> item : saleHistory) {
			TextView tvSaleItem = new TextView(this);
			tvSaleItem.setTextColor(getResources().getColor(R.color.black));
			String key = simpleDateFormat.format(new Date(item.getKey()));
			String value = Util.IntToScaleStr(item.getValue(), Consts.QTY_SCALE);
			if(history.isAction(item.getKey())) {
				tvSaleItem.setText(Html.fromHtml(
						String.format("%s<br><font color='red'>%s</font>", key, value)));
			} else {
				tvSaleItem.setText(Html.fromHtml(String.format("%s<br>%s", key, value)));
			}
			tvSaleItem.setLines(2);
			tvSaleItem.setPadding(5, 3, 5, 3);
			ll.addView(tvSaleItem);
		}

//		super.createSimpleHistory(p, ll);
	}
}

class SalesHistoryEx extends SalesHistory {
	private static final long serialVersionUID = 1L;
	
	Set<Long> actions = new HashSet<Long>();

	public boolean isAction(Long key) { return actions.contains(key);}
	protected void putItem(com.grsoft.napoleon.documents.Document<?> doc, OrderItem item, int weight) {
		if(((OrderItemEx)item).hasAction > 0)
			actions.add(Util.getDayStart(doc.getDate()).getTime());
		super.putItem(doc, item, weight);
	}
}

class GetDivisionPlanQty extends DataTraveler.Travel<DivisionPlan> {

	int qty = 0;
	String id;
	
	public GetDivisionPlanQty(String id) { this.id = id; }	
	
	@Override
	public boolean travel(DataTraveler<DivisionPlan> item) {
		for(AgentPlanItem ai : item.data.items) {
			if(ai.id.equals(id)) {
				qty = ai.qty;
				break;
			}
		}
		return false;
	}
	
	public int getQty() { return qty; }
}