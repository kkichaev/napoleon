package com.grsoft.napoleon;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImpl;

public class AddPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpage);
		init();
	}
	
	private void init() {
		OrderImpl order = CreateOrder.currentOrder();
		OrderEx o = (OrderEx)order.getData();
	
		EditText ed = (EditText)findViewById(R.id.edNotes);
		ed.setText(o.rem2);
	}
	
	public void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();
		o.rem2 = ((EditText)findViewById(R.id.edNotes)).getText().toString();
	}
}
