package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Gather;
import com.grsoft.dataobjects.GatherItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.GatherImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.InvDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class GatherEdit extends Activity implements SendResultListener {
	GatherImpl gatherImpl = new GatherImpl();
	GatherItem item;
	PriceImpl wp = new PriceImpl();

	ListAdapter adapter;
	
	private static final String ROWID = "rowid";
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, GatherEdit.class);
		intent.putExtra(ROWID, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gather_edit);
		
		Intent intent = getIntent();
				
		if(intent != null){
			long rowid = intent.getLongExtra(ROWID, ExtrasConst.INVALID_ID);
			
			if(rowid != ExtrasConst.INVALID_ID) {
				gatherImpl.read(rowid);
				setListAdapter(gatherImpl);
			}
			
			findViewById(R.id.btnSend).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View arg0) { send();  }
			});
		}
	}
	
	void send() {
		if(!gatherImpl.checkCompleete()) {
			Toast.makeText(this, "Не полностью заполнен документ", Toast.LENGTH_SHORT).show();
		} else {
			gatherImpl.write();
			new DocumentSender(this, findViewById(R.id.btnSend), 
					InvDoc.instance().getObjectName(), gatherImpl, gatherImpl.getRowid(), this).execute((Void[])null);
		}
	}

	OnClickListener changeQty = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(gatherImpl.isEditable() == false)
				return;
			item = (GatherItem)v.getTag();
			InputNumberDlg.open(v.getContext(), new InputNumber() {
				@Override public void applayInput(int value, Object... params) {
					item.factQty = value;
					item.used++;
					gatherImpl.write();
					adapter.notifyDataSetChanged();
				}
				
				@Override public int getValue() { return item.factQty; } 					
			}, Consts.QTY_SCALE, false, "Количество");
		}
	};
	
	OnClickListener changeWeight = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(gatherImpl.isEditable() == false)
				return;
			item = (GatherItem)v.getTag();
			InputNumberDlg.open(v.getContext(), new InputNumber() {
				@Override public void applayInput(int value, Object... params) {
					item.factWeight = value;
					item.used++;
					gatherImpl.write();
					adapter.notifyDataSetChanged();
				}
				
				@Override public int getValue() { return item.factWeight; } 					
			}, Consts.QTY_SCALE, false, "Вес");
		}
	};

	private void setListAdapter(GatherImpl g) {
		ListView lv = (ListView)findViewById(android.R.id.list);
		adapter = new ListAdapter(g.getData());
		lv.setAdapter(adapter);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		gatherImpl.close();
		wp.close();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if( gatherImpl.isEditable()) {
			gatherImpl.checkCompleete();
			gatherImpl.write();
		}
	}


	@Override
	public void postSendExecute(boolean result) {
		gatherImpl.read(gatherImpl.getRowid(), false);
	}

	class ListAdapter extends BaseAdapter{
		Gather gather;
		
		public ListAdapter(Gather g){ this.gather = g; }
		
		@Override public int getCount() { return gather.items == null ? 0 :  gather.items.size(); }
		@Override public Object getItem(int position) { return gather.items.get(position); }	
		@Override public long getItemId(int position) { return position; }
	
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(GatherEdit.this, R.layout.gather_row, null);
			
			GatherItem item = (GatherItem) getItem(position);
			Price price = wp.getData();
			price.id = item.id;
			wp.read();
			
			int back = (item.used >= 2) ? R.drawable.done_item : R.drawable.list_selector;
			view.setBackgroundResource(back);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(price.name);
			
			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
			
			tv = (TextView)view.findViewById(R.id.tvWeight);
			tv.setText(Util.IntToScaleStr(item.weight, Consts.QTY_SCALE));

			tv = (TextView)view.findViewById(R.id.tvFactQty);
			tv.setTag(item);
			tv.setBackgroundResource(back);
			tv.setOnClickListener(changeQty);
			tv.setText(Util.IntToScaleStr(item.factQty, Consts.QTY_SCALE));

			tv = (TextView)view.findViewById(R.id.tvFactWeight);
			tv.setTag(item);
			tv.setBackgroundResource(back);
			tv.setOnClickListener(changeWeight);
			tv.setText(Util.IntToScaleStr(item.factWeight, Consts.QTY_SCALE));

			return view;
		}
	}
}
