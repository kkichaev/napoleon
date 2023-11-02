package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.ActionDataItem;
import com.grsoft.dataobjects.TrdPromo;
import com.grsoft.dataobjects.TrdPromoItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

public class TrdActionEdit extends BaseActivity {
	static final String ACTION_TAG = "action";
	public static final String ITEMS_TAG = "items";
	
	public static final int TRD_ACTION_EDIT_CODE = 123;
	protected static final int FIND_ITEM_DLG = 1;

	LinesCountController linesController;
	
	long rowId;
	TrdPromo action;
	ArrayList<ActionDataItem> items;
	long baseSum = 0;
	
	Adapter adapter;
	
	public static void open(Activity context, long rid, TrdPromo action, ArrayList<ActionDataItem> items, long docBaseSum) {
		Intent i = new Intent(context, TrdActionEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rid);
		i.putExtra(ACTION_TAG, action);
		i.putParcelableArrayListExtra(ITEMS_TAG, items);
		i.putExtra(TrdItemEdit.SUM_TAG, docBaseSum);
		
		context.startActivityForResult(i, TRD_ACTION_EDIT_CODE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.trd_action_edit);
		
		Bundle i = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
		rowId = i.getLong(ExtrasConst.DOC_ROW_ID_STR);
		action = i.getParcelable(ACTION_TAG);
		items = i.getParcelableArrayList(ITEMS_TAG);
		
		OrderImplEx oie = new OrderImplEx();
		oie.read(rowId);
		oie.close();
		
		baseSum = i.getLong(TrdItemEdit.SUM_TAG, 0);
		
		OrgImpl oi = new OrgImpl();
		oi.read("id", oie.getId());
		
		final String name = oi.getData().name + "/" + action.name;
		((TextView)findViewById(R.id.tvOrg)).setText(name);
		
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				save();
				finish();
			}
		});
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { finish(); }
		});
		
		findViewById(R.id.btnFind).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(FIND_ITEM_DLG); }
		});
		
		adapter = new Adapter(action.items);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TrdPromoItem i = (TrdPromoItem)arg0.getItemAtPosition(arg2);
				TrdItemEdit.open(TrdActionEdit.this, baseSum, name, i, items);
			}
		});
		linesController = (new LinesOnClickListener(lv, (ImageView) findViewById(R.id.btnLines), this, true)).getController();
	}
	
	void save() {
		Intent i = new Intent();
		i.putParcelableArrayListExtra(ITEMS_TAG, items);
		setResult(RESULT_OK, i);
	}
	
	@Override
	public void onBackPressed() {
		save();
		super.onBackPressed();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == TrdItemEdit.TRD_ITEM_EDIT_CODE) {
			items = data.getParcelableArrayListExtra(ITEMS_TAG);
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
	
	protected void doSearch(String string) {
		if(string.length() == 0)
			adapter.refresh(action.items);
		else {
			List<TrdPromoItem> items = new ArrayList<TrdPromoItem>();
			for(TrdPromoItem i : action.items)
				if(i.name.contains(string))
					items.add(i);
			adapter.refresh(items);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
		updateTotalSum(sumItems() + baseSum, 0);
	}
	
	private long sumItems() {
		long sum = 0;
		for(ActionDataItem adi : items)
			sum += adi.sum();
		return sum;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, rowId);
		outState.putParcelable(ACTION_TAG, action);
		outState.putParcelableArrayList(ITEMS_TAG, items);
	}
	
	class Adapter extends BaseAdapter {
		List<TrdPromoItem> items;
		
		public Adapter(List<TrdPromoItem> items) {
			this.items = items;
		}
		
		public void refresh(List<TrdPromoItem> items) {
			this.items = items;
			notifyDataSetChanged();
		}

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(TrdActionEdit.this, R.layout.trd_action_edit_row, null);
			}
			
			TrdPromoItem i = (TrdPromoItem) getItem(pos);
			TextView tv;

			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(i.name);
			linesController.prepareTextView(tv);
			
			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(countQty(i), Consts.QTY_SCALE));

			tv = (TextView)view.findViewById(R.id.tvCost);
			tv.setText(i.valType == TrdPromoItem.TYPE_COST ? "" : Util.IntToScaleStr(i.val, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			return view;
		}
	}

	public int countQty(TrdPromoItem i) {
		int qty = 0;
		for(ActionDataItem adi : items)
			if(adi.promoId == i.id)
				qty += adi.qty;
		return qty;
	}
}
