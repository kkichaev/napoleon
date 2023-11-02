package com.grsoft.napoleon;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.SettingActivity;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class OrderDefaultSetting extends SettingActivity {

	private ArrayList<CharSequence> dlvMethod = new ArrayList<CharSequence>();
	private ArrayList<CharSequence> ctrlType = new ArrayList<CharSequence>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_defaults);
		update();
	}
	
	@Override
	public void save() {
		OrderSettings os = new OrderSettings();
		Spinner sp;
		
		CheckBox cash = (CheckBox)findViewById(R.id.cbBank);			
		os.cash = !cash.isChecked();

		sp = (Spinner) findViewById(R.id.spDlvMethod);
		CharSequence v = (CharSequence) sp.getSelectedItem();
		if( v != null )
			os.dlvMethod = v.toString();

		sp = (Spinner) findViewById(R.id.spControlType);
		v = (CharSequence) sp.getSelectedItem();
		if( v != null )
			os.ctrlType = v.toString();
		
		os.save(this);
	}

	@Override
	public void update() {
		Spinner sp;
		OrderSettings os = OrderSettings.load(this);
		ConfigImpl config = new ConfigImpl();
		
		if( os.cash == false )
			((CheckBox)findViewById(R.id.cbBank)).setChecked(true);

		sp = (Spinner) findViewById(R.id.spDlvMethod);
		DialogHelper.loadSpinnerFromConfig(config, "МетодДоставки", dlvMethod, sp, os.dlvMethod);

		sp = (Spinner) findViewById(R.id.spControlType);
		DialogHelper.loadSpinnerFromConfig(config, "ТипУчета", ctrlType, sp, os.ctrlType);

		config.close();
	}

	@Override public int getName() { return R.string.order_defaults; }

	@Override public int getIcon() { return R.drawable.order_doc; }
}
