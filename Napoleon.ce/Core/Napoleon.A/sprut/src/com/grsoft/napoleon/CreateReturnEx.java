package com.grsoft.napoleon;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CreateReturnEx extends CreateReturn {
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ReturnEx r = (ReturnEx) doc.getData();
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = r.id;
		oi.read();
		
        ConfigImpl c = new ConfigImpl();
        Spinner spCause = (Spinner)findViewById(R.id.spCause);
		DialogHelper.loadSpinnerWithKey(c, "ПричиныВозврата", values, spCause , r.cause);
		c.close();
		
		ArrayAdapter<KeyValue> causeAdapter = (ArrayAdapter<KeyValue>) spCause.getAdapter();
		boolean needDocWrite = false;
		if (causeAdapter != null && causeAdapter.getCount() > 0){
			KeyValue sel = (KeyValue) causeAdapter.getItem(0);
			
			if( sel != null ){
				r.cause = sel.key.toString();
				needDocWrite = true;
			}
		}
		
		Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
		if( spAddress != null ) {
			ArrayList<KeyValue> addresses = new ArrayList<KeyValue>();
			int selected = -1;
			for(OrgAddress addr : oi.getData().orgAddress) {
				KeyValue kv = new KeyValue(addr.id, addr.name);
				if( kv.key.toString().equals(r.adrCode))
					selected = addresses.size();
				addresses.add(kv);
			}
			ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, addresses);
			spAddress.setAdapter(aa);
			if( selected >= 0 && selected < spAddress.getCount())
				spAddress.setSelection(selected);
			else if(selected == -1 && aa.getCount() > 0){
				KeyValue sel = (KeyValue) aa.getItem(0);
				if( sel != null ){
					r.adrCode = sel.key.toString();
					needDocWrite = true;
				}
			}
		}
		
		if (needDocWrite)
			doc.write();
		
		View ok = findViewById(R.id.btnOK);
		if( !doc.isExported() ) {
			ok.setOnClickListener(new OKHandler());
		} else
			ok.setEnabled(false);
	}
	
	class OKHandler implements View.OnClickListener {
		@Override 
		public void onClick(View v) {
			ReturnEx r = (ReturnEx) doc.getData();
			r.date = dateHandler.getDate();
			r.remark = ((EditText)findViewById(R.id.edNotes)).getText().toString();
			
			KeyValue kv  = (KeyValue)((Spinner)findViewById(R.id.spCause)).getSelectedItem();
			if( kv != null )
				r.cause = kv.key.toString();
			
			Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
			if( spAddress != null ) {
				KeyValue sel = (KeyValue) spAddress.getSelectedItem();
				if( sel != null )
					r.adrCode = sel.key.toString();
			}
			
			doc.write();

			if(!editMode)
				Warehouse.open(CreateReturnEx.this, doc, false);

			finish();
		}
	}
}
