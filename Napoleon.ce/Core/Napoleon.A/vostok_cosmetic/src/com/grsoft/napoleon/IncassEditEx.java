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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.calculator2.Calculator;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class IncassEditEx extends IncassEdit {
	
	private static final int CLEAR_SUMS = 100;
	ArrayList<Item> deliveries = new ArrayList<Item>();
	ArrayList<String> agents = new ArrayList<String>();
	HashMap<DlvKey, Integer> sums = new HashMap<DlvKey, Integer>();
	ItemsAdapter adapter;
	boolean autoMode;
	EditText edCount;

	Date minDate;
	Date check = new Date();
	private ImageButton btnRemark;
	
	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		edCount = (EditText) findViewById(R.id.edCount);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -5);
		minDate = c.getTime();
		
		IncassEx ie = (IncassEx) doc.getData();
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx)oi.getData();
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
			rb.setChecked(true);
		}
		
		edCount.setText(Util.IntToScaleStr(ie.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
//		View v = findViewById(R.id.btnCalc);
//		v.setOnClickListener( new View.OnClickListener() {			
//			@Override public void onClick(View v) { distributeSum(); }
//		});
//		v.setEnabled(autoMode);
		
		findViewById(R.id.edCount).setEnabled(autoMode);
		

		loadDeliveries(org);
		Spinner sp = (Spinner)findViewById(R.id.spAgents);
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) { changeAgent(arg2); }
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, agents);
		sp.setAdapter(aa);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new ItemsAdapter();
		adapter.refreshData(null);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectDelivery((Item) adapter.getItem(arg2));
			}
		});
		
		ImageButton btnCalc = (ImageButton) findViewById(R.id.btnCalc);
		btnCalc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), Calculator.class);
				i.putExtra(Calculator.START_CALC_VAL, 
						edCount.getText().toString().replace(',', '.'));
				startActivityForResult(i,R.id.calc_activity);
			}
		});

		btnCalc.setEnabled(!doc.isExported());
		
		btnRemark = (ImageButton) findViewById(R.id.btnRemark);
		btnRemark.setOnClickListener(onRemarkClick());
		
		updateRemarkImg();
	}
	
	private void updateRemarkImg() {
		btnRemark.setImageResource(doc.getData().remark.length() > 0 ? R.drawable.fullrem : R.drawable.emptyrem);
	}

	private OnClickListener onRemarkClick() {
		return new OnClickListener() { @Override public void onClick(View v) { showDialog(R.id.remark_dlg);	}};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.id.calc_activity && 
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
		}else if(id == R.id.remark_dlg)
			return createRemarkDlg();
		
		return super.onCreateDialog(id);
	}
	
	private Dialog createRemarkDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.remarkdlg, null));
		builder.setTitle(R.string.remark_dlg_title);
		builder.setPositiveButton(R.string.ok, okRemark());
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	private DialogInterface.OnClickListener okRemark() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(doc.isEditable()){
					doc.getData().remark = ((EditText)((Dialog)dialog).findViewById(R.id.edText)).getText().toString().trim();
					updateRemarkImg();
				}
			}
		};
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case R.id.remark_dlg:
			prepareRemarkDlg(dialog);
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	private void prepareRemarkDlg(Dialog dialog) {
		((EditText)((Dialog)dialog).findViewById(R.id.edText)).setText(doc.getData().remark);
	}

	private void loadDeliveries(OrgEx org) {
		
		for(IncassItem item : ((IncassEx)doc.getData()).items) {
			sums.put(new DlvKey(item), item.sum);
		}

		HashSet<String> aset = new HashSet<String>();
		
		DeliveryEx d = new DeliveryEx();
		String table = DataObjectInfo.getInstance().getTableName(d.getClass());
		DbReader r = new DbReader();
		String where = "ido='" + org.ido + "' and sumD > 0";
		boolean bdo = r.select(d, table, where, "payDate");
		while(bdo) {
			Item item = new Item(d); 
			deliveries.add(item);
			d = new DeliveryEx();
			bdo = r.selectNext(d);
			aset.add(item.agent);
		}
		r.close();
		
		agents.add("<все>");
		for(String ai : aset) {
			agents.add(ai);
		}
	}

	void changeAgent(int selectedAgent) {
		String agent = (selectedAgent == 0) ? null : agents.get(selectedAgent);
		adapter.refreshData(agent);
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
			String str = "Осталось: " + Util.IntToScaleStr(availSum, Consts.SUM_SCALE, Util.DEC_DELIM, false); 
			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected int getSum() {
		int sum = 0;
		for(Entry<DlvKey, Integer> e : sums.entrySet())
			sum += e.getValue();
		
		if(sum == 0)
			sum = Util.StrToScale(edCount.getText().toString(), Consts.SUM_SCALE);		
		
		return sum;
	}
	
	class SumInput extends InputNumber {

		Item i;
		public SumInput(Item i) {
			this.i = i;
		}
		
		@Override
		public void applayInput(int value, Object... params) {
			if( value == 0 )
				sums.remove(i.dlv);
			else {
				sums.put(i.dlv, value);
			}
			adapter.notifyDataSetChanged();
		}

		@Override
		public int getValue() {
			Integer sum = sums.get(i.dlv);
			if( sum == null )
				sum = i.sum;
			return sum;
		}
		
	}
	
	void selectDelivery(Item i) {
		if( doc.isExported() )
			return;
		
		InputNumberDlg.open(this, new SumInput(i), Consts.SUM_SCALE, false, "Введите сумму");
//		int ts = getSum();
//		Integer sum = sums.get(i.dlv);
//		if(autoMode) {
//			int availSum = ts;				
//			if( sum != null ) {
//				sums.remove(i.dlv);				
//			} else {
//				for(Entry<DlvKey, Integer> e : sums.entrySet())
//					availSum -= e.getValue();
//				
//				if( availSum > 0 ) {
//					int is = i.sum;
//					if( is > availSum ) is = availSum;
//					sums.put(i.dlv, is);
//				}					
//			}
//			
//			sayAvailSum(false);
//			adapter.notifyDataSetChanged();
//			return;
//		}
//		
//		if( sum != null ) {
//			sums.remove(i.dlv);
//			ts -= sum;
//		} else {
//			sums.put(i.dlv, i.sum);
//			ts += i.sum;
//		}
//		setSum(ts);
//		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			doc.read(doc.getRowid());
			if( !doc.isExported() ) {
				save();
				IncassEx ie = (IncassEx)doc.getData();
				if( ie.sum == 0 && (ie.items == null || ie.items.size() == 0 )) {
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
	protected void setDocument() {
		super.setDocument();
		
		IncassEx ie = (IncassEx) doc.getData();
		ie.items = new ArrayList<IncassItem>();
		for(Entry<DlvKey, Integer> e : sums.entrySet()) {
			IncassItem ii = new IncassItem();
			ii.date = e.getKey().date;
			ii.number = e.getKey().number;
			ii.sum = e.getValue();
			ii.id = e.getKey().id;
			ie.items.add(ii);
		}
		
		if( autoMode )
			ie.params |= IncassEx.AUTO_FLAG;
		else
			ie.params &= (~IncassEx.AUTO_FLAG);
	}
	
	@Override
	protected void setRemark(Incass incass) {}
	
	class DlvKey {
		public Date date;
		public Date payDate;
		public String number;
		public String id;
		public int color = Color.BLACK;
		
		public DlvKey(DeliveryEx d) {
			date = d.date;
			number = d.number;
			payDate = d.payDate;
			id = d.id;
			
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
		public String agent;
		
		public Item(DeliveryEx d) {
			dlv = new DlvKey(d);
			sum = (int) d.sumD;
			agent = d.agent;
		}
	}
	
	class ItemsAdapter extends BaseAdapter {

		ArrayList<Item> items = new ArrayList<Item>();
		
		public void refreshData(String agent) {
			items.clear();
			for(Item i : deliveries) {
				if( agent == null || i.agent.equals(agent))
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
			
			tv = (TextView)view.findViewById(R.id.tvAgent);
			str = item.agent;
			tv.setText(str);
			tv.setTextColor(item.dlv.color);

			if( item.dlv.payDate != null ) {
				tv = (TextView)view.findViewById(R.id.tvPayDate);
				str = sd.format(item.dlv.payDate);
				tv.setText(str);
				tv.setTextColor(item.dlv.color);
			}
			
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
