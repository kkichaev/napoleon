package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class AddPage extends Activity {

	private ArrayList<CharSequence> specConds = new ArrayList<CharSequence>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.addpage);

		init();
	}
	
	private void init() {
		OrderImpl order = CreateOrder.currentOrder();
		if( order == null )
			return;
		
		OrderEx o = (OrderEx)order.getData();

		final char sep = ';';
		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();

		c.key = "СпецУсловия";
		specConds.add("нет");
		if( config.read() )
			MainPage.makeList(c.value, sep, specConds);

		config.close();
	    
		ArrayAdapter<CharSequence> adapter;
		Spinner spinner;

		adapter = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_layout, specConds);
		spinner = (Spinner) findViewById(R.id.spSpecCond);
		spinner.setAdapter(adapter);
		spinner.setSelection(o.specCondition);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				findViewById(R.id.edDiscount).setEnabled(pos != 0);
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});

		EditText ed;
		ed = (EditText) findViewById(R.id.edNotes);
		ed.setText(o.remark);
		
		ed = (EditText)findViewById(R.id.edDiscount);
		ed.setText(Util.IntToScaleStr(o.discount, Consts.DISCOUNT_SCALE, Util.DEC_DELIM, false));	
	}

	public void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();

		EditText ed;
		ed = (EditText) findViewById(R.id.edNotes);
		o.remark = ed.getText().toString();
		
		Spinner s = (Spinner) findViewById(R.id.spSpecCond);
		o.specCondition = s.getSelectedItemPosition();

		ed = (EditText) findViewById(R.id.edDiscount);
		o.discount = Util.StrToScale(ed.getEditableText().toString(), Consts.DISCOUNT_SCALE);
	}
}
