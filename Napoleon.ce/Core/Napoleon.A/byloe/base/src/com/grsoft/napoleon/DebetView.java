package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
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

public class DebetView extends DocumentsBase {
	public static void open(Context ctx, String orgId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		ctx.startActivity(i);
	}
	
	@Override protected int getContentViewID() { return R.layout.debet_view; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DebetAdapter da = new DebetAdapter();
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(da);
		
		TextView tv = (TextView)findViewById(R.id.orderInfo);
		String text = "Сумма накладных " + Util.IntToScaleStr(da.getOrderSum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + " руб.";
		tv.setText(text);
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType != DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			Documents.open(this, org.getData());
			finish();
		} else {
			super.adjustViewForDocType(docType);
			findViewById(R.id.SumColumnTitle).setVisibility(View.GONE);
		}
	}
	
	class DebetAdapter extends BaseAdapter {
		
		ArrayList<PaymentEx> docs = new ArrayList<PaymentEx>();
		int ordSum = 0;
		
		int getOrderSum() { return ordSum; }
		
		public DebetAdapter() {
			PaymentEx pe = new PaymentEx();
			String table = DataObjectInfo.getInstance().getTableName(pe.getClass());
			String where = "id='" + org.getData().id + "'";
			DbReader r = new DbReader();
			boolean bdo = r.select(pe, table, where);
			while( bdo ) {
				ordSum += pe.dlvSum;
				docs.add(pe);
				pe = new PaymentEx();
				bdo = r.selectNext(pe);
			}
			r.close();
		}

		@Override public int getCount() { return docs.size(); }

		@Override
		public Object getItem(int arg0) {
			return (arg0 < docs.size()) ? docs.get(arg0) : null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(DebetView.this, R.layout.debt_row, null);
			
			PaymentEx pe = (PaymentEx)getItem(arg0);
			if( pe != null ) {
				TextView tv;
				String text;
				
				tv = (TextView) view.findViewById(R.id.tvOther);
				text = Util.simpleDateFormat.format(pe.date) + "<br>" + pe.number;
				tv.setText(Html.fromHtml(text));
				
				
				tv = (TextView) view.findViewById(R.id.tvDate);
				text = Util.IntToScaleStr(pe.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "<br>" + 
						Util.IntToScaleStr(pe.dlvSum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				tv.setText(Html.fromHtml(text));

				tv = (TextView) view.findViewById(R.id.tvSum);
				text = Integer.toString(pe.payDelay) + "<br>" + Integer.toString(pe.overDelay);
				tv.setText(Html.fromHtml(text));

				tv = (TextView) view.findViewById(R.id.tvAgent);
				text = pe.manager;
				tv.setText(Html.fromHtml(text));
			}
			return view;
		}
		
	}
}
