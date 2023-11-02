package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.MovementWh;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.MovementWhImpl;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class MovementProps extends BaseActivity {
	
	boolean isNew = false;
	MovementWhImpl doc;
	int selectedItem;
	
	public static void open(Context context, MovementWhImpl doc, boolean isNew) {
		Intent i = new Intent(context, MovementProps.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.EDIT_MODE_STR, !isNew);
		
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.movement_props);
		
		doc = (MovementWhImpl) MovementDoc.instance().create();
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rowid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rowid);
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(doc.getData().remark);

		isNew = !(b.getBoolean(ExtrasConst.EDIT_MODE_STR, true));
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if( isNew )
					doc.delete();
				finish();
			}
		});
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveDoc();
				if( isNew )
					WhMovement.open(MovementProps.this, doc, !isNew);
				finish();
			}
		});
	
		final MovementWh d = doc.getData();
		
		selectedItem = 0;
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		final List<Firm> firms = new ArrayList<Firm>();
		DataTraveler.travel(FirmEx.class, new DataTraveler.Travel<FirmEx>() {
			@Override
			public boolean travel(DataTraveler<FirmEx> item) {
				if( item.data.id.equals(d.firma))
					selectedItem = firms.size();
				
				firms.add(item.data);
				item.data = new FirmEx();
				return true;
			}}, null);
		
		BaseAdapter adapter = new ArrayAdapter<Firm>(this,  R.layout.simple_spinner_layout, firms);
		spFirma.setAdapter(adapter);
		if( selectedItem < adapter.getCount() )
			spFirma.setSelection(selectedItem);
		
		ConfigImpl config = new ConfigImpl();
		
		Spinner spWh = (Spinner) findViewById(R.id.spWhSrc);
		DialogHelper.loadSpinnerWithKey(config, "Склады", new ArrayList<KeyValue>(), spWh, d.whSrc);
		
		spWh = (Spinner) findViewById(R.id.spWhDest);
		DialogHelper.loadSpinnerWithKey(config, "Склады", new ArrayList<KeyValue>(), spWh, d.whDest);
		
		config.close();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	protected void saveDoc() {
		MovementWh d = doc.getData();
		
		FirmEx fe = (FirmEx)((Spinner) findViewById(R.id.spFirma)).getSelectedItem();
		if( fe != null )
			d.firma = fe.id;
		
		KeyValue val = (KeyValue)((Spinner) findViewById(R.id.spWhSrc)).getSelectedItem();
		if( val != null )
			d.whSrc = val.key.toString();

		val = (KeyValue)((Spinner) findViewById(R.id.spWhDest)).getSelectedItem();
		if( val != null )
			d.whDest = val.key.toString();
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		d.remark = remark.getText().toString();

		doc.write();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putBoolean(ExtrasConst.EDIT_MODE_STR, !isNew);
	}
}
