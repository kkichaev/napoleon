package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.grsoft.dataobjects.DgvItem;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocListEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DebetView extends DocumentsBase {
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault());
	private DebtAdapter da;
	
	public static void open(Context ctx, String orgId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		ctx.startActivity(i);
	}

	@Override protected int getContentViewID() { return R.layout.debetview; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		da = new DebtAdapter();
		
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(da);
		
		lv.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				RowData d = (RowData) arg0.getAdapter().getItem(arg2);
				if( d != null )
					d.open(arg1.getContext());
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		da.load(org.getData().id);
		tvOrgInfo.setText(Html.fromHtml(orgInfo(org.getData())));
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
	
	class DebtAdapter extends BaseAdapter {
		List<RowData> data = new ArrayList<RowData>();
		
		DebtAdapter() {}
				
		public void load(String orgId) {
			data.clear();
			RowData total = new RowData();
			data.add(total);
			
			DebtDocListEx docs = new DebtDocListEx("id='" + orgId + "'", "date", true);
			for(Document<?> d : docs) {
				Object obj = d.getData();
				if(obj instanceof DgvItem) {
					RowData rd = new RowData((DgvItem)obj, d);
					total.add(rd);
					data.add(rd);
				}
				
			}
			docs.close();
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int position) { return data.get(position); }
		@Override public long getItemId(int position) { return position; }
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(DebetView.this, R.layout.debet_item_row, null);
			
			RowData dd = (RowData) getItem(position);
			if( dd != null ) {				
				TextView tv;
				String docText = dd.getType();
				
				boolean isTotal = docText.length() == 0;
				
				tv = (TextView)convertView.findViewById(R.id.tvRemark);
				tv.setText(isTotal ? "" : Html.fromHtml(docText + " <b>" + dd.getNumber() + "</b>"));

				tv = (TextView)convertView.findViewById(R.id.tvDate);
				tv.setText(isTotal ? "" : sdf.format(dd.getDate()));

				tv = (TextView)convertView.findViewById(R.id.tvNachost);
				tv.setText(Util.IntToScaleStr(dd.getNachost(), Consts.SUM_SCALE));
				tv.setTypeface(null, isTotal ? Typeface.BOLD : Typeface.NORMAL);
				
				tv = (TextView)convertView.findViewById(R.id.tvPrihod);
				tv.setText(Util.IntToScaleStr(dd.getPrihod(), Consts.SUM_SCALE));
				tv.setTypeface(null, isTotal ? Typeface.BOLD : Typeface.NORMAL);
				
				tv = (TextView)convertView.findViewById(R.id.tvRashod);
				tv.setText(Util.IntToScaleStr(dd.getRashod(), Consts.SUM_SCALE));
				tv.setTypeface(null, isTotal ? Typeface.BOLD : Typeface.NORMAL);
				
				tv = (TextView)convertView.findViewById(R.id.tvKonost);
				tv.setText(Util.IntToScaleStr(dd.getKonost(), Consts.SUM_SCALE));
				tv.setTypeface(null, isTotal ? Typeface.BOLD : Typeface.NORMAL);
			}
			return convertView;
		}
	}
}

class RowData implements DgvItem {

	String number;
	Date date;
	int nachost, prihod, konost, rashod;
	
	String type;
	Document<?> src;
	
	public RowData(DgvItem dd, Document<?> doc) {
		type = dd.getType();
		
		number = dd.getNumber();
		date = dd.getDate();
		nachost = dd.getNachost();
		prihod = dd.getPrihod();
		konost = dd.getKonost();
		rashod = dd.getRashod();
		
		try {
			src = doc.getClass().newInstance();
			src.read(doc.getRowid());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public RowData() {
		type = "";
		number = "";
		
		this.nachost = 0;
		this.prihod = 0;
		this.konost = 0;
		this.rashod = 0;
	}
	
	public void open(Context c) {
		if(src != null)
			src.open(c);
	}
	
	public void add(DgvItem item) {
		this.nachost += item.getNachost();
		this.prihod += item.getPrihod();
		this.konost += item.getKonost();
		this.rashod += item.getRashod();
	}
	
	public String getType() { return type; }
	
	@Override public String getNumber() { return number; }
	@Override public Date getDate() { return date; }
	@Override public int getNachost() { return nachost; }
	@Override public int getPrihod() { return prihod; }
	@Override public int getKonost() { return konost; }
	@Override public int getRashod() { return rashod; }
}
