/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Форма для редатированя пуктов заявки
 *
 * kki   24/01/2011   creating
 */
package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.FocusedGroupItem;
import com.grsoft.dataobjects.FocusedItemsItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.FocusedGroupImpl;
import com.grsoft.dataobjects.impl.FocusedItemsImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ObjectExchange;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.script.ScriptActivity;
import com.grsoft.script.ScriptHelper;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.ListViewRefresher;
import com.grsoft.view.RegDurationActivity;

public class OrderDetail extends RegDurationActivity implements DataSetNotify,
		DocType.CountTextResolver, SendResultListener, ScriptActivity
{
	public static Class<? extends Activity> activity = OrderDetail.class;
	protected OrderImplBase<? extends Order> doc;
	protected DocType docType;
	protected PriceImpl price = new PriceImpl();
	protected ListView lvItems;
	protected ImageButton btnSend;
	protected LinesCountController linesController;
	protected ImageButton btnAddItems;
	protected ImageButton btnLines;
	protected ImageButton btnEditOrder;
	private OptionsMenuHelper optionsMenuHelper = new OptionsMenuHelper();
	protected static final int FOCUS_WARNING_DLG = 1;
	protected static final int SYNC_END_DIALOG = 2;
	protected OrgImpl org = new OrgImpl();
	protected static final int CANT_SEND_EMPTY_DOC_DLG = R.id.cant_send_empty_doc_dlg;
	
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	protected String getOrgText(Org o) {
		return o.name;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
		
		setDocType();
		
		doc = createDocInstance();
		
		long orderRowId;
		if( savedInstanceState == null )
			orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			orderRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		
		doc.read(orderRowId);
		org.getData().id = doc.getId();
		org.read();
		org.close();
		
		
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(Html.fromHtml(getOrgText(org.getData())));
		
		btnEditOrder = (ImageButton) findViewById(R.id.btnEditOrder);
		btnEditOrder.setOnClickListener(new EditOrderClickListener());
		
		btnAddItems = (ImageButton) findViewById(R.id.btnAddItems);
		btnAddItems.setOnClickListener(new AddItemsClickListener());

		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListenerToNotify() {
			@Override
			public void onClick(View v) {
				super.onClick(v);
				doSend();
			}
		});

		ScriptHelper.initView(this, docType.getObjectName(), doc.getData().created, doc.getId() );
		
		init();
		
		lvItems = (ListView) findViewById(R.id.lvItems);
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lvItems, btnLines, this, true);
		linesController = linesOnClickListener.getController();
