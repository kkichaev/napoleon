package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceSalesQty;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SalesPriceCount extends PriceCount {
	List<PriceSalesQty> qty = new ArrayList<PriceSalesQty>();
	Adapter adapter;
	
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, SalesPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}

	double dcost = 0;
	double taxVal = 0;
	int prevQty = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override protected int getContentViewId() { return R.layout.sales_price_count; }
	
	@Override
	protected void refreshData() {		
		super.refreshData();
		
		ListView lv = (ListView)findViewById(R.id.lvParts);
		PriceEx pe = (PriceEx) price.getData();
		SalesItemEx sie = (SalesItemEx) ((SalesImplEx)document).findItem(pe.id);
		if(sie != null) {
			prevQty = sie.qty;
			qty = sie.party;
			sie.party = new ArrayList<PriceSalesQty>();
			pe.add(qty);
		} else {
			qty = pe.distrubuteFIFO(prevQty); // init new
		}
		
		adapter = new Adapter(pe.party);
		lv.setAdapter(adapter);
		
		int vsbl = View.GONE;
		
		SalesEx se = (SalesEx)document.getData(); 
		if(se.taxType == OrgEx.TAX_ABOVE) {
			int tax = pe.tax1;
			taxVal = ((double)(tax)) / 100.0;
			dcost = priceVal * (1 +  taxVal);
			
			TextView tv;
			tv = (TextView)findViewById(R.id.tvTaxLabel);
			tv.setText("НДС(" + Integer.toString(tax) + "%)");
			vsbl = View.VISIBLE;
		}
		
		findViewById(R.id.trTax).setVisibility(vsbl);
		updateSumTextView();
	}
	
	int getQty() {
		int count = getCountValue();
		if(cbPackets.isChecked())
			count = (int)((long)count * qtyInPack / Consts.QTY_SCALE);
		return count;
	}
	
	@Override
	protected long getSumValue() {
		if(dcost != 0) {
			qtyItems = getCountValue();
			return (long)(getQty() * dcost / Consts.QTY_SCALE);
		}
		return super.getSumValue();
	}
	
	@Override
	protected void updateSumTextView() {
		super.updateSumTextView();

		int newQty = getQty();
		if(document.isEditable() && newQty != prevQty) {
			prevQty = newQty;
			if(adapter != null) {
				PriceEx pe = (PriceEx) price.getData();
				qty = pe.distrubuteFIFO(newQty);
				adapter.notifyDataSetChanged();
			}
		}
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvTax);
		int taxSum = (int)((dcost - priceVal) * newQty / Consts.QTY_SCALE + 0.5);
		tv.setText(Util.IntToScaleStr(taxSum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
	
	PriceSalesQty findParty(PriceSalesQty src) {
		for(PriceSalesQty psq : qty)
			if(psq.date.equals(src.date))
				return psq;
		
		return null;
	}

	int freeQty() {
		int free = prevQty;
		for(PriceSalesQty psq : qty) {
			free -= psq.qty;
		}
		
		return free;
	}
	

	void makeFreeQtyAlert(int fq) {
		Toast.makeText(SalesPriceCount.this, "Не распределено по сериям " + Integer.toString(fq / Consts.QTY_SCALE) + " шт.", 
				Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		int fq = freeQty();
		if(fq > 0) {
			makeFreeQtyAlert(fq);
			return false;
		}
		return true; 
	}
	
	@Override
	protected boolean updateOrder() {
		SalesImplEx seDoc = (SalesImplEx)document; 
		seDoc.updateItem(price, qty, (int)priceVal, cbPackets.isChecked());
		return false;
	}
	
	View.OnClickListener changeQty = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(document.isEditable() == false)
				return;
			
			PriceSalesQty src = (PriceSalesQty)arg0.getTag();
			PriceSalesQty dest = findParty(src);
			int fq = freeQty();
			if(dest != null) {
				qty.remove(dest);
			} else {
				if(fq == 0) { // нет свободных - распределяем все заново
					qty.clear();
					fq = prevQty;
				}
				dest = new PriceSalesQty();
				dest.date = src.date;
				dest.qty = (fq < src.qty) ? fq : src.qty;
				qty.add(dest);
			}

			fq = freeQty();
			if(fq > 0) {
				makeFreeQtyAlert(fq);
			}
			adapter.notifyDataSetChanged();
		}
	};
	
	class Adapter extends BaseAdapter {

		public List<PriceSalesQty> data;
		
		public Adapter(List<PriceSalesQty> src) { data = src; }
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(SalesPriceCount.this, R.layout.sales_party_row, null);
			
			PriceSalesQty item = (PriceSalesQty) getItem(arg0);
			TextView tv;
			String text;
			
			boolean fakeParty = item.isFake();
			
			tv = (TextView)view.findViewById(R.id.tvParty);
			text = fakeParty ? "" : Util.simpleDateFormat.format(item.date);
			tv.setText(text);
			
			int qip = price.getData().qtyInPack;
			if(qip == 0)
				qip = Consts.QTY_SCALE;
			int qty = item.qty; 
			text = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + "<br/>" + 
				Util.IntToScaleStr((int)((long)qty * Consts.QTY_SCALE / qip), Consts.QTY_SCALE) + " уп.";
			tv = (TextView)view.findViewById(R.id.tvPartQty);
			tv.setText(Html.fromHtml(text));
			
			PriceSalesQty dest = findParty(item);
			text = "";
			int bk = R.drawable.list_selector;
			if(dest != null) {
				bk = R.drawable.even_row_selector;
				qty = dest.qty; 
				text = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + "<br/>" + 
					Util.IntToScaleStr((int)((long)qty * Consts.QTY_SCALE / qip), Consts.QTY_SCALE) + " уп.";
			}
			tv = (TextView)view.findViewById(R.id.tvInQty);
			tv.setText(Html.fromHtml(text));
			
			view.setBackgroundResource(bk);
			view.setTag(item);
			if( !fakeParty )
				view.setOnClickListener(changeQty);
			
			return view;
		}
		
	}
}
