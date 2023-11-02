package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.MoneyProxy;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.MoneyProxyImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.MoneyProxyDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.InputNumberHelper;

public class MoneyProxyForm extends Activity {

	public static Class<? extends Activity> activity = MoneyProxyForm.class;

	private MoneyProxyImpl doc;

	public static void open(Context context, MoneyProxyImpl doc) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}

	protected int getLayoutId() { return R.layout.moneyproxy; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());

		long docRowId = ExtrasConst.INVALID_ID;
		if( savedInstanceState == null )
			docRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			docRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		
		doc = (MoneyProxyImpl)MoneyProxyDoc.instance().create();
		doc.read(docRowId);
		
		MoneyProxy d = doc.getData();
		
		OrgImpl o = new OrgImpl();
		o.getData().id = d.id;
		o.read();

		init(d, o.getData());

		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MoneyProxyForm.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, doc.getDate().getTime());
				startActivityForResult(i, 0);
			}
		});

		findViewById(R.id.btnSend).setOnClickListener(new OnClickListenerToNotify() {
			@Override
			public void onClick(View v) {
				if( doc.isEditable() )
					saveDocument();
				send();
			}
		});
	}

	void send() {
		if(doc.isEmpty()) {
			Toast.makeText(this, R.string.cant_send_empty_doc_str, Toast.LENGTH_LONG).show();
		} else {
			new DocumentSender(MoneyProxyForm.this, findViewById(R.id.btnSend), MoneyProxyDoc.OBJ_NAME, doc, doc.getRowid()).execute((Void[]) null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == 0 ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			doc.getData().date = newDate;
			updateDate();
		}
	}

	@Override
	protected void onStop() {
		doc.close();
		super.onStop();
	}

	String getTitle(Org org) { return org.name; }

	protected void init(MoneyProxy doc, Org org) {
		TextView tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(Html.fromHtml(getTitle(org)));

		updateDate();

		EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.setInputType(InputType.TYPE_NULL);

		ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(doc.remark);

		InputNumberHelper nh = new InputNumberHelper((EditText)findViewById(R.id.edCount));
		nh.makeNumericKeypad(findViewById(R.id.keyPad));
	}

	private void updateDate() {
		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
		String text = "<u>" + sf.format(doc.getDate()) + "</u>";
		TextView tv = (TextView)findViewById(R.id.tvDate);
		tv.setText(Html.fromHtml(text));
	}
	
	void saveDocument() {
		updateDoc(doc.getData());
		doc.write();
	}

	@Override
	public void onBackPressed() {
		if(doc.isEditable()) {
			updateDoc(doc.getData());
			if(doc.isEmpty()) doc.delete();
			else doc.write();
			MoneyProxyDoc.instance().refreshDocSum(doc.getId());
		}
		super.onBackPressed();
	}

	protected void updateDoc(MoneyProxy d) {
		EditText ed = (EditText)findViewById(R.id.edCount);
		d.sum = (int)Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);

		ed = (EditText)findViewById(R.id.edRemark);
		d.remark = ed.getText().toString();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if( doc.isEditable() )
			saveDocument();
	}
}
