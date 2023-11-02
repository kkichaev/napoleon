package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.IncomeItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class IncomeList extends Activity {
	
	protected static final int ASK_FROM_DATE = 0;
	protected static final int ASK_TO_DATE = 1;
	
	Date fromDate = new Date();
	Date toDate = new Date();
	Adapter adapter;
	
	PriceImpl pi = new PriceImpl();
	
	public static void open(Context context) {
		Intent i = new Intent(context, IncomeList.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.income_list);
		
		findViewById(R.id.tvDateFrom).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(IncomeList.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, fromDate.getTime());
				startActivityForResult(i, ASK_FROM_DATE);
			}
		});

		findViewById(R.id.tvDateTo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(IncomeList.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, fromDate.getTime());
				startActivityForResult(i, ASK_TO_DATE);
			}
		});
		
		refreshFromDate();
		refreshToDate();
		
		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		adapter.refresh();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}
	
	private void refreshToDate() {
		String text = " по <b><u><font color='blue'>" + Util.simpleDateFormat.format(toDate) + "</font?</u></b>";
		((TextView)findViewById(R.id.tvDateTo)).setText(Html.fromHtml(text));
	}

	private void refreshFromDate() {
		String text = "Приходы с <b><u><font color='blue'>" + Util.simpleDateFormat.format(fromDate) + "</font></u></b> ";
		((TextView)findViewById(R.id.tvDateFrom)).setText(Html.fromHtml(text));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);

			if( requestCode == ASK_FROM_DATE ) {
				fromDate = newDate;
				refreshFromDate();
			} else if(requestCode == ASK_TO_DATE) {
				toDate = newDate;
				refreshToDate();
			}
			adapter.refresh();
		}
	}
	
	public void updateTotalSum(int sum, int qty) {
		TextView tvTotalSum = (TextView) findViewById(R.id.tvTotalSum);		
		if (tvTotalSum != null)
		{
			tvTotalSum.setVisibility(View.VISIBLE);
			String sumStr = DocType.SumConverter.toString(sum);
			String qtyStr = Integer.toString(qty) + " уп";
			String str = qtyStr + "<br/><b>" + sumStr + "</b>";
			tvTotalSum.setText(Html.fromHtml(str));
		}
	}
	
	class Adapter extends BaseAdapter {
		List<Data> data = new ArrayList<Data>();
		
		public void refresh() {
			final HashMap<ItemKey, ItemData> items = new HashMap<ItemKey, ItemData>();
			
			String where = "date >= " + Long.toString(Util.getDayStart(fromDate).getTime()) + 
					" and date <= " + Long.toString(Util.getDayEnd(toDate).getTime());
			
			DataTraveler.travel(Income.class, new DataTraveler.Travel<Income>(true) {

				@Override
				public boolean travel(DataTraveler<Income> item) {
					for(IncomeItem i : item.data.items) {
						ItemKey key = new ItemKey(i);
						ItemData data = items.get(key);
						if( data == null ) {
							data = new ItemData(i);
							items.put(key, data);
						} else {
							data.add(i);
						}
					}
					return true;
				}
			}, where);
			
			int qty = 0;
			int sum = 0;
			data.clear();
			for(Entry<ItemKey, ItemData> kv : items.entrySet()) {
				Data d = new Data(kv.getKey(), kv.getValue());
				data.add(d);
				qty += d.qty;
				sum += d.sum;
			}
			
			Collections.sort(data);
			
			updateTotalSum(sum, qty / Consts.QTY_SCALE);
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(IncomeList.this, R.layout.income_row, null);
			
			Data d = (Data) getItem(arg0);
			TextView tv;
			Price p = pi.getData();
			
			tv = (TextView)view.findViewById(R.id.tvName);

			p.id = d.id;
			if( pi.read() )
				tv.setText(p.name);
			else
				tv.setText("Товар с кодом <" + p.id + ">");
			
			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(d.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
			
			tv = (TextView)view.findViewById(R.id.tvCost);
			tv.setText(Util.IntToScaleStr(d.cost, Consts.SUM_SCALE, Util.DEC_DELIM, true));

			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(d.sum, Consts.SUM_SCALE, Util.DEC_DELIM, true));
			return view;
		}
		
	}
}

class Data implements Comparable<Data> {
	public String id = "";
	public int cost;
	public int qty;
	public int sum;
	
	public Data(ItemKey key, ItemData data) {
		id = key.id;
		cost = key.cost;
		qty = data.qty;
		sum = data.sum;
	}

	@Override
	public int compareTo(Data arg0) {
		int cmp = id.compareTo(arg0.id);
		return cmp != 0 ? cmp : cost - arg0.cost;
	}
}

class ItemKey {
	public String id = "";
	public int cost;
	
	public ItemKey(IncomeItem i) {
		id = i.id;
		cost = i.cost;
	}
	
	@Override
	public int hashCode() {
		return (id + Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false)).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof ItemKey) {
			ItemKey ik = (ItemKey)obj;
			return id.equals(ik.id) && cost == ik.cost;
		}
		return false;
	}
}

class ItemData {
	public int qty;
	public int sum;
	
	public ItemData(IncomeItem i) {
		qty = i.qty;
		sum = i.sum;
	}

	public void add(IncomeItem item) {
		qty += item.qty;
		sum += item.sum;
	}
}
