package com.grsoft.napoleon;

import com.grsoft.dataobjects.DiscountMonitoringItem;
import com.grsoft.dataobjects.MntrGoods;
import com.grsoft.dataobjects.impl.DiscountMonitoringImpl;
import com.grsoft.dataobjects.impl.MntrGoodsImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DiscountMonitoringDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MntrDetail extends BaseActivity implements SendResultListener {
	DiscountMonitoringImpl doc;
	LinesCountController linesController;
	Adapter adapter;
	MntrGoodsImpl goods = new MntrGoodsImpl();
	
	public static void open(Context c, DiscountMonitoringImpl doc) {
		Intent i = new Intent(c, MntrDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.monitor_detail);
		doc = new DiscountMonitoringImpl();

		long orderRowId;
		if( savedInstanceState == null )
			orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			orderRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		
		doc.read(orderRowId);
		OrgImpl org = new OrgImpl();
		org.getData().id = doc.getId();
		org.read();
		org.close();
				
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(Html.fromHtml(org.getData().name));
		
		findViewById(R.id.btnAddItems).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { Warehouse.open(v.getContext(), doc, true); }
		});
		
		final View btnSend = findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DocumentSender(MntrDetail.this, btnSend, DiscountMonitoringDoc.instance().getObjectName(), doc, 
						doc.getRowid(), MntrDetail.this).execute((Void[])null);
			}
		});
		if( Features.CANT_SEND_SCRIPT_PART ) {
			if(ScriptImpl.containsDocument(DiscountMonitoringDoc.instance().getObjectName(), doc.getData().created, doc.getId()))
				btnSend.setVisibility(View.GONE);
		}

		adapter = new Adapter();
		ListView lvItems = (ListView) findViewById(R.id.lvItems);
		ImageButton btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lvItems, btnLines, this, true);
		linesController = linesOnClickListener.getController();

		lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DiscountMonitoringItem i =  (DiscountMonitoringItem) arg0.getAdapter().getItem(arg2);
				editItem(i);
			}
		});
		
		lvItems.setAdapter(adapter);
		registerForContextMenu(lvItems);		
	}
	
	void editItem(DiscountMonitoringItem i) {
		MntrGoods item = goods.getData();
		item.id = i.id;
		goods.read();
		doc.editItem(goods.getRowid(), this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(doc.isEditable()) {
			getMenuInflater().inflate(R.menu.monitoring_context_menu, menu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();
		DiscountMonitoringItem edItem = (DiscountMonitoringItem)adapter.getItem(mi.position);
		if(item.getItemId() == R.id.itEdit) {
			editItem(edItem);
		} else if (item.getItemId() == R.id.itEdit) {
			doc.deleteItem(edItem.id);
			adapter.refresh();
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		goods.close();
		doc.close();
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		doc.read(doc.getRowid(), false);
		adapter.refresh();
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		if(doc.isEmpty()) {
			doc.delete();
		}
		super.onBackPressed();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	CompoundButton.OnCheckedChangeListener setAction = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			DiscountMonitoringItem item = (DiscountMonitoringItem)arg0.getTag();
			item.isAction = arg0.isChecked() ? 1 : 0;
			doc.write();
			adapter.notifyDataSetChanged();
		}
	};
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }
		@Override public Object getItem(int arg0) { return doc.getData().items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		public void refresh() {
			notifyDataSetChanged();
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			if(v == null) {
				v = View.inflate(MntrDetail.this, R.layout.monitoring_detail_row, null);
			}
			DiscountMonitoringItem item = (DiscountMonitoringItem) getItem(arg0);
			MntrGoods price = goods.getData();
			price.id = item.id;
			goods.read();
			
			TextView tv = (TextView)v.findViewById(R.id.tvName);
	
			linesController.prepareTextView(tv);
			tv.setText(price.name);

			tv = (TextView)v.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(item.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

			tv = (TextView)v.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));

			tv = (TextView)v.findViewById(R.id.tvFacing);
			tv.setText(Util.IntToScaleStr(item.facing, Consts.QTY_SCALE, Util.DEC_DELIM, true));

			CheckBox cb = (CheckBox)v.findViewById(R.id.cbIsAction);
			cb.setChecked(item.isAction == 1);
			cb.setTag(item);
			cb.setOnCheckedChangeListener(setAction);
			return v;
		}
		
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid(), false);
	}
}
