package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceSeries;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

public class SalesPriceCount extends PriceCount {
	Adapter adapter;
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, SalesPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CheckBox)findViewById(R.id.cbPackets)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateTotalQTY();
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override protected int getContentViewId() { return R.layout.sales_item_edit; }
	@Override protected boolean isComplexSalesHistory() { return false; }
	
	@Override
	protected boolean updateOrder() {
		if(document.isEditable() == false)
			return false;
		
		boolean inPack = ((CheckBox)findViewById(R.id.cbPackets)).isChecked();
		SalesEx doc = (SalesEx)document.getData();
		PriceEx pe = (PriceEx)price.getData();
		@SuppressWarnings("unchecked")
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) (document.getClass()));
		int cost = cs.getItemCost(price.getData(), document);
		
		for(SalesItemEx si : ((SalesImplEx)document).findItems(pe))
			doc.items.remove(si);
		
		List<PriceSeries> psl = new ArrayList<PriceSeries>();
		
		int newQty = 0;
		for(ItemData id : adapter.getItems()) {
			int diff = id.qtyLimit - id.qty;
			if(id.createdFromPrice) {
				newQty = pe.vanQty - (id.order - diff);
			} else if(id.ref != null ) {
				id.ref.qty -= id.order - diff;
				newQty += id.ref.qty;
				psl.add(id.ref);
			}
			if(id.order == 0)
				continue;
			
			SalesItemEx dest = new SalesItemEx();
			dest.id = pe.id;
			dest.cost = cost;
			dest.qty = id.order;
			dest.country = id.country;
			dest.ntd = id.ntd;
			dest.party = id.party;
			dest.prdDate = id.prdDate;
			dest.countryCode = id.countryCode;
			if(inPack) dest.flags |= OrderItem.IN_PACK;
			
			dest.countTax(doc, pe.tax1);
			doc.items.add(dest);
		}
		pe.vanQty = newQty;
		price.write();
		
		DbWriter wr = new DbWriter();
		for(PriceSeries ps : psl)
			wr.insertRecord(ps);
		wr.close();
		document.write();
		SalesDoc.instance().refreshDocSum(document.getId());
		
		return false;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		if(adapter == null) {
			adapter = new Adapter();
			
			ListView lv = ((ListView)findViewById(R.id.lvItems));
			lv.setAdapter(adapter);
			lv.setOnKeyListener(new View.OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent event) {
					int kc = event.getKeyCode();
					if(event.getAction() == KeyEvent.ACTION_DOWN && 
							(kc == KeyEvent.KEYCODE_ENTER || kc == KeyEvent.KEYCODE_DPAD_CENTER || kc == 160)) {
						ListView lv = ((ListView)findViewById(R.id.lvItems));
						if(lv != null) {
							ItemData id = (ItemData) lv.getSelectedItem();
							if(id != null) {
								id.setQty(SalesPriceCount.this);
							}
						}
						return true;
					}
					return false;
				}
			});
			
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					ItemData id = (ItemData) arg0.getItemAtPosition(arg2);
					if(id != null) {
						id.setQty(SalesPriceCount.this);
					}
				}
			});
