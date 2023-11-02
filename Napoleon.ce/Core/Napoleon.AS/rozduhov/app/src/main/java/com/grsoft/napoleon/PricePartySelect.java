package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.FirmRozduhov;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PartsData;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.WhData;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PricePartySelect extends PriceCountEx {
	List<WhData> parts = new ArrayList<WhData>();
	Adapter adapter;
	int countValue;
	
	public static void openPartSelect(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, PricePartySelect.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.price_party;
	}
	
	@Override
	protected boolean isComplexSalesHistory() {
		return false;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		if(document != null) {
			OrderImplEx doc = (OrderImplEx)document;
			FirmRozduhov firm = doc.getFirm();
			PriceEx pe = (PriceEx) price.getData();
			OrderItemEx oie = (OrderItemEx) doc.findItem(pe.id);
			if(oie != null) {
				parts.clear();
				parts.addAll(oie.parts);
			}
			PartsData pd = firm.qty < pe.parts.size() ? pe.parts.get(firm.qty) : new PartsData();
			pd.items.addAll(parts);
			
			ListView lv = (ListView)findViewById(R.id.lvItems);
			adapter = new Adapter(pd.items);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					WhData data = (WhData) arg0.getItemAtPosition(arg2);
					if(parts.contains(data))
						parts.remove(data);
					else
						parts.add(data);
					adapter.notifyDataSetChanged();
					refreshQty();
				}
			});
			refreshQty();
		}
	}
	
	@Override
	protected boolean updateOrder() {
		((OrderImplEx)document).updateParts((PriceEx) price.getData(), parts, priceVal);
		price.write();
		
		return false;
	}
	
	protected void refreshQty() {
		countValue = 0;
		for(WhData wd : parts)
			countValue += wd.weight;
		qtyItems = countValue; 
		
		TextView tv =(TextView)findViewById(R.id.tvTotalQty);
		tv.setText(Util.IntToScaleStr(countValue, Consts.QTY_SCALE));
		updateSumTextView();
	}
	
	@Override
	protected boolean getStartInPack() {
		return false;
	}
	
	@Override protected int getCountValue() { return countValue; }

	public class Adapter extends BaseAdapter {
		List<WhData> items;
		
		public Adapter(List<WhData> items) {
			this.items = items;
		}
		
		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(PricePartySelect.this, R.layout.price_party_item, null);
			}
			
			WhData data = (WhData) getItem(pos);
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvWeight);
			tv.setText(Util.IntToScaleStr(data.weight, Consts.QTY_SCALE));
			
			ImageView iv = (ImageView)view.findViewById(R.id.ivHave);
			iv.setVisibility(parts.contains(data) ? View.VISIBLE : View.INVISIBLE);
			
			return view;
		}
		
	}

}
