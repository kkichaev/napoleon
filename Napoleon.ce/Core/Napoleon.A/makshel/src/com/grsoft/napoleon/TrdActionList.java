package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.ActionDataItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.TrdPromo;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TrdActionList extends BaseActivity {
	
	protected static final int FIND_ITEM_DLG = 1;
	
	public static OrderImplEx doc;
	LinesCountController linesController;
	
	List<TrdPromo> actions = new ArrayList<TrdPromo>();
	ArrayList<ActionDataItem> items = new ArrayList<ActionDataItem>();
	long docBaseSum = 0;
	Adapter adapter;
	
	public static void open(Context context, OrderImpl doc) {
		Intent i = new Intent(context, TrdActionList.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.trd_action_list);
		
		doc = new OrderImplEx();
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rid);
		docBaseSum = doc.sum();
		
		OrgImpl oi = new OrgImpl();
		final OrgEx oe = (OrgEx)oi.getData();
		oe.id = doc.getId();
		oi.read();
		oi.close();
		
		((TextView)findViewById(R.id.tvOrg)).setText(oe.name);
		
		String now = Long.toString(Util.getDayStart(doc.getDate()).getTime());
		String where = "start <= " + now + " and end >= " + now;
		
		DataTraveler.travel(TrdPromo.class, new DataTraveler.Travel<TrdPromo>(true) {

			@Override
			public boolean travel(DataTraveler<TrdPromo> item) {
				if(item.data.isActive(oe))
					actions.add(item.data);
				return true;
			}
		}, where);
		
		for(OrderItem odi : doc.getData().items) {
			OrderItemEx oei = (OrderItemEx)odi;
			if(oei.IsActionItem()) {
				ActionDataItem adi = new ActionDataItem(oei);
				items.add(adi);
				docBaseSum -= adi.sum();
			}
		}
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { 
				finish();
				Warehouse.open(TrdActionList.this, doc, true);
			}
		});
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { 
				save(); 
				finish();
				Warehouse.open(TrdActionList.this, doc, true);
			}
		});
		
		findViewById(R.id.btnFind).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(FIND_ITEM_DLG); }
		});
		
		adapter = new Adapter(actions);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TrdPromo item = (TrdPromo) arg0.getItemAtPosition(arg2);				
				TrdActionEdit.open(TrdActionList.this, doc.getRowid(), item, items, docBaseSum);
			}
		});
		linesController = (new LinesOnClickListener(lv, (ImageView) findViewById(R.id.btnLines), this, true)).getController();
	}
	
	@Override
	public void onBackPressed() {
		save();
		super.onBackPressed();
		Warehouse.open(this, doc, true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == TrdActionEdit.TRD_ACTION_EDIT_CODE) {
			items = data.getParcelableArrayListExtra(TrdActionEdit.ITEMS_TAG);
		} else
			super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == FIND_ITEM_DLG) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("¬ведите название");

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			b.setView(input);
			
			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					doSearch(input.getText().toString());
				}
			});
			
			b.setNegativeButton("ќчистить", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					doSearch("");
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void doSearch(String srchString) {
		List<TrdPromo> newItems;
		if(srchString.length() == 0)
			newItems = actions;
		else {
			newItems = new ArrayList<TrdPromo>();
			for(TrdPromo ti : actions) {
				if(ti.name.contains(srchString))
					newItems.add(ti);
			}
		}
		
		adapter.refresh(newItems);
	}

	protected void save() {
		doc.updateActions(items);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		adapter.notifyDataSetChanged();
		updateTotalSum(sumItems(null) + docBaseSum, 0);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
//		Warehouse.open(TrdActionList.this, doc, true);
		doc.close();
	}
	
	class Adapter extends BaseAdapter {
		List<TrdPromo> data;
		
		public Adapter(List<TrdPromo> data) {
			this.data = data;
		}
		
		public void refresh(List<TrdPromo> data) {
			this.data = data;
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(TrdActionList.this, R.layout.trd_action_row, null);
			
			TrdPromo i = (TrdPromo)getItem(pos);
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(i.name);
			linesController.prepareTextView(tv);

			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(sumItems(i), Consts.SUM_SCALE, Util.DEC_DELIM, false));
			
			tv = (TextView)view.findViewById(R.id.tvStart);
			tv.setText(Util.simpleDateFormat.format(i.start));

			tv = (TextView)view.findViewById(R.id.tvEnd);
			tv.setText(Util.simpleDateFormat.format(i.end));
			return view;
		}
		
	}

	public long sumItems(TrdPromo i) {
		long sum = 0;
		for(ActionDataItem adi : items)
			if(i == null || i.haveItem(adi.promoId))
				sum += adi.sum();
		return sum;
	}
}
