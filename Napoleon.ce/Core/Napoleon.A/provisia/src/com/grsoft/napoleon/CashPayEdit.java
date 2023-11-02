package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.CashPay;
import com.grsoft.dataobjects.CashPayItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.CashPayImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CashPayDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class CashPayEdit extends BaseActivity implements SendResultListener  {
	
	private static final int ASK_REMOVE = 0;
	CashPayImpl doc = new CashPayImpl();
	Adapter adapter;
	
	public static void open(Context context, CashPayImpl doc) {
		Intent i = new Intent(context, CashPayEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cash_pay);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
		doc.read(rid);

		CashPay cp = doc.getData();
	
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
		
		ed = (EditText)findViewById(R.id.edNumber);
		ed.setText(cp.number);
		
		ConfigImpl cfg= new ConfigImpl();
		DialogHelper.loadSpinnerWithKey(cfg, "Организация", new ArrayList<KeyValue>(), 
				(Spinner)findViewById(R.id.spFirma), cp.supplier);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
	
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { send(); }
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	protected void send() {
		save();
		DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), 
				CashPayDoc.instance().getObjectName(), doc, doc.getRowid(), this);
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

		CashPay cp = doc.getData();
		cp.sum = getInputSum(); 
		
		EditText ed = (EditText)findViewById(R.id.edNumber);
		cp.number = ed.getText().toString();
		
		KeyValue kv = (KeyValue)((Spinner)findViewById(R.id.spFirma)).getSelectedItem();
		if( kv != null )
			cp.supplier = kv.key.toString();
		
		doc.write();
		CashPayDoc.instance().refreshDocSum(doc.getId());
	}
	
	int getInputSum() {
		EditText ed = (EditText)findViewById(R.id.edSum);
		return Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
	}
	
	int getAvailSum() {
		int sum = getInputSum();
		for(CashPayItem item : doc.getData().items) {
			sum -= item.sum;
		}
		return sum;
	}
	
	View.OnClickListener setSum = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int cs = getInputSum();
			if( cs == 0 ) {
				MessageBox.show(CashPayEdit.this, "Ошибка", "Введите, пожалуйста, сумму");
				findViewById(R.id.edSum).requestFocus();
				return;
			}
			
			final PaymentEx item = (PaymentEx) ((View)v.getParent()).getTag();
			final int asum = getAvailSum() + doc.getData().getItemSum(item);
			final int sum = (asum < item.sum) ? asum : item.sum;
			
			if( sum == 0 ) {
				MessageBox.show(CashPayEdit.this, "Ошибка", "Вся сумма распеределена");
				return;
			}
			
			InputNumberDlg.open(CashPayEdit.this,
					new InputNumber() {
						
						@Override public int getValue() { return sum; }
						
						@Override
						public void applayInput(int value, Object... params) {
							if( value > item.sum )
								value = item.sum;
							if( value > asum )
								value = asum;
							
							doc.getData().putItemSum(item, value);
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
			boolean bdo = r.select(pe, table, where, "dlvDate");
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
				view = View.inflate(CashPayEdit.this, R.layout.cash_item, null);
			
			PaymentEx item = (PaymentEx)getItem(arg0);
			int csum = doc.getData().getItemSum(item);
			
			int color = Util.GrServerColorToSystem(item.color);
			view.setTag(item);
			view.setBackgroundColor(color);
			
			TextView tv;
			String text;
			tv = (TextView)view.findViewById(R.id.tvNumber);
			tv.setText(item.number);
			tv.setOnClickListener(setSum);
			
			tv = (TextView)view.findViewById(R.id.tvDate);
			text = Util.simpleDateFormat.format(item.dlvDate) + "\n" + Util.simpleDateFormat.format(item.date);
			tv.setOnClickListener(setSum);
			tv.setText(text);
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			text = Util.IntToScaleStr(item.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			if( csum != 0 )
				text += "\n" + Util.IntToScaleStr(csum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			tv.setText(text);
			tv.setOnClickListener(setSum);

			tv = (TextView)view.findViewById(R.id.tvType);
			text = item.type;			
			tv.setText(text);
			tv.setOnClickListener(setSum);

			return view;
		}
		
	}
}
