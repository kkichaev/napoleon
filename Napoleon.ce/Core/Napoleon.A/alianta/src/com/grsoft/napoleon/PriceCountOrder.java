package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.grsoft.dataobjects.ActionFindData;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrderWhItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Store;
import com.grsoft.dataobjects.WhData;
import com.grsoft.dataobjects.WhDataItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.ActionHelper.ActionData;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

public class PriceCountOrder extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	ActionFindData action = null;
	Adapter adapter = new Adapter();
	boolean actionAssigned = false; 
//	String actionCode = "";
	List<Item> items = new ArrayList<Item>();
	OrgEx org;
	
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		if(doc.getRowid() == ExtrasConst.INVALID_ROWID) {
			PriceCount.open(context, priceRoid, doc);
			return;
		}
		Intent i = new Intent(context, PriceCountOrder.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}

	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((OrderImplEx)document).setUpdateQtyHandler(this);
	}

	@Override
	protected void refreshData() {
		if(org == null) {
			if(document == null)
				org = new OrgEx();
			else {
				OrgImpl oi = new OrgImpl();
				org = (OrgEx) oi.getData();
				org.id = document.getId();
				oi.read();
				oi.close();
			}
		}

		super.refreshData();
		
		PriceEx p = (PriceEx) price.getData();

		((TextView)findViewById(R.id.tvInfo)).setText(Html.fromHtml(p.info));

		OrderEx oe = (OrderEx) document.getData();
		OrderItemEx item = (OrderItemEx) ((OrderImplEx)document).findItem(p.id);
		
		actionAssigned = (oe.haveAction != 0);
//		if(item != null && item.action.length() > 0) {
//			actionAssigned = true;
//			actionCode = item.action;
//		}
		
		List<ActionHelper.ActionData> actions = ActionHelper.getActions(document.getId(), document.getDate(), p.id);
		if(actions == null || actions.size() == 0) {
			findViewById(R.id.llActions).setVisibility(View.GONE);
		} else {
			ActionAdapter aa = new ActionAdapter(actions);
			ListView lva = (ListView)findViewById(R.id.lvActions);
			lva.setAdapter(aa);
		}		
//		View av = findViewById(R.id.llAction);

		//		action = Action.getAction(p.id, org.cfo);
//		if(action != null && action.item != null) {
//			av.setVisibility(View.VISIBLE);
//			TextView tv;
//			tv = (TextView)findViewById(R.id.tvActionText);
//			tv.setText(action.item.text);
//			
//			tv = (TextView)findViewById(R.id.tvActionDiscount);
//			tv.setText(Util.IntToScaleStr(action.item.discount, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " %");
//			
//			CheckBox cb = (CheckBox)findViewById(R.id.cbAction);
//			cb.setChecked(item != null && item.action.equals(action.action.id));
//		} else
//			av.setVisibility(View.INVISIBLE);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);

		adapter.refresh(item);
		updateSumTextView();
	}
	
	class ActionAdapter extends BaseAdapter {
		
		List<ActionHelper.ActionData> actions;
		public ActionAdapter(List<ActionHelper.ActionData> actions) {
			this.actions = actions;
		}

		@Override public int getCount() { return actions == null ? 0 : actions.size(); }
		@Override public Object getItem(int arg0) { return actions.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(PriceCountOrder.this, R.layout.price_action_row, null);
			}
			final ActionHelper.ActionData item = (ActionData) getItem(pos);
			CheckBox cb = (CheckBox)view.findViewById(R.id.cbApplyAction);
			if(item.isManual > 0) {
				cb.setVisibility(View.VISIBLE);
				cb.setChecked(actionAssigned);
				cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override public void onCheckedChanged(CompoundButton arg0, boolean arg1) { 
						actionAssigned = arg1; 
//						actionCode = item.id;
					}
				});
			} else {
				cb.setVisibility(View.INVISIBLE);
			}
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvDiscount);
			int dsum = CostStrategy.costWithDiscount(priceVal, item.discount, Consts.SUM_SCALE);
			String text =Util.IntToScaleStr(item.discount, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " %";
			text += "<br/><i>" +  Util.IntToScaleStr(dsum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</i>";
			tv.setText(Html.fromHtml(text));
			
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(item.makeText());
			return view;
		}
		
	}
	
	class Adapter extends BaseAdapter {
		
		Store newSklad(String id) {
			Store s = new Store();
			s.id = id;
			s.name = "Склад с кодом '" + s.id + "'";

			return s;
		}
		
		Item findItem(OrderWhItem item) {
			for(Item i : items) {
				if(i.store.id.equals(item.id) && i.year == item.year)
					return i;
			}
			
			return null;
		}
		
		public Adapter() {}
		
		public void refresh(OrderItemEx item) {
			final HashMap<String, Store> stores = Store.load();
						
			items.clear();
			
			PriceEx p = (PriceEx) price.getData();
			
			stores.put(Store.MAIN_WH_ID, Store.mainStore());
			
			DataTraveler.travel(WhData.class, new DataTraveler.Travel<WhData>() {

				@Override
				public boolean travel(DataTraveler<WhData> item) {
					Item oi = new Item();
					if(item.data.idStore.equals(Store.MAIN_WH_ID)) {
						oi.cost = priceVal;
					} else {
						for(WhDataItem whi : item.data.items) {
							if(whi.cfo.equals(org.cfo))
								oi.cost = whi.cost;
						}
					}
					oi.qty = item.data.qty;
					oi.reserv = item.data.rezerv;
					oi.store = stores.get(item.data.idStore);
					oi.year = item.data.year;
					
					if(oi.store == null) {
						Store s = newSklad(item.data.idStore);
						stores.put(s.id, s);
						oi.store = s;
					}
					
					if(item.data.idStore.equals(Store.MAIN_WH_ID)) {
						items.add(0, oi);
					} else {
						items.add(oi);
					}
					return true;
				}
			}, "id='" + p.id + "'");
			
			if( item != null ) {
				for(OrderWhItem owi : item.whData) {
					Item fi = findItem(owi);
					if(fi == null) {
						fi = new Item();
						fi.store = newSklad(owi.id);
						fi.qty = 0;
						fi.cost = owi.cost;
						fi.year = owi.year;
						items.add(fi);
					}
					
					fi.pack = owi.pack;
					fi.setOrderQty(qtyInPack, owi.qty);
				}
			}
			
			if(items.size() == 0) {
				Item i = new Item();
				i.store = Store.mainStore();
				i.cost = priceVal;
				i.qty = p.qty;
				i.reserv = p.rezerv;
				items.add(i);				
			}
			notifyDataSetChanged();
		}

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(PriceCountOrder.this, R.layout.price_count_row, null);
			
			Item item = items.get(arg0);
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvText);
			tv.setText(item.store.name);
			
			tv = (TextView)view.findViewById(R.id.tvCost);
			tv.setText(Util.IntToScaleStr(item.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(item.orderQty, Consts.QTY_SCALE));
			tv.setTag(item);
			tv.setOnClickListener(setQty);
			
			String text = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
			text += " / " + Util.IntToScaleStr(item.reserv, Consts.QTY_SCALE);
			tv = (TextView)view.findViewById(R.id.tvMaxQty);
			tv.setText(text);
			
//			tv = (TextView)view.findViewById(R.id.tvReservQty);
//			tv.setText(Util.IntToScaleStr(item.reserv, Consts.QTY_SCALE));
			
			tv = (TextView)view.findViewById(R.id.tvTotalQty);
			tv.setText(Util.IntToScaleStr(item.reserv + item.qty, Consts.QTY_SCALE));
			
			tv = (TextView)view.findViewById(R.id.tvYear);
			tv.setText(item.year == 0 ? "" : "год урожая " + Integer.toString(item.year));
			
			CheckBox cb = (CheckBox)view.findViewById(R.id.cbPack);
			cb.setTag(item);
			cb.setChecked(item.pack > 0);
			cb.setOnCheckedChangeListener(packListener);
			return view;
		}
		
	}
	
	CompoundButton.OnCheckedChangeListener packListener = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Item item = (Item)buttonView.getTag();
			item.pack = isChecked ? 1 : 0;
			updateSumTextView();
		}
	};
	
	protected long getSumValue() {
		long sum = 0;
		for(Item i : items) {
			sum += (long)i.cost * i.getOrderQty(qtyInPack) / Consts.QTY_SCALE;
		}
		return sum;
	}
	
	View.OnClickListener setQty = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Item i = (Item)v.getTag();
			InputNumberDlg.open(v.getContext(), new InputNumber() {
				
				@Override public int getValue() { return i.orderQty; }
				
				@Override
				public void applayInput(int value, Object... params) {
//					if(value > i.qty) {
//						return;
//					}
					i.orderQty = value;
					updateSumTextView();
					adapter.notifyDataSetChanged();
				}
				
				public boolean isInpack() {
					return i.pack != 0;
				}
				
			}, Consts.QTY_SCALE, true, "Введите количество");
		}
	};

	protected boolean updateOrder() {
		// update qtyItems before
		qtyItems = 0;
		for(Item i : items) {
			if(i.orderQty > 0) {
				qtyItems += i.getOrderQty(qtyInPack);
			}
		}
		return super.updateOrder();		
	}
	
	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oe = (OrderItemEx)item;
		
		if(oe.uid.length() == 0)
			oe.uid = UUID.randomUUID().toString().replace("-", "");
		
