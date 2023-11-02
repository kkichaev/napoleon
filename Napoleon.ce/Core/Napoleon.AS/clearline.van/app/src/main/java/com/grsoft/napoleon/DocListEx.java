package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PKO1cDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.GpsCoord;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class DocListEx extends DocList {
	
	public static final String SHOW_NEW_ORDERS = "show_new_orders";

	boolean showNewOrders;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		showNewOrders = getIntent().getBooleanExtra(SHOW_NEW_ORDERS, false);

		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected DatePeriod makeInitialDatePeriod(Date begin, Date end) {
		if(showNewOrders)
			begin = new Date(begin.getTime() - 3 * 24 * 3600 * 1000);
		return super.makeInitialDatePeriod(begin, end);
	}
	
	@Override
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		if(doc instanceof OrderImplEx)
			return R.drawable.apply;

		return super.getDocStatusResource(doc);
	}
	
	@Override
	protected DocType getInitialDocType() {
		return showNewOrders ? OrderDoc.instance() : super.getInitialDocType();
	}
	
	@Override
	protected DocFilterOnClickListener createDocListFilter() {
		return new DocFilterEx(this, true, false);
	}
	
	@Override protected boolean countSumFromDocuments(boolean useFilter) { return true; }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Object obj = adapter.getItem(((AdapterContextMenuInfo)menuInfo).position);
		if(obj instanceof OrderImplEx) {
			if(((OrderEx)((OrderImplEx)obj).getData()).orderNumber.length() > 0) {
				menu.add(Menu.NONE, R.id.make_sales, Menu.NONE, "Создать накладную");
			}
		}
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.make_sales) {
			Object obj = adapter.getItem(((AdapterContextMenuInfo)item.getMenuInfo()).position);
			if(obj instanceof OrderImplEx) {
				SalesImpl si = ((SalesImpl)SalesDoc.instance().create());
				si.initFromOrder(((OrderImplEx)obj), new GpsCoord(0, 0, 0));
				si.write();
				si.open(this);
				finish();
				
			}
			return false;
		}
		return super.onContextItemSelected(item);
	}
}

class DocFilterEx extends DocFilterOnClickListener {

	public DocFilterEx(Selector docTypeSelector, boolean createable, boolean showScriptOnly) {
		super(docTypeSelector, createable, showScriptOnly);
	}

	@Override
	protected void initData(boolean creatableFilter) {
		boolean addDoc = data.size() == 0;
		super.initData(creatableFilter);
		if(addDoc)
			data.add(PKO1cDoc.instance());
	}
}
