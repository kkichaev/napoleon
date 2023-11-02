package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.database.CheckConfirmHitching;
import com.grsoft.dataobjects.ChekBase;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.RequestChek;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RequestChekImpl;
import com.grsoft.dataobjects.impl.ReturnChekBackImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RequestCheckDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RequestChekEdit extends BaseActivity implements SendResultListener {
	private static final int ASK_DELETE_NOT_SENDED = 0;
	private static final int CONFIRM_SEND = 1;

	RequestChekImpl doc;
	KeypadHelper helper;
	String orgName;
	boolean doSend = false;
	
	public static void Open(Context context, RequestChekImpl doc) {
		Intent i = new Intent(context, RequestChekEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.request_chek);
		
		doc = new RequestChekImpl();
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
		
		RequestChek rc = doc.getData();
		
		helper = new KeypadHelper(this, R.id.edSum);
		EditText ed = (EditText)findViewById(R.id.edSum);
		ed.setText(Util.IntToScaleStr(rc.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.selectAll();
		
		View ok = findViewById(R.id.btnOK);
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isValid(false))
					finishInt();
			}
		});
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});
		
		if(doc.isEditable() == false) {
			ed.setEnabled(false);
			ok.setEnabled(false);
		}
		
		findViewById(R.id.btnChekReturn).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { makeChekReturn(); }
		});
		
		refreshData();
	}
	
	void finishInt() {
		saveData();
		if(doc.sum() == 0 && doc.isEditable())
			doc.delete();
		finish();
	}
	
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == CONFIRM_SEND) {
			String msg = "Будет отправлен на печать чек на сумму " + 
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
					new DocumentSender(RequestChekEdit.this, findViewById(R.id.btnSend), "RequestChekOnLine", doc, doc.getRowid(), 
							RequestChekEdit.this).execute((Void[])null);
				}
			});
			return b.create();
		}
		if( id == ASK_DELETE_NOT_SENDED) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Вопрос");
			b.setMessage("Документ не передан. Удалить документ?");
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					doc.delete();
					finish();
				}
			});
			
			b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {  dialog.dismiss(); }
			});
			return b.create();
		}
		
		if(id == R.id.have_same_doc) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Вопрос");
			b.setMessage("За текущую дату у клиента уже есть чек на ту же сумму. Сохранить текущий документ?");
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if(doSend)
						sendInt();
					else
						finishInt();
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
	
	protected void makeChekReturn() {
		if( !doc.isExported()) {
			showDialog(ASK_DELETE_NOT_SENDED);
			return;
		}
		ReturnChekBackImpl ret = ReturnChekBackImpl.find(doc);
		if(ret == null) {
			if( doc.canCreateReturnChek() == false ) {
				Toast.makeText(this, "Нельзя создать возвратный чек.", Toast.LENGTH_SHORT).show();
				return;
			}
			ret = ReturnChekBackImpl.createFrom(doc);
		}

		if(ret != null) {
			ret.open(this);
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(doc.isExported() == false && doc.sum() == 0)
			doc.delete();
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}

	void refreshData() {
		EditText ed = (EditText)findViewById(R.id.edSum);
		View ok = findViewById(R.id.btnOK);
		if(doc.isEditable() == false) {
			ed.setEnabled(false);
			ok.setEnabled(false);
		}
		
		TextView tv = (TextView)findViewById(R.id.tvInfo);
		tv.setText(Html.fromHtml(doc.getData().getInfoText()));
	}
	
	protected void send() {
		if(isValid(true))
			sendInt();
	}
	
	void sendInt() {
		saveData();
		RequestChek rc = doc.getData();
		if(rc.canSend()) 
			showDialog(CONFIRM_SEND);		
	}

	boolean isValid(boolean doSend) {
		this.doSend = doSend;
		
		if(doc.isEditable()) {
			long date = Util.getDayStart(doc.getData().created).getTime();
			String where = "id='" + doc.getId() + "' and created > " + Long.toString(date);
			
			HaveSameDocs hsd = new HaveSameDocs(doc.getData(), getEditSum());
			DataTraveler.travel(RequestChek.class, hsd, where);
			if(hsd.haveSame) {
				showDialog(R.id.have_same_doc);
				return false;
			}
		}
		return true;
	}
	
	long getEditSum() {
		EditText ed = (EditText)findViewById(R.id.edSum);
		return Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
	}
	
	protected void saveData() {
		if(doc.isEditable()) {
			RequestChek rc = doc.getData();
			rc.sum = getEditSum();
			doc.write();

			RequestCheckDoc.instance().refreshDocSum(doc.getId());
		}
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

class HaveSameDocs extends DataTraveler.Travel<RequestChek> {
	boolean haveSame = false;
	Date created;
	long sum;

	public HaveSameDocs(RequestChek chek, long sum) { 
		this.created = chek.created;
		this.sum = sum;
	}
	
	@Override
	public boolean travel(DataTraveler<RequestChek> item) {
		if(item.data.created.compareTo(created) != 0 && item.data.sum == sum) {
			haveSame = true;
			return false;
		}
		return true;
	}
	
	public boolean IsHaveSame() { return haveSame; }
}
