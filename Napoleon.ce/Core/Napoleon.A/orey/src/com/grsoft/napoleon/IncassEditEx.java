package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class IncassEditEx extends IncassEdit {
	
	private static final int CLEAR_SUMS = 100;
	private static final int SUM_LIMIT = 99999;
	ArrayList<Item> deliveries = new ArrayList<Item>();
	HashMap<DlvKey, Integer> sums = new HashMap<DlvKey, Integer>();
	ItemsAdapter adapter;
	boolean autoMode, inited = false;

	Date minDate;
	Date check = new Date();
	
	private EditText edCount;

	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		View v;
		v = findViewById(R.id.btnOK);
		v.setVisibility(View.VISIBLE);
		v.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				save();
			}
		});

		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -5);
		minDate = c.getTime();
		
		IncassEx ie = (IncassEx) doc.getData();
		OrgImpl oi = new OrgImpl();
		Org org = (Org)oi.getData();
		org.id = doc.getId();
		oi.read();
		
		autoMode = ((ie.params & IncassEx.AUTO_FLAG) != 0);
		RadioButton rb;
		rb = (RadioButton) findViewById(R.id.rbAuto);
		rb.setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { changeMode(true); }
		});
		if( autoMode ) {			
			keyHelper.setTargetID(-1);
			rb.setChecked(true);
		}

		rb = (RadioButton) findViewById(R.id.rbCustom);
		rb.setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { changeMode(false); }
		});
		if( !autoMode ) {
			rb.setChecked(true);
		}
		
		EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		ed.setText(Util.IntToScaleStr(ie.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));		
		ed.setEnabled(!autoMode);
		ed.selectAll();

		loadDeliveries(doc.getId());
		
		Spinner sp = (Spinner)findViewById(R.id.spDover);
		ConfigImpl ci = new ConfigImpl();
		DialogHelper.loadSpinnerWithKey(ci, "Доверенность", new ArrayList<KeyValue>(), sp, ie.dover);
				
		for(IncassItem item : ((IncassEx)doc.getData()).items) {
			sums.put(new DlvKey(item), item.sum);
		}

		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new ItemsAdapter();
		adapter.refreshData();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectDelivery((Item) adapter.getItem(arg2));
			}
		});
		
		edCount = (EditText)findViewById(R.id.edCount);
		edCount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		edCount.setOnTouchListener(null);
		edCount.setOnClickListener(null);
	}
	
	@Override
	protected void send() {
		if(!checkOverheadSum())
			Toast.makeText(this, R.string.incass_overhead, Toast.LENGTH_SHORT).show();
		else
			super.send();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CLEAR_SUMS ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.question);
			b.setMessage(R.string.clear_sums);
			b.setNegativeButton(R.string.no, null);
			b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sums.clear();
					setSum(0);
					adapter.notifyDataSetChanged();
				}
			});
			
			return b.create();
		}
		else
			return super.onCreateDialog(id);
	}
	
	private void loadDeliveries(String orgId) {
		
		deliveries.clear();
		
		DbReader r = new DbReader();
		Class<? extends DataObject> dlvType = DbObject.getDataType(Delivery.class);
		
		try {
			Delivery d = (Delivery) dlvType.newInstance();
			String table = DataObjectInfo.getInstance().getTableName(d.getClass());
			String where = "id='" + orgId + "' and sumD > 0";
			boolean bdo = r.select(d, table, where, "date");
			while(bdo) {
				Item item = new Item(d); 
				deliveries.add(item);
				d = (Delivery) dlvType.newInstance();
				bdo = r.selectNext(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		r.close();

		if( inited ) {
			if( autoMode )
				setSum(0);
			sums.clear();
			adapter.notifyDataSetChanged();
		} else
			inited = true;
		
	}

	void changeMode(boolean isAutoMode) {
		if(autoMode == isAutoMode)
			return;
		
		if( doc.isExported() )
			return;

		if(sums.size() > 0 || getSum() > 0)
			showDialog(CLEAR_SUMS);
		
		autoMode = isAutoMode;
		
		keyHelper.setTargetID((autoMode) ? -1 : R.id.edCount);
		findViewById(R.id.edCount).setEnabled(!autoMode);
	}
	
	void selectDelivery(final Item i) {
		if( doc.isExported()  )
			return;
		
		if (autoMode){
		int ts = getSum();
		Integer sum = sums.get(i.dlv);
		if( sum != null ) {
			sums.remove(i.dlv);
			ts -= sum;
		} else {
			sums.put(i.dlv, i.sum);
			ts += i.sum;
		}
		setSum(ts);
		
		}
		
		else {
			InputNumberDlg.open(this, new InputNumber() {
				@Override
				public int getValue() {
					return (sums.get(i.dlv)!=null)?sums.get(i.dlv): i.sum;
				}

				@Override
				public boolean useComma() {
					return true;
				}

				@Override
				public void applayInput(int value, Object... params) {
					int ts = getSum();
					Integer sum = sums.get(i.dlv);
					
					if( sum != null ) {
						sums.remove(i.dlv);
						ts -= sum;
						
					} 
					
					sums.put(i.dlv, value);
					ts += value;
					
					setSum(ts);
					adapter.notifyDataSetChanged();
				}
			}, Consts.SUM_SCALE, true, getString(R.string.enter_incass_sum), false);
			
			
			/*
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.enter_incass_sum);
			final EditText et = (EditText) getLayoutInflater()
					.inflate(R.layout.incass_enter_sum, null);
			Integer s=sums.get(i.dlv);
			int hintedSum = (s!=null) ? hintedSum=s : i.sum;
			et.setText(Util.IntToScaleStr(hintedSum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			b.setView(et);
			b.setNegativeButton(R.string.cancel, null);
			b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

					if (et.getText().toString().trim().length() > 0){
					int add_sum=Util.StrToScale(et.getText().toString(), Consts.QTY_SCALE);
					int ts = getSum();
					Integer sum = sums.get(i.dlv);
					if( sum != null ) {
						sums.remove(i.dlv);
						ts -= sum;
						
					} 
						sums.put(i.dlv, add_sum);
						ts += add_sum;
					
					
					setSum(ts);
					adapter.notifyDataSetChanged();
					}
				}
			});
			AlertDialog alertDialog = b.create();
			alertDialog.show();*/
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if(!checkOverheadSum())
				Toast.makeText(this, R.string.incass_overhead, Toast.LENGTH_SHORT).show();
			else{
				doc.read(doc.getRowid());
				if( !doc.isExported() ) {
					save();
					IncassEx ie = (IncassEx)doc.getData();
					if( (ie.items == null || ie.items.size() == 0) && doc.sum() == 0) {
						doc.delete();
						DocType.getCurDoc().refreshDocSum(doc.getId());
					}
				}
				finish();
			}
			return true;
		} 
		
		return super.onKeyDown(keyCode, event);
	}

	public boolean checkOverheadSum() {
		double sum = 0;
		
		try{
			sum = Double.parseDouble(edCount.getText().toString().trim().replace(",", "."));
		}catch(Exception e){
			Toast.makeText(this, R.string.integer_input_error, Toast.LENGTH_SHORT).show();
		}
		
		return sum <= SUM_LIMIT;
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
		
		if( autoMode )
			ie.params |= IncassEx.AUTO_FLAG;
		else
			ie.params &= (~IncassEx.AUTO_FLAG);
		
		Spinner sp = (Spinner)findViewById(R.id.spDover);
		KeyValue kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			ie.dover = kv.key.toString();
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
		
		public Item(Delivery d) {
			dlv = new DlvKey(d);
			sum = (int)d.sumD;
		}
	}
	
	class ItemsAdapter extends BaseAdapter {

		ArrayList<Item> items = new ArrayList<Item>();
		
		public void refreshData() {
			items.clear();
			for(Item i : deliveries) {
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
			SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvNumber);
			str = item.dlv.number;
			tv.setText(str);
			tv.setTextColor(item.dlv.color);

			tv = (TextView)view.findViewById(R.id.tvDlvDate);
			str = sd.format(item.dlv.date);
			tv.setText(str);
			tv.setTextColor(item.dlv.color);
			
//			tv = (TextView)view.findViewById(R.id.tvDogovor);
//			str = item.dogovor;
//			tv.setText(str);
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
	@Override
	protected void save(){
		int sumOfSums=0;

		Iterator<DlvKey> myVeryOwnIterator = sums.keySet().iterator();
		while(myVeryOwnIterator.hasNext()) {
		    DlvKey key=(DlvKey)myVeryOwnIterator.next();
		    Integer value= sums.get(key);
		   sumOfSums+=value;
		}
		
		if (getSum()<sumOfSums){
			Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.please_increase_incass_sum), Toast.LENGTH_SHORT).show();
			return;
		}
		else 
			super.save();
	}
	
	@Override
	protected int getSum() {
		try{
			EditText ed;
			ed = (EditText)findViewById(R.id.edCount);
			return Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
		}catch(Exception e){
			Toast.makeText(this, R.string.integer_input_error, Toast.LENGTH_SHORT).show();
			return doc.getData().sum;
		}
	}
}