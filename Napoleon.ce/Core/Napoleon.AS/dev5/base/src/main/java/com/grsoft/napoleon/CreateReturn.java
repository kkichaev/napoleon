package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImplBase;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateReturn extends BaseActivity {
	public static Class<? extends CreateReturn> activity = CreateReturn.class;
	static final int DATE_DIALOG = 1;
	
	protected boolean editMode = false;
	CreatableDocument<? extends Return> doc = null;
	protected OrgImpl oi = new OrgImpl();
	
	ArrayList<KeyValue> values = new ArrayList<KeyValue>();
	
	//DateHandler dateHandler;

	public static void open(Context ctx, Document<?> doc, boolean editOldDoc) {
		Intent i = new Intent(ctx, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldDoc);
		ctx.startActivity(i);
	}
	
	int getContentViewID() { return R.layout.createreturn; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getContentViewID());

		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		editMode = b.getBoolean(ExtrasConst.EDIT_MODE_STR, false);
		
		doc = (ReturnImplBase<Return>) DocType.getCurDoc().create();
		if(rid != ExtrasConst.INVALID_ID)
			doc.read(rid);
	
		Return r = doc.getData();
	
		oi.getData().id = r.id;
		oi.read();
		oi.close();
		
		if( !editMode )
			init(r ,oi.getData());
		
		initView();
		
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);

		//dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), r.date, DATE_DIALOG);
        
        findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateReturn.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, doc.getData().date.getTime());
				startActivityForResult(i, DATE_DIALOG);
			}
		});

		
		View ll = findViewById(R.id.llCost);
		if(Features.USE_COST_IN_RETURNS && ll != null) {
			ll.setVisibility(View.VISIBLE);
			initCost(r);
		}
		
		((EditText)findViewById(R.id.edNotes)).setText(r.remark);
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) {
				deleteEmptyDoc();
				finish();
			}
		});
		
		View ok = findViewById(R.id.btnOK);
		if( doc.isEditable() ) {
			ok.setOnClickListener(new OKHandler());
		} else
			ok.setEnabled(false);
		
		init();
		refreshDate();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DATE_DIALOG ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			doc.getData().date = newDate;
			refreshDate();
		}
	}

	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(doc.getDate()));		
	}
	
	protected void initCost(Return r) {
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		ConfigImpl config = new ConfigImpl();
		DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", new ArrayList<CharSequence>(), spPrices, r.sumType);

		config.getData().key = "ћожно»змен€ть÷ену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		config.close();
	}
	
	protected void initView() {	}

	protected void init(Return r, Org data) {
		if(Features.USE_COST_IN_RETURNS)
			r.sumType = data.costype;
	}

	protected void init() {
	}

	protected boolean canChange() { return true; }
	
	class OKHandler implements View.OnClickListener {
		@Override 
		public void onClick(View v) {
			if(canChange() == false)
				return;
			
			Return r = doc.getData();
			updateReturn(r);
			doc.write();

			if(!editMode)
				Warehouse.open(CreateReturn.this, doc, false);
			
			finish();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		//if( id == DATE_DIALOG )
		//	return dateHandler.createDialog();
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

	protected void updateReturn(Return r) {
		//r.date = dateHandler.getDate();
		r.remark = ((EditText)findViewById(R.id.edNotes)).getText().toString();

		if(Features.USE_COST_IN_RETURNS ) {
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			if( spPrices != null ) {
				int sel = spPrices.getSelectedItemPosition();
				if( sel >= 0 )
					r.sumType = sel;
			}
		}
	}
}
