package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.calculator2.Calculator;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

public class IncassEditEx extends IncassEdit {
	public static int CALCULATOR = 1;
	private static final int CLEAR_SUMS = 100;
	ArrayList<Item> deliveries = new ArrayList<Item>();
	HashMap<DlvKey, Integer> sums = new HashMap<DlvKey, Integer>();
	ItemsAdapter adapter;
	Date minDate;
	Date check = new Date();
	private EditText edCount;
	
	@Override
	protected KeypadHelper createKeypadHelper() {return null;}
	
	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -5);
		minDate = c.getTime();
		
		IncassEx ie = (IncassEx) doc.getData();
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx)oi.getData();
		org.id = doc.getId();
		oi.read();
		
		edCount = (EditText)findViewById(R.id.edCount);
		edCount.setText(Util.IntToScaleStr(ie.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		edCount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), Calculator.class);
				i.putExtra(Calculator.START_CALC_VAL, 
						edCount.getText().toString().replace(',', '.'));
				startActivityForResult(i,CALCULATOR);
			}
		});

		
		loadDeliveries(org);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new ItemsAdapter();
		adapter.refreshData(null);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectDelivery((Item) adapter.getItem(arg2));
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CALCULATOR && 
				resultCode == Activity.RESULT_OK && 
				data != null)
			edCount.setText(data.getStringExtra(
					Calculator.CALCULATOR_RESULT_VALUE).replace('.', ','));
	}

	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CLEAR_SUMS ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Вопрос");
			b.setMessage("Очистить суммы?");
			b.setNegativeButton("Нет", null);
			b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sums.clear();
					setSum(0);
					adapter.notifyDataSetChanged();
				}
			});
			
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	private void loadDeliveries(OrgEx org) {
		
		for(IncassItem item : ((IncassEx)doc.getData()).items) {
			sums.put(new DlvKey(item), item.sum);
		}

		HashSet<String> aset = new HashSet<String>();
		
		Delivery d = new Delivery();
		String table = DataObjectInfo.getInstance().getTableName(d.getClass());
		DbReader r = new DbReader();
		String where = String.format("'sumD' > 0 and id='%s'",org.id);
		boolean bdo = r.select(d, table, where, "date");
		while(bdo) {
			Item item = new Item(d); 
			deliveries.add(item);
			d = new Delivery();
			bdo = r.selectNext(d);
			aset.add(item.remark);
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
		
		sayAvailSum(true);
		adapter.notifyDataSetChanged();
	}
	
	void sayAvailSum(boolean noSay0) {
		int availSum = getSum();
		for(Entry<DlvKey, Integer> e : sums.entrySet())
			availSum -= e.getValue();

		if( !noSay0 || availSum > 0 ) {
			String str = "Осталось: " + Util.IntToScaleStr(availSum, Consts.SUM_SCALE, Util.DEC_DELIM, false); 
			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		}
	}
	
	void selectDelivery(Item i) {
		if( doc.isExported() )
			return;
		
		int ts = getSum();
		Integer sum = sums.get(i.dlv);
		
		int availSum = ts;				
		if( sum != null ) {
			sums.remove(i.dlv);				
		} else {
			for(Entry<DlvKey, Integer> e : sums.entrySet())
				availSum -= e.getValue();
			
			if( availSum > 0 ) {
				int is = i.sum;
				if( is > availSum ) is = availSum;
				sums.put(i.dlv, is);
			}					
		}
		
		sayAvailSum(false);
		adapter.notifyDataSetChanged();
		return;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			doc.read(doc.getRowid());
			if( !doc.isExported() ) {
				save();
//				IncassEx ie = (IncassEx)doc.getData();
//				if( ie.items == null || ie.items.size() == 0 ) {
//					doc.delete();
//					DocType.getCurDoc().refreshDocSum(doc.getId());
//				}
			}
			finish();
			return true;
		} 
		return super.onKeyDown(keyCode, event);
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
		public String remark;
		
		public Item(Delivery d) {
			dlv = new DlvKey(d);
			sum = (int) d.sumD;
		}
	}
	
	class ItemsAdapter extends BaseAdapter {

		ArrayList<Item> items = new ArrayList<Item>();
		
		public void refreshData(String agent) {
			items.clear();
			for(Item i : deliveries) {
				if( agent == null || i.remark.equals(agent))
					items.add(i);
			}
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
			SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yy");
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvNumber);
			str = item.dlv.number;
			tv.setText(str);
			tv.setTextColor(item.dlv.color);

			tv = (TextView)view.findViewById(R.id.tvDlvDate);
			str = sd.format(item.dlv.date);
			tv.setText(str);
			tv.setTextColor(item.dlv.color);
			
			tv = (TextView)view.findViewById(R.id.tvRemark);
			str = item.remark;
			tv.setText(str);
			tv.setTextColor(item.dlv.color);

			tv = (TextView)view.findViewById(R.id.tvSum);
			str = Util.IntToScaleStr(item.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			Integer sum = sums.get(item.dlv);
			if( sum != null ) {
				str += "\n" + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			}
			tv.setText(str);
			tv.setTextColor(item.dlv.color);
			return view;
		}
		
	}
}