//		btnLines.setOnClickListener(linesOnClickListener);
		
		if( haveFocusedGroup() ) {
			View v = findViewById(R.id.llFocus);
			if(v != null){
				v.setVisibility(View.VISIBLE);
				View focus = (View)findViewById(R.id.btnFocus);
				focus.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) { openFocusItemEditor(); }
				});
			}
		}
		
		setAdapter();
		lvItems.setOnItemClickListener(createItemsCL());
		lvItems.setOnItemLongClickListener(createLongItemsCL());
		registerForContextMenu(lvItems);
		updateTotalSum();
	}

	public void doSend() {
		if(!Features.CAN_SEND_EMPTY_DOCS && doc.isEmpty() )
			showDialog(CANT_SEND_EMPTY_DOC_DLG);
		else
			send();
	}

	@SuppressWarnings("unchecked")
	protected OrderImplBase<? extends Order> createDocInstance() {
		return (OrderImplBase<? extends Order>) docType.create();
	}

	protected void setDocType() {
		docType = (DocType) DocType.getCurDoc();
		if( OrderDoc.class.isAssignableFrom(docType.getClass()) == false ) {
			docType = OrderDoc.instance();
			DocType.setCurDoc(docType);
		}
	}
	
	public OnItemLongClickListener createLongItemsCL() {
		return null;
	}

	protected void openFocusItemEditor() {
		FocusItemEditor.open(OrderDetail.this, doc);
	}

	protected ItemsOnClickListener createItemsCL() {
		return new ItemsOnClickListener();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		PriceCount.PriceMover = null;
	}
	
	/**
	 * Используется ли в документе работа с фокусным товаром
	 * @return
	 */
	protected boolean haveFocusedGroup() { return Features.FOCUSED_GROUP || Features.FOCUSED_ITEMS; }
	
	protected void init() {
	}

	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter());
	}
	
	protected void setContentView(){
		setContentView(R.layout.orderdetail);
	}
	
	protected void updateTotalSum(){
		updateTotalSum(doc.sum(), doc.weight(), 
				((CfgNplW)ConfigManager.getConfig()).isPackView ? doc.countPack() : doc.count());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
		price.close();
	}
	
	protected void afterDocReaded() {}
	
	protected boolean haveUnsettedFocusedGroups() {
		List<FocusedGroupItem> fg = FocusedGroupImpl.getUnsettedGroups(doc);
		List<FocusedItemsItem> fi = FocusedItemsImpl.getUnsettedItems(doc);
		return fg.size() > 0 || fi.size() > 0;
	}

	protected List<OrderItem> docItems() { return doc.getData().items; }

	@Override
	protected void onResume()
	{
		super.onResume();
		doc.read(doc.getRowid(), false);
		afterDocReaded();

		((OrderItemsAdapter) lvItems.getAdapter()).setItems(docItems());

		checkFocused();
		updateTotalSum();
	}

	protected int focusButtonColor() { return Color.BLACK; }
	
	protected void checkFocused() {
		if( haveFocusedGroup() ) {
			Button btn = (Button)findViewById(R.id.btnFocus);
			
			if(btn != null){
				if(haveUnsettedFocusedGroups()) {
					btn.setText(R.string.order_required_price);
					btn.setTextColor(Color.RED);
					btnSend.setEnabled(!disableSendWithoutFocusedGroup());
				} else {
					btn.setText(R.string.required_price_ordered);
					btn.setTextColor(focusButtonColor());
					btnSend.setEnabled(true);
				}
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		outState.putString(ExtrasConst.ORG_ID_STR, doc.getId());
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)	{
		if (doc.isEditable())
			getMenuInflater().inflate(
				R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		OrderItem orderItem = (OrderItem)((AdapterContextMenuInfo)
				item.getMenuInfo()).targetView.getTag();
		
		if (item.getItemId() == R.id.itDelete) {
			deleteItem(orderItem);
		} else if (item.getItemId() == R.id.itEdit) {
			editItem(orderItem);
		}
		
		return super.onContextItemSelected(item);
	}

	protected void deleteDocItem(PriceImpl price){
		doc.updateQty(price, 0, 0, false);
	}

	protected void deleteItem(OrderItem orderItem) {
		PriceImpl pi = new PriceImpl();
		pi.getData().id = orderItem.id;
		pi.read();
		pi.close();
		deleteDocItem(pi);
		((BaseAdapter)lvItems.getAdapter()).notifyDataSetChanged();
		updateTotalSum();
		checkFocused();
	}
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
		ListViewRefresher.refresh(lvItems);
	}

	protected void editItem(OrderItem orderItem) {
		if( ((CfgNpl)ConfigManager.getConfig()).usePriceMover )
			PriceCount.PriceMover = new OrderPriceMover(doc);

		PriceImpl pi = new PriceImpl();
		pi.getData().id = orderItem.id;
		pi.read();
		pi.close();
		doc.editItem(pi.getRowid(), this);
//		PriceCount.open(this, orderItem, doc.getRowid());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == FOCUS_WARNING_DLG)
			return createFocusWarningDlg();
		else if(id == SYNC_END_DIALOG)
			return createSyncEndDlg();
		else if(id == CANT_SEND_EMPTY_DOC_DLG)
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

	protected Dialog createFocusWarningDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error);
		builder.setMessage(R.string.need_to_order_all_required_price);
		builder.setNegativeButton(R.string.close, null);
		return builder.create();
	}

	String syncTitle;
	String syncMsg;
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if( id == SYNC_END_DIALOG ) {
			dialog.setTitle(syncTitle);
			((AlertDialog)dialog).setMessage(syncMsg);
			((AlertDialog)dialog).setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override public void onDismiss(DialogInterface dialog) { openDocAgain(); }
			});
		}
	}
	
	protected Dialog createSyncEndDlg() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.error);
		b.setMessage(R.string.necessary);
		return b.create();
	}
	
	/**
	 * Запрет передачи заявки без заказанных фокусных позиций
	 * @return
	 */
	protected boolean disableSendWithoutFocusedGroup() { return Features.BLOCK_ORDER_WITHOUT_FOCUS; }

	
