package com.grsoft.napoleon;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.Plan;
import com.grsoft.dataobjects.impl.PlanImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

public class PlanEdit extends BaseActivity {
	
	private static final int DATE_DIALOG = 0;
	PlanImpl plan;
	protected KeypadHelper keyHelper;
	DateHandler dateHandler;

	public static void open(Context ctx, PlanImpl pi) {
		Intent i = new Intent(ctx, PlanEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, pi.getRowid());
		ctx.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan_edit);
		
		plan = new PlanImpl();
		Plan p = plan.getData();

		Bundle b = (savedInstanceState==null) ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		plan.read(rid);
		plan.close();
		
		keyHelper = new KeypadHelper(this, R.id.edCount);
		EditText ed;
		
		ed = (EditText)findViewById(R.id.edCount);
		ed.setInputType(InputType.TYPE_NULL);
		setSum(p.sum);
			
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), p.date, DATE_DIALOG);
		findViewById(R.id.btnOK).setVisibility(View.INVISIBLE);
	
		View btnSend = findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				send();
			}
		});
	}	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			save();
			finish();
			return true;
		}else
			return super.onKeyDown(keyCode, event);
	}

	protected int getSum() {
		EditText ed;
		ed = (EditText)findViewById(R.id.edCount);
		return Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);		
	}

	protected void setSum(int sum) {
		EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.selectAll();
	}

	private void send() {
		save();
		DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), "Plan", plan, plan.getRowid());
		ds.execute((Void[])null);
	}

	protected void setDocument() {
		Plan p = plan.getData();
		p.sum = getSum();
				
		p.date = dateHandler.getDate();
	}
	
	protected void save() {
		plan.read(plan.getRowid());
		if( plan.isExported() )
			return;
		
		setDocument();		
		plan.write();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DATE_DIALOG:
				return dateHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}
}
