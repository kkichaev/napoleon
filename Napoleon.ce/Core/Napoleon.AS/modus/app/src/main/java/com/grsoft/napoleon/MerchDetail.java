package com.grsoft.napoleon;

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
import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;


public class MerchDetail extends BaseActivity 
implements DataSetNotify{
public static Class<? extends Activity> activity = MerchDetail.class;
	
	protected MerchImpl merchImpl;
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
		
		merchImpl = (MerchImpl) DocType.getCurDoc().create();
		setContentView(getLayoutId());
		
		long rowid;
		if( savedInstanceState == null )
			rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			rowid = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		
		if (rowid == ExtrasConst.INVALID_ID)
			return;
		
		merchImpl.read(rowid);
		lvRemnantItems = (ListView) findViewById(R.id.lvRemnantItems);
		lvRemnantItems.setAdapter(createAdapter());
		lvRemnantItems.setOnItemClickListener(createItemsOnClickHandler());
		registerForContextMenu(lvRemnantItems);
		
		OrgImpl orgIml = new OrgImpl();
		orgIml.getData().id = merchImpl.getData().id;
		
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
		
		if( Features.CANT_SEND_SCRIPT_PART ) 
				btnSend.setVisibility(View.GONE);
		
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(merchImpl.getData().items.size() == 0)
					showDialog(CANT_SEND_EMPTY_DOC_DLG);
				else
					send();
			}
		});
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
	
	protected int getLayoutId() { return R.layout.merchdetail; }

	protected ItemsOnClickListener createItemsOnClickHandler() {
		return new ItemsOnClickListener();
	}

	protected MerchItemsAdapter createAdapter() {
		return new MerchItemsAdapter();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		if (!merchImpl.isExported())
			getMenuInflater().inflate(
				R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		merchImpl.read(merchImpl.getRowid(), false);
		notifyDataSetChanged();
		updateTotalSum(merchImpl.sum(), 0);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		MerchItem remnantItem = (MerchItem)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
		PriceImpl pi = new PriceImpl();
		pi.getData().id = remnantItem.id;
		pi.read();
		pi.close();
		
		if (item.getItemId() == R.id.itDelete) {
			merchImpl.removeItem(remnantItem.id);
		} else if (item.getItemId() == R.id.itEdit) {
			merchImpl.editItem(pi.getRowid(), this);
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
	
	public static void open(Context context, MerchImpl doc)
	{
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);	
	}
	
	@Override
	protected void onStop() {
		merchImpl.close();
		super.onStop();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, merchImpl.getRowid());
		super.onSaveInstanceState(outState);
	}

	class ItemsOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (!merchImpl.isExported()){
				MerchItem item = (MerchItem)arg1.getTag();
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.getData().id = item.id;
				
				if (priceImpl.read())
					merchImpl.editItem(priceImpl.getRowid(), MerchDetail.this);
				priceImpl.close();
			}
		}
	}
	
	class MerchItemsAdapter extends BaseAdapter {
		@Override
		public int getCount() { return merchImpl.getData().items.size(); }

		@Override
		public Object getItem(int arg0) { return merchImpl.getData().items.get(arg0); }

		@Override
		public long getItemId(int arg0) { return 0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if (view == null)
				view = View.inflate(MerchDetail.this, getViewId(), null);
			
			MerchItem mi = (MerchItem) getItem(pos);
			
			if (mi != null){
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.getData().id = mi.id;
				priceImpl.read();
				priceImpl.close();
				
				TextView tvName = (TextView)view.findViewById(R.id.tvName);
				linesController.prepareTextView(tvName);
				tvName.setText(priceImpl.getData().name);
				TextView tv = (TextView)view.findViewById(R.id.tvStart);
				tv.setText(Util.IntToScaleStr(mi.start, Consts.QTY_SCALE));
				tv = (TextView)view.findViewById(R.id.tvFinish);
				tv.setText(Util.IntToScaleStr(mi.finish, Consts.QTY_SCALE));
				
				view.setTag(mi);
			}
			
			return view;
		}

		protected int getViewId() {	return R.layout.merchdetail_list_row; }
	}

	@Override
	public void notifyDataSetChanged() {
		BaseAdapter adapter = (BaseAdapter) lvRemnantItems.getAdapter();
		
		if(adapter != null)
			adapter.notifyDataSetChanged();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing() && merchImpl.getData().items.size() == 0)
				merchImpl.delete();
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
		new DocumentSender(MerchDetail.this, null,
				RemnantsDoc.instance().getObjectName(), merchImpl, 
				merchImpl.getRowid()).execute((Void[])null);
	}
	
	protected void addItem() {	Warehouse.open(MerchDetail.this, merchImpl, true); }
}