//	@Override
//	protected void onPause() {
//		super.onPause();
//		
//		if (isFinishing()){
//			// remove empty order
//			if( doc.getData().items.size() == 0 )
//				doc.delete();
//			else if( doc.isEditable() && haveFocusedGroup() && disableSendWithoutFocusedGroup() ) {
//				if( haveUnsettedFocusedGroups() ) {
//					doUnsettedFocus();
//					return;
//				}
//			}
//			
//			keyBackPressed();
//		}
//	}
	
	@Override
	public void onBackPressed() {
		// remove empty order
		if( Features.REMOVE_EMPTY_ORDERS && doc.isEmpty() )
			doc.delete();
		else if( doc.isEditable() && haveFocusedGroup() && disableSendWithoutFocusedGroup() ) {
			if( haveUnsettedFocusedGroups() ) {
				doUnsettedFocus();
				return;
			}
		}
					
		if (keyBackPressed())
			super.onBackPressed();
	}
	
	protected void doUnsettedFocus(){
		showDialog(FOCUS_WARNING_DLG);
	}

	protected boolean keyBackPressed() { return true;}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		optionsMenuHelper.onCreateOptionsMenu(menu);
		
		if( Features.CANT_SEND_SCRIPT_PART ) {
			if(ScriptImpl.containsDocument(docType.getObjectName(), doc.getData().created, doc.getId()) != null)
				menu.removeItem(OptionsMenuHelper.MNU_SEND_ID);
		}
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		optionsMenuHelper.onOptionsItemSelect(item);
		return super.onOptionsItemSelected(item);
	}
	
	boolean reopenDocs = false;
	
	void openDocAgain() {
		if( reopenDocs ) {
			doc.open(this);
			finish();
		}
	}
	
	
	private void writeDelivery(Order o) {
		DeliveryImpl dlv = new DeliveryImpl();
		Delivery d = dlv.getData();
		
		d.id = o.id;
		d.number = o.number;
		d.date = o.date;
		d.created = o.created;
		d.items = new ArrayList<DeliveryItem>();
		
		for(OrderItem oi : o.items) {
			DeliveryItem di = new DeliveryItem();
			di.id = oi.id;
			di.qty = oi.qty;
			di.sum = (int)((long)oi.qty * oi.cost / Consts.QTY_SCALE);
			
			d.items.add(di);
		}
		
		d.sumD = d.sum();
		
		dlv.write();
		dlv.close();
	}

	protected void checkObjectSendResult(OrderImplBase<? extends Order> object, String response, int result) {
		reopenDocs = false;

		if( result <  0 ) {
			syncTitle = getString(R.string.error);
			syncMsg = response;
		} else {
			Order o = object.getData();
			if( result == ObjectExchange.RESULT_FAIL ) {
				syncTitle = getString(R.string.error_processing);
				syncMsg = response;
			} else {
				reopenDocs = true;
				
				syncTitle = getString(R.string.result);
				if( result == ObjectExchange.RESULT_SAVE ) {
					syncMsg = (response.length() > 0) ? response : getString(R.string.doc_save_error);
					doc.getData().params &= (~(ParamState.ofExported | ParamState.ofProceeded));
				}
				else if( result == ObjectExchange.RESULT_COMMIT ) {
					syncMsg = getString(R.string.doc_process_succs);
					doc.setExported(true);
					doc.setProceeded();
				}
	
				Order ord = doc.getData();
				ord.number = o.number;
				ord.podRemark = response;
				doc.write();	
				
				writeDelivery(o);
				doc.getDocumentType().refreshDocSum(doc.getId());
			}
		}
		
		this.runOnUiThread(new Runnable() {			
			@Override public void run() { try{ showDialog(SYNC_END_DIALOG); }catch(Exception e){}}
		});
	}
	
	public void send() {
		if(Features.ORDER_ONLINE && ((CfgNplW)ConfigManager.getConfig()).onLineIP != CfgNplW.NO_ONLINE_IP ) {
			if( doc.isProceeded() ) {
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle(R.string.question);
				b.setMessage(R.string.ask_to_resend);
				b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {					
					@Override public void onClick(DialogInterface dialog, int which) {
						objectExchange();
					}
				});
				b.setNegativeButton(R.string.no, null);
				b.create().show();
			} else
				objectExchange();
		} else
			new DocumentSender(OrderDetail.this, btnSend, 
					docType.getObjectName(), doc, 
					doc.getRowid(), this).execute((Void[])null);
	}

	private void objectExchange() {
		Document<?> sendDoc = docType.create();
		sendDoc.read(doc.getRowid());
		sendDoc.close();
		new ObjectExchange(OrderDetail.this, btnSend, 
				docType.getObjectName(), 
				ObjectExchange.WRITE_OBJECTS, sendDoc, new ObjectExchange.ObjectSendedHandler() {						
					@SuppressWarnings("unchecked")
					@Override
					public void sended(DbObject<?> object, String response, int result) {
						checkObjectSendResult((OrderImplBase<? extends Order>) object, response, result);
					}
				}).execute((Void[])null);
	}
	
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		boolean showPack = (item.inPack() && ((CfgNplW)ConfigManager.getConfig()).isPackView);
		String qtyText;
		if( !showPack )
			qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
		else {
			qtyText = makePackQtyStr(item.qty, getString(R.string.pack_lbl));
		}
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
	
	protected long getItemSum(OrderItem item) {
		return (long)item.cost * item.qty / Consts.QTY_SCALE;
	}

	protected String makePackQtyStr(long iqty, String packLabel) {
		Price p = price.getData();
		int inPack = p.qtyInPack;
		if( inPack == 0 )
			inPack = Consts.QTY_SCALE;
		int qty = (int)(iqty * Consts.QTY_SCALE / inPack);
		String qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + packLabel;
		return qtyText;
	}

	@Override
	public boolean closeDocument() {
		if(doc.isEmpty()) {
			doc.delete();
			return false;
		}
		return true;
	}

	class EditOrderClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) { 
			super.onClick(v);
			doc.editProperties(v.getContext());
		}
	}
	
	class AddItemsClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			Warehouse.open(v.getContext(), doc, true);
		}		
	}
	
	class ItemsOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			editItem((OrderItem)arg1.getTag());
		}
	}
	
	class OptionsMenuHelper {
		public static final int MNU_ADD_ITEM_ID = 0;
		public static final int MNU_EDIT_DOC_ID = 1;
		public static final int MNU_ROW_COUNT_DOC_ID = 2;
		public static final int MNU_SEND_ID = 3;
		
		public void onCreateOptionsMenu(Menu menu)
		{
			menu.add(Menu.NONE, MNU_ADD_ITEM_ID, Menu.NONE, R.string.add);
			menu.add(Menu.NONE, MNU_EDIT_DOC_ID, Menu.NONE, R.string.edit);
			menu.add(Menu.NONE, MNU_ROW_COUNT_DOC_ID, Menu.NONE, R.string.lines);
			menu.add(Menu.NONE, MNU_SEND_ID, Menu.NONE, R.string.send);
		}
		
		public void onOptionsItemSelect(MenuItem item)
		{
			switch(item.getItemId())
			{
				case MNU_ADD_ITEM_ID:
					selectForAddItem();
					break;
				case MNU_EDIT_DOC_ID:
					selectForEditDoc();
					break;
				case MNU_ROW_COUNT_DOC_ID:
					selectForCountRow();
					break;
				case MNU_SEND_ID:
					selectFormSend();
					break;
			}
		}

		
		private void selectFormSend()
		{
			if(btnSend != null)
				btnSend.performClick();
		}

		private void selectForCountRow()
		{
			if(btnLines != null)
				btnLines.performClick();
		}

		private void selectForEditDoc()
		{
			if(btnEditOrder != null)
				btnEditOrder.performClick();
		}

		private void selectForAddItem()
		{
			if(btnAddItems != null)
				btnAddItems.performClick();
		}
	}

	public class OrderItemsAdapter extends BaseAdapter
	{
		protected List<OrderItem> items = null;
	
		public OrderItemsAdapter() {}
		
		@Override public int getCount() { return (items != null) ? items.size() : 0; }
	
		@Override public Object getItem(int arg0) { return (items != null) ? items.get(arg0) : null; }
	
		@Override public long getItemId(int arg0) { return arg0; }
		
		protected void setItems(List<OrderItem> items) {
			this.items = items;
			notifyDataSetChanged();
		}
		
		protected long getItemSum(OrderItem item) {
			return OrderDetail.this.getItemSum(item);
		}

		protected void drawSum(TextView tvSum, OrderItem item, int color) {
			long sum = getItemSum(item);
			tvSum.setText(Util.IntToScaleWStr(sum, Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, false));
			tvSum.setGravity(Gravity.RIGHT);
			tvSum.setTextColor(color);
		}

		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			TextView tvName = (TextView)view.findViewById(R.id.tvName);
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);						
	
			linesController.prepareTextView(tvName);
			tvName.setText(name);
			tvName.setTextColor(color);

			drawSum(tvSum, item, color);

			TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
			drawItemQty(color, item, tvQty);
		
			if( Features.SHOW_NUMBER_IN_ORDER ) {
				TextView tv; 
				tv = (TextView)view.findViewById(R.id.tvOrder);
				if( tv != null ) {
					tv.setVisibility(View.VISIBLE);
					tv.setText(Integer.toString(pos + 1));
				}
			}			
		}
		
		int getResourceID() { return R.layout.orderdetail_list_row; }
	
		@Override
		public View getView(int pos, View arg1, ViewGroup arg2)
		{
			String name;
			OrderItem item = (OrderItem) getItem(pos);
			Price p = price.getData();
			p.id = item.id;
			if( price.read() )
				name = p.name;
			else
				name = "< " + getString(R.string.id) + " '" + item.id + "' >";
			
			View view = arg1; 			
			if (view == null)
				view = View.inflate(OrderDetail.this, getResourceID(), null);
			
			view.setTag(item);
			view.findViewById(R.id.tvName).setTag(item.id);
			
			drawInternal(view, name, getItemColor(pos), item, pos);
			return view;
		}
		
		protected int getItemColor(int pos){
			return Color.BLACK;
		}
	}

	@Override
	public void notifyDataSetChanged() {
		if( doc != null && doc.getRowid() != ExtrasConst.INVALID_ID ) {
			doc.read(doc.getRowid(), false);
			updateTotalSum();
		}
		
		OrderItemsAdapter adapter = ((OrderItemsAdapter) lvItems.getAdapter());
		
		if (adapter != null) {
			adapter.setItems(doc.getData().items);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public String getCountText() {
		return getString(((CfgNplW)ConfigManager.getConfig()).isPackView ? R.string.pack_lbl: R.string.sht);
	}

	@Override public void postSendExecute(boolean result) {}

}