package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.PPay;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PPayImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.PPayDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.view.BaseActivity;

public class PPayEdit extends BaseActivity implements SendResultListener {
	
	protected static final int CAUSE_DLG = 0;
	private static final int ASK_REMOVE = 1;
	PPayImpl doc = new PPayImpl();
	Adapter adapter; 
	
	public static void open(Context context, PPayImpl doc) {
		Intent i = new Intent(context, PPayEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ppay_edit);
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
		doc.read(rid);
		PPay pp = doc.getData();
		
		OrgImpl oi = new OrgImpl();
		Org org = oi.getData();
		org.id = pp.id;
		oi.read();
		oi.close();
		
		TextView tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(org.name);
		
		tv = (TextView)findViewById(R.id.tvDate);
		tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(PPayEdit.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, doc.getDate().getTime());
				startActivityForResult(i, 0);
			}
		});

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { send(); }
		});
		
		EditText ed = (EditText)findViewById(R.id.edSum);
		ed.setText(Util.IntToScaleStr(pp.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.selectAll();
		
		ed = (EditText)findViewById(R.id.edCause);
		ed.setText(pp.remark);
		
		findViewById(R.id.btnCause).setOnClickListener(new View.OnClickListener() {
			
			@Override public void onClick(View v) { showDialog(CAUSE_DLG); }
		});
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		
		refreshDate();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder b;
		switch(id) {
		case CAUSE_DLG:
			b = new AlertDialog.Builder(this);
			b.setTitle("Выберите причину");
			
			ConfigImpl ci = new ConfigImpl();
			Config c = ci.getData();
			c.key = "ОбещанныйПлатеж";
			ci.read();
			
			final ArrayList<CharSequence> values = new ArrayList<CharSequence>();
			DialogHelper.makeList(c.value, values);
			CharSequence[] items = new CharSequence[values.size()];
			values.toArray(items);
			
			b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((EditText)findViewById(R.id.edCause)).setText(values.get(which).toString());
					dialog.dismiss();
				}
			});
			
			b.setNegativeButton("Закрыть", null);
			return b.create();
			
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
		
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode == RESULT_OK ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			doc.getData().date = new Date(ct);
			
			refreshDate();
		}
	}

	protected void send() {
		save();
		DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), 
				PPayDoc.instance().getObjectName(), doc, doc.getRowid(), this);
		ds.execute((Void[])null);
	}

	private void save() {
		if( doc.isExported() )
			return;

		PPay pp = doc.getData();
		EditText ed = (EditText)findViewById(R.id.edSum);
		pp.sum = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE); 
		
		ed = (EditText)findViewById(R.id.edCause);
		pp.remark = ed.getText().toString();
		doc.write();
	}

	private void refreshDate() {
		TextView tv = (TextView)findViewById(R.id.tvDate);
		tv.setText(Util.simpleDateFormat.format(doc.getDate()));
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
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
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid());
	}
	
	View.OnClickListener checkItem = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			PaymentEx item = (PaymentEx) ((View)arg0.getParent()).getTag();
			doc.getData().reverseItem(item);
			adapter.notifyDataSetChanged();
		}
	};
	
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
				view = View.inflate(PPayEdit.this, R.layout.ppay_item, null);

			PaymentEx item = (PaymentEx) getItem(arg0);
			
			int color = Util.GrServerColorToSystem(item.color);
			view.setTag(item);
			view.setBackgroundColor(color);

			CheckBox cb = (CheckBox)view.findViewById(R.id.cbCheck);
			cb.setChecked(doc.getData().haveItem(item));
			cb.setOnClickListener(checkItem);
			
			TextView tv;
			String text;
			tv = (TextView)view.findViewById(R.id.tvNumber);
			tv.setText(item.number);
			
			tv = (TextView)view.findViewById(R.id.tvDate);
			text = Util.simpleDateFormat.format(item.dlvDate) + "\n" + Util.simpleDateFormat.format(item.date);
			tv.setText(text);
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			text = Util.IntToScaleStr(item.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "\n" + Util.IntToScaleStr(item.delay, 1);			
			tv.setText(text);

			tv = (TextView)view.findViewById(R.id.tvType);
			text = item.type;			
			tv.setText(text);

			return view;
		}
		
	}
}
