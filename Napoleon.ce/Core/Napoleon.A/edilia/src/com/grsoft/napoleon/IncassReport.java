package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class IncassReport extends BaseActivity {
	
	protected static final int DIALOG_DATE_PICKER_ID = 0;
	Date date = new Date();
	Adapter adapter = new Adapter();
	
	public static void open(Context ctx) {
		Intent i = new Intent(ctx, IncassReport.class);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incass_report);
		
		refreshDate();
		findViewById(R.id.tvCaption).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(IncassReport.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, date.getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
		((ListView)findViewById(R.id.lvItems)).setAdapter(adapter);
		reloadData();
	}
	
	private void reloadData() {
		adapter.refresh();
		TextView tv;
		tv = (TextView)findViewById(R.id.tvTotalSum);
		long sumAll = adapter.getSum(false);
		long sumExp = adapter.getSum(true);
		String text = Util.IntToScaleStr(sumExp, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		text += " / <b>" + Util.IntToScaleStr(sumAll, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
		tv.setText(Html.fromHtml(text));
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, date.getTime());
			date = new Date(ct);
			refreshDate();
			reloadData();
		}
	}
	
	
	void refreshDate() {
		TextView tv;
		tv = (TextView)findViewById(R.id.tvCaption);
		String text = String.format("Инкассации на дату <font color='blue'><u>%s</u></font>", Util.simpleDateFormat.format(date));
		tv.setText(Html.fromHtml(text));
	}
	
	class Adapter extends BaseAdapter {
		
		List<Item> data = new ArrayList<Item>();
		
		public void refresh() {
			data.clear();
			
			OrgImpl oi = new OrgImpl();
			Org o = oi.getData();
			
			DatePeriod dp = new DatePeriod(Util.getDayStart(date), Util.getDayEnd(date));
			
			DocList dl = IncassDoc.instance().docList(null, null, dp);
			for(Document<?> d : dl) {
				String id = d.getId();
				long sum = d.sum();
				o.id = id;
				oi.read();
				String number = ((IncassDebDistrEx)d.getData()).docNumber;
				String text = "<b>" + number + "</b>";
				boolean isExported = ((CreatableDocument<?>)d).isExported(); 
				if(isExported == false)
					text += "<i>не отправлен!</i>";
				data.add(new Item(o.name, sum, text, number.length() > 0));
			}
			dl.close();
			
			dl = OrderDoc.instance().docList(null, null, dp);
			for(Document<?> d : dl) {
				OrderEx oe = (OrderEx) d.getData();
				String id = d.getId();
				long sum = oe.incass;
				if( sum > 0 ) {
					o.id = id;
					oi.read();
					String text = "";
					if( oe.incassNum.length() > 0 )
						text = "<b>" + oe.incassNum + "</b> накладная <b>" + oe.number + "</b>";
					boolean isExported = ((CreatableDocument<?>)d).isExported(); 
					if(isExported == false)
						text += "<i>не отправлен!</i>";
					data.add(new Item(o.name, sum, text, oe.incassNum.length() > 0));
				}
			}
			dl.close();
			
			Collections.sort(data);
			
			oi.close();
			notifyDataSetChanged();
		}
		
		public long getSum(boolean exportedOnly) {
			long sum = 0;
			for(Item i : data) {
				if(!exportedOnly || i.isExported)
					sum += i.sum;
			}
			return sum; 
		}

		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return arg0 < getCount() ? data.get(arg0) : null; }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg1 == null)
				arg1 = View.inflate(IncassReport.this, R.layout.incass_report_row, null);
			
			Item i = (Item) getItem(arg0);
			if( i != null ) {
				TextView tv;
				String text = i.name;
				tv = (TextView)arg1.findViewById(R.id.tvName);
				if(i.number.length() > 0)
					text += "<br/>" + i.number;
				tv.setText(Html.fromHtml(text));
				
				tv = (TextView)arg1.findViewById(R.id.tvSum);
				text = "";
				if(i.number.length() > 0)
					text += "<b>";
				text += Util.IntToScaleStr(i.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				if(i.number.length() > 0)
					text += "</b>";
				tv.setText(Html.fromHtml(text));
			}
			return arg1;
		}
		
	}
}

class Item implements Comparable<Item> {
	public String name;
	public long sum;
	public String number;
	boolean isExported;
	
	public Item(String name, long sum, String number, boolean isExported) {
		this.name = name;
		this.sum = sum;
		this.number = number;
		this.isExported = isExported;
	}
	
	@Override
	public int compareTo(Item another) {
		return name.compareTo(another.name);
	}
}
