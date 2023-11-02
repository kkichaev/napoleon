package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.grsoft.dataobjects.Distrib;
import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

public class DistribEdit extends BaseActivity {
	
	KeypadHelper kh;
	DistribImpl doc;
	
	public static void open(Context context, DistribImpl doc) {
		Intent i = new Intent(context, DistribEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.distrib);
		doc = new DistribImpl();
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		Distrib dd = doc.getData();
		
		kh = new KeypadHelper(this, R.id.edOur);

		EditText ed;
		ed = (EditText)findViewById(R.id.edOur);
		ed.setText(Integer.toString(dd.ourFaces));
		ed.setInputType(InputType.TYPE_NULL);
		if( doc.isEditable() ) {
			ed.selectAll();
			ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus ) {
						kh.setTargetID(v.getId());
						((EditText)v).selectAll();
					}
				}
			});
		} else
			ed.setEnabled(false);
		
		ed = (EditText)findViewById(R.id.edTheir);
		ed.setText(Integer.toString(dd.theirFaces));
		ed.setInputType(InputType.TYPE_NULL);
		if( doc.isEditable() )
			ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus ) {
						kh.setTargetID(v.getId());
						((EditText)v).selectAll();
					}
				}
			});
		else
			ed.setEnabled(false);
			
	
		View v = findViewById(R.id.btnOK);
		v.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveDoc();
				finish();
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	
	
	@Override
	public void onBackPressed() {
		saveDoc();
		super.onBackPressed();
	}

	private void saveDoc() {
		if( doc.isEditable() ) {
			Distrib dd = doc.getData();
	
			EditText ed;
			try {
				ed = (EditText)findViewById(R.id.edOur);
				dd.ourFaces = Integer.parseInt(ed.getText().toString());
			} catch(Exception e){
				e.printStackTrace();
			}
			
			try {
				ed = (EditText)findViewById(R.id.edTheir);
				dd.theirFaces = Integer.parseInt(ed.getText().toString());
			} catch(Exception e){
				e.printStackTrace();
			}
	
			doc.write();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
}
