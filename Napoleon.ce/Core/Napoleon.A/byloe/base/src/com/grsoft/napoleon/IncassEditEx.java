package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class IncassEditEx extends BaseActivity implements SendResultListener {
	private static final int ASK_REMOVE = 0;
	protected static final int DIALOG_DATE_PICKER_ID = 0;
	IncassImpl doc = new IncassImpl();
	Adapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.cash_pay);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
		doc.read(rid);

		Incass cp = doc.getData();
	
		OrgImpl oi = new OrgImpl();
		Org org = oi.getData();
		org.id = cp.id;
		oi.read();
		oi.close();
		
		TextView tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(org.name);
		
		EditText ed = (EditText)findViewById(R.id.edSum);
		ed.setText(Util.IntToScaleStr(cp.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.selectAll();
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
	
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { send(); }
		});
		
		findViewById(R.id.tvDvrDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( doc.isEditable() ) {
					Intent i = new Intent(IncassEditEx.this, CalendarActivity.class);
					i.putExtra(ExtrasConst.DATE_TAG, ((IncassEx)doc.getData()).dovDate.getTime());
					startActivityForResult(i, DIALOG_DATE_PICKER_ID);
				}
			}
		});
		
		refreshDvrDate();
		ed = (EditText)findViewById(R.id.edDvrNum);
		ed.setText(((IncassEx)cp).dovNumber);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null ) {
			if( requestCode == DIALOG_DATE_PICKER_ID ) {
				Date curDate = new Date();
				long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
				((IncassEx)doc.getData()).dovDate = new Date(ct);
				refreshDvrDate();
			} 
		}
	}

	private void refreshDvrDate() {
		TextView tv = (TextView)findViewById(R.id.tvDvrDate);
		IncassEx ie = (IncassEx)doc.getData();
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		tv.setText(Html.fromHtml("<u>"+sd.format(ie.dovDate) + "</u>"));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	protected void send() {
		save();
		DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), 
				IncassDoc.instance().getObjectName(), doc, doc.getRowid(), this);
		ds.execute((Void[])null);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder b;
		switch(id) {
		case ASK_REMOVE:
			b = new AlertDialog.Builder(this);
			b.setTitle("Пустой документ");
			b.setMessage("Удалить документ?");
			b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doc.delete();
					dialog.dismiss();
					finish();
				}
			});
			b.setNegativeButton(R.string.no, null);
			return b.create();
		}
		
		return super.onCreateDialog(id);	}
	
	@Override
	public void onBackPressed() {
		if( !doc.isExported() ) {
			EditText ed = (EditText)findViewById(R.id.edSum);
			int sum = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
			if( sum == 0 ) {
				showDialog(ASK_REMOVE);
				return;
			}
			save();
		}
		super.onBackPressed();
	}
		
	private void save() {
		if( doc.isExported() )
			return;

		IncassEx cp = (IncassEx) doc.getData();
		cp.sum = getInputSum(); 
		
		EditText ed = (EditText)findViewById(R.id.edDvrNum);
		cp.dovNumber = ed.getText().toString();
		
		doc.write();
		IncassDoc.instance().refreshDocSum(doc.getId());
	}
	
	int getInputSum() {
		EditText ed = (EditText)findViewById(R.id.edSum);
		return Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
	}
	
	int getAvailSum() {
		int sum = getInputSum();
		for(IncassItem item : ((IncassEx)doc.getData()).items) {
			sum -= item.sum;
		}
		return sum;
	}
	
	View.OnClickListener setSum = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int cs = getInputSum();
			if( cs == 0 ) {
				MessageBox.show(IncassEditEx.this, "Ошибка", "Введите, пожалуйста, сумму");
				findViewById(R.id.edSum).requestFocus();
				return;
			}
			
			final PaymentEx item = (PaymentEx) v.getTag();
			final int asum = getAvailSum() + ((IncassEx)doc.getData()).getItemSum(item);
			final int sum = (asum < item.sum) ? asum : item.sum;
			
			if( sum == 0 ) {
				MessageBox.show(IncassEditEx.this, "Ошибка", "Вся сумма распеределена");
				return;
			}
			
			InputNumberDlg.open(IncassEditEx.this,
					new InputNumber() {
						
						@Override public int getValue() { return sum; }
						
						@Override
						public void applayInput(int value, Object... params) {
							if( value > item.sum )
								value = item.sum;
							if( value > asum )
								value = asum;
							
							((IncassEx)doc.getData()).putItemSum(item, value);
							adapter.notifyDataSetChanged();
						}
					}, Consts.SUM_SCALE, false, "Введите сумму");
			
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid());
	}
	
	class Adapter extends BaseAdapter {
		
		ArrayList<PaymentEx> values = new ArrayList<PaymentEx>();
		
		public Adapter() {
			PaymentEx pe = new PaymentEx();
			String table = DataObjectInfo.getInstance().getTableName(pe.getClass());
			String where = "id='" + doc.getId() + "'";
			DbReader r = new DbReader();
			boolean bdo = r.select(pe, table, where, "date");
			while(bdo) {
				values.add(pe);
				pe = new PaymentEx();
				bdo = r.selectNext(pe);
			}
			r.close();
		}

		@Override public int getCount() { return values.size(); }
		@Override public Object getItem(int arg0) { return values.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(IncassEditEx.this, R.layout.cash_item, null);
			
			PaymentEx item = (PaymentEx)getItem(arg0);
			IncassEx ie = (IncassEx)doc.getData();
			int csum = ie.getItemSum(item);
			
			TextView tv;
			String text;
			tv = (TextView)view.findViewById(R.id.tvNumber);
			text = item.number + "<br>" + Util.simpleDateFormat.format(item.date);
			tv.setText(Html.fromHtml(text));
			tv.setTag(item);
			tv.setOnClickListener(setSum);
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			text = Util.IntToScaleStr(item.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			if( csum != 0 )
				text += "<br>" + Util.IntToScaleStr(csum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			tv.setText(Html.fromHtml(text));
			tv.setTag(item);
			tv.setOnClickListener(setSum);
			
			tv = (TextView)view.findViewById(R.id.tvOverhead);
			text = Integer.toString(item.payDelay) + "<br>" + Integer.toString(item.overDelay);
			tv.setText(Html.fromHtml(text));
			tv.setTag(item);
			tv.setOnClickListener(setSum);

			tv = (TextView) view.findViewById(R.id.tvAgent);
			text = item.manager;
			tv.setText(Html.fromHtml(text));
			tv.setTag(item);
			tv.setOnClickListener(setSum);

			return view;
		}
		
	}
}
