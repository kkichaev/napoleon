package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrderQtyItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	int dsc;
	int minCost;
	int priceCost;
	
	Boolean canChangeCost = null;

	List<Item> skladItems = new ArrayList<>();
	Adapter adapter;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected boolean canChangeCost() {
		if(!(document != null && document instanceof OrderImplBase<?>) )
			return false;
		
		if(canChangeCost == null) {
			canChangeCost = false;
	        ConfigImpl config = new ConfigImpl();
			config.getData().key = "МожноИзменятьЦену";
			try {
				if (config.read() && Integer.parseInt(config.getData().value) == 1)
					canChangeCost = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			config.close();
		}
		return canChangeCost;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		if( canChangeCost() ) {
			findViewById(R.id.trDiscount).setVisibility(View.VISIBLE);
			TextView tv = (TextView)findViewById(R.id.tvPrice);
			updateCost();
	
			PriceEx p = (PriceEx) price.getData();
			minCost = p.minCost;
	
			int sumType = document != null ? document.getSumType() : 0;
			priceCost = (p.cost.size() > sumType && sumType >= 0) ? 
					p.cost.get(sumType).cost : 0;			
	
			if(priceCost != 0)
				dsc = (int)(1000 - (long)priceVal * 1000 / priceCost);
			else
				dsc = 0;
			
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					CostInputDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
						@Override public int getValue() { return priceVal; }		
					}, minCost); 
				}
			});
			
			
			tv = (TextView)findViewById(R.id.tvDiscount);
			updateNac();
			
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public void applayInput(int value, Object... params) { onNacChange(value); }
						@Override public int getValue() { return dsc; }		
					}, 10, false, "Наценка"); 
				}
			});
		}
	}

	@Override
	protected void postOnCreate() {
		super.postOnCreate();

		if(document instanceof OrderImpl) {
			loadSklads();
		}
	}

	private void loadSklads() {
		((OrderImpl)document).setUpdateQtyHandler(this);

		OrgImpl oi = new OrgImpl();
		oi.read("id", document.getId());

		Map<String, KeyValue> sklads = new HashMap<>();
		ConfigImpl ci = new ConfigImpl();
		ci.read("key", "Склады");

		List<KeyValue> values = new ArrayList<>();
		DialogHelper.makeListWithKey(ci.getData().value, values, "");
		for(KeyValue kv : values) {
			sklads.put(kv.key.toString(), kv);
		}

		for(String id : ((OrgEx)oi.getData()).whCodes.split(",")) {
			KeyValue val = sklads.get(id);
			if(val != null) {
				Item i = new Item();
				i.whCode = id;
				i.name = val.value.toString();
				i.whIndex = values.indexOf(val);

				skladItems.add(i);
			}
		}
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		if(skladItems.size() > 0) {
			for(Item i : skladItems) {
				i.qty = 0;
				i.pack = 0;
			}
			OrderItemEx oie = (OrderItemEx) ((OrderImpl) document).findItem(price.getData().id);
			if (oie != null) {
				for(OrderQtyItem oqi : oie.qtys) {
					for(Item i : skladItems) {
						if(oqi.whCode.equals(i.whCode)) {
							i.setOrderQty(i.pack, oqi.qty);
							i.pack = oqi.pack;
						}
					}
				}
			}

			ListView lv = findViewById(R.id.lvItems);
			adapter = new Adapter();
			lv.setAdapter(adapter);
		}
	}

	void updateNac() {
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(dsc, 10, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}
	
	@Override
	protected void onChangeCost( int newCost ) {
		priceVal = newCost;
		Price p = price.getData();

		if( p.cost.get(0).cost != 0 )
			dsc = (int)(1000 - (long)priceVal * 1000 / priceCost);

		updateCost();
		updateSumTextView();
		updateNac();
	}
	
	void onNacChange( int newNac ) {
		dsc = newNac;
		priceVal = (int)(((long)priceCost * (1000 - dsc)) / 1000);
		
		updateCost();
		updateSumTextView();
		updateNac();
	}

	@Override
	protected long getSumValue() {
		long sum = 0;
		for(Item i : skladItems) {
			sum += (long)priceVal * i.getOrderQty(i.pack) / Consts.QTY_SCALE;
		}
		return sum;
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oid = (OrderItemEx) item;
		oid.qtys.clear();
		item.qty = 0;
		for(Item i : skladItems) {
			if(i.qty != 0) {
				OrderQtyItem oqi = new OrderQtyItem();
				oqi.whCode = i.whCode;
				oqi.pack = i.pack;
				oqi.qty = i.getOrderQty(i.pack);

				item.qty += oqi.qty;

				oid.qtys.add(oqi);
			}
		}
	}

	@Override
	protected int fixOrderQty(boolean inPack, int qty, Price price) {
		int res = 0;

		for(Item i : skladItems) {
			if (i.qty != 0) {
				res += i.getOrderQty(i.pack);
			}
		}

		return res;
	}

	class Adapter extends BaseAdapter {

		@Override public int getCount() { return skladItems.size();  }
		@Override public Object getItem(int position) { return skladItems.get(position); }
		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null) {
				view = View.inflate(PriceCountEx.this, R.layout.price_qty_row, null);
			}

			Price pe = price.getData();

			Item i = (Item) getItem(position);
			TextView tv;

			tv = view.findViewById(R.id.tvName);
			tv.setText(i.name);

			int whQty = (i.whIndex == 0 || i.whIndex > pe.whQty.size()) ? pe.qty : pe.whQty.get(i.whIndex-1).qty;
			tv = view.findViewById(R.id.tvWhQty);
			tv.setText(Util.IntToScaleStr(whQty, Consts.QTY_SCALE));

			CheckBox cb = view.findViewById(R.id.cbPack);
			cb.setChecked(i.pack > 0);
			cb.setTag(i);
			cb.setOnCheckedChangeListener(packListener);

			tv = view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(i.qty, Consts.QTY_SCALE));
			tv.setTag(i);
			tv.setOnClickListener(setQty);
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


	View.OnClickListener setQty = new OnClickListenerToNotify() {
		@Override
		public void onClick(View v) {
			final Item i = (Item)v.getTag();
			InputNumberDlg.open(v.getContext(), new InputNumber() {

				@Override public int getValue() { return i.qty; }

				@Override
				public void applayInput(int value, Object... params) {
					i.qty = value;
					updateSumTextView();
					adapter.notifyDataSetChanged();
				}

				public boolean isInpack() {
					return i.pack != 0;
				}

			}, Consts.QTY_SCALE, true, "Введите количество");
		}
	};
}

class Item {
	public String name = "";
	public String whCode = "";
	public int whIndex = 0;
	public int pack = 0;
	public int qty = 0;

	public int getOrderQty(int inPack) {
		return pack == 0 ? qty : (int)((long)qty * inPack / Consts.QTY_SCALE);
	}

	public void setOrderQty(int inPack, int totQty) {
		if(pack == 0)
			qty = totQty;
		else {
			qty = (int)((long)totQty * Consts.QTY_SCALE / inPack);
		}
	}
}