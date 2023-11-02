package com.grsoft.napoleon;

import java.io.File;
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
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.modules.print.util.BTPrinterSettings;
import com.grsoft.napoleon.printsources.SilentReflector;
import com.grsoft.napoleon.printsources.SupplSource;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.WaitDlg;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class IncassEditEx extends IncassEdit {
	
	private static final int CLEAR_SUMS = 100;
	ArrayList<Item> deliveries = new ArrayList<Item>();
	HashMap<DlvKey, Integer> sums = new HashMap<DlvKey, Integer>();
	ItemsAdapter adapter;
	boolean autoMode, inited = false;

	Date minDate;
	Date check = new Date();
	private Spinner spFirma;
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	private ImageButton btnPrint;
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	protected String fileName = "";
	
	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (NPrinter.SEND_TXT_FILE_ACTION.equals(action)) {
				if (bluetoothAdapter == null)
					Toast.makeText(context, "Bluetooth недоступен",
							Toast.LENGTH_LONG).show();
				else {
					fileName = intent.getStringExtra("file");
					if (!bluetoothAdapter.isEnabled()) {
						Intent enableBtIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent,
								REQUEST_ENABLE_BT);
					} else {
						printing();
					}
				}
			}
		}
	};
	
	protected void printing() {
		BTPrinterSettings cfg = BTPrinterHelper.getSettings(this);
		if( cfg.address.length() > 0 ){
			BTPrinterHelper.printing(cfg.address, cfg.copies, fileName, this);
			finish();
		}else {
			Toast.makeText(this, "Настройте, пожалуйста, принтер", Toast.LENGTH_SHORT).show();
			Setting.open(this, PrinterSetting.class);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
			printing();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NPrinter.SEND_TXT_FILE_ACTION);
		registerReceiver(receiver, intentFilter);
	}
	
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
				finish();
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
		ed.setText(Util.IntToScaleStr(ie.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));		
		ed.setEnabled(!autoMode);
		ed.selectAll();

		Spinner sp = (Spinner)findViewById(R.id.spDogovor);
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				OrgDogovor dog = (OrgDogovor) arg0.getAdapter().getItem(arg2);
				loadDeliveries(doc.getId(), dog.id);
				adapter.refreshData();
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		DocHelper.prepareSpinners(sp, null, ((OrgEx)org).dogovors, ie.iddog, null);
		if( ie.iddog.length() == 0 && sp.getAdapter().getCount() > 0 ) {
			sp.setSelection(0);
		}
		
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
		
		btnSend.setVisibility(View.GONE);
		
		ConfigImpl config = new ConfigImpl();
		spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms,
				spFirma, ie.supplyer);
		
		btnPrint = (ImageButton) findViewById(R.id.btnPrint);

		btnPrint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, File>() {

					@Override
					protected File doInBackground(Void... params) {
						save();
						return NPrinter.print(IncassEditEx.this, "incass",
								new IncassDataPrint((IncassEx) doc.getData()));
					}

					protected void onPreExecute() {
						showDialog(R.id.wait_dlg);
					};

					protected void onPostExecute(File result) {
						dismissDialog(R.id.wait_dlg);

						if (result != null)
							NPrinter.sendPrintTask(IncassEditEx.this, result);
					};

				}.execute((Void[]) null);
			}
		});
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
		}else if (id == R.id.wait_dlg)
			return WaitDlg.createDialog(this);
		else
			return super.onCreateDialog(id);
	}
	
	private void loadDeliveries(String orgId, String idDog) {
		
		HashSet<String> aset = new HashSet<String>();
		
		deliveries.clear();
		
		DbReader r = new DbReader();
		Class<? extends DataObject> dlvType = DbObject.getDataType(Delivery.class);
		
		try {
			Delivery d = (Delivery) dlvType.newInstance();
			String table = DataObjectInfo.getInstance().getTableName(d.getClass());
			String where = "id='" + orgId + "' and sumD > 0 and dogovor='" + idDog + "'";
			boolean bdo = r.select(d, table, where, "date");
			while(bdo) {
				Item item = new Item(d); 
				deliveries.add(item);
				d = (Delivery) dlvType.newInstance();
				bdo = r.selectNext(d);
				aset.add(item.dogovor);
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
	
	void selectDelivery(Item i) {
		if( doc.isExported() || !autoMode )
			return;
		
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
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
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
		
		if( autoMode )
			ie.params |= IncassEx.AUTO_FLAG;
		else
			ie.params &= (~IncassEx.AUTO_FLAG);
		
		Spinner sp = (Spinner)findViewById(R.id.spDogovor);
		OrgDogovor od = (OrgDogovor)sp.getSelectedItem();
		
		if(od != null)
			ie.iddog = od.id;
		
		ie.supplyer = spFirma.getSelectedItemPosition();
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
		public String dogovor;
		
		public Item(Delivery d) {
			dlv = new DlvKey(d);
			sum = d.sumD;
			dogovor = ((DeliveryEx)d).dogovor;
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
	
}

class IncassDataPrint implements DataSource {
	public static String SUM_TEXT_FORMAT = "%s, %02d";
	public String date = "";
	public String number = "";
	public String dogovor = "";
	public String name = "";
	public String torg = "";
	public String sum = "";
	protected SupplSource supplSource = new SupplSource();
	
	public IncassDataPrint(IncassEx doc) {
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		cfg.getValue(sb, "Организация");
		if (sb.length() > 0) {
			String[] firms = sb.toString().split(";");

			if (firms.length > doc.supplyer)
				supplSource.supl_name = firms[doc.supplyer];
		}

		OrgImpl impl = new OrgImpl();
		impl.getData().id = doc.id;
		impl.read();
		impl.close();

		OrgEx org = (OrgEx) impl.getData();
		String iddog = doc.iddog;
		
		for (OrgDogovor od : org.dogovors) {
			if (od.id.equals(iddog)) {
				dogovor = od.name;
				break;
			}
		}

		date = Util.simpleDateFormat.format(doc.created);
		number = doc.number;
		name = org.name;

		AgentPrefix pref = AgentPrefix.get();
		torg = pref.name;
		sum = Util.IntToScaleStr(doc.sum, Consts.SUM_SCALE, Util.DEC_DELIM,
				false);
	}

	@Override
	public boolean moveNext() {	return false; }

	@Override
	public boolean haveMoreData() { return true; }

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		if (value != null && name != null) {
			value.setLength(0);
			ConfigImpl config = new ConfigImpl();

			return config.getValue(value, name)
					|| supplSource.getValue(value, name, format)
					|| SilentReflector.getFieldValue(value, name, this, format);
		} else
			return false;
	}

	@Override
	public DataSource getObject(String name) {	return null; }

	@Override
	public void calculate() {}

	@Override
	public void startPage() {}
}
