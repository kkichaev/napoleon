package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RejectAct;
import com.grsoft.dataobjects.RejectActItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RejectActImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import android.annotation.SuppressLint;
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

public class RejectItemEdit extends BaseActivity {
	protected static final int EDIT_REMARK = 1;
	RejectActImpl doc;
	PriceImpl price = new PriceImpl();
	Adapter adapter;
 	boolean starting = true;
 	RejectRowData editItem = null;
 	EditText input;
 	
	public static void open(Context context, RejectActImpl doc, long itemRowid) {
		Intent i = new Intent(context, RejectItemEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, itemRowid);
		
		context.startActivity(i);
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.reject_item_edit);
	
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		
		doc = new RejectActImpl();
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		price.read(b.getLong(ExtrasConst.PRICE_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		doc.setFormText(this);
		
		TextView tv = (TextView) findViewById(R.id.tvPrice);
		tv.setText(((PriceEx)price.getData()).getName());
		
		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				RejectRowData rrd = (RejectRowData) arg0.getAdapter().getItem(arg2);
				RejectActItem ri = doc.findItem(price.getData().id, rrd.dlvNumber, rrd.dlvDate, rrd.party);
				RejectPriceCount.open(RejectItemEdit.this, doc, price.getData().id, rrd.dlvNumber, 
						rrd.dlvDate, ri == null ? 0 : ri.qty,
						rrd.party, rrd.expired);
			}
		});
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { finish(); }
		});
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
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putLong(ExtrasConst.PRICE_ROW_ID_STR, price.getRowid());
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == EDIT_REMARK) {
			input.setText(editItem.src.remark);
			return;
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == EDIT_REMARK) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("¬ведите примечание");
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			input = new EditText(this);
			input.setLayoutParams(lp);
			b.setView(input);

			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					editItem.src.remark = input.getText().toString();
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
	protected void onStop() {
		super.onStop();
		doc.close();
		price.close();
	}
	
	View.OnClickListener editRemark = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			editItem = (RejectRowData) arg0.getTag();
			showDialog(EDIT_REMARK);
		}
	};
	
//	View.OnClickListener editCount = new View.OnClickListener() {
//		
//		@Override
//		public void onClick(View arg0) {
//			RejectRowData rrd = (RejectRowData) arg0.getTag();
//			RejectActItem ri = doc.findItem(price.getData().id, rrd.dlvNumber, rrd.dlvDate);
//			RejectPriceCount.open(RejectItemEdit.this, doc, price.getData().id, rrd.dlvNumber, 
//					rrd.dlvDate, ri == null ? 0 : ri.qty,
//					rrd.party, rrd.expired);
//		}
//	};
	
	class Adapter extends BaseAdapter {

		List<RejectRowData> data = new ArrayList<RejectRowData>();
		
		public Adapter() { refresh(); }
		
		@Override public int getCount() { return data.size(); }

		public void refresh() {
			data.clear();
			
			RejectAct document = doc.getData();
			String orgId = document.id;
			String itemId = price.getData().id;
			
			Date stExpDate = Util.getDayStart(document.getStartExpiredDate());
			Date endExpDate = Util.getDayStart(document.getEndExpiredDate());
			DocList dl = DeliveryDoc.instance().docList(orgId, "", "firm='" + document.firmCode + "'");
			for(Document<?> d : dl) {
				Delivery ddoc = (Delivery)d.getData(); 
				for(DeliveryItem item : ddoc.items) {
					if(!item.id.equals(itemId))
						continue;
					Date dexp = ((DeliveryItemEx)item).expired;
					if( dexp.compareTo(stExpDate) >= 0 && dexp.compareTo(endExpDate) < 0) {
						RejectRowData rrd = new RejectRowData();
						
						rrd.dlvDate = ddoc.date;
						rrd.dlvNumber = ddoc.number;
						rrd.expired = dexp;
						rrd.party = ((DeliveryItemEx)item).party;
						rrd.incomeQty = item.qty;
						
						rrd.src = doc.findItem(item.id, ddoc.number, ddoc.date, rrd.party);
						
						data.add(rrd);
					}
				}
			}
			
			Collections.sort(data);
			notifyDataSetChanged();
		}

		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(RejectItemEdit.this, R.layout.reject_item_row, null);
			}
			RejectRowData item = (RejectRowData) getItem(arg0);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvIncome);
			tv.setText(Util.simpleDateFormat.format(item.dlvDate));
			tv = (TextView)view.findViewById(R.id.tvExpired);
			tv.setText(Util.simpleDateFormat.format(item.expired));
			tv = (TextView)view.findViewById(R.id.tvMfr);
			tv.setText(item.party);
			tv = (TextView)view.findViewById(R.id.tvQtyIncome);
			tv.setText(Util.IntToScaleStr(item.incomeQty, Consts.QTY_SCALE, Util.DEC_DELIM, true));

			tv = (TextView)view.findViewById(R.id.tvQty);
			if(item.src == null) {
				tv.setText("");
			} else {
				tv.setText(Util.IntToScaleStr(item.src.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
			}
			tv = (TextView)view.findViewById(R.id.tvRemark);
			if(item.src == null) {
				tv.setText("");
				tv.setOnClickListener(null);
			} else {
				tv.setTag(item);
				tv.setText(item.src.remark);
				tv.setOnClickListener(editRemark);
			}
			
//			view.setTag(item);
//			view.setOnClickListener(editCount);
			return view;
		}
	}
}


class RejectRowData implements Comparable<RejectRowData> {
	public Date dlvDate;
	public String dlvNumber;
	public String party;
	public Date expired;
	public int incomeQty;

	public RejectActItem src;

	@Override
	public int compareTo(RejectRowData arg0) {
		return dlvDate.compareTo(arg0.dlvDate);
	}
}
