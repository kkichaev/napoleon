package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.script.ScriptActivity;
import com.grsoft.script.ScriptHelper;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.view.KeypadHelper;
import com.grsoft.view.RegDurationActivity;

public class IncassEdit extends RegDurationActivity implements SendResultListener, ScriptActivity {
	
	final int DATE_DIALOG = 1;
	
	protected CreatableDocument<? extends Incass> doc;
//	protected IncassImpl doc;
	protected KeypadHelper keyHelper;
	DateHandler dateHandler;
	
	static public Class<? extends Activity > activity = IncassEdit.class;
	
	protected int getContentViewID() { return R.layout.incass; }
	
	public static void open(Context context, IncassImpl doc) {
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewID());
		setTitle(R.string.incas_doc_title);
		
		if( doc == null )
			doc = (CreatableDocument<? extends Incass>) IncassDoc.instance().create();
		init((savedInstanceState==null) ? getIntent().getExtras() : savedInstanceState);
		
	}
	
	protected String orgInfo(Org o) {
		return o.name;
	}
	
	@Override
	protected void onStop() {
		doc.close();
		super.onStop();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}

	protected void init(Bundle bundle) {
		long rid = bundle.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc.read(rid);
		
		Incass incass = doc.getData();
		OrgImpl oi = new OrgImpl();
		oi.getData().id = incass.id;
		oi.read();
		oi.close();
		
		TextView tv = (TextView)findViewById(R.id.tvOrgName);
		tv.setText(Html.fromHtml(orgInfo(oi.getData())));
		
		keyHelper = createKeypadHelper();
		
		EditText ed;
		
		ed = (EditText)findViewById(R.id.edCount);
		ed.setInputType(InputType.TYPE_NULL);
		ed.requestFocus();
		setSum(incass.sum);
		
		ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(incass.remark);
		ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				View view = findViewById(R.id.llKeyboard);
				view.setVisibility(!hasFocus? View.VISIBLE : View.GONE);

				if (!hasFocus){
					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		});
	
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), incass.date, DATE_DIALOG);
		View btnOk = findViewById(R.id.btnOK); 
		if( Features.OK_BTN_INCASS ) {
			btnOk.setVisibility(View.VISIBLE);
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { btnOkPressed(); }
			});
		} else
			btnOk.setVisibility(View.INVISIBLE);
	
		btnSend = findViewById(R.id.btnSend);
		ScriptHelper.initView(this, IncassDoc.instance().getObjectName(), doc.getData().created, doc.getId());

		btnSend.setOnClickListener(new OnClickListenerToNotify() {
			@Override
			public void onClick(View v) {
				super.onClick(v);
				send();
			}
		});

		childInit(doc.getData(), oi.getData());
	}

	protected void childInit(Incass incass, Org org) {
	}

	protected void btnOkPressed() {
		save();
		finish();
	}

	protected KeypadHelper createKeypadHelper() {
		return new KeypadHelper(this, R.id.edCount, false);
	}

	String syncTitle;
	String syncMsg;

	protected View btnSend;

	protected void send() {
		if (save()) {
			DocumentSender ds = createDocumentSender();
			ds.execute((Void[]) null);
		}
	}

	protected DocumentSender createDocumentSender() {
		return new DocumentSender(this, findViewById(R.id.btnSend), IncassDoc.OBJ_NAME, doc, doc.getRowid(), this);
	}

	@Override
	public void onBackPressed() {
		if( !Features.OK_BTN_INCASS )
			save();
		else if( doc.isEditable() && doc.sum() == 0 )
			doc.delete();
		super.onBackPressed();
	}
	
	protected int getSum() {
		EditText ed;
		ed = (EditText)findViewById(R.id.edCount);
		return (int)Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
	}
	
	protected void setSum(int sum) {
		EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.selectAll();
	}

	protected void setDocument() {
		Incass incass = doc.getData();
		incass.sum = getSum();
		setRemark(incass);
		incass.date = dateHandler.getDate();
	}

	protected void setRemark(Incass incass) {
		EditText ed;
		ed = (EditText)findViewById(R.id.edRemark);

		incass.remark = ed.getText().toString();
	}
	
	protected boolean save() {
		boolean res = true;

		if( !doc.isEditable() )
			return res;
		
		setDocument();
		
		if( doc.getData().sum <= 0 ) {
			handlingInvalidSum();
			res = false;
		}else
			doc.write();

		IncassDoc.instance().refreshDocSum(doc.getId());

		return res;
	}

	protected void handlingInvalidSum(){ doc.delete(); }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DATE_DIALOG:
				return dateHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}

	@Override
	public boolean closeDocument() {
		return save();
	}
}
