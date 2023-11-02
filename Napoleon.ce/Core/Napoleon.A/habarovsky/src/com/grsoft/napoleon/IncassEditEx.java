package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class IncassEditEx extends IncassEdit {
	
	private static final int CLEAR_SUMS = 100;
	ArrayList<Item> deliveries = new ArrayList<Item>();
	HashMap<DlvKey, Integer> sums = new HashMap<DlvKey, Integer>();
	ItemsAdapter adapter;
	boolean autoMode, inited = false;

	Date minDate;
	Date check = new Date();
//	private Spinner spDover;
	private OnFocusChangeListener onFocusSet = new OnFocusChangeListener() { @Override public void onFocusChange(View v, boolean hasFocus) { keyHelper.setTargetID(v.getId());	} };
	
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
		loadDeliveries(org.id);
		
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
		ed.setText(Util.IntToScaleStr(ie.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));		
		ed.setEnabled(!autoMode);
		ed.selectAll();
		ed.setOnFocusChangeListener(onFocusSet);
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
		
//		spDover = (Spinner) findViewById(R.id.spDover);
//		spDover.setAdapter(new SpinnerAdapter() {
//			private List<Dover> data = new ArrayList<Dover>();
//			
//			{
//				Dover dvr = new Dover();
//				DbReader reader = new DbReader();
//				boolean bdo = reader.select(dvr, DataObjectInfo.getInstance().getTableName(dvr.getClass()), null);
//				
//				while(bdo){
//					data.add((Dover) dvr.clone());
//					bdo = reader.selectNext(dvr);
//				}
//			}
//			
//			@Override
//			public void unregisterDataSetObserver(DataSetObserver observer) {}
//			
//			@Override
//			public void registerDataSetObserver(DataSetObserver observer) {}
//			
//			@Override
//			public boolean isEmpty() { return false; }
//			
//			@Override
//			public boolean hasStableIds() {	return false; }
//			
//			@Override
//			public int getViewTypeCount() {	return 0; }
//			
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) { return getDropDownView(position, convertView, parent); }
//			
//			@Override
//			public int getItemViewType(int position) { return 0; }
//			
//			@Override
//			public long getItemId(int position) { return 0;	}
//			
//			@Override
//			public Object getItem(int position) { return data.get(position); }
//			
//			@Override
//			public int getCount() { return data.size(); }
//			
//			@Override
//			public View getDropDownView(int position, View convertView, ViewGroup parent) {
//				if (convertView == null){
//					convertView = View.inflate(IncassEditEx.this, R.layout.simple_spinner_layout, null);
//				}
//				
//				Dover dvr = (Dover) getItem(position);
//				TextView tv = (TextView) convertView.findViewById(R.id.tvFirmaName);
//				tv.setText(String.format("%s (%s)", dvr.number, Util.simpleDateFormat.format(dvr.date)));
//				
//				return convertView;
//			}
//		});
//		
		IncassEx incass = (IncassEx)doc.getData();
		
		AutoCompleteTextView atv = (AutoCompleteTextView)findViewById(R.id.tvDover);
		atv.setAdapter(new DoverAdapter());
		if(incass.dvrnum.length() > 0)
			atv.setText(String.format("%s (%s)", incass.dvrnum, Util.simpleDateFormat.format(incass.dvrdate)));
		atv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if( doc.isEditable() ) {
					Dover d = (Dover) arg0.getAdapter().getItem(arg2);
					IncassEx ie = (IncassEx)doc.getData();; 
					ie.dvrnum = d.number;
					ie.dvrdate = d.date;
					doc.write();
				}
			}
		});

		atv.setOnFocusChangeListener(onFocusSet);
		atv.setInputType(InputType.TYPE_NULL);
//		for(int i = 0; i < spDover.getCount(); i++){
//			Dover d = (Dover) spDover.getItemAtPosition(i);
//			
//			if(d.number.equals(incass.dvrnum) && d.date.equals(incass.dvrdate)){
//				spDover.setSelection(i, true);
//				break;
//			}
//		}
		
		
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
		return super.onCreateDialog(id);
	}
	
	private void loadDeliveries(String orgId) {
		deliveries.clear();
		
		Item fake = new Item("Аванс", new Date(110, 0, 1));
		deliveries.add(fake);
		
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
		
		if( !doc.isEditable() )
			return;

		if(sums.size() > 0 || getSum() > 0)
			showDialog(CLEAR_SUMS);
		
		autoMode = isAutoMode;
		
		keyHelper.setTargetID((autoMode) ? -1 : R.id.edCount);
		findViewById(R.id.edCount).setEnabled(!autoMode);
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
		keyHelper.setTargetID(-1);
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
		if( !doc.isEditable() )
			return;
		
		int ts = getSum();
		if( i.isFake ) {
			boolean needExit = sums.containsKey(i.dlv);
			sums.remove(i.dlv);
			if( !needExit ) {
				for(Entry<DlvKey, Integer> e : sums.entrySet())
					ts -= e.getValue();
				
				sums.put(i.dlv, ts);
			}
			adapter.notifyDataSetChanged();
			return;
		}
		
		Integer sum = sums.get(i.dlv);
		
		if( !autoMode ) {
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
		
		if( sum != null ) {
			sums.remove(i.dlv);
			ts -= sum;
		} else {
			sums.put(i.dlv, i.sum);
			ts += i.sum;
		}
		setSum(ts);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing()){
			if( doc.isEditable() ) {
				IncassEx ie = (IncassEx)doc.getData();
				if( (ie.items == null || ie.items.size() == 0) && doc.sum() == 0) {
					doc.delete();
					DocType.getCurDoc().refreshDocSum(doc.getId());
				}
				
			}
		}
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
		
		ie.params |= IncassEx.SAVED;
		
//		Dover d = (Dover) spDover.getSelectedItem();
//		ie.dvrnum = d.number;
//		ie.dvrdate = d.date;
	}
	
	class DlvKey {
		public Date date;
		public String number;
		public int color = Color.BLACK;
		
		public DlvKey(String number, Date date) {
			this.date = date;
			this.number = number;
		}
		
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
		public boolean isFake = false;
		
		public Item(String number, Date date) {
			dlv = new DlvKey(number, date);
			sum = 0;
			isFake = true;
		}
		
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
	
	class DoverAdapter extends BaseAdapter implements Filterable {

		List<Dover> allData = new ArrayList<Dover>();
		List<Dover> data;
		
		public DoverAdapter() {
			DataTraveler.travel(Dover.class, new DataTraveler.Travel<Dover>() {

				@Override
				public boolean travel(DataTraveler<Dover> item) {
					allData.add(item.data);
					item.data = new Dover();
					return true;
				}
			}, "");
			
			data = allData;
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			if (convertView == null)
				convertView = View.inflate(IncassEditEx.this, R.layout.simple_spinner_layout, null);
			
			Dover dvr = (Dover) getItem(position);
			TextView tv = (TextView) convertView.findViewById(R.id.tvFirmaName);
			tv.setText(dvr.toString());
			
			return convertView;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence str) {
					int len = str.length();
					FilterResults fr = new FilterResults();
					List<Dover> res = new ArrayList<Dover>();
					for(Dover d : allData) {
						if( d.number.length() >= len && d.number.substring(d.number.length() - len).equals(str) )
							res.add(d);
					}
					fr.count = res.size();
					fr.values = res;
					return fr;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence arg0, FilterResults arg1) {
					if(arg1 != null && arg1.count > 0 )
						data = (List<Dover>)arg1.values;
					else
						data = allData;
					notifyDataSetChanged();
				}				
			};
		}
		
	}
	
}
