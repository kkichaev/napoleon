package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class DebetView extends DocumentsBase {

	DebetAdapter adapter;
	
	public static void open(Context ctx, String orgId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		ctx.startActivity(i);
	}
	
	@Override protected int getContentViewID() { return R.layout.debet_docs; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		adapter = new DebetAdapter();
		
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(adapter);		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) { adapter.clicked(arg2); }
		});
		
		findViewById(R.id.NameTitle).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { adapter.moveUp(); }
		});
	}

	@Override
	protected void onDestroy() {
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

	@Override
	public void onBackPressed() {
		if(adapter.selected != null) {
			adapter.refresh();
		} else
			super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.refresh();
	}
	
	class DocData implements Comparable<DocData> {
		PaymentEx payment;
		DeliveryEx delivery;

		String number;
		Long sum;
		Date date;

		public DocData(PaymentEx p) {
			payment = p;
			sum = p.sum;
			number = p.number;
			date = p.date;
		}

		public DocData(DeliveryEx p) {
			delivery = p;
			sum = p.sumD;
			number = p.number;
			date = p.date;
		}

		@Override
		public int compareTo(DocData another) {
            int cmp;
            cmp = date.compareTo(another.date);
            if( cmp < 0 ) return 1;
            if( cmp > 0 ) return -1;

            return number.compareTo(another.number);
		}
	}
	
	class DebetData implements Comparable<DebetData> {
		public String name = "";
		public String id = "";
		public Dogovor d;
		int sum = 0;
		boolean loadedDocs = false;

		ArrayList<DocData> leafs = new ArrayList<DocData>();
		
		public DebetData(Dogovor d) {
			name = d.name;
			id = d.id;
			this.d = d;
		}

		@Override
		public int compareTo(DebetData another) { return name.compareTo(another.name); }
	}

	class DebetAdapter extends BaseAdapter {
		ArrayList<DebetData> dogovorData = new ArrayList<DebetData>();
		DebetData selected = null;

		private void loadDogovors() {
			OrgEx o = (OrgEx) org.getData();

			dogovorData.clear();

			for(Dogovor d : o.dogovors) {
				dogovorData.add(new DebetData(d));
			}

			loadDocs();

			Iterator<DebetData> f = dogovorData.iterator();
			while(f.hasNext()) {
				if(f.next().loadedDocs == false) {
					f.remove();
				}
			}
		}
		
		DebetData findDogovor(String id) {
			for(DebetData dd : dogovorData)
				if(dd.id.equals(id))
					return dd;
			
			return null;
		}
		
		private void loadDocs() {
			PaymentEx p = new PaymentEx();
			String where = "id='" + ((OrgEx)org.getData()).ido + "'";

			DataTraveler.travel(PaymentEx.class, new DataTraveler.Travel<PaymentEx>(true) {
				@Override
				public boolean travel(DataTraveler<PaymentEx> item) {
					DebetData dd = findDogovor(item.data.dogovor);
					if( dd != null ) {
						dd.leafs.add(new DocData(item.data));
						dd.sum += item.data.sum;
						dd.loadedDocs = true;
					}
					return true;
				}
			}, where);

			DataTraveler.travel(DeliveryEx.class, new DataTraveler.Travel<DeliveryEx>(true) {
				@Override
				public boolean travel(DataTraveler<DeliveryEx> item) {
					DebetData dd = findDogovor(item.data.dogovor);
					if( dd != null ) {
						dd.leafs.add(new DocData(item.data));
						dd.sum += item.data.sumD;
						dd.loadedDocs = true;
					}
					return true;
				}
			}, where);

			for(DebetData dd : dogovorData)
				Collections.sort(dd.leafs);
		}
		
		public void refresh() {
			selected = null;

			loadDogovors();
			notifyDataSetChanged();
		}
		
		public void clicked(int pos) {
			if( selected == null ) {
				selected = (pos < dogovorData.size()) ? dogovorData.get(pos) : null;
				if( selected != null ) {
					TextView tv = (TextView)findViewById(R.id.NameTitle);
					tv.setText(selected.name);
					notifyDataSetChanged();
				}
			} else {
				DocData dd = selected.leafs.get(pos);
				if(dd.delivery != null) {
					DeliveryImpl di = new DeliveryImpl();
					DeliveryEx dsrc = (DeliveryEx) di.getData();
					dsrc.id = dd.delivery.id;
					dsrc.number = dd.number;
					di.read();
					di.close();
					di.open(DebetView.this);
				}
			}
		}
		
		public void moveUp() {
			if( selected != null ) {
				TextView tv = (TextView)findViewById(R.id.NameTitle);
				tv.setText("");
				selected = null;
				notifyDataSetChanged();
			}
		}
		
		@Override
		public int getCount() { 
			return (selected == null) ? dogovorData.size() : selected.leafs.size();
		}

		@Override
		public Object getItem(int position) {
			if( selected == null )
				return (position < dogovorData.size()) ? dogovorData.get(position) : null;
			return (position < selected.leafs.size()) ? selected.leafs.get(position) : null;
		}

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			Object item = getItem(position);
			if( view == null )
				view = View.inflate(DebetView.this, R.layout.debs_list_row, null);
			if( item instanceof DebetData )
				drawDebetData(view, (DebetData)item);
			else
				drawDocData(view, (DocData)item);
			return view;
		}

		private void drawDocData(View view, DocData item) {
			SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
			String txt;
			TextView tv;
			
			tv = (TextView)view.findViewById(R.id.tvName);
			txt = item.number;
			tv.setText(txt);
			
			tv = (TextView)view.findViewById(R.id.tvDate);
			tv.setVisibility(View.VISIBLE);
			txt = sf.format(item.date);
			tv.setText(txt);

			tv = (TextView)view.findViewById(R.id.tvSum);
			txt = Util.IntToScaleStr(item.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			tv.setText(txt);
			

			view.findViewById(R.id.ivFolder).setVisibility(View.INVISIBLE);
		}

		private void drawDebetData(View view, DebetData item) {
			view.findViewById(R.id.ivFolder).setVisibility(View.VISIBLE);
			view.findViewById(R.id.tvDate).setVisibility(View.GONE);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(item.name);
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(item.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		}
		
	}
}
