package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderBundleImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderBundleDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class OrderBundleEdit extends BaseActivity {
	OrderBundleImpl doc;
	OrderImplEx order;
	
	Adapter adapter;
	
	public static void open(Context context, OrderBundleImpl doc) {
		Intent i = new Intent(context, OrderBundleEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_bundle_edit);
		
		doc = new OrderBundleImpl();
		order = new OrderImplEx();
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid()));
		
		OrgImpl org = new OrgImpl();
		org.getData().id = doc.getId();
		org.read();
		org.close();
		
		TextView tv = (TextView) findViewById(R.id.tvOrgInfo);
		tv.setText(Html.fromHtml(orgInfo(org.getData())));
		
		ListView lv = (ListView) findViewById(R.id.lvDocs);
		lv.setDividerHeight(0);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DocType.setCurDoc(OrderDoc.instance());
				OrderImplEx doc = (OrderImplEx) arg0.getItemAtPosition(arg2);
				doc.open(OrderBundleEdit.this);
			}
		});
		adapter = new Adapter();
		lv.setAdapter(adapter);
		registerForContextMenu(lv);
		
		findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { 
				if(doc.isEditable()) {
					if(doc.isEmpty())
						doc.initChildOrder();
					DocType.setCurDoc(OrderDoc.instance());
					OrderDocEdit.open(OrderBundleEdit.this, doc.getData().created.getTime());
				}
			}
		});
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.order_bundle_ctx_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(!doc.isEditable())
			return false;
		
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
		OrderImplEx odoc = (OrderImplEx) adapter.getItem(menuInfo.position);
		if(item.getItemId() == R.id.itDelete) {
			odoc.delete();
			doc.refreshDocs();
			adapter.notifyDataSetChanged();
			refreshDocSum();
		} else if(item.getItemId() == R.id.itEdit) {
			odoc.open(this);
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
		order.close();
	}
	
	void refreshDocSum() {
		OrderBundleDoc.instance().updateTotalSum(this, doc.sum(), 0, 0);
	}
	
	@Override
	public void onBackPressed() {
		if(doc.isEditable() && doc.isEmpty()) {
			doc.delete();
		}
		super.onBackPressed();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.refreshDocs();
		adapter.notifyDataSetChanged();
		refreshDocSum();
	}
	
	protected String orgInfo(Org o) {
		String ret = o.name;
		if(Features.SHOW_ORG_ADDRESS && o.address.length() > 0 ) {
			ret += "<br><i>" + o.address + "</i>";
		}
		return ret; 
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }

		@Override public Object getItem(int arg0) {
			long rid = doc.getData().items.get(arg0).created.getTime();
			order.read(rid);
			return order;
		}

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(OrderBundleEdit.this, R.layout.order_bundle_row, null);
			}
			
			OrderImplEx doc = (OrderImplEx) getItem(arg0);
			TextView tv;
			tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(Html.fromHtml(doc.getDescription(OrderBundleEdit.this)));
			
			tv = (TextView) view.findViewById(R.id.tvDate);
			tv.setText(Util.simpleDateFormat.format(((OrderEx)doc.getData()).dlvDate));
			
			tv = (TextView) view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false));

			view.setBackgroundResource((arg0 % 2) != 0 ? R.drawable.even_row_selector: R.drawable.list_selector);
			return view;
		}
		
	}
}
