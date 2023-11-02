package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class IncassEditEx extends IncassEdit {
	
	ArrayList<Item> deliveries = new ArrayList<Item>();
	HashMap<DlvKey, Integer> sums = new HashMap<DlvKey, Integer>();
	ItemsAdapter adapter;

	Date minDate;
	Date check = new Date();
	boolean autoAddSum = false;
	
	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -5);
		minDate = c.getTime();
		
		IncassEx ie = (IncassEx) doc.getData();
		OrgImpl oi = new OrgImpl();
		Org org = (Org)oi.getData();
		org.id = doc.getId();
		oi.read();
				
		EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(ie.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.addTextChangedListener(new TextWatcher() {			
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(autoAddSum) {
					autoAddSum = false;
					return;
				}
				distributeSum();
			}			
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override public void afterTextChanged(Editable s) { }
		});
		ed.selectAll();
		
		btnSend.setEnabled(doc.isEditable());
		
		ConfigImpl ci = new ConfigImpl();
		DialogHelper.loadSpinnerWithKey(ci, "Организация", new ArrayList<KeyValue>(), (Spinner)findViewById(R.id.spFirma), ie.firmCode);
		ci.close();

		loadDeliveries(org);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new ItemsAdapter();
		adapter.refreshData();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectDelivery((Item) adapter.getItem(arg2));
			}
		});
		
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Item i = (Item) adapter.getItem(arg2);
				if( i != null ) {
					DeliveryImpl di = new DeliveryImpl();
					Delivery d = di.getData();
					d.id = doc.getId();
					d.number = i.dlv.number;
					di.read();
					di.close();
					di.open(IncassEditEx.this);
				}
				return false;
			}
		});
	}
	
	@Override
	public void postSendExecute(boolean result) {
		super.postSendExecute(result);
		if( result )
			btnSend.setEnabled(doc.isEditable());
	}
	
	private void loadDeliveries(Org org) {
		
		for(IncassItem item : ((IncassEx)doc.getData()).items) {
			sums.put(new DlvKey(item), item.sum);
		}

		Delivery d = new Delivery();
		String table = DataObjectInfo.getInstance().getTableName(d.getClass());
		DbReader r = new DbReader();
		String where = "id='" + org.id + "' and sumD > 0";
		boolean bdo = r.select(d, table, where, "");
		while(bdo) {
			Item item = new Item(d); 
			deliveries.add(item);
			d = new Delivery();
			bdo = r.selectNext(d);
		}
		r.close();
	}

	void distributeSum() {
		int sum = getSum();
		sums.clear();
		for( Item i : adapter.getItems()) {
			int cs = i.sum;
			if( sum < cs )
				cs = sum;
			sums.put(i.dlv, cs);
			sum -= cs;
			if(sum <= 0)
				break;
		}
		
		adapter.notifyDataSetChanged();
	}
	
	void selectDelivery(Item i) {
		if( !doc.isEditable() )
			return;
		
		DlvKey dk = new DlvKey(i.dlv);
		int ts = getSum();
		Integer sum = sums.get(dk);
		if( sum != null ) {
			sums.remove(dk);
			ts -= sum;
		} else {
			sums.put(dk, i.sum);
			ts += i.sum;
		}
		autoAddSum = true;
		setSum(ts);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		doc.read(doc.getRowid());
		if( !doc.isExported() ) {
			save();
			IncassEx ie = (IncassEx)doc.getData();
			if( ie.items == null || ie.items.size() == 0 ) {
				doc.delete();
				DocType.getCurDoc().refreshDocSum(doc.getId());
			}
		}

		super.onBackPressed();
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		
		IncassEx ie = (IncassEx) doc.getData();
		ie.items = new ArrayList<IncassItem>();
		for(Entry<DlvKey, Integer> e : sums.entrySet()) {
			IncassItem ii = new IncassItem();
			ii.date = e.getKey().date;
			ii.number = e.getKey().number;
			ii.sum = e.getValue();
			ie.items.add(ii);
		}
		
		ie.firmCode = ((KeyValue)((Spinner)findViewById(R.id.spFirma)).getSelectedItem()).key.toString();
	}
	
	class DlvKey {
		public Date date;
		public String number;
		public int color = Color.BLACK;
		
		public DlvKey(Delivery d) {
			date = d.date;
			number = d.number;
			
			if(d.sumD > 0 && date.before(check) && date.after(minDate))
				color = Color.RED;
		}
		
		public DlvKey(IncassItem item) {
			date = item.date;
			number = item.number;
		}
		
		public DlvKey(DlvKey dlv) {
			date = new Date(dlv.date.getTime());
			number = new String(dlv.number);
		}

		@Override
		public int hashCode() {
			return (date.toString() + number).hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof DlvKey) {
				DlvKey ref = (DlvKey)o;
				return date.equals(ref.date) && number.equals(ref.number);
			}
			return false;
		}
	}
	
	class Item {
		public DlvKey dlv;
		public int sum;
		
		public Item(Delivery d) {
			dlv = new DlvKey(d);
			sum = (int)d.sumD;
		}
	}
	
	class ItemsAdapter extends BaseAdapter {

		ArrayList<Item> items = new ArrayList<Item>();
		
		public void refreshData() {
			items.clear();
			for(Item i : deliveries)
				items.add(i);
			notifyDataSetChanged();
		}
		
		public ArrayList<Item> getItems() { return items; }
		
		@Override public int getCount() { return items.size(); }

		@Override public Object getItem(int position) { return (position<items.size()) ? items.get(position) : null; }

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(IncassEditEx.this, R.layout.incass_row, null);
			
			Item item = (Item)getItem(position);
			if( item == null )
				return null;
			
			String str;
			SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvNumber);
			str = item.dlv.number;
			tv.setText(str);
			tv.setTextColor(item.dlv.color);

			tv = (TextView)view.findViewById(R.id.tvDate);
			str = sd.format(item.dlv.date);
			tv.setText(str);
						
			tv = (TextView)view.findViewById(R.id.tvSum);
			int isum = item.sum;
			Integer sum = sums.get(item.dlv);
			if( sum != null )
				isum -= sum;

			str = Util.IntToScaleStr(isum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			tv.setText(str);

			CheckBox cb = (CheckBox)view.findViewById(R.id.cbUsed);
			cb.setChecked(sum != null);
			return view;
		}
		
	}
}
