package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Agent;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class AddPage extends Activity {

	private ArrayList<KeyValue> values = new ArrayList<KeyValue>();
	private ArrayList<KeyValue> agents = new ArrayList<KeyValue>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpage);
		init();
	}
	
	void initSpinner(Spinner sp, String selValue, ArrayList<KeyValue> v) {
		int selected = -1;
		for( int i=0; i<v.size(); i++ ) {
			if( v.get(i).key.equals(selValue)) {
				selected = i;
				break;
			}
		}
		
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, v);
		sp.setAdapter(aa);
		if( selected >= 0 )
			sp.setSelection(selected);
	}

	private void init() {
		OrderImpl order = CreateOrder.currentOrder();
		OrderEx o = (OrderEx)order.getData();
	
		Org oe = new Org();
		String table = DataObjectInfo.getInstance().getTableName(Org.class);
		DbReader r = new DbReader();
		r.setReadingFields("id,name");
		boolean bdo = r.select(oe, table, null, "name");
		while( bdo ) {
			KeyValue kv = new KeyValue(oe.id, oe.name);
			values.add(kv);			
			oe = new Org();
			bdo = r.selectNext(oe);
		}
		r.close();
		
		Agent a = new Agent();
		bdo = r.select(a, DataObjectInfo.getInstance().getTableName(a.getClass()), "", "name");
		while( bdo ) {
			KeyValue kv = new KeyValue(a.id, a.name);
			agents.add(kv);			
			a = new Agent();
			bdo = r.selectNext(a);
		}
		
		initSpinner((Spinner)findViewById(R.id.spConsignee), o.consignee, values);
		initSpinner((Spinner)findViewById(R.id.spGprm), o.gprm, values);
		initSpinner((Spinner)findViewById(R.id.spPayer), o.payer, values);
		initSpinner((Spinner)findViewById(R.id.spExecutive), o.executiveManager, agents);
		
		if( (o.paramsex & OrderEx.PASPORT_FLAG) != 0 )
			((CheckBox)findViewById(R.id.cbPasport)).setChecked(true);

		if( (o.paramsex & OrderEx.SERT_FLAG) != 0 )
			((CheckBox)findViewById(R.id.cbSert)).setChecked(true);

		if( (o.paramsex & OrderEx.SHEMA_PROEZDA_FLAG) != 0 )
			((CheckBox)findViewById(R.id.cbShema)).setChecked(true);

		if( (o.paramsex & OrderEx.BILL_FLAG) != 0 )
			((CheckBox)findViewById(R.id.cbSchet)).setChecked(true);

		if( (o.paramsex & OrderEx.PREDSTAVIT_FLAG) != 0 )
			((CheckBox)findViewById(R.id.cbPredstav)).setChecked(true);
	}
	
	public void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();
		Spinner sp;
		KeyValue kv;
		
		sp = (Spinner) findViewById(R.id.spConsignee);
		kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			o.consignee = kv.key.toString();

		sp = (Spinner) findViewById(R.id.spGprm);
		kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			o.gprm = kv.key.toString();

		sp = (Spinner) findViewById(R.id.spPayer);
		kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			o.payer = kv.key.toString();

		sp = (Spinner) findViewById(R.id.spExecutive);
		kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			o.executiveManager = kv.key.toString();

		if(((CheckBox)findViewById(R.id.cbPasport)).isChecked())
			o.paramsex |= OrderEx.PASPORT_FLAG;
		else
			o.paramsex &= (~OrderEx.PASPORT_FLAG);

		if(((CheckBox)findViewById(R.id.cbSert)).isChecked())
			o.paramsex |= OrderEx.SERT_FLAG;
		else
			o.paramsex &= (~OrderEx.SERT_FLAG);

		if(((CheckBox)findViewById(R.id.cbShema)).isChecked())
			o.paramsex |= OrderEx.SHEMA_PROEZDA_FLAG;
		else
			o.paramsex &= (~OrderEx.SHEMA_PROEZDA_FLAG);

		if(((CheckBox)findViewById(R.id.cbSchet)).isChecked())
			o.paramsex |= OrderEx.BILL_FLAG;
		else
			o.paramsex &= (~OrderEx.BILL_FLAG);

		if(((CheckBox)findViewById(R.id.cbPredstav)).isChecked())
			o.paramsex |= OrderEx.PREDSTAVIT_FLAG;
		else
			o.paramsex &= (~OrderEx.PREDSTAVIT_FLAG);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			CreateOrder.checkOrder();
		return super.onKeyDown(keyCode, event);
	}
}