//			lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//				@Override
//				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//					arg1.requestFocus();
////					((ItemData)arg0.getSelectedItem()).selected();
//				}
//
//				@Override public void onNothingSelected(AdapterView<?> arg0) {}
//			});
		}
		
		PriceEx pe = (PriceEx)price.getData();
		adapter.refresh(pe);
		updateTotalQTY();
	}
	
	void updateTotalQTY() {
		TextView tv = (TextView)findViewById(R.id.tvTotalQty);
		tv.setText(Util.IntToScaleStr(adapter.orderQty(), Consts.QTY_SCALE));
	}
	
	class ItemData implements Comparable<ItemData>, View.OnClickListener {
		public Date prdDate;
		public int qty;
		public int qtyLimit;
		public int order;
		public String ntd = "";
		public String party = "";
		public String country;
		public String countryCode;
		public PriceSeries ref = null;
		public boolean createdFromPrice = false;
		
		public ItemData(PriceSeries src) {
			prdDate = src.prdDate;
			qty = src.qty;
			order = 0;
			ntd = src.ntd;
			party = src.party;
			country = src.country;
			countryCode = src.countryCode;
			qtyLimit = qty;
			ref = src;
		}
		
		public ItemData(SalesItemEx src) {
			prdDate = src.prdDate;
			qty = 0;
			order = src.qty;
			ntd = src.ntd;
			party = src.party;
			country = src.country;
			countryCode = src.countryCode;
			qtyLimit = qty;
		}
		
		public ItemData(PriceEx p) {
			prdDate = new Date(0);
			qty = p.vanQty;
			ntd = p.ntd;
			country = p.country;
			countryCode = p.countryCode;
			qtyLimit = qty;
			createdFromPrice = true;
		}

		@Override
		public int compareTo(ItemData o) {
			return prdDate.compareTo(o.prdDate);
		}
		
		public void draw(View v) {
			boolean inPack = ((CheckBox)findViewById(R.id.cbPackets)).isChecked();
			int qip = price.getData().qtyInPack;
			if(qip == 0)
				qip = Consts.QTY_SCALE;
			
			String text = "";
			TextView tv;
			tv = (TextView)v.findViewById(R.id.tvPrdDate);
			if(prdDate.getTime() < 365 * 24 * 3600 * 1000) 
				text = "";
			else
				text = Util.simpleDateFormat.format(prdDate);
			tv.setText(text);

			tv = (TextView)v.findViewById(R.id.tvParty);
			tv.setText(party);
			
			tv = (TextView)v.findViewById(R.id.tvVanQty);
			tv.setText(Util.IntToScaleStr(inPack ? (long)qty * Consts.QTY_SCALE / qip : qty, Consts.QTY_SCALE));

			tv = (TextView)v.findViewById(R.id.edCount);
			tv.setText(Util.IntToScaleStr(inPack ? (long) order * Consts.QTY_SCALE / qip : order, Consts.QTY_SCALE));
			
//			tv = (TextView)v.findViewById(R.id.tvQty);
//			tv.setText(Util.IntToScaleStr(inPack ? (long) order * Consts.QTY_SCALE / qip : order, Consts.QTY_SCALE));
//			tv.setOnClickListener(this);
		}
		
		void setQty(Context context) {
			InputNumberDlg.open(context, new InputNumber() {
				
				@Override public int getValue() { 
					int qty = order;
					if(((CheckBox)findViewById(R.id.cbPackets)).isChecked())
						qty = (int)((long)qty * Consts.QTY_SCALE / price.getData().qtyInPack);
					return qty; 
				}
				
				@Override
				public void applayInput(int value, Object... params) {
					if( value < 0 || qtyLimit < 0 )
						return;
					
					if( ((CheckBox)findViewById(R.id.cbPackets)).isChecked())
						value = (int)((long)value * price.getData().qtyInPack / Consts.QTY_SCALE);
					if( ((SalesImplEx)document).noCheckQty() == false && value > qtyLimit)
						value = qtyLimit;
					order = value;
					updateTotalQTY();
					adapter.notifyDataSetChanged();
				}
			}, Consts.QTY_SCALE, true, "¬ведите количество");
		}
		

		@Override public void onClick(View v) { setQty(v.getContext());}
	}
	
	class Adapter extends BaseAdapter {
		List<ItemData> items;
		
		public Adapter() {
			items = new ArrayList<ItemData>();
		}

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int position) { return items.get(position); }
		@Override public long getItemId(int position) { return position; }
		
		List<ItemData> getItems() { return items; }
		
		public long orderQty() {
			long ret = 0;
			for(ItemData id : items)
				ret += id.order;
			return ret;
		}
		
		public void refresh(PriceEx p) {
			final Map<MapKey, ItemData> tempMap = new HashMap<MapKey, SalesPriceCount.ItemData>();
			items.clear();
			DataTraveler.travel(PriceSeries.class, new DataTraveler.Travel<PriceSeries>(true) {

				@Override
				public boolean travel(DataTraveler<PriceSeries> item) {
					ItemData id = new ItemData(item.data); 
					items.add(id);
					tempMap.put(new MapKey(item.data), id);
					return true;
				}
			}, "id='" + p.id + "'");
			
			if(items.size() == 0) {
				ItemData id = new ItemData(p); 
				items.add(id);
				tempMap.put(new MapKey(id), id);
			}
			
			for(SalesItemEx si : ((SalesImplEx)document).findItems(p)) {
				MapKey key = new MapKey(si);
				ItemData id = tempMap.get(key);
				if(id == null) {
					id = new ItemData(si);
					items.add(id);
					continue;
				}
				
				id.order = si.qty;
				if(document.isEditable())
					id.qtyLimit += si.qty;
			}
			
			Collections.sort(items);
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(SalesPriceCount.this, R.layout.sales_item_row, null);
			
			ItemData id = (ItemData) getItem(position);
			id.draw(view);
			return view;
		}
	}
}

class MapKey
{
	String ntd;
	String party;
	Date prdDate = new Date();
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof MapKey) && ntd.equals(((MapKey)o).ntd) && party.equals(((MapKey)o).party) && 
				prdDate.equals(((MapKey)o).prdDate);
	}
	
	@Override
	public int hashCode() {
		return ntd.hashCode() ^ party.hashCode();
	}
	
	public MapKey(PriceSeries ps) {
		ntd = ps.ntd;
		party = ps.party;
		prdDate = ps.prdDate;
	}

	public MapKey(SalesPriceCount.ItemData ps) {
		ntd = ps.ntd;
		party = ps.party;
		prdDate = ps.prdDate;
	}
	
	public MapKey(SalesItemEx i) {
		ntd = i.ntd;
		party = i.party;
		prdDate = i.prdDate;
	}
}
