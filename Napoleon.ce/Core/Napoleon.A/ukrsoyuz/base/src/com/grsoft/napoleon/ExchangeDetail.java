/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Редактирование остатков
 *
 * kki   12/04/2011   creating
 */
package com.grsoft.napoleon;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.ExchangeItem;
import com.grsoft.dataobjects.impl.ExchangeImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ExchangeDoc;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.RegDurationActivity;

/***
 * Редатирование остатков
 * @author kki
 *
 */
public class ExchangeDetail extends RegDurationActivity 
	implements DataSetNotify {
	
	protected ExchangeImpl doc;
	private ListView lvItems;
	private OptionsMenuHelper optionsMenuHelper = new OptionsMenuHelper();
	private ImageButton btnAdd;
	protected ImageButton btnSend;
	protected LinesCountController linesController;
	protected ImageButton btnLines;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doc = (ExchangeImpl) ExchangeDoc.instance().create();
		setContentView(R.layout.exchangedetail);
		
		long rowid;
		if( savedInstanceState == null )
			rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			rowid = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		
		if (rowid == ExtrasConst.INVALID_ID)
			return;
		
		doc.read(rowid);
		lvItems = (ListView) findViewById(R.id.lvItems);
		lvItems.setAdapter(new ItemsAdapter());
		lvItems.setOnItemClickListener(new ItemsOnClickListener());
		registerForContextMenu(lvItems);
		
		OrgImpl orgIml = new OrgImpl();
		orgIml.getData().id = doc.getData().id;
		
		if(orgIml.read()) {
			TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
			tvOrg.setText(orgIml.getData().name);
			orgIml.close();
		}
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDate(); }
		});
		refreshDate();
		
		
		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { addItem(); }
		});
		
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { send(); }
		});
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lvItems, btnLines, this);
		linesController = linesOnClickListener.getController();
	}

	private void refreshDate() {
		TextView tv = (TextView)findViewById(R.id.tvDate);
		String value = Util.simpleDateFormat.format(doc.getDate());
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	protected void changeDate() {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, doc.getDate().getTime());
		startActivityForResult(i, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode == RESULT_OK ) {
			Date curDate = new Date();
			if( data != null ) {
				Date newDate = new Date(data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime()));
				doc.getData().date = newDate;
				doc.write();
				
				refreshDate();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)	{
		if (!doc.isExported())
			getMenuInflater().inflate(
				R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(doc.getRowid(), false);
		notifyDataSetChanged();
		updateTotalSum(doc.sum(), 0);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		ExchangeItem remnantItem = (ExchangeItem)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
		PriceImpl pi = new PriceImpl();
		pi.getData().id = remnantItem.id;
		pi.read();
		pi.close();
		
		if (item.getItemId() == R.id.itDelete) {
			doc.updateQty(pi, 0, new Date());
		} else if (item.getItemId() == R.id.itEdit) {
			doc.editItem(pi.getRowid(), this);
		}
		
		notifyDataSetChanged();
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenuHelper.onCreateOptionsMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		optionsMenuHelper.onOptionsItemSelect(item);
		return super.onOptionsItemSelected(item);
	}
	
	public static void open(Context context, ExchangeImpl doc)
	{
		Intent i = new Intent(context, ExchangeDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);	
	}
	
	@Override
	protected void onStop() {
		doc.close();
		super.onStop();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		super.onSaveInstanceState(outState);
	}

	class ItemsOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (!doc.isExported()){
				ExchangeItem item = (ExchangeItem)arg1.getTag();
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.getData().id = item.id;
				
				if (priceImpl.read())
					doc.editItem(priceImpl.getRowid(), ExchangeDetail.this);
				priceImpl.close();
			}
		}
		
	}
	
	class ItemsAdapter extends BaseAdapter {

		@Override
		public int getCount() { return doc.getData().items.size(); }

		@Override
		public Object getItem(int arg0) { return doc.getData().items.get(arg0); }

		@Override
		public long getItemId(int arg0) { return 0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			ExchangeItem item = (ExchangeItem) getItem(arg0);
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.getData().id = item.id;
			priceImpl.read();
			priceImpl.close();
			
			if (view == null)
				view = View.inflate(ExchangeDetail.this, R.layout.exchange_row, null);
			
			TextView tvName = (TextView)view.findViewById(R.id.tvName);
			linesController.prepareTextView(tvName);
			tvName.setText(priceImpl.getData().name);

			TextView tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
			
			tv = (TextView)view.findViewById(R.id.tvDate);
			tv.setText(Util.simpleDateFormat.format(item.date));

			
			view.setTag(item);
			return view;
		}
	}

	@Override
	public void notifyDataSetChanged() {
		BaseAdapter adapter = (BaseAdapter) lvItems.getAdapter();
		
		if(adapter != null)
			adapter.notifyDataSetChanged();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			// remove empty remnants
			if( doc.getData().items.size() == 0 )
				doc.delete();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	class OptionsMenuHelper
	{
		public static final int MNU_ADD_ITEM_ID = 0;
		public static final int MNU_SEND_ID = 1;
		
		public void onCreateOptionsMenu(Menu menu)
		{
			menu.add(Menu.NONE, MNU_ADD_ITEM_ID, Menu.NONE, R.string.add);
			menu.add(Menu.NONE, MNU_SEND_ID, Menu.NONE, R.string.send);
		}
		
		public void onOptionsItemSelect(MenuItem item)
		{
			switch(item.getItemId())
			{
				case MNU_ADD_ITEM_ID:
					selectForAddItem();
					break;
				case MNU_SEND_ID:
					selectFormSend();
					break;
			}
		}

		private void selectFormSend() {
			send();
		}

		private void selectForAddItem() {
			addItem();
		}
	}
	
	protected void send() {
		new DocumentSender(this, null,ExchangeDoc.OBJ_NAME, doc,doc.getRowid()).execute((Void[])null);
	}
	
	protected void addItem() {
		DocType.setCurDoc(ExchangeDoc.instance());
		Warehouse.open(this, doc, true);
	}
}
