package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.grsoft.database.CheckConfirmHitching;
import com.grsoft.dataobjects.ChekBase;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.RequestChek;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RequestChekImpl;
import com.grsoft.dataobjects.impl.ReturnChekBackImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ReturnChekBackDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class ReturnCheckEdit extends BaseActivity implements SendResultListener {
	protected static final int ASK_DELETE = 0;
	private static final int CONFIRM_SEND = 1;
	ReturnChekBackImpl doc;
	String orgName;
	
	public static void open(Context context, ReturnChekBackImpl doc) {
		Intent i = new Intent(context, ReturnCheckEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.return_chek);
		
		doc = new ReturnChekBackImpl();
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR));
		
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = doc.getId();
		oi.read();
		oi.close();
		
		orgName = o.name;

		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(o.name);
		
		RequestChekImpl src = new RequestChekImpl();
		RequestChek rc = src.getData();
		rc.created = doc.getData().chek;
		src.read();
		src.close();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		String info = "возврат по чеку от " + sdf.format(rc.created) + " на сумму " + Util.IntToScaleStr(rc.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		
		TextView tv = (TextView)findViewById(R.id.tvChekInfo);
		tv.setText(info);

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});
	
		findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(ASK_DELETE); }
		});
		
		refreshData();
	}
	
	void refreshData() {
		TextView tv = (TextView)findViewById(R.id.tvInfo);
		tv.setText(Html.fromHtml(doc.getData().getInfoText()));
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();	
		
		ReturnChekBackDoc.instance().refreshDocSum(doc.getId());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == CONFIRM_SEND) {
			String msg = "Будет отправлен на печать чек на возврат на сумму  " + 
					Util.IntToScaleStr(doc.getData().sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + 
					" для " + orgName + ". Продолжить?";
			
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Подтверждение отправки");
			b.setMessage(msg);
			b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { arg0.dismiss(); }
			});
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { 
					arg0.dismiss();
					new DocumentSender(ReturnCheckEdit.this, findViewById(R.id.btnSend), "ReturnChekBackOnLine", doc, doc.getRowid(), 
							ReturnCheckEdit.this).execute((Void[])null);
				}
			});
			return b.create();
		}
		if(id == ASK_DELETE) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Подтверждение");
			b.setMessage("Удалить документ?");
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doc.delete();
					finish();
				}
			});
			
			b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {  dialog.dismiss(); }
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}

	protected void send() {
		showDialog(CONFIRM_SEND);
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result) {
			String et = CheckConfirmHitching.getErrorText();
			if(et != null ) {
				ChekBase cb = doc.getData();
				cb.handleStatus = -1;
				cb.handleRemark = et;
				doc.write();
			} else
				doc.read(doc.getRowid(), false);
			
			refreshData();
		}		
	}
}
