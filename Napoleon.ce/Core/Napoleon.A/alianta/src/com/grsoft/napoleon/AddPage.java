package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class AddPage extends Activity {

	private ArrayList<CharSequence> dlvMethod = new ArrayList<CharSequence>();
	private ArrayList<CharSequence> ctrlType = new ArrayList<CharSequence>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpage);
		init();
	}

	private void init() {
		OrderImpl order = CreateOrder.currentOrder();
		OrderEx o = (OrderEx)order.getData();
	
		Spinner sp;
		ConfigImpl config = new ConfigImpl();
		
		if( (o.params & ParamState.ofCash) == 0 )
			((CheckBox)findViewById(R.id.cbBank)).setChecked(true);

		sp = (Spinner) findViewById(R.id.spDlvMethod);
		DialogHelper.loadSpinnerFromConfig(config, "МетодДоставки", dlvMethod, sp, o.dlvMethod);

		sp = (Spinner) findViewById(R.id.spControlType);
		DialogHelper.loadSpinnerFromConfig(config, "ТипУчета", ctrlType, sp, o.ctrlType);

		sp = (Spinner) findViewById(R.id.spFirm);
		DialogHelper.loadSpinnerWithKey(config, "Организация", new ArrayList<KeyValue>(), sp, o.firmCode);

		sp = (Spinner) findViewById(R.id.spPayMethod);
		DialogHelper.loadSpinnerFromConfig(config, "МетодОплаты", new ArrayList<CharSequence>(), sp, o.payMethod);

		sp = (Spinner) findViewById(R.id.spDlvDir);
		DialogHelper.loadSpinnerWithKey(config, "НаправлениеОтгрузки", new ArrayList<KeyValue>(), sp, o.dlvDir);
		
		config.close();
	}
	
	public void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();
		Spinner sp;
		
		CheckBox cash = (CheckBox)findViewById(R.id.cbBank);			
		if( !cash.isChecked() ) o.params |= ParamState.ofCash;
		else o.params &= (~ParamState.ofCash);

		sp = (Spinner) findViewById(R.id.spDlvMethod);
		CharSequence v = (CharSequence) sp.getSelectedItem();
		if( v != null )
			o.dlvMethod = v.toString();

		sp = (Spinner) findViewById(R.id.spControlType);
		v = (CharSequence) sp.getSelectedItem();
		if( v != null )
			o.ctrlType = v.toString();

		sp = (Spinner) findViewById(R.id.spPayMethod);
		v = (CharSequence) sp.getSelectedItem();
		if( v != null )
			o.payMethod = v.toString();
				
		sp = (Spinner) findViewById(R.id.spDlvDir);
		KeyValue kv = (KeyValue)sp.getSelectedItem();
		if(kv != null)
			o.dlvDir = kv.key.toString();
		
		sp = (Spinner) findViewById(R.id.spFirm);
		o.supplyer = sp.getSelectedItemPosition();
		o.firmCode = ((KeyValue)sp.getSelectedItem()).key.toString();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			CreateOrder.checkEmptyOrder();
			finish();
		}
		
		return true;
	}
}