//		CheckBox cb = (CheckBox)findViewById(R.id.cbAction);
//		oe.action = (cb.isChecked()) ? action.action.id : "";
		
		((OrderEx)order).haveAction = actionAssigned ? 1 : 0;
//		oe.action = (actionAssigned) ? actionCode : "";
		
		int totQty = 0;
		long totSum = 0;
		
		HashMap<String, Integer> chQty = new HashMap<String, Integer>();
		for(OrderWhItem owh : oe.whData)
			chQty.put(owh.id, owh.qty);
		oe.whData.clear();
		for(Item i : items) {
			if(i.orderQty > 0) {
				int oq = i.getOrderQty(qtyInPack);
				totQty += oq;
				totSum += (long)oq * i.cost / Consts.QTY_SCALE;
				
				OrderWhItem newI = new OrderWhItem();
				newI.id = i.store.id;
				newI.cost = i.cost;
				newI.pack = i.pack;
				newI.qty = oq;
				newI.year = i.year;
				
				oe.whData.add(newI);
				
				Integer chq = chQty.get(newI.id);
				if(chq == null)
					chq = 0;
				chq -= newI.qty;
				chQty.put(newI.id, chq);
			}
		}
		
		WhData.updateQty(price.getData().id, chQty);
		item.qty = totQty;
		item.cost = totQty == 0 ? 0 : (int) (totSum * Consts.QTY_SCALE / totQty);
	}
}

class Item {
	public Store store;
	public int qty;
	public int cost;
	public int orderQty;
	public int pack;
	public int reserv;
	public int year;
	
	public int getOrderQty(int inPack) {
		return pack == 0 ? orderQty : (int)((long)orderQty * inPack / Consts.QTY_SCALE);
	}
	
	public void setOrderQty(int inPack, int totQty) {
		if(pack == 0)
			orderQty = totQty;
		else {
			orderQty = (int)((long)totQty * Consts.QTY_SCALE / inPack);
		}
	}
}
