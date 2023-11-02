package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class IncassEditEx extends IncassEdit {
	private static final String KEYBOARD_VISIBLE = "keyboard_visible";
	private static final int CLEAR_SUMS = 100;
	private static int REMARK_DLG = R.id.remark_dlg;
	
	ArrayList<Item> deliveries = new ArrayList<Item>();
	//ArrayList<String> agents = new ArrayList<String>();
	HashMap<DlvKey, Integer> sums = new HashMap<DlvKey, Integer>();
	ItemsAdapter adapter;
	boolean autoMode;

	Date minDate;
	Date check = new Date();
	private ImageButton btnKeyboard;
	private LinearLayout llKeyboard;
	private ImageButton btnSign;
	
	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		btnKeyboard = (ImageButton) findViewById(R.id.btnKeyboard);
		llKeyboard = (LinearLayout) findViewById(R.id.llKeyboard);
		btnSign = (ImageButton) findViewById(R.id.btnSign);
		
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
			rb.setChecked(true);
		}

		rb = (RadioButton) findViewById(R.id.rbCustom);
		rb.setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { changeMode(false); }
		});
		if( !autoMode ) {
			keyHelper.setTargetID(-1);
			rb.setChecked(true);
		}
		
		EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(ie.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		View v = findViewById(R.id.btnCalc);
		v.setOnClickListener( new View.OnClickListener() {			
			@Override public void onClick(View v) { distributeSum(); }
		});
		v.setEnabled(autoMode);
		
		findViewById(R.id.edCount).setEnabled(autoMode);
		

		loadDeliveries(org);
//		Spinner sp = (Spinner)findViewById(R.id.spAgents);
//		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) { changeAgent(arg2); }
//			@Override public void onNothingSelected(AdapterView<?> arg0) {}
//		});
//		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, agents);
//		sp.setAdapter(aa);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.addHeaderView(View.inflate(this, R.layout.incass_row_header, null));
		adapter = new ItemsAdapter();
		adapter.refreshData(null);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if(pos > 0)
					selectDelivery((Item) adapter.getItem(pos-1));
			}
		});
		
		btnKeyboard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean val = true;
				
				if(llKeyboard.isShown()){
					llKeyboard.setVisibility(View.GONE);
					val = false;
				}else{
					llKeyboard.setVisibility(View.VISIBLE);
					val = true;
				}
				
				Editor ed = getPreferences(Context.MODE_PRIVATE).edit();
				ed.putBoolean(KEYBOARD_VISIBLE, val);
				ed.commit();
			}
		});
		
		llKeyboard.setVisibility(getPreferences(Context.MODE_PRIVATE).getBoolean(KEYBOARD_VISIBLE, true) ? View.VISIBLE : View.GONE);
		btnSign.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(REMARK_DLG);
			}
		});
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CLEAR_SUMS ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Вопрос");
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
		}else if( id == REMARK_DLG)
			return createRemarkDlg();
		
		return super.onCreateDialog(id);
	}
	
	private Dialog createRemarkDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.remark, null);
		builder.setTitle(R.string.incass_remark_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doc.getData().remark = ((EditText)((AlertDialog)dialog).findViewById(R.id.edRemark)).getText().toString().trim();
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == REMARK_DLG)
			prepareRemarkDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	private void prepareRemarkDlg(Dialog dialog) {
		((EditText)(dialog).findViewById(R.id.edRemark)).setText(doc.getData().remark);
	}

	private void loadDeliveries(Org org) {
		
		for(IncassItem item : ((IncassEx)doc.getData()).items) {
			sums.put(new DlvKey(item), item.sum);
		}

		HashSet<String> aset = new HashSet<String>();
		
		DeliveryEx d = new DeliveryEx();
		String table = DataObjectInfo.getInstance().getTableName(d.getClass());
		DbReader r = new DbReader();
		String where = "id='" + org.id + "' and sumD > 0";
		boolean bdo = r.select(d, table, where, "date");
		while(bdo) {
			Item item = new Item(d); 
			deliveries.add(item);
			d = new DeliveryEx();
			bdo = r.selectNext(d);
			aset.add(item.dogovor);
		}
		r.close();
		
//		agents.add(getString(R.string.all));
//		for(String ai : aset) {
//			agents.add(ai);
//		}
	}

