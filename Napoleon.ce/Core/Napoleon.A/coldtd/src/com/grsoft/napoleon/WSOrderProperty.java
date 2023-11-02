package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

public class WSOrderProperty extends BaseActivity {
	private static final int REQ_DATE = 123;
	private static final int TIME_DIALOG = 0;
	protected static final int SELECT_ORG = 213;

	WSOrderImpl doc;
	TimeHandler timeHandler; 
	Date loadTime;
	
	public static void open(Context context, OrderImplBase<?> doc) {
		Intent i = new Intent(context, WSOrderProperty.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ws_properties);
		
		doc = new WSOrderImpl();
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras(): savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
	
		findViewById(R.id.tvLoadDate).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeLoadDate(); }
		});

		final WSOrder wo = doc.getData();
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, wo.loadTime);
		c.set(Calendar.MINUTE, 0);
		
		loadTime = c.getTime();
		
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvLoadTime), loadTime, TIME_DIALOG) {
			@Override
			public void updateDate() {
				super.updateDate();
				
				Calendar cdr = Calendar.getInstance();
				cdr.setTime(date);
				wo.loadTime = cdr.get(Calendar.HOUR_OF_DAY);
			}
			
			@Override protected String displayFormat() { return "HH"; }
		};
		
		refreshDate();
		refreshOrg();
		
		EditText ed = (EditText)findViewById(R.id.edComment);
		ed.setText(doc.getData().remark);
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { finish(); }
		});

		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doc.getData().remark = ((EditText)findViewById(R.id.edComment)).getText().toString();
				doc.write();
				finish();
			}
		});
		
		findViewById(R.id.tvOrg).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if( doc.isEditable() ) {
					Intent i = new Intent(WSOrderProperty.this, SelectOrg.class);
					startActivityForResult(i, SELECT_ORG);
				}
			}
		});
	}
	
	private void refreshOrg() {
		TextView tv = (TextView)findViewById(R.id.tvOrg);
		String text = "<u>нажмите, для выбора контрагента</u>";
		String id = doc.getData().orgId;
		if( id.length() > 0 ) {
			OrgImpl oi = new OrgImpl();
			Org o = oi.getData();
			o.id = id;
			oi.read();
			oi.close();
			
			text = "<u><b>" + o.name + "</b></u><br/><i>" + o.address + "</i>";
		}
		
		tv.setText(Html.fromHtml(text));
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == TIME_DIALOG )
			return timeHandler.createDialog();
		
		return super.onCreateDialog(id);
	}
	
	protected void changeLoadDate() {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, ((WSOrder)doc.getData()).loadDate);
		startActivityForResult(i, REQ_DATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data == null || resultCode != RESULT_OK )
			return;
		
		if( requestCode == REQ_DATE ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			((WSOrder)doc.getData()).loadDate = newDate;
			
			refreshDate();
		} else if( requestCode == SELECT_ORG ) {
			((WSOrder)doc.getData()).orgId = data.getExtras().getString(ExtrasConst.ORG_ID_STR);
			refreshOrg();
		}
		DocType.setCurDoc(WSOrderDoc.instance());
	}

	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvLoadDate)).setText(sd.format(((WSOrder)doc.getData()).loadDate));		
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
}
