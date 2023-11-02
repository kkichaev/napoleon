/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Редактирование остатков
 *
 * kki   12/04/2011   creating
 */
package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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

import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.script.ScriptActivity;
import com.grsoft.script.ScriptHelper;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
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
public class RemnantsDetail extends RegDurationActivity 
	implements DataSetNotify, ScriptActivity {
	
	public static Class<? extends Activity> activity = RemnantsDetail.class;
	
	protected RemnantsImpl remnantsImpl;
	protected ListView lvRemnantItems;
	private OptionsMenuHelper optionsMenuHelper = new OptionsMenuHelper();
	private ImageButton btnAdd;
	protected ImageButton btnSend;
	protected LinesCountController linesController;
	protected ImageButton btnLines;
	protected static final int CANT_SEND_EMPTY_DOC_DLG = R.id.cant_send_empty_doc_dlg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		remnantsImpl = (RemnantsImpl) RemnantsDoc.instance().create();
		setContentView(getLayoutId());
		setTitle(R.string.remains_doc_title);
		
		long rowid;
		if( savedInstanceState == null )
			rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			rowid = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		
		if (rowid == ExtrasConst.INVALID_ID)
			return;
		
		remnantsImpl.read(rowid);
		lvRemnantItems = (ListView) findViewById(R.id.lvRemnantItems);
		lvRemnantItems.setAdapter(createAdapter());
		lvRemnantItems.setOnItemClickListener(createItemsOnClickHandler());
		registerForContextMenu(lvRemnantItems);
		
		OrgImpl orgIml = new OrgImpl();
		orgIml.getData().id = remnantsImpl.getData().id;
		
		if(orgIml.read())
		{
			TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
			tvOrg.setText(orgIml.getData().name);
			orgIml.close();
		}
		
		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addItem();
			}
		});
		
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(remnantsImpl.getData().items.size() == 0)
					showDialog(CANT_SEND_EMPTY_DOC_DLG);
				else
					send();
			}
		});

		ScriptHelper.initView(this, RemnantsDoc.instance().getObjectName(), remnantsImpl.getData().created, remnantsImpl.getId());
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lvRemnantItems, btnLines, this, true);
		linesController = linesOnClickListener.getController();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == CANT_SEND_EMPTY_DOC_DLG)
			return cantSendEmptyDocDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog cantSendEmptyDocDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error);
		builder.setMessage(R.string.cant_send_empty_doc_str);
		return builder.create();
	}
	
	protected int getLayoutId() {
		return R.layout.remnantsdetail;
	}

	protected ItemsOnClickListener createItemsOnClickHandler() {
		return new ItemsOnClickListener();
	}

	protected RemnantItemsAdapter createAdapter() {
		return new RemnantItemsAdapter();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		if (!remnantsImpl.isExported())
			getMenuInflater().inflate(
				R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		remnantsImpl.read(remnantsImpl.getRowid(), false);
		notifyDataSetChanged();
		updateTotalSum(remnantsImpl.sum(), 0);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		RemnantItem remnantItem = (RemnantItem)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
		PriceImpl pi = new PriceImpl();
		pi.getData().id = remnantItem.id;
		pi.read();
		pi.close();
		
		if (item.getItemId() == R.id.itDelete) {
			remnantsImpl.deleteItem(pi.getData());
//			remnantsImpl.updateQty(pi, 0, 0 ,false);
		} else if (item.getItemId() == R.id.itEdit) {
			remnantsImpl.editItem(pi.getRowid(), this);
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
	
	public static void open(Context context, 
			RemnantsImpl remnantsImpl)
	{
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, remnantsImpl.getRowid());
		context.startActivity(i);	
	}
	
	@Override
	protected void onStop() {
		remnantsImpl.close();
		super.onStop();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, remnantsImpl.getRowid());
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean closeDocument() {
		return remnantsImpl.getData().items.size() > 0;
	}

	class ItemsOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (!remnantsImpl.isExported()){
				RemnantItem item = (RemnantItem)arg1.getTag();
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.getData().id = item.id;
				
				if (priceImpl.read())
					remnantsImpl.editItem(priceImpl.getRowid(), RemnantsDetail.this);
				priceImpl.close();
			}
		}
	}
	
	class RemnantItemsAdapter extends BaseAdapter {

		@Override
		public int getCount() { return remnantsImpl.getData().items.size(); }

		@Override
		public Object getItem(int arg0) { return remnantsImpl.getData().items.get(arg0); }

		@Override
		public long getItemId(int arg0) { return 0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			RemnantItem remnantItem = (RemnantItem) getItem(arg0);
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.getData().id = remnantItem.id;
			priceImpl.read();
			priceImpl.close();
			
			View view = setView(arg1, priceImpl, remnantItem.qty, remnantItem);
			if( Features.SHOW_NUMBER_IN_ORDER ) {
				TextView tv; 
				tv = (TextView)view.findViewById(R.id.tvOrder);
				if( tv != null ) {
					tv.setVisibility(View.VISIBLE);
					tv.setText(Integer.toString(arg0+1));
				}
			}			
			return view;
		}
		
		protected View setView(View view, PriceImpl priceImpl, int qty, Object tag) {
			if (view == null)
				view = View.inflate(RemnantsDetail.this, getViewId(), null);
			
			TextView tvName = (TextView)view.findViewById(R.id.tvName);
			linesController.prepareTextView(tvName);
			tvName.setText(priceImpl.getData().name);
			TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
			tvQty.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
			
			view.setTag(tag);

			return view;
		}

		protected int getViewId() { return getItemViewId();	}
	}
	
	protected int getItemViewId(){	return R.layout.remnantsdetail_list_row; }

	@Override
	public void notifyDataSetChanged() {
		BaseAdapter adapter = (BaseAdapter) lvRemnantItems.getAdapter();
		
		if(adapter != null)
			adapter.notifyDataSetChanged();
		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing() && remnantsImpl.isEditable())
			removeEmptyDoc();
	}
	
	protected void removeEmptyDoc(){
		if(remnantsImpl.isEmpty()){
			remnantsImpl.delete();
			remnantsImpl.close();
		}
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
		new DocumentSender(RemnantsDetail.this, null,
				RemnantsDoc.instance().getObjectName(), remnantsImpl, 
				remnantsImpl.getRowid()).execute((Void[])null);
	}
	
	protected void addItem() {
		DocType.setCurDoc(RemnantsDoc.instance());
		Warehouse.open(RemnantsDetail.this,
				remnantsImpl, true);
	}

}
