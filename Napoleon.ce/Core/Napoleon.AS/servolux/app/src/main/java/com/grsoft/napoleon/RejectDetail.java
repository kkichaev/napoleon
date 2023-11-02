package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RejectActItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RejectActImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RejectActDoc;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RejectDetail extends BaseActivity {
	RejectActImpl doc;
	boolean starting = true;
	Adapter adapter;
	
	protected static final int EDIT_REMARK = 1;
	RejectActItem editItem = null;
 	EditText input;
	
	static public void open(Context context, RejectActImpl doc) {
		Intent i = new Intent(context, RejectDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reject_detail);
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;

		doc = new RejectActImpl();
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.setFormText(this);

		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				RejectDetailRow item = (RejectDetailRow) arg0.getAdapter().getItem(arg2);
				RejectActItem src = item.item;
				RejectPriceCount.open(RejectDetail.this, doc, src.id, src.number, src.date, src.qty, src.party, src.expired);
			}
		});
		
		findViewById(R.id.btnDetail).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { doc.editProperties(RejectDetail.this); }
		});
		
		findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { RejectActPrice.open(RejectDetail.this, doc); }
		});
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send();}
		});
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == EDIT_REMARK) {
			input.setText(editItem.remark);
			return;
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == EDIT_REMARK) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Введите примечание");
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			input = new EditText(this);
			input.setLayoutParams(lp);
			b.setView(input);

			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					editItem.remark = input.getText().toString();
					doc.write();
					arg0.dismiss();
					adapter.notifyDataSetChanged();
				}
			});
			b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { arg0.cancel(); }
			});
			
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(doc.isEditable() && doc.isEmpty()) {
			doc.delete();
		}
	}
	
	void send() {
		DocumentSender snd = new DocumentSender(this, findViewById(R.id.btnSend), RejectActDoc.instance().getObjectName(), doc, doc.getRowid());
		snd.execute((Void[])null);
	}
	
	void updateTotal() {
		int weight = doc.weight();
		TextView tv = (TextView) findViewById(R.id.tvTotalSum);
		tv.setText(RejectActDoc.instance().weightToString(weight, getString(R.string.kg)));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(starting) {
			starting = false;
		} else {
			doc.read(doc.getRowid(), false);
			adapter.refresh();
		}
		updateTotal();
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
	
	View.OnClickListener editRemark = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			editItem = (RejectActItem) arg0.getTag();
			showDialog(EDIT_REMARK);
		}
	};
	
	class Adapter extends BaseAdapter {
		List<RejectDetailRow> data = new ArrayList<RejectDetailRow>();

		public Adapter() { refresh(); }
		
		public void refresh() {
			data.clear();
			
			PriceImpl pi = new PriceImpl();
			PriceEx p = (PriceEx) pi.getData();
			
			for(OrderItem oi : doc.getData().items) {
				RejectActItem i = (RejectActItem)oi;
				p.id = oi.id;
				
				RejectDetailRow rdr = new RejectDetailRow();
				rdr.item = i;
				if(pi.read()) {
					rdr.name = p.getName();
				} else {
					rdr.name = "Код " + p.id;
				}
				 
				data.add(rdr);
			}
			
			pi.close();
			
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(RejectDetail.this, R.layout.reject_detail_row, null);
			}
			RejectDetailRow item = (RejectDetailRow) getItem(arg0);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(item.name);
			
			tv = (TextView)view.findViewById(R.id.tvParty);
			tv.setText(item.item.party);
			
			tv = (TextView)view.findViewById(R.id.tvExpired);
			tv.setText(Util.simpleDateFormat.format(item.item.expired));

			tv = (TextView)view.findViewById(R.id.tvRemark);
			tv.setText(item.item.remark);
			tv.setTag(item.item);
			tv.setOnClickListener(editRemark);
			
			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(item.item.qty, Consts.QTY_SCALE));

			return view;
		}
		
	}
}

class RejectDetailRow {
	public String name = "";
	public RejectActItem item;
}
