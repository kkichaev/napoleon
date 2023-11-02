package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class ReturnProperties extends BaseActivity {
	
	static final int DATE_DIALOG = 1;
	
	private boolean editMode = false;
	ReturnImpl doc = new ReturnImpl();
	
	ArrayList<KeyValue> values = new ArrayList<KeyValue>();
	
	DateHandler dateHandler;

	public static void open(Context ctx, ReturnImpl doc, boolean editOldDoc) {
		Intent i = new Intent(ctx, ReturnProperties.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldDoc);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.createreturn);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		editMode = b.getBoolean(ExtrasConst.EDIT_MODE_STR, false);
		
		if(rid != ExtrasConst.INVALID_ID)
			doc.read(rid);
	
		ReturnEx r = (ReturnEx)doc.getData();
	
		OrgImpl oi = new OrgImpl();
		oi.getData().id = r.id;
		oi.read();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);

        /*		
        ConfigImpl c = new ConfigImpl();
		DialogHelper.loadSpinnerWithKey(c, "ПричиныВозврата", values, (Spinner)findViewById(R.id.spCause), r.cause);
		c.close();

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
		}
*/
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), r.date, DATE_DIALOG);
		
		((EditText)findViewById(R.id.edNotes)).setText(r.remark);
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) {
				deleteEmptyDoc();
				finish();
			}
		});
		
		View ok = findViewById(R.id.btnOK);
		if( !doc.isExported() ) {
			ok.setOnClickListener(new OKHandler());
		} else
			ok.setEnabled(false);
	}
	
	class OKHandler implements View.OnClickListener {
		@Override 
		public void onClick(View v) {
			ReturnEx r = (ReturnEx)doc.getData();
			r.date = dateHandler.getDate();
			r.remark = ((EditText)findViewById(R.id.edNotes)).getText().toString();
			
//			KeyValue kv  = (KeyValue)((Spinner)findViewById(R.id.spCause)).getSelectedItem();
//			if( kv != null )
//				r.cause = kv.key.toString();
//			
			//Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
			//if( spAddress != null ) {
			//	KeyValue sel = (KeyValue) spAddress.getSelectedItem();
			//	if( sel != null )
			//		r.adrCode = sel.key.toString();
			//}
			
			doc.write();

			if(!editMode)
				Warehouse.open(ReturnProperties.this, doc, false);

			finish();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == DATE_DIALOG )
			return dateHandler.createDialog();
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putBoolean(ExtrasConst.EDIT_MODE_STR, editMode);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_BACK)
			return super.onKeyDown(keyCode, event);
			
		deleteEmptyDoc();
		finish();
		return true;
	}

	private void deleteEmptyDoc() {
		if(!editMode) {
			if( doc.getData().items == null || doc.getData().items.size() == 0 )
				doc.delete();
		}
	}
}