//	void changeAgent(int selectedAgent) {
//		String agent = (selectedAgent == 0) ? null : agents.get(selectedAgent);
//		adapter.refreshData(agent);
//	}
	
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
	
	void changeMode(boolean isAutoMode) {
		if(autoMode == isAutoMode)
			return;
		
		if( doc.isExported() )
			return;

		if(sums.size() > 0 || getSum() > 0)
			showDialog(CLEAR_SUMS);
		
		autoMode = isAutoMode;
		if( !autoMode ) {
			keyHelper.setTargetID(-1);
		} else {
			keyHelper.setTargetID(R.id.edCount);
		}
		findViewById(R.id.btnCalc).setEnabled(autoMode);
		findViewById(R.id.edCount).setEnabled(autoMode);
	}
	
	void sayAvailSum(boolean noSay0) {
		int availSum = getSum();
		for(Entry<DlvKey, Integer> e : sums.entrySet())
			availSum -= e.getValue();

		if( !noSay0 || availSum > 0 ) {
			String str = getString(R.string.remain) + Util.IntToScaleStr(availSum, Consts.SUM_SCALE, Util.DEC_DELIM, false); 
			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		}
	}
	
	void selectDelivery(Item i) {
		if( doc.isExported() )
			return;
		
		int ts = getSum();
		Integer sum = sums.get(i.dlv);
		if(autoMode) {
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			doc.read(doc.getRowid());
			if( !doc.isExported() ) {
				save();
				IncassEx ie = (IncassEx)doc.getData();
				if( ie.items == null || ie.items.size() == 0 ) {
					doc.delete();
					DocType.getCurDoc().refreshDocSum(doc.getId());
				}
			}
			finish();
			return true;
		} 
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void setRemark(Incass incass) {
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
		
		public DlvKey(DeliveryEx d) {
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
		public int sumDoc;
		public String dogovor;
		public Date date;
		public Date payDate;
		
		public Item(DeliveryEx d) {
			dlv = new DlvKey(d);
			sum = d.sumD;
			sumDoc = d.sum();
			date = d.date;
			payDate = d.payDate;
		}
	}
	
	class ItemsAdapter extends BaseAdapter {

		ArrayList<Item> items = new ArrayList<Item>();
		
		public void refreshData(String agent) {
			items.clear();
			for(Item i : deliveries) {
				if( agent == null || i.dogovor.equals(agent))
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
				view = View.inflate(IncassEditEx.this, R.layout.incass_row_ex, null);
			
			Item item = (Item)getItem(position);
			if( item == null )
				return null;
			String str;
			//SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yy");
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvNumber);
			str = item.dlv.number;
			tv.setText(str);
			tv.setTextColor(item.dlv.color);

			tv = (TextView)view.findViewById(R.id.tvSumDoc);
			tv.setText( Util.IntToScaleStr(item.sumDoc, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			tv.setTextColor(item.dlv.color);
			
//			tv = (TextView)view.findViewById(R.id.tvDogovor);
//			str = item.dogovor;
//			tv.setText(str);
//			tv.setTextColor(item.dlv.color);

			tv = (TextView)view.findViewById(R.id.tvSum);
			str = Util.IntToScaleStr(item.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			Integer sum = sums.get(item.dlv);
			if( sum != null ) {
				str += "\n" + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			}
			tv.setText(str);
			tv.setTextColor(item.dlv.color);
			
			tv = (TextView)view.findViewById(R.id.tvDate);
			tv.setTextColor(item.dlv.color);
			str = Util.simpleDateFormat.format(item.date);
			str += "\n";
			str += Util.simpleDateFormat.format(item.payDate);
			tv.setText(str);
			
			return view;
		}
	}
}
