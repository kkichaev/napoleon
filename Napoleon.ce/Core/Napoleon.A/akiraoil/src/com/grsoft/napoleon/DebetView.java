package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DebetView extends DocumentsBase {
	
	PaymentEx payment;
	PayData data;
	
	public static void open(Context ctx, String orgId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		ctx.startActivity(i);
	}

	@Override protected int getContentViewID() { return R.layout.debet_docs; }
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType != DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			Documents.open(this, org.getData());
			finish();
		} else
			super.adjustViewForDocType(docType);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		OrgEx oe = (OrgEx) org.getData();
		
		PaymentImpl pi = new PaymentImpl();
		payment = (PaymentEx) pi.getData();
		pi.read("agreeId", oe.agreeId);
		
		data = new PayData();
		data.limit = oe.limit;
		
		JsonElement root = new JsonParser().parse(payment.json);
		data.read(root);
		
		String text = "<b>Ваш кредитный лимит<br/><font color='red'>" + Util.IntToScaleStr(data.limit(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</font>";
		TextView tv = (TextView)findViewById(R.id.tvTotalSum);
		tv.setText(Html.fromHtml(text));
		
		Adapter a = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(a);
	}
	
	@Override
	protected void refreshTotalSum() {
	}
		
	@SuppressLint("SimpleDateFormat")
	class Adapter extends BaseAdapter {

		List<String[]> data = new ArrayList<String[]>();
		
		public Adapter() {
			String[] val;
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			
			val = new String[] {
					"<b>Сумма общего долга</b>", 
					"<b>" + Util.IntToScaleStr(DebetView.this.data.debetSum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>",
			};
			data.add(val);
			Collections.sort(DebetView.this.data.debet);
			for(DebetData dd : DebetView.this.data.debet) {
				val = new String[] {
					Util.IntToScaleStr(dd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false),
					sdf.format(dd.payDate),
				};
				data.add(val);
			}
			
			val = new String[] {
					"<b>Сумма просроченного долга</b>", 
					"<b>" + Util.IntToScaleStr(DebetView.this.data.overdueSum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>",
			};
			data.add(val);
			Collections.sort(DebetView.this.data.overdue);
			for(OverdueData dd : DebetView.this.data.overdue) {
				val = new String[] {
						Util.IntToScaleStr(dd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false),
						Integer.toString(dd.dueDays),
				};
				data.add(val);
			}
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(DebetView.this, R.layout.debet_row, null);
			}
			TextView tv;
			String[] str = (String[]) getItem(arg0);
			tv = (TextView)view.findViewById(R.id.tvInfo1);
			tv.setText(Html.fromHtml(str[0]));
			tv = (TextView)view.findViewById(R.id.tvInfo2);
			tv.setText(Html.fromHtml(str[1]));
			
			return view;
		}
		
	}
}
