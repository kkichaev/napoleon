package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class IncassEditEx extends IncassEdit {
	
	private static final int CLEAR_SUMS = 100;
	ArrayList<Item> deliveries = new ArrayList<Item>();
	HashMap<DlvKey, Integer> sums = new HashMap<DlvKey, Integer>();
	ItemsAdapter adapter;
	boolean autoMode, inited = false;

	Date minDate;
	Date check = new Date();
	private Date syncDate;
	private HashMap<String, Integer> prevSums = new HashMap<String, Integer>();
	
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
		ed.setEnabled(false);
		ed.selectAll();
		
		for(IncassItem item : ((IncassEx)doc.getData()).items) {
			sums.put(new DlvKey(item), item.sum);
		}

		initSyncDate();
		
		DatePeriod dp = new DatePeriod(syncDate, doc.getData().created);
		dp.periodType = DatePeriod.CREATED;
		DocList docs = IncassDoc.instance().docList(doc.getId(), null, dp);

		prevSums.clear();
		
		for(Document<?> d : docs){
			IncassEx iex = (IncassEx)d.getData();
			
			if (!iex.created.equals(doc.getData().created) && iex != null && iex.items != null && iex.items.size() > 0){
				for (IncassItem ii : iex.items){
					int val = ii.sum;
					
					Integer s = prevSums.get(ii.number);
					
					if(s != null)
						val += s;
					
					prevSums.put(ii.number, val);
				}
			}
		}
		
		loadDeliveries(org.id);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new ItemsAdapter();
		adapter.refreshData();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, final int pos, long arg3) {
				if(doc.isEditable())
					InputNumberDlg.open(IncassEditEx.this, new InputNumber() {
						
						@Override
						public int getValue() {
							Item item = (Item) adapter.getItem(pos);
							Integer result = sums.get(item.dlv);
							int is = prevSums.get(item.dlv.number) == null ? item.sum : item.sum - prevSums.get(item.dlv.number);
							
							return result == null ? is : result;
						}
						
						@Override
						public void applayInput(int value, Object... params) {
							Item item = (Item) adapter.getItem(pos);
							
							if(value > item.sum)
								Toast.makeText(IncassEditEx.this, R.string.incass_limit_exceed, Toast.LENGTH_SHORT).show();
							else{
								Integer v = sums.get(item.dlv);
								int sum = getSum();
								
								if(v != null)
									sum -= v;
									
								sum += value;
								setSum(sum);
								
								if(value > 0)
									sums.put(item.dlv, value);
								else
									sums.remove(item.dlv);
								
								adapter.notifyDataSetChanged();
							}
						}
					}, Consts.SUM_SCALE, false,  getString(R.string.value));
			}
		});
		
		btnSend.setVisibility(View.GONE);
		
	}
	
	@Override
	protected void save() {
		super.save();
		
		DebtDocEx.instance().refreshDocSum(doc.getId());
	}
	
	protected void initSyncDate() {
		Cursor c = null;
		
		try{
			StringBuilder sql = new StringBuilder();
			sql.append("select max(created) from syncinfo where (([syncparam] & ").append(SyncInfo.DEBT).append(" ) == ").append(SyncInfo.DEBT).append(")  and result=1");
			
			c = DataBaseManager.getDataBase().rawQuery(sql.toString(), null);
			
			if(c.moveToFirst())
				syncDate = new Date(c.getLong(0));
			else 
				syncDate = new Date();
			
			c.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c != null){
				c.close();
			}
		}
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
		
		DbReader r = new DbReader();
		Class<? extends DataObject> dlvType = DbObject.getDataType(Delivery.class);
		
		try {
			Delivery d = (Delivery) dlvType.newInstance();
			String table = DataObjectInfo.getInstance().getTableName(d.getClass());
			String where = "id='" + orgId + "' and sumD > 0";
			boolean bdo = r.select(d, table, where, "date");
			while(bdo) {
				Item item = new Item(d); 
				
				Integer s = prevSums.get(d.number);
				
				if(s == null)
					s = 0;
				
				if(d.sumD - s > 0)
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
			Integer cs = i.sum;
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
			int is = prevSums.get(item.dlv.number) == null ? item.sum : item.sum - prevSums.get(item.dlv.number);
			str = Util.IntToScaleStr(is, Consts.SUM_SCALE, Util.DEC_DELIM, false);
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
