/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Список документов
 *
 * kki   12/03/2011   creating
 */
package com.grsoft.napoleon;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeSender;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.DialogOwner;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BackgroudProcess;
import com.grsoft.view.Refreshable;
import com.grsoft.view.RegDurationActivity;
import com.grsoft.view.RunnableProcess;

public class DocList extends RegDurationActivity 
	implements Selector, OnDateSetListener, 
	Refreshable, DialogOwner, SendResultListener
{
	
	public static Class<? extends Activity> activity = DocList.class;
	
	protected static final int DLG_FILTER_SELECT = 0;

	private static final int BEGIN_DATE_CODE = 10;
	private static final int END_DATE_CODE = 11;
	private static final int FILTER_ITEM_CODE = 12;
	
	protected OrgImpl org = new OrgImpl();
	protected ListView lvDocs;
	protected DocListAdapter adapter;
	protected DocStatusChangeListener sendStatusClickListener;
	protected ImageButton btnDocFilter;
	private OptionsMenuHelper optionsMenuHelper = createOptionsMenu();
	protected ImageButton btnSend;
	protected ImageButton btnDelete;
	private Dialog activeDialog;
	protected LinearLayout llFilterPanel;
	protected ImageButton btnFilter;
	private String itemId;
	private String itemName;
	protected DatePeriod saveDatePeriod;
	private String startOrgID;
	//private boolean oldScriptOff;
	
	PriceImpl priceImpl = new PriceImpl();
	Price price = null;
	@SuppressLint("UseSparseArrays")
	HashMap<Long, Integer> values = new HashMap<Long, Integer>();
	
	boolean inited = false;
	
	static void open(Context context) {
		Intent i = new Intent(context, activity);
		context.startActivity(i);		
	}

	static void open(Context context, String orgId) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		context.startActivity(i);		
	}
	
	@Override
	protected void onStop() {
		close();
		super.onStop();
	}
	
	void close() {
		if( adapter != null )
			adapter.close();
		org.close();
		priceImpl.close();
	}
	
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		return doc.isProceeded() ? R.drawable.pcd : 
				doc.isExported() ? R.drawable.sent : 
				R.drawable.notsend;
	}

	protected DocListAdapter createListAdapter(DocType docType){
		return new DocListAdapter(this, docType, saveDatePeriod);
	}
	
	protected DocStatusChangeListener createStatusChangeListener() {
		return new DocStatusChangeListener();
	}
	
	protected String getDocText(Org o, Document<?> doc) {
		String text = "<b>" + o.name + "</b>";
		if( Features.SHOW_ORG_ADDRESS && o.address.length() > 0 )
			text += "<br>" + o.address;
		
		return text;
	}
	
	protected int getDocColor(Document<?> doc) { return Color.BLACK; }
	
	protected void drawData(View view, Document<?> doc, int position) {
		if( doc != null ) {
			Org o = org.getData();
			o.id = doc.getId();
			boolean readed = org.read();
			
			int color = getDocColor(doc);
			
			ImageView ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
			if( doc instanceof CreatableDocument<?> ) {
				ivStatus.setVisibility(View.VISIBLE);
				ivStatus.setImageResource(getDocStatusResource((CreatableDocument<?>)doc));
				
				if (!Features.CANT_CHANGE_SEND_FLAG){
					ivStatus.setOnClickListener(sendStatusClickListener);
					ivStatus.setTag(position);
				}
			} else {
				ivStatus.setImageResource(R.drawable.notsend);
				ivStatus.setVisibility(View.INVISIBLE);
			}
				//ivStatus.setImageResource(android.R.color.transparent);
			
			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			String text = "";
			if( readed )
				text = getDocText(o, doc);
			tvName.setText(Html.fromHtml(text));
			tvName.setTextColor(color);
			
			TextView tvDate = (TextView)view.findViewById(R.id.tvDate);
			tvDate.setText(Util.simpleDateFormat.format(doc.getDate()));
			tvDate.setTextColor(color);
			
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
			text = docSumText(doc);
			Integer qty = values.get(doc.getRowid());
			if( qty != null ) {
				boolean packView = ((CfgNplW)ConfigManager.getConfig()).isPackView; 
				if( packView && price != null && price.qtyInPack != 0 )
					qty = (int)((long)qty * Consts.QTY_SCALE / price.qtyInPack);
				String qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE);
				if( packView )
					qtyText += " у.";
				
				text += "<br><i>(" + qtyText + ")</i>"; 
			}
			tvSum.setText(Html.fromHtml(text));
			tvSum.setTextColor(color);
			if(Features.DOC_STATUS_IN_DOC_LIST) {
				TextView tv = (TextView)view.findViewById(R.id.tvStatus);
				tv.setText(Html.fromHtml(doc.getDescription(this)));
				tv.setVisibility(View.VISIBLE);
			}
		}
	}

	protected String docSumText(Document<?> doc) {
		return Util.IntToScaleWStr(getDocSum(doc), Consts.SUM_SCALE, 2, false);
	}
	
	class DocListAdapter extends DocumentsAdapter {
		private final static String DEF_ORDER = "created desc"; 
		public DocListAdapter(Context context, DocType docType, DatePeriod filter) {
			this(context, docType, filter, DEF_ORDER);
		}
		
		public DocListAdapter(Context context, DocType docType, DatePeriod filter, String order) {
			super(context, docType, null, order, R.layout.docs_list_row2, filter);
		}
		
		protected DocListAdapter(Context context, DocType docType, int layoutid) {
			super(context, docType, null, DEF_ORDER, layoutid);
		}
		
		protected DocListAdapter(Context context, DocType docType, DatePeriod filter, int layoutid) {
			super(context, docType, null, DEF_ORDER, layoutid, filter);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			drawData(view, doc, position);
		}
		
		@Override
		public void fetchByPeriod(DocType docType, DatePeriod dp) {
			fetchByPeriod(docType, dp, null, null, values);
		}
		
		public void fetch(DocType docType, DatePeriod dp, String id, Price p) {
			orgId = id;
			fetchByPeriod(docType, dp, orgId, p, values);
		}
		
		public DatePeriod getFilter(){
			return datePeriod;
		}
		
		public String getItemId() {
			return itemId;
		}

		public String getOrgId() {
			return orgId;
		}

		public void setOrgID(String orgID) {
			this.orgId = orgID;
			fetchByPeriod(curDocType, datePeriod, orgID, null, values);
		}
	}
	
	protected OptionsMenuHelper createOptionsMenu() {
		return new OptionsMenuHelper();
	}
	
	protected DocType getDefaultDocType(){
		if (DocType.docTypes.size() > 0)
			return (DocType) DocType.docTypes.get(0);
		else
			return OrderDoc.instance();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		Debug.startMethodTracing("docList");
		
		super.onCreate(savedInstanceState);
		setContentView(getViewID());
		llFilterPanel = (LinearLayout) findViewById(R.id.llFilterPanel);
		Calendar calendar = Calendar.getInstance();
		Date now = Util.getDate();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		
		saveDatePeriod = makeInitialDatePeriod(now, calendar.getTime());

		initUI();
		
		if(DocType.getCurDoc().isCreatable() == false)
			DocType.setCurDoc(getDefaultDocType());
		
		loadConfig((savedInstanceState != null) ? savedInstanceState : getIntent().getExtras());

		llFilterPanel.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				llFilterPanelClick();
			}});
		
		sendStatusClickListener = createStatusChangeListener();
	}
	
	protected DatePeriod makeInitialDatePeriod(Date begin, Date end) {
		return new DatePeriod(begin, end);
	}
	
	protected void llFilterPanelClick() {
		adapter.fetchByPeriod((DocType) DocType.getCurDoc(), null);
		refreshTotalSum(false);
		llFilterPanel.setVisibility(View.GONE);
	}
	
	protected void initUI() { }

	protected int getViewID() {
		return R.layout.doclist;
	}
	
	@Override
	protected void onDestroy() {
//		Debug.stopMethodTracing();
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
//		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
//		oldScriptOff = cfg.scriptOff;
//		
//		if(!oldScriptOff && Features.SCRIPT_OFF_IN_DOC_LIST)
//			cfg.scriptOff = true;
		
		if( inited )
			onResumeAdapter();
		else
			inited = true;
		
		if (adapter != null)
			refreshTotalSum(adapter.getFilter() != null);
	}

	protected void onResumeAdapter() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}
	
	protected void loadConfig(Bundle b) {
//		if(ScriptDefImpl.canScripting() && (Features.SCRIPT_OFF_IN_DOC_LIST || oldScriptOff))
		if(ScriptDefImpl.canScripting() )
			DocType.setCurDoc(getDefaultDocType());
		
		if( b != null )
			startOrgID = b.getString(ExtrasConst.ORG_ID_STR);
		
		init(getInitialDocType());
	}
	
	protected DocType getInitialDocType() {
		return (DocType) DocType.getCurDoc();
	}
	
	protected void init(DocType docType)
	{
		lvDocs = (ListView) findViewById(R.id.lvDocs);
		
		registerForContextMenu(lvDocs);
		
		btnDocFilter = (ImageButton)findViewById(R.id.btnDocFilter);
		btnDocFilter.setOnClickListener(createDocListFilter());
		
		btnDelete = (ImageButton) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new DeleteOnClickListener());
		
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new SendOnClickListener());
		
		btnFilter = (ImageButton)findViewById(R.id.btnFilter);
		btnFilter.setOnClickListener(new FilterOnClickListener());
		
		adjustViewForDocType(docType);
	}

	protected DocFilterOnClickListener createDocListFilter() {
		return new DocFilterOnClickListener(this, true, false);
	}
	
	protected long getDocSum(Document<?> doc) {
		return doc == null ? 0 : doc.sum();
	}
	
	/**
	 * Итоговая сумма расчитывается из сумм документов или беретсся из OrgSumImpl   
	 * @param useFilter
	 * @return
	 */
	protected boolean countSumFromDocuments(boolean useFilter) { return useFilter; }
	
	protected int countDocs(DocListAdapter adapter) { return adapter.getCount(); }
	
	protected void refreshTotalSum(boolean useFilter) {
		if( Features.SHOW_WEIGHT_IN_DOC_LIST && DocType.getCurDoc() instanceof OrderDoc) {
			long sum = 0;
			int weight = 0;
			int count = 0;

			for( int i=0; i<adapter.getCount(); i++ ) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				
				if(d != null){
					sum += getDocSum(d);
					if(d instanceof OrderImplBase<?>) {
						weight += ((OrderImplBase<?>)d).weight();
						count += ((OrderImplBase<?>)d).count();
					}
				}
			}
			
			DocType.getCurDoc().updateTotalSum(this, sum, weight, count, R.id.tvDocSum);
			return;
		}
		TextView tv = (TextView)findViewById(R.id.tvDocSum);
		if( tv != null ) {
			long sum = 0;
			if( countSumFromDocuments(useFilter) ) {
				for( int i=0; i<adapter.getCount(); i++ ) {
					Document<?> d = (Document<?>) adapter.getItem(i);
					sum += getDocSum(d);
				}
			} else
				sum = OrgSumImpl.docSum(DocType.getCurDoc().getName());
			
			tv.setTypeface(Typeface.create(tv.getTypeface(), Typeface.NORMAL));
			String sumstr = DocType.SumConverter.toString(sum);
			String text = "<b>" + sumstr + "</b>"; 
			if( Features.COUNT_DOCS_IN_DOCSLIST ) {
				text += "<br>" + Integer.toString(countDocs(adapter));
			}
			tv.setText(Html.fromHtml(text));
		}
	}
	
	protected void setFilterText(DatePeriod dp, String name) {
		Calendar c = Calendar.getInstance();
		
		c.setTime(dp.begin);
		int y1 = c.get(Calendar.YEAR);
		int m1 = c.get(Calendar.MONTH);
		int d1 = c.get(Calendar.DATE);

		c.setTime(dp.end);
		int y2 = c.get(Calendar.YEAR);
		int m2 = c.get(Calendar.MONTH);
		int d2 = c.get(Calendar.DATE);

		llFilterPanel.setVisibility(View.VISIBLE);

		if( itemId != null && itemId.length() > 0 ) {
			price = priceImpl.getData();
			price.id = itemId;
			priceImpl.read();
			
			String priceFilter = "товар: " + itemName; 
			if( name == null )
				name = priceFilter;
			else
				name += " " + priceFilter;
		} else
			price = null;

		setFilterText(d1, m1, y1, d2, m2, y2, name);
	}
	
	protected void setFilterText(int d1,int m1, int y1,int d2, int m2, int y2, String org){
		TextView tvFilter = (TextView) llFilterPanel.findViewById(R.id.tvFilter);
		String data = getString(R.string.date_filter, d1,m1+1,y1,d2,m2+1,y2);
		if( org != null )  
			data += "<br>по " + org;
		tvFilter.setText(Html.fromHtml(data));
	}
	
	protected void adjustViewForDocType(DocType docType)
	{
		DocType.setCurDoc(docType);
		
		if( adapter == null ) {
			adapter = createListAdapter(docType);
			if( startOrgID != null ) {
				OrgImpl oi = new OrgImpl();
				Org o = oi.getData();
				o.id = startOrgID;
				oi.read();
				oi.close();
								
				TextView tvFilter = (TextView) llFilterPanel.findViewById(R.id.tvFilter);
				tvFilter.setText(o.name);
				adapter.setOrgID(startOrgID);
			} else {
				llFilterPanel.setVisibility(View.VISIBLE);
				setFilterText(saveDatePeriod, null);
			}
			lvDocs.setAdapter(adapter);
			lvDocs.setDividerHeight(0);
			lvDocs.setOnItemClickListener( adapter.clickListner() );
		}
		else 
			adapter.setDocType(docType);
		
		DatePeriod filter = adapter.getFilter();
		
		llFilterPanel.setVisibility(filter == null && startOrgID == null ? View.GONE : View.VISIBLE);
		
		if(filter != null){
			String name = null;
			String id = adapter.getOrgId();
			if( id != null ) {
				OrgImpl oi = new OrgImpl();
				oi.getData().id = id;
				if( oi.read() )
					name = oi.getData().name;
			}
			setFilterText(filter, name);
		}
		
		ImageButton btnDocFilter = (ImageButton)findViewById(R.id.btnDocFilter);
		btnDocFilter.setImageResource(docType.getResurce2Id());
		
		refreshTotalSum(filter != null);
	}

	public void selectedType(DocType newDocType){
		DocType docType = (DocType) DocType.getCurDoc();
		if( newDocType != null && (docType == null  || newDocType.equals(docType) == false) )
			adjustViewForDocType((DocType) newDocType);
		
		btnSend.setEnabled(newDocType.isCreatable());
		btnDelete.setEnabled(newDocType.isCreatable());
	}
	
	class DocStatusChangeListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			int index = (Integer)v.getTag();
			CreatableDocument<?> cd = (CreatableDocument<?>)adapter.getItem(index);
			if( cd != null && isAllowChangeStatus(cd) ) {
				changeStatus(cd);
				((ImageView) v).setImageResource(getDocStatusResource(cd));
			}
		}

		protected void changeStatus(CreatableDocument<?> cd) {
			cd.setExported(!cd.isExported());
		}

		protected boolean isAllowChangeStatus(CreatableDocument<?> cd) {
			if( cd.isProceeded() )
				return false;
			
			return Features.CAN_SET_SEND_FLAG || cd.isExported();
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		if (adapter.getItem(((AdapterContextMenuInfo)menuInfo).position) 
				instanceof CreatableDocument<?>){
			getMenuInflater().inflate(getContextMenu(), menu);
			MenuItem item = menu.findItem(R.id.itCopy);
			item.setVisible(false);
		}
	}

	protected int getContextMenu() { return R.menu.doc_context_menu; }
	
	protected void docDelete(CreatableDocument<?> doc){
		DocDeleteHelper.delete(doc, this);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo menuInfo = 
			(AdapterContextMenuInfo) item.getMenuInfo();
		
		CreatableDocument<?> doc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
		if( doc != null ) {
			if (item.getItemId() == R.id.itDelete) {
				docDelete(doc);
			} else if (item.getItemId() == R.id.itEdit) {
				doc.open(this);
			}
		}
		
		return false;
	}
	
	class DeleteOnClickListener extends OnClickListenerToNotify
	{
		
		public DeleteOnClickListener() {
		}
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(v.getContext(), CalendarActivity.class);
			i.putExtra(ExtrasConst.DATE_TAG, Calendar.getInstance().getTime().getTime());
			startActivityForResult(i, R.id.delete_dlg);
		}
		
	}

	@Override
	public void onDateSet(DatePicker view, final int year, final int monthOfYear,
			final int dayOfMonth)
	{
		removeDocs(year, monthOfYear, dayOfMonth);
	}

	protected void removeDocs(final int year, final int monthOfYear, final int dayOfMonth) {
		BackgroudProcess backgroudProcess = new BackgroudProcess(this, 
				new RunnableProcess()
				{
					
					@Override
					public void run()
					{
						Calendar calendar = Calendar.getInstance();
						calendar.set(year,monthOfYear,dayOfMonth,23, 59, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						
						Date dateBegin = calendar.getTime();
						
						DocType.getCurDoc().removeTill(dateBegin);
					}
					
					@Override
					public void onPreExecute(){}
					
					@Override
					public void onPostExecute() { refreshContent(); }
				});
		
		backgroudProcess.execute((Void[])null);
	}
	
	class SendOnClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			if (DocType.getCurDoc().isCreatable())
				 send();
		}
		
	}
	
	/***
	 * Отправляет все документы выбранного типа
	 */
	protected void send(){ new DocTypeSender(this, findViewById(R.id.btnSend), DocType.getCurDoc()).execute((Void[])null); }
	
	class FilterOnClickListener extends OnClickListenerToNotify{
		@Override
		public void onClick(View v) {
			super.onClick(v);
			filter();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		optionsMenuHelper.onCreateOptionsMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		optionsMenuHelper.onOptionsItemSelect(item);
		return super.onOptionsItemSelected(item);
	}
	
	class OptionsMenuHelper
	{
		public static final int MNU_DOC_LIST_ID = 0;
		public static final int MNU_SEND_DOC_ID = 1;
		public static final int MNU_DEL_DOC_ID = 2;
		public static final int MNU_FILTER_ID = 3;
		
		public void onCreateOptionsMenu(Menu menu)
		{
			menu.add(Menu.NONE, MNU_DOC_LIST_ID, Menu.NONE, R.string.docs);
			menu.add(Menu.NONE, MNU_SEND_DOC_ID, Menu.NONE, R.string.send);
			menu.add(Menu.NONE, MNU_DEL_DOC_ID, Menu.NONE, R.string.delete);
			menu.add(Menu.NONE, MNU_FILTER_ID, Menu.NONE, R.string.filter_by_date);
		}
		
		public void onOptionsItemSelect(MenuItem item)
		{
			switch(item.getItemId())
			{
				case MNU_DOC_LIST_ID:
					selectForNewDocType();
					break;
				case MNU_SEND_DOC_ID:
					selectForSendDoc();
					break;
				case MNU_DEL_DOC_ID:
					selectForDelDoc();
					break;
				case MNU_FILTER_ID:
					selectForFilter();
					break;
			}
		}

		
		private void selectForFilter() {
			if (btnFilter != null)
				btnFilter.performClick();
		}

		private void selectForDelDoc()
		{
			if (btnDelete != null)
				btnDelete.performClick();
			
		}

		private void selectForSendDoc()
		{
			if (btnSend != null)
				btnSend.performClick();
			
		}

		private void selectForNewDocType()
		{
			if (btnDocFilter != null)
				btnDocFilter.performClick();
		}
	}
	
	@Override
	public void refreshContent() {
		adapter.setDocType((DocType) DocType.getCurDoc());
	}

	public void filter() {
		showDialog(DLG_FILTER_SELECT);
	}

	@Override
	public void setActiveDialog(Dialog dlg) {
		activeDialog = dlg;
	}
	
	@Override
	protected void onPause() {
		//CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		//cfg.scriptOff = oldScriptOff; 

		if (activeDialog != null){
			try{
				activeDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
			
		super.onPause();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DLG_FILTER_SELECT:
			return createDlgFilter();
		default:
			return super.onCreateDialog(id);
		}
	}

	void setDate(int dateType, Date date) {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, date.getTime());
		startActivityForResult(i, dateType);
	}
	
	void setFilterItem(String text) {
		Intent i = new Intent(this, Warehouse.activity);
		i.putExtra(ExtrasConst.WAREHOUSE_ID_TAG, text);
		startActivityForResult(i, FILTER_ITEM_CODE);
	}
	
	View dialogView;
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		
		if (FILTER_ITEM_CODE == requestCode)
		{
			itemId = data.getExtras().getString(ExtrasConst.WAREHOUSE_ID_TAG);
			if (null == itemId)
				return;
			
			itemName = data.getExtras().getString(ExtrasConst.WAREHOUSE_NAME_TAG);
			TextView tv = (TextView)dialogView.findViewById(R.id.tvFilterItem);
			tv.setText(itemName);
			CheckBox cbFilterItem = (CheckBox)dialogView.findViewById(R.id.cbByItem);
			cbFilterItem.setChecked(true);
		}
		else if (requestCode == R.id.delete_dlg) {
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, new Date().getTime());
			Date newDate = new Date(ct);
			
			removeDocs(newDate.getYear() + 1900, newDate.getMonth(), newDate.getDate());
		}else{
			Date curDate = new Date();
			if( data != null ) {
				long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
	
				Date newDate = new Date();
				int id = R.id.tvDateBegin;
				DatePeriod dp = null;
				
				if(adapter != null)
					dp = adapter.getFilter();
				
				if( dp == null ) {
					dp = saveDatePeriod;
				}
				
				if( requestCode == BEGIN_DATE_CODE ) {
					dp.begin = new Date(ct);
					newDate = dp.begin;
					id = R.id.tvDateBegin;
				}
				else if( requestCode == END_DATE_CODE) {
					ct += (24 * 3600 - 1) * 1000;
					dp.end = new Date(ct);
					newDate = dp.end;
					id = R.id.tvDateEnd;
				}
				TextView dv = (TextView) dialogView.findViewById(id);
				dv.setText(Util.simpleDateFormat.format(newDate));
			}
		}
	}

	protected Dialog createDlgFilter() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		dialogView = View.inflate(this, getFilterLayout(), null);
		((TextView)dialogView.findViewById(R.id.tvBegin)).setText(getString(R.string.begin) + ":");
		((TextView)dialogView.findViewById(R.id.tvEnd)).setText(getString(R.string.end) + ":");
		final DatePeriod dp = (adapter != null && adapter.getFilter() != null) ? adapter.getFilter() : saveDatePeriod;
		TextView dv = (TextView) dialogView.findViewById(R.id.tvDateBegin);
		dv.setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) {
				setDate(BEGIN_DATE_CODE, dp.begin); 
			}
		});
		dv.setText(Util.simpleDateFormat.format(dp.begin));
		
		dv = (TextView) dialogView.findViewById(R.id.tvDateEnd);
		dv.setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { 
				setDate(END_DATE_CODE, dp.end); 
			}
		});
		dv.setText(Util.simpleDateFormat.format(dp.end));
		
		if (Features.FILTER_DOCUMENTS_BY_ITEM)
		{
			LinearLayout itemFilterLayout = (LinearLayout)dialogView.findViewById(R.id.llItemFilter);
			if (itemFilterLayout != null) {
				itemFilterLayout.setVisibility(View.VISIBLE);

				final TextView filterItem = (TextView)dialogView.findViewById(R.id.tvFilterItem);
				filterItem.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) {
						setFilterItem(((TextView)v).getText().toString());
					}
				});

				CheckBox cbFilterItem = (CheckBox)dialogView.findViewById(R.id.cbByItem);
				cbFilterItem.setChecked(false);
				cbFilterItem.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if( !((CheckBox)v).isChecked()) {
							filterItem.setText(getResources().getString(R.string.all));
							itemId = null;
						}
					}
				});
			}
		}
		
		Spinner sp = (Spinner)dialogView.findViewById(R.id.spOrg);
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();
		values.add(new KeyValue("", getResources().getString(R.string.all)));

		Org o = new Org();
		String table = DataObjectInfo.getInstance().getTableName(o.getClass());
		DbReader r = new DbReader();
		r.setReadingFields("id,name,address");
		int selected = -1;
		boolean bdo = r.select(o, table, null, "name");
		while(bdo) {
			if( startOrgID != null && o.id.equals(startOrgID) )
				selected = values.size();
			KeyValue kv = new KeyValue(o.id, o.name + " (" + o.address + ")");
			values.add(kv);
			bdo = r.selectNext(o);
		}
		r.close();
		setOrgSelectAdapter(sp, values);
