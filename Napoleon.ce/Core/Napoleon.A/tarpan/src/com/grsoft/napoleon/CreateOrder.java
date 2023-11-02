/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrdFlag;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_FROM_ID = 1;
	private static final int DIALOG_DATE_WEEK_END = 2;
	private static final int DIALOG_TIME_PICKER_TO_ID = 3;

	protected static final int DIALOG_FLAGS = R.id.flags_dlg;
	
	private boolean editMode = false;
	
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
	
	class OrdFlagItem{
		OrdFlag flag;
		boolean checked;
	}
	
	private OrdFlagItem[] flagData;
	private HashMap<String, OrdFlag> flagMap;
	
//	DateHandler dateHandler;
	TimeHandler timeHandlerFrom;
	TimeHandler timeHandlerTo;
	CheckBox cbBuh;
	TextView tvFlags;

	private int fix_date;
	private final String DELIMETER = ";";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder);
		
		cbBuh = (CheckBox) findViewById(R.id.cbBuh);
		tvFlags = (TextView) findViewById(R.id.tvFlags);
		
		init();
	}
	
	public static void open(Context context, OrderImpl order) { 
		open(context, order, true); 
	}
	
	public static void open(Context context, OrderImpl order, boolean editOldOrder) {
		Intent i = new Intent(context, CreateOrder.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		OrderEx o = (OrderEx) order.getData();
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
		oi.close();
		((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);

		if( !editMode ) 
			initOrder(o, oi.getData());

		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, o.supplyer);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", priceType, spPrices, o.sumType);

		config.getData().key = "МожноИзменятьЦену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();
		
		if( Features.DELIVERY_ADDRESS ) {
			View v = findViewById(R.id.ftrAddress);
			if( v != null ) {
				Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
				if( spAddress != null ) {
					v.setVisibility(View.VISIBLE);
					ArrayList<KeyValue> addresses = new ArrayList<KeyValue>();
					int selected = -1;
					for(OrgAddress addr : oi.getData().orgAddress) {
						KeyValue kv = new KeyValue(addr.id, addr.name);
						if( kv.key.toString().equals(o.adrCode))
							selected = addresses.size();
						addresses.add(kv);
					}
					ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, addresses);
					spAddress.setAdapter(aa);
					if( selected >= 0 && selected < spAddress.getCount())
						spAddress.setSelection(selected);
				}
			}
		}
		
		TextView tvDelay = (TextView) findViewById(R.id.tvDelay); 
		tvDelay.setOnClickListener(new DelayClickListener());
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		if( (o.params & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbCreateOrderCash)).setChecked(true);
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
		cbBuh.setChecked(o.buh > 0);
		
//		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeHandlerFrom = new TimeHandler((TextView)findViewById(R.id.tvTimeFrom), o.date, DIALOG_TIME_PICKER_FROM_ID);
		timeHandlerTo = new TimeHandler((TextView)findViewById(R.id.tvTimeTo), o.date2, DIALOG_TIME_PICKER_TO_ID);
		
		if(order.isEditable())
			tvFlags.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDialog(DIALOG_FLAGS);
				}
			});
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(order.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        
        DbReader reader = new DbReader();
		OrdFlag of = new OrdFlag();
		boolean bdo = reader.select(of, DataObjectInfo.getInstance()
				.getTableName(OrdFlag.class), null);
		ArrayList<OrdFlag> list = new ArrayList<OrdFlag>();
		
		while (bdo){
			list.add((OrdFlag) of.clone());
			bdo = reader.selectNext(of);
		}
		
		flagData = new OrdFlagItem[list.size()];
		flagMap = new HashMap<String, OrdFlag>();
		String[] custoFlags = ((OrderEx)order.getData()).customFlags.split(DELIMETER);
		HashSet<String> hashIds = new HashSet<String>(custoFlags.length);
		for(String id:custoFlags)
			hashIds.add(id);
		
		for(int i = 0; i < list.size(); i++){
			OrdFlagItem ofi = new OrdFlagItem();
			OrdFlag oflag = list.get(i); 
			ofi.flag = oflag;
			if(hashIds.contains(oflag.id))
				ofi.checked = true;
			flagData[i] = ofi;
			flagMap.put(oflag.id, oflag);
		}
		
        updateDisplayDelay();
        updateFlagsText();
		refreshDate();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			order.getData().date = newDate;
			refreshDate();
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(OrderEx o, Org org) {
		o.sumType = org.costype;
		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			fix_date = 1;
			showDialog(DIALOG_DATE_WEEK_END);
			
		}else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			fix_date = 2;
			showDialog(DIALOG_DATE_WEEK_END);
		}
		
		SharedPreferences pref = getApplicationContext()
				.getSharedPreferences(BehaviorSettingEx.SETING_NAME, Context.MODE_PRIVATE);
		Date d = c.getTime();
		c.add(Calendar.HOUR_OF_DAY, pref.getInt(BehaviorSettingEx.TIME_FROM_HOUR, 
				BehaviorSettingEx.DEF_HOUR_FROM));
		c.add(Calendar.MINUTE, pref.getInt(BehaviorSettingEx.TIME_FROM_MIN,
				BehaviorSettingEx.DEF_MIN_FROM));
		o.date = c.getTime();
		c.setTime(d);
		c.add(Calendar.HOUR_OF_DAY, pref.getInt(BehaviorSettingEx.TIME_TO_HOUR, 
				BehaviorSettingEx.DEF_HOUR_TO));
		c.add(Calendar.MINUTE, pref.getInt(BehaviorSettingEx.TIME_TO_MIN,
				BehaviorSettingEx.DEF_MIN_TO));
		o.date2 = c.getTime();
		o.buh = (pref.getBoolean(BehaviorSettingEx.BUH_KEY, false)) ? 1 : 0;
	}
	
	private void updateDisplayDelay() {
		((TextView)findViewById(R.id.tvDelay)).setText("отсрочка: " + 
				order.getData().delay);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_TIME_PICKER_FROM_ID:
				return timeHandlerFrom.createDialog();
			case DIALOG_TIME_PICKER_TO_ID:
				return timeHandlerTo.createDialog();	
			case DIALOG_DATE_WEEK_END:
				return createDateWeekEndDlg();
			case DIALOG_FLAGS:
				return createFlagsDlg();
		}
		return super.onCreateDialog(id);
	}
	
	private void updateFlagsText(){
		tvFlags.setText(getString(R.string.flags, convertOrdFlagToStr()));
	}
	
	private String convertOrdFlagToStr() {
		String[] ids = ((OrderEx)order.getData()).customFlags.split(DELIMETER);
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < ids.length; i++)	{
			if( i > 0)
				sb.append(DELIMETER);
			
			if(flagMap.containsKey(ids[i]))
				sb.append(flagMap.get(ids[i]).name);
			else
				sb.append(ids[i]);
		}
		
		return sb.toString();
	}

	class OrdFlagAdapter extends BaseAdapter{
			OnClickListener togle = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckedTextView ctv = ((CheckedTextView) v);
				ctv.toggle();
				((OrdFlagItem)ctv.getTag()).checked = ctv.isChecked();
			}
		};
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(CreateOrder.this, R.layout.flags_dlg_row, null);
			OrdFlagItem flag = (OrdFlagItem) getItem(position);
			CheckedTextView tvItem = ((CheckedTextView) view.findViewById(R.id.tvItem));
			tvItem.setText(flag.flag.name);
			tvItem.setChecked(flag.checked);
			tvItem.setTag(flag);
			tvItem.setOnClickListener(togle);
			return view;
		}
		
		@Override
		public long getItemId(int position) { return 0; }
		@Override
		public Object getItem(int position) { return flagData[position]; }
		@Override
		public int getCount() {	return flagData.length; }
	}
	private Dialog createFlagsDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.flags_dlg_title);
		View view = View.inflate(this, R.layout.flags_dlg, null);
		ListView list = (ListView)view.findViewById(R.id.list);
		list.setAdapter(new OrdFlagAdapter());
		
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ListView list = (ListView) ((AlertDialog)dialog).findViewById(R.id.list);
				OrdFlagAdapter adapter = (OrdFlagAdapter)list.getAdapter();
				
				StringBuilder sb = new StringBuilder();
				boolean f = false;
				for(int i = 0; i < adapter.getCount(); i++){
					OrdFlagItem item = (OrdFlagItem) adapter.getItem(i);
					if(item.checked){
						if(f)
							sb.append(DELIMETER);
						else
							f = true;
						sb.append(item.flag.id);
					}
				}
				
				((OrderEx)order.getData()).customFlags = sb.toString();
				updateFlagsText();
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		
		return builder.create();
	}

	private Dialog createDateWeekEndDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.question);
		builder.setMessage(R.string.ask_to_move_order_date);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Calendar c = Calendar.getInstance();
				c.setTime(order.getData().date);
				c.add(Calendar.DAY_OF_MONTH, fix_date);
				order.getData().date = c.getTime();
				refreshDate();
			}

		});
		
		builder.setNegativeButton(R.string.cancel, null);
		
		return builder.create();
	}

	@Override
	protected void onStop() {
		order.close();
		super.onStop();
	}
	
	class DelayClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setTitle("Отсрочка");
			View dialogView = View.inflate(v.getContext(), R.layout.counter, null);
			
			builder.setView(dialogView);
			final AlertDialog dialog = builder.create();
			
			Button btnCounterUp = (Button) dialogView.findViewById(R.id.btnCounterUp);
			Button btnCounterDown = (Button) dialogView.findViewById(R.id.btnCounterDown);
			Button btnCounterOK = (Button) dialogView.findViewById(R.id.btnCounterOk);
			Button btnCounterCancel = (Button) dialogView.findViewById(R.id.btnCounterCancel);
			final  TextView  tvCounter = (TextView) dialogView.findViewById(R.id.edCounter);
			tvCounter.setText(Integer.toString(order.getData().delay));
			tvCounter.setFocusable(false);
			
			btnCounterUp.setOnClickListener(new OnClickListenerToNotify() {
				
				@Override
				public void onClick(View v) {
					super.onClick(v);
					int val = Integer.parseInt(tvCounter.getText().toString());
					++val;
					tvCounter.setText(Integer.toString(val));
				}
			});
			
			btnCounterDown.setOnClickListener(new OnClickListenerToNotify() {
				@Override
				public void onClick(View v) {
					super.onClick(v);
					int val = Integer.parseInt(tvCounter.getText().toString());
					
					if (val > 0)
						--val;
					
					tvCounter.setText(Integer.toString(val));
				}
			});
			
			btnCounterOK.setOnClickListener(new OnClickListenerToNotify() {
				
				@Override
				public void onClick(View v) {
					super.onClick(v);
					order.getData().delay = Integer.parseInt(tvCounter.getText().toString());
					updateDisplayDelay();
					dialog.hide();
				}
			});
			
			btnCounterCancel.setOnClickListener(new OnClickListenerToNotify() {
				
				@Override
				public void onClick(View v) {
					super.onClick(v);
					dialog.hide();
				}
			});
		
			dialog.show();
		}
	}
	
	class CancelClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			deleteEmptyOrder();			
			finish();
		}
	}
	
	private void deleteEmptyOrder() {
		if(!editMode) {
			if( order.getData().items == null || order.getData().items.size() == 0 )
				order.delete();
		}
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();
			
			if (editMode && (order.getSumType() != costType && costType >= 0))
				askToApplyNewSumType(v.getContext(), costType);
			else 
				okDone(false);
		}
		
		private void okDone(boolean updateSumType) {
			OrderEx o = (OrderEx) order.getData();
			o.date = timeHandlerFrom.adjustTime(o.date);
			o.date2 = timeHandlerTo.adjustTime(o.date2);
			
			if (o.created == null)
				o.created = new Date();
			
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			int suppl = spFirma.getSelectedItemPosition();
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();

			if( suppl >= 0 )
				o.supplyer = suppl;
			if( costType >= 0 )
				o.sumType = costType;
			
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			if( Features.DELIVERY_ADDRESS ) {
				Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
				if( spAddress != null ) {
					KeyValue sel = (KeyValue) spAddress.getSelectedItem();
					if( sel != null )
						o.adrCode = sel.key.toString();
				}
			}
			
			o.buh = cbBuh.isChecked() ? 1 : 0;
			
			if (updateSumType)
				order.updateItemsCost(o.sumType);
			else
				order.write();
			
			
			if(!editMode)
				Warehouse.open(CreateOrder.this, order, false);
			
			finish();
		}
		
		private void askToApplyNewSumType(Context context, final int newSumType){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Внимание");
			builder.setMessage("Тип цены был изменен, пересчитать заказ?");

			builder.setPositiveButton("Пересчитать", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(true);
				}
			});
			
			builder.setNegativeButton("Оставить", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(false);
				}
			});
			
			builder.create().show();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				deleteEmptyOrder();
				finish();
				return true;
			}else
				return super.onKeyDown(keyCode, event);
	}
}
