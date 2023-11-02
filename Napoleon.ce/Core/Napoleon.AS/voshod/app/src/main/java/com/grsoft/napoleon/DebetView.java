package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class DebetView extends DocumentsBase {

	DbReader r = new DbReader();
	DbWriter w = new DbWriter();

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
		r.close();
		w.close();
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
		String dogovor;
		
		IncassEx pko = null;
		
		public DocData(PaymentEx p) {
			payment = p;
			pko = new IncassEx();
			String where = String.format("dogId='%s' and id='%s' and number='%s'",p.dogId, p.id, p.number);

			if(!r.select(pko, pko.getTableName(), where))
				pko = null;

			dogovor = getDogovor(p.dogId);
		}
		
		private String getDogovor(String id) {
			for(OrgDogovor od : ((OrgEx)org.getData()).dogovors)
				if( od.number.equals(id))
					return od.name;
			
			return "";
		}

		@Override
		public int compareTo(DocData another) {
            int cmp;
            cmp = dogovor.compareTo(another.dogovor);
            if( cmp < 0 ) return 1;
            if( cmp > 0 ) return -1;

            cmp = payment.date.compareTo(another.payment.date);
            if( cmp < 0 ) return 1;
            if( cmp > 0 ) return -1;

            return payment.number.compareTo(another.payment.number);
		}
	}
	
	class DebetData implements Comparable<DebetData> {
		public String firm = "";
		public String id = "";
		int sum = 0;
		boolean loadedDocs = false;

		ArrayList<DocData> leafs = new ArrayList<DocData>();
		
		public DebetData(KeyValue kv) {
			firm = kv.value.toString();
			id = kv.key.toString();
		}

		@Override
		public int compareTo(DebetData another) { return firm.compareTo(another.firm); }
	}

	class DebetAdapter extends BaseAdapter {
		ArrayList<DebetData> firms = new ArrayList<DebetData>();
		DebetData selected = null;

		private void loadFirms() {
			ConfigImpl c = new ConfigImpl();
			c.getData().key = "Организация";
			c.read();
			c.close();

			firms.clear();

			String value = c.getData().value;
			int pos = value.indexOf(DialogHelper.SEP_SYMBOL); 
			
			while(pos != -1) {
				String f = value.substring(0,pos);
				
				KeyValue kv = new KeyValue(f);				
				value = value.substring(pos+1);
				firms.add(new DebetData(kv));
				pos = value.indexOf(DialogHelper.SEP_SYMBOL); 
			}

			if(pos == -1 && value.length() > 0)
				firms.add(new DebetData(new KeyValue(value)));
			
			loadDocs();

			Iterator<DebetData> f = firms.iterator();
			while(f.hasNext()) {
				if(f.next().loadedDocs == false) {
					f.remove();
				}
			}
		}
		
		DebetData findFirm(String id) {
			for(DebetData dd : firms)
				if(dd.id.equals(id))
					return dd;
			
			return null;
		}
		
		private void loadDocs() {
			PaymentEx p = new PaymentEx();
			String table = DataObjectInfo.getInstance().getTableName(p.getClass());
			DbReader r = new DbReader();
			
			boolean bdo = r.select(p, table, "id='" + org.getData().id + "'");
			while( bdo ) {
				DebetData dd = findFirm(p.supplyer);
				if( dd != null ) {
					dd.leafs.add(new DocData(p));
					dd.sum += p.sum;
					dd.loadedDocs = true;
				}
				p = new PaymentEx();
				bdo = r.selectNext(p);
			}
			
			for(DebetData dd : firms)
				Collections.sort(dd.leafs);
		}
		
		public void refresh() {
			selected = null;

			loadFirms();
			notifyDataSetChanged();
		}
		
		public void clicked(int pos) {
			if( selected == null ) {
				selected = (pos < firms.size()) ? firms.get(pos) : null;
				if( selected != null ) {
					TextView tv = (TextView)findViewById(R.id.NameTitle);
					tv.setText(selected.firm);
					notifyDataSetChanged();
				}
			} else {
				if( pos < selected.leafs.size() ) {
					final DocData dd = selected.leafs.get(pos);
					if( dd.pko == null ) {
						dd.pko = new IncassEx();
						dd.pko.init(dd.payment);
					}
					InputNumberDlg.open(DebetView.this, new InputNumber() {
						@Override
						public void applayInput(int value, Object... params) {
							dd.pko.sum = value;
							w.insertRecord(dd.pko);
							adapter.notifyDataSetChanged();
						}

						@Override public int getValue() { return dd.pko.sum; }
					}, Consts.SUM_SCALE, false, "Введите сумму");
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
			return (selected == null) ? firms.size() : selected.leafs.size(); 
		}

		@Override
		public Object getItem(int position) {
			if( selected == null )
				return (position < firms.size()) ? firms.get(position) : null;
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
			txt = item.payment.number + "\n" + item.dogovor;			
			tv.setText(txt);
			
			tv = (TextView)view.findViewById(R.id.tvDate);
			tv.setVisibility(View.VISIBLE);
			txt = sf.format(item.payment.date);
			tv.setText(txt);

			tv = (TextView)view.findViewById(R.id.tvSum);
			txt = Util.IntToScaleStr(item.payment.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false); 
			if( item.pko != null ) {
				txt += "\n" + Util.IntToScaleStr(item.pko.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			}
			tv.setText(txt);
			

			view.findViewById(R.id.ivFolder).setVisibility(View.INVISIBLE);
		}

		private void drawDebetData(View view, DebetData item) {
			view.findViewById(R.id.ivFolder).setVisibility(View.VISIBLE);
			view.findViewById(R.id.tvDate).setVisibility(View.GONE);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(item.firm);
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(item.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		}
		
	}
}