//		ArrayAdapter<KeyValue> a = new ArrayAdapter<KeyValue>(this, getOrgSpinnerLayout(), values);
//		sp.setAdapter(a);
		if( selected >= 0 )
			sp.setSelection(selected);
		startOrgID = null;

		postUpdateFilterView(dialogView);
		builder.setView(dialogView);
		builder.setPositiveButton(R.string.ok, setFilter);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	protected void setOrgSelectAdapter(Spinner sp, List<KeyValue> values) {
		ArrayAdapter<KeyValue> a = new ArrayAdapter<KeyValue>(this, getOrgSpinnerLayout(), values) {
			@Override
			public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
				if(convertView == null)
					convertView = View.inflate(DocList.this, R.layout.doc_list_org_row, null);

				String text = getItem(position).toString();
				TextView tv = ((TextView)convertView.findViewById(R.id.tvFirmaName));
				tv.setText(text);
				tv.setEllipsize(null);
				tv.setHorizontallyScrolling(false);
				return convertView;
			}

			@NonNull
			@Override
			public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
				return super.getDropDownView(position, convertView, parent);
			}
		};
		sp.setAdapter(a);
	}

	protected int getOrgSpinnerLayout() { return R.layout.simple_spinner_layout; }

	protected void postUpdateFilterView(View view) {}

	protected int getFilterLayout() {
		return R.layout.date_selection;
	}
	
	private DialogInterface.OnClickListener setFilter = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) { filterClick(dialog); }
	};
	
	@Override
	public void postSendExecute(boolean result) {
		BaseAdapter baseAdapter = (BaseAdapter) lvDocs.getAdapter();
		
		if (baseAdapter != null)
			baseAdapter.notifyDataSetChanged();
		
	}

	protected void applyFilter(DatePeriod dp, String id, String name) {
		llFilterPanel.setVisibility(View.VISIBLE);

		if( itemId != null && itemId.length() > 0 ) {
			price = priceImpl.getData();
			price.id = itemId;
			priceImpl.read();
			
			String priceFilter = "товар: " + itemName; 
			if( name == null )
				name = priceFilter;
			else
				name += " " + priceFilter;
		} else
			price = null;

		setFilterText(dp, name);

		adapterFilter(dp, id);
		refreshTotalSum(true);
	}

	protected void adapterFilter(DatePeriod dp, String id) {
		adapter.fetch((DocType) DocType.getCurDoc(),dp, id, price);
	}

	protected void filterClick(DialogInterface dialog) {
		AlertDialog alertDialog = (AlertDialog)dialog;
		
		CheckBox cbCreatedFiltered = (CheckBox) alertDialog.findViewById(R.id.cbCreatedFiltered);
		DatePeriod dp = null;
		
		if (adapter != null)
			dp = adapter.getFilter();
		
		if( dp == null )
			dp = saveDatePeriod;
		
		dp.periodType = cbCreatedFiltered.isChecked() ? DatePeriod.CREATED : DatePeriod.DATE;
		
		String id = null;
		String name = null;
		Spinner sp = (Spinner)alertDialog.findViewById(R.id.spOrg);
		KeyValue kv = (KeyValue)sp.getSelectedItem();
		if( kv != null ) {
			id = kv.key.toString();
			if( id.length() == 0 )
				id = null;
			else
				name = kv.value.toString();
		}

		postFilterClick(alertDialog);
		applyFilter(dp, id, name);
	}

	protected void postFilterClick(AlertDialog alertDialog) {
	}
}


class MayDatePicker extends DatePickerDialog {

	public MayDatePicker(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE)
			super.onClick(dialog, which);
	}
	
	@Override
	protected void onStop() {
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		getButton(BUTTON_POSITIVE).setText(R.string.delete);
	}
}