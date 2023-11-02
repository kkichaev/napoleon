package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class DebetView extends DocumentsBase {
	
	Adapter adapter;
	
	public static void open(Context ctx, String orgId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		ctx.startActivity(i);
	}
	
	@Override protected int getContentViewID() { return R.layout.debet;	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		adapter = new Adapter();

		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Document<?> doc = (Document<?>) adapter.getItem(arg2);
				if( doc != null )
					doc.open(DebetView.this);
			}
		});
		
		Spinner sp = (Spinner)findViewById(R.id.spDebetType);
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				adapter.refresh(arg2 == 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Spinner sp = (Spinner)findViewById(R.id.spDebetType);
		adapter.refresh(sp.getSelectedItemPosition()==1);
	}
	
	@Override
	protected void onDestroy() {
		adapter.close();
		super.onDestroy();
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType != DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			Documents.open(this, org.getData());
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
	
	class Adapter extends BaseAdapter {
		
		DebetList debetList;
		DocList documents;
		
		ArrayList<Integer> items = new ArrayList<Integer>();
		
		public Adapter() {}
		
		public void refresh(boolean showAllDocs) {
			String id = org.getData().id;

			if( documents != null )
				documents.close();
			
			documents = DebtDoc.instance().docList(id, "date");
			debetList = new DebetList();
			debetList.load(documents);
			
			items.clear();
			
			for( int i=0; i<documents.getCount(); i++ ) {
				Document<?> doc = documents.get(i);
				
				String docid = doc.getId();
				if( showAllDocs || docid.equals(id) )
					items.add(i);
			}
			
			notifyDataSetChanged();
		}
		
		public void close() { documents.close(); }

		@Override public int getCount() { return items.size(); }

		@Override
		public Object getItem(int arg0) {
			int index = items.get(arg0);
			return documents.get(index);
		}

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			int index = items.get(arg0);
			Document<?> doc =  documents.get(index);
			DocDebtData data = debetList.get(index);

			if( view == null )
				view = View.inflate(DebetView.this, R.layout.docs_list_row, null);
			
			int color = (data != null && data.isOutOfPayLimit(1)) ? Color.RED : Color.BLACK;
			
			String text;
			TextView tv = (TextView)view.findViewById(R.id.tvDate);

			if (doc.getDate() == null)
				tv.setText(R.string.doc_error);
			else {
				text = Util.simpleDateFormat.format(doc.getDate());
				if( data != null ) {
					text += "<br>" + Util.simpleDateFormat.format(data.payDate);
				}
				tv.setText(Html.fromHtml(text));
			}
			tv.setTextColor(color);
			tv.setBackgroundColor(Color.WHITE);
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setVisibility(View.VISIBLE);
			text = Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false);
			if( data != null && data.sumD != 0 ) {
				text += "<br>" +  Util.IntToScaleWStr(data.sumD, Consts.SUM_SCALE, 2, false);
			}
			tv.setText(Html.fromHtml(text));			
			tv.setTextColor(color);
			tv.setBackgroundColor(Color.WHITE);
			
			tv = (TextView)view.findViewById(R.id.tvOther);
			text = doc.getDescription(view.getContext());
			if( data != null && data.payDate != null && data.sumD > 0 ) {
				int daysOut = (int) ((Calendar.getInstance().getTime().getTime() - data.payDate.getTime()) / (24 * 3600000l));
				if( daysOut > 0 ) {
					text += "<br><b>" + Integer.toString(daysOut) + "</b>";
				}
			}
			tv.setText(Html.fromHtml(text));		
			tv.setTextColor(color);
			tv.setBackgroundColor(Color.WHITE);
			
			view.setBackgroundColor(Color.WHITE);
			
			return view;
		}
		
	}
}
