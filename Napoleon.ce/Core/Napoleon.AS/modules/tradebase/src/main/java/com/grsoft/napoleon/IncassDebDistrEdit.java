package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryKey;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.IncassDebDistrItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.script.ScriptHelper;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class IncassDebDistrEdit extends IncassEdit {
	
	private static final int CLEAR_SUMS = 100;
	ArrayList<Item> deliveries = new ArrayList<Item>();
	HashMap<DlvKey, Long> sums = new HashMap<DlvKey, Long>();
	ItemsAdapter adapter;
	boolean autoMode, inited = false;
	public static Class<? extends Activity> editActivity = IncassDebDistrEdit.class; 

	Date minDate;
	Date check = new Date();
	
	public static void open(Context context, CreatableDocument<? extends Incass> doc) {
		Intent i = new Intent(context, editActivity);
		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	
	@Override protected int getContentViewID() { return R.layout.incass_deb_distr; }

	@Override
	protected void childInit(Incass incass, Org org) {
		super.childInit(incass, org);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -5);
		minDate = c.getTime();

		IncassDebDistr ie = (IncassDebDistr) incass;
		loadDeliveries(org);

		autoMode = ((ie.params & IncassDebDistr.AUTO_FLAG) != 0);
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

		final EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(ie.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.setEnabled(!autoMode);
		ed.selectAll();
		ed.setOnFocusChangeListener((v,f)->ed.post(()->ed.selectAll()));

		for(IncassDebDistrItem item : ((IncassDebDistr)doc.getData()).items) {
			sums.put(new DlvKey(item), (long)item.sum);
		}


		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = createAdapter();
		adapter.refreshData();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectDelivery((Item) adapter.getItem(arg2));
			}
		});

		ScriptHelper.initView(this, IncassDoc.instance().getObjectName(), doc.getData().created, doc.getId());
	}

