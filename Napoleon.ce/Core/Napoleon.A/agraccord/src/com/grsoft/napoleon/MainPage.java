package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Region;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class MainPage extends Activity {

	private static final int DIALOG_DATE_PICKER_ID = 0;

	private static final String TAG = "MainPage";

	private ArrayList<KeyValue> sklads = new ArrayList<KeyValue>();
	ArrayList<OrgDogovor> dogovors = new ArrayList<OrgDogovor>();
	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
	
	boolean haveAddresses;
	
	DateHandler dateHandler;

	private Spinner spPrices;

	private Spinner spDogovors;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder);
		init();
	}

	private void init() {
		OrderImpl order = CreateOrder.currentOrder();
		OrderEx o = (OrderEx)order.getData();
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
		oi.close();
		OrgEx org = (OrgEx) oi.getData();
        ((TextView) findViewById(R.id.tvOrgName)).setText(org.name);

        ConfigImpl config = new ConfigImpl();
		
		Spinner sp = (Spinner) findViewById(R.id.spWH);
		DialogHelper.loadSpinnerWithKey(config, "—клады", sklads, sp, o.whCode);

		int selected = -1;
		ArrayAdapter<KeyValue> aa;
		ArrayList<KeyValue> values;
		
		values = new ArrayList<KeyValue>();
		Region r = new Region();
		String table = DataObjectInfo.getInstance().getTableName(Region.class);
		DbReader rdr = new DbReader();
		boolean bdo = rdr.select(r, table, null, "name");
		while( bdo ) {
			KeyValue kv = new KeyValue(r.id, r.name);
			if( kv.key.equals(o.regCode))
				selected = values.size();
			
			values.add(kv);
			r = new Region();
			bdo = rdr.selectNext(r);
		}
		rdr.close();
		if(o.regCode.length() == 0)
			values.add(0, new KeyValue("", ""));
		ArrayAdapter<KeyValue> regAdapter = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values);
		Spinner spReg = (Spinner) findViewById(R.id.spRegions);
		spReg.setAdapter(regAdapter);
		if( selected >= 0 && selected < spReg.getCount())
			spReg.setSelection(selected);
		
		if( org.region.length() > 0 )
			spReg.setEnabled(false);
		
		spDogovors = (Spinner) findViewById(R.id.spDogovors);
		
		// set dogovor
		if( org.dogovors != null ) {
			values = new ArrayList<KeyValue>();
			for(OrgDogovor od : org.dogovors) {
				KeyValue kv = new KeyValue(od.id, od.name);
				if( kv.key.equals(o.dogCode))
					selected = values.size();
				dogovors.add(od);
				values.add(kv);
			}
			aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values);
			spDogovors.setAdapter(aa);
			
			if( selected >= 0 && selected < spDogovors.getCount())
				spDogovors.setSelection(selected);
		}
		
		values = new ArrayList<KeyValue>();
		selected = -1;
		
		if(oi != null)
			for(OrgAddress addr : oi.getData().orgAddress) {
				KeyValue kv = new KeyValue(addr.id, addr.name);
				if( kv.key.toString().equals(o.adrCode))
					selected = values.size();
				values.add(kv);
			}
		
		haveAddresses = (values.size() > 0);
		aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values);
		sp = (Spinner) findViewById(R.id.spAddress);
		sp.setAdapter(aa);
		if( selected >= 0 && selected < sp.getCount())
			sp.setSelection(selected);
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		CheckBox cb = ((CheckBox)findViewById(R.id.cbSelf));
		if( (o.paramsex & OrderEx.PICKUP_FLAG) != 0 ) {
			cb.setChecked(true);
			cb.setEnabled(false);
			findViewById(R.id.spRegions).setEnabled(false);
		} else 
			cb.setOnClickListener(new View.OnClickListener() {
				@Override 
				public void onClick(View v) { findViewById(R.id.spRegions).setEnabled(!((CheckBox)v).isChecked()); }
			});
		
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		
		spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", priceType, spPrices, o.sumType);
		
		spDogovors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private int lastPosition = -1;
			
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { 
            	if ((lastPosition != -1) && (lastPosition != position))
            		dogovorChanged(dogovors.get(position));
            	
				lastPosition = position;
			} 
			
			@Override public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		spPrices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			private int lastPosition = -1;
			
			@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if ((lastPosition != -1) && (lastPosition != position))
					priceChanged(position);
				
				lastPosition = position;
			}
			
			@Override public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		config.close();
		Log.d(TAG, "oncreate END");
	}
	
	private void priceChanged(int costType){
		Order o = CreateOrder.currentOrder().getData();
		Log.d(TAG, String.format("priceChanged: %d", costType));

		if(o.sumType != costType && o.items != null && o.items.size() > 0 ){
			CreateOrder.currentOrder().updateItemsCost(costType);
		}else
			o.sumType = costType;
	}
	
	protected void dogovorChanged(final OrgDogovor dogovor) {
		Order o = CreateOrder.currentOrder().getData();
		Log.d(TAG, String.format("dogovorChanged: %d", dogovor.costype));
		spPrices.setSelection(dogovor.costype, true);

		if(o.sumType != dogovor.costype && o.items != null && o.items.size() > 0 ){
			CreateOrder.currentOrder().updateItemsCost(dogovor.costype);
		}else
			o.sumType = dogovor.costype;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return dateHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}
	
	boolean update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();
		o.date = dateHandler.getDate();
		
		if (o.created == null)
			o.created = new Date();
		
		if(((CheckBox)findViewById(R.id.cbSelf)).isChecked())
			o.paramsex |= OrderEx.PICKUP_FLAG;
		else
			o.paramsex &= (~OrderEx.PICKUP_FLAG);
		
		Spinner sp = (Spinner) findViewById(R.id.spWH);
		KeyValue kv = (KeyValue) sp.getSelectedItem();
		if( kv != null ) {
			o.whCode = kv.key.toString();
			o.whIndex = sp.getSelectedItemPosition();
		}
		
		sp = (Spinner) findViewById(R.id.spAddress);
		kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			o.adrCode = kv.key.toString();

		sp = (Spinner) findViewById(R.id.spRegions);
		kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			o.regCode = kv.key.toString();

		sp = (Spinner) findViewById(R.id.spDogovors);
		kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			o.dogCode = kv.key.toString();

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		o.remark = remark.getText().toString();
		
		boolean ret = true;
		if( haveAddresses && (o.adrCode == null || o.adrCode.length()==0) )
			ret = false;
		return ret;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			CreateOrder.checkOrder();
		return super.onKeyDown(keyCode, event);
	}
}
