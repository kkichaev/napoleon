package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class SunnySKUReport extends BaseActivity {
	
	protected static final int DIALOG_DATE_PICKER_ID = 0;
	long rowid;
	Date dateStart = new Date();
	
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, SunnySKUReport.class);
		i.putExtra(ExtrasConst.ROW_ID_FIELD, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sku_report_view);

		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		rowid = b.getLong(ExtrasConst.ROW_ID_FIELD, ExtrasConst.INVALID_ROWID);
	
		List<Date> weekStart = new ArrayList<Date>();

		Calendar c = Calendar.getInstance(Locale.getDefault());
		while(c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
			c.add(Calendar.DAY_OF_MONTH, -1);
		}
		long ct = Util.getDayStart(c.getTime()).getTime();
		for(int i=0; i<4; i++) {
			// from sunday to monday
			Date d = new Date(ct);
			weekStart.add(d);
						
			ct -= (7 * 24 * 3600 * 1000);
		}
		dateStart = new Date(ct);
		
		refreshDate();
		
		doReport(weekStart);
		
		((TextView)findViewById(R.id.tvDate)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(SunnySKUReport.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, dateStart.getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
	}

	private void doReport(List<Date> weekStart) {
		WebView wv = (WebView) findViewById(R.id.webView1);
		WebSettings settings = wv.getSettings();
		settings.setSupportZoom(true);
		settings.setDefaultTextEncodingName("utf-8");
		
		String data = makeReport(rowid, weekStart, dateStart);
		wv.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

		settings.setBuiltInZoomControls(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Calendar c = Calendar.getInstance();
			c.setTime(new Date(ct));
			while( c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				c.add(Calendar.DAY_OF_MONTH, -1);
			}
			
			List<Date> weekStart = new ArrayList<Date>();
			dateStart = c.getTime();
			ct = dateStart.getTime();
			for(int i=0; i<4; i++) {
				ct += (7 * 24 * 3600 * 1000);

				// from sunday to monday
				Date d = new Date(ct);
				weekStart.add(d);
							
			}
			Collections.reverse(weekStart);
			refreshDate();
			doReport(weekStart);
		}
	}
	
	private void refreshDate() {
		String text = "Дата начала периода <font color='blue'><u>" + Util.simpleDateFormat.format(dateStart) + "</u></font>";
		
		((TextView)findViewById(R.id.tvDate)).setText(Html.fromHtml(text));
	}

	StringBuilder appendTH(StringBuilder sb, String text) {
		sb.append("<th width='20%'>").append(text).append("</th>");
		return sb;
	}

	StringBuilder appendTD(StringBuilder sb, String text, boolean selected) {
		sb.append("<td ");
		if(selected)
			sb.append("bgcolor='yellow'");
		sb.append(">").append(text);
		sb.append("</td>");
		return sb;
	}
	
	private String makeReport(long rid, List<Date> weekStart, Date startDate) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head> <style type='text/css'> TABLE { border-collapse: collapse; } TD, TH { padding: 3px; border: 1px solid black; } TH { background: #b0e0e6; } </style></head><body><table><tr>");
		OrgImpl oi = new OrgImpl();
		oi.read(rid);
		oi.close();
		
//		List<Date> weekStart = new ArrayList<Date>();
//
//		Calendar c = Calendar.getInstance(Locale.getDefault());
//		while(c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
//			c.add(Calendar.DAY_OF_MONTH, -1);
//		}
//		long ct = Util.getDayStart(c.getTime()).getTime();
//		for(int i=0; i<4; i++) {
//			// from sunday to monday
//			Date d = new Date(ct);
//			weekStart.add(d);
//						
//			ct -= (7 * 24 * 3600 * 1000);
//		}
//		Date startDate = new Date(ct);
		Date ed = new Date(startDate.getTime() + 6 * 24 * 3600 * 1000);
		appendTH(sb, Util.simpleDateFormat.format(startDate) + " - " + Util.simpleDateFormat.format(ed));
		for(int i=3; i >=0; i--) {
			Date curDate = weekStart.get(i);
			ed = new Date(curDate.getTime() + 6 * 24 * 3600 * 1000);
			appendTH(sb, Util.simpleDateFormat.format(curDate) + " - " + Util.simpleDateFormat.format(ed));
		}

		DatePeriod dp = new DatePeriod(startDate, ed);
		dp.periodType = DatePeriod.CREATED;
		
		HashMap<String, RowData> data = fillData(oi.getData().id, weekStart, dp);
		List<RowData> src = new ArrayList<RowData>(data.values());
		Collections.sort(src);
		
		for(RowData rd : src) {
			sb.append("<tr>");
			for(int i=4; i>=0; i--) {
				if(!rd.isSold(i))
					appendTD(sb, "", false);
				else
					appendTD(sb, rd.name(), rd.isGrow(i));
			}
			sb.append("</tr>");
		}
		sb.append("</table></body></html>");
		
		return sb.toString();
	}

	private HashMap<String, RowData> fillData(String id, List<Date> weekStart, DatePeriod dp) {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		
		HashMap<String, RowData> data = new HashMap<String, RowData>();
		DocList dl = OrderDoc.instance().docList(id, null, dp);
		for(Document<?> d : dl) {
			Order doc = (Order)d.getData();
			int wi = getWeekIndex(doc.created, weekStart);
			for(OrderItem item : doc.items) {
				RowData rd = data.get(item.id);
				if(rd == null) {
					p.id = item.id;
					pi.read();
					rd = new RowData(p.name, wi);
					data.put(item.id, rd);
				} else
					rd.markSold(wi);
			}
		}
		dl.close();
		pi.close();
		
		return data;
	}

	private int getWeekIndex(Date created, List<Date> weekStart) {
		int index = 0;
		for(Date d : weekStart) {
			if(d.compareTo(created) < 0)
				break;
			index++;
		}
		return index;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.ROW_ID_FIELD, rowid);
	}
}

class RowData implements Comparable<RowData> {
	boolean[] sold = new boolean[5];
	String name;
	
	public RowData(String name, int index) { 
		this.name = name;
		sold[index] = true;
	}
	
	public void markSold(int index) { sold[index] = true; }
	
	public boolean isSold(int index) { return sold[index]; }
	public String name() { return name; }
	
	public boolean isGrow(int index) {
        if (index < 0 || index >= 4 || !sold[index++])
            return false;
		
        for (; index <= 4; index++)
            if (sold[index])
               return false;
         return true;
	}

	@Override
	public int compareTo(RowData o) {
		return name.compareTo(o.name);
	}
}