//	@Override
//	protected void init(Bundle bundle) {
//		super.init(bundle);
//
//	}


	protected ItemsAdapter createAdapter() { return new ItemsAdapter(); }
	
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
					if(!autoMode) {
						findViewById(R.id.edCount).requestFocus();
					}
					adapter.notifyDataSetChanged();
				}
			});
			
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected Item createItem(Delivery d) { return new Item(d); }

	protected String makeDeliveryWhere(Org o) {
		return "id='" + o.id + "'";
	}

	protected void loadDeliveries(Org o) {
		deliveries.clear();
		
		DbReader r = new DbReader();
		Class<? extends DataObject> dlvType = DbObject.getDataType(Delivery.class);
		
		try {
			Delivery d = (Delivery) dlvType.newInstance();
			String where = makeDeliveryWhere(o);
			HashSet<DlvKey> docs = null;
			if(doc.isEditable())
				where += " and sumD > 0";
			else {
				docs = new HashSet<IncassDebDistrEdit.DlvKey>();
				for(IncassDebDistrItem idi : ((IncassDebDistr)doc.getData()).items)
					docs.add(new DlvKey(idi));
			}
			boolean bdo = r.select(d, d.getTableName(), where, "date");
			while(bdo) {
				Item item = createItem(d); 
				if( docs == null || docs.contains(item.dlv)) {
					deliveries.add(item);
					d = (Delivery) dlvType.newInstance();
				}
				bdo = r.selectNext(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		r.close();

		if( inited ) {
			sums.clear();
			if( autoMode )
				setSum(0);
		} else
			inited = true;

		if(adapter != null)
			adapter.refreshData();
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

		View v = findViewById(R.id.edCount);
		v.setEnabled(!autoMode);
		if(!autoMode) {
			((EditText)v).selectAll();
			v.requestFocus();
		}
	}
	
	void distributeSum() {
		int sum = getSum();
		sums.clear();
		for( Item i : adapter.getItems()) {
			long cs = i.sum;
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
		for(Entry<DlvKey, Long> e : sums.entrySet())
			availSum -= e.getValue();

		if( !noSay0 || availSum > 0 ) {
			String str = "Осталось: " + Util.IntToScaleStr(availSum, Consts.SUM_SCALE, Util.DEC_DELIM, false); 
			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		}
	}

	void selectDelivery(Item i) {
		if( !doc.isEditable() )
			return;
		
		long ts = getSum();
		Long sum = sums.get(i.dlv);
		
		if( !autoMode ) {
			long availSum = ts;				
			if( sum != null ) {
				sums.remove(i.dlv);				
			} else {
				for(Entry<DlvKey, Long> e : sums.entrySet())
					availSum -= e.getValue();
				
				if( availSum > 0 ) {
					long is = i.sum;
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
		setSum((int)ts);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing()){
			if( doc.isEditable() ) {
				IncassDebDistr ie = (IncassDebDistr)doc.getData();
				if(!isDocCanProcessed(ie)) {
					doc.delete();
					DocType.getCurDoc().refreshDocSum(doc.getId());
				}
				
			}
		}
	}

	protected boolean isDocCanProcessed(IncassDebDistr ie) {
		return !((ie.items == null || ie.items.size() == 0) && doc.sum() == 0);
	}

	@Override
	protected void send() {
		setDocument();
		if (isDocCanProcessed((IncassDebDistr) doc.getData()))
			super.send();
		else
			Toast.makeText(this, "Документ не может быть отправлен!", Toast.LENGTH_SHORT).show();
	}

	protected int getRowLayoutID() { return R.layout.incass_deb_distr_row; }

	protected IncassDebDistrItem createItem(Entry<DlvKey, Long> e) {
		IncassDebDistrItem ii = new IncassDebDistrItem();
		return ii;
	}

	@Override
	protected void setDocument() {
		super.setDocument();
		
		IncassDebDistr ie = (IncassDebDistr) doc.getData();
		ie.items = new ArrayList<IncassDebDistrItem>();
		for(Entry<DlvKey, Long> e : sums.entrySet()) {
			IncassDebDistrItem ii = createItem(e);
			ii.date = e.getKey().date;
			ii.number = e.getKey().number;
			ii.sum = (int)((long)e.getValue());
			ie.items.add(ii);
		}
		
		if( autoMode )
			ie.params |= IncassDebDistr.AUTO_FLAG;
		else
			ie.params &= (~IncassDebDistr.AUTO_FLAG);
		
		ie.autoMode = autoMode ? 1 : 0;
		
	}
	
	class DlvKey extends DeliveryKey {
		public int color = Color.BLACK;
		
		public DlvKey(Delivery d) {
			super(d);

			if(d.sumD > 0 && date.before(check) && date.after(minDate))
				color = Color.RED;
		}

		public DlvKey(IncassDebDistrItem item) {
			date = item.date;
			number = item.number;
		}		
	}
	
	class Item {
		public DlvKey dlv;
		public long sum;
		
		public Item(Delivery d) {
			dlv = new DlvKey(d);
			sum = d.sumD;
		}
	}
	
	class ItemsAdapter extends BaseAdapter {

		ArrayList<Item> items = new ArrayList<Item>();
		
		public void refreshData() {
			items.clear();
			for(Item i : deliveries) {
				if (filter(i))
					items.add(i);
			}

			notifyDataSetChanged();
		}

		public boolean filter(Item i){ return true; }

		public ArrayList<Item> getItems() { return items; }

		@Override public int getCount() { return items.size(); }

		@Override public Object getItem(int position) { return (position<items.size()) ? items.get(position) : null; }

		@Override public long getItemId(int position) { return position; }

		protected void childDraw(View view, Item item, int color) {}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(IncassDebDistrEdit.this, getRowLayoutID(), null);
			
			Item item = (Item)getItem(position);
			if( item == null )
				return null;
			Long sum = sums.get(item.dlv);

			int color = sum == null ? item.dlv.color : Color.BLUE;
			String str;
			SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvNumber);
			str = item.dlv.number;
			tv.setText(str);
			tv.setTextColor(color);

			tv = (TextView)view.findViewById(R.id.tvDlvDate);
			str = sd.format(item.dlv.date);
			tv.setText(str);
			tv.setTextColor(color);
			
//			tv = (TextView)view.findViewById(R.id.tvDogovor);
//			str = item.dogovor;
//			tv.setText(str);
			tv.setTextColor(color);

			tv = (TextView)view.findViewById(R.id.tvSum);
			str = Util.IntToScaleStr(item.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			if( sum != null ) {
				str += "\n" + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			}
			tv.setText(str);
			tv.setTextColor(color);

			childDraw(view, item, color);
			return view;
		}
		
	}
}
