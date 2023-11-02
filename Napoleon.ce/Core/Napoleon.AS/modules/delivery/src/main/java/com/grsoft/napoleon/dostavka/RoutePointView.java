package com.grsoft.napoleon.dostavka;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.DVisit;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.DispatchTime;
import com.grsoft.dataobjects.ItemDef;
import com.grsoft.dataobjects.RouteItem;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.impl.DFakeShipmentImpl;
import com.grsoft.dataobjects.impl.DVisitImpl;
import com.grsoft.dataobjects.impl.DispatchImpl;
import com.grsoft.dataobjects.impl.RouteItemImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.dostavka.R;
import com.grsoft.napoleon.DispositionActivity;
import com.grsoft.napoleon.documents.DShipmentDoc;
import com.grsoft.napoleon.documents.DVisitDoc;
import com.grsoft.napoleon.util.HorizontalListView;
import com.grsoft.napoleon.util.ImagesItemsAdapter;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.view.BaseActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class RoutePointView extends BaseActivity {
	public static Class<? extends RoutePointView> activity = RoutePointView.class;
	
	public static final String POINTID = "pointid";
	public static final String NUMBER = "number";

	protected RoutePointImpl rpi = new RoutePointImpl();
	protected DispatchImpl doc = DispatchImpl.create();
	protected Adapter adapter;
	protected RouteItemImpl rii = new RouteItemImpl();
	ImagesItemsAdapter iadapter;
	DVisitImpl vis;
	
	public static void open(Context context, RouteItem routeItem) {
		Intent i = new Intent(context, activity);
		i.putExtra(POINTID, routeItem.itemid);
		context.startActivity(i);
	}

	public static void open(Context context, RouteItem routeItem, String number) {
		Intent i = new Intent(context, activity);
		i.putExtra(POINTID, routeItem.itemid);
		i.putExtra(NUMBER, number);
		context.startActivity(i);
	}
	
	protected int getLayoutId() { return R.layout.point; } 
	
	@SuppressLint("StringFormatInvalid")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		
		String iid = b.getString(POINTID, ""); 
		rii.read("itemid", iid);
		rpi.read("id", rii.getData().id);
		doc.readFromId(rii.getData().itemid);
		Collections.sort(rii.getData().docs, new Comparator<ItemDef>() {

			@Override
			public int compare(ItemDef lhs, ItemDef rhs) {
				return lhs.pos - rhs.pos;
			}});
		
		
		RoutePoint rp = rpi.getData();
		
		TextView tv = (TextView) findViewById(R.id.tvName);
		tv.setText(rp.name);
		
		tv = (TextView) findViewById(R.id.tvAddress);
		tv.setText(rp.address);
		
		tv = (TextView) findViewById(R.id.tvPhone);
		
		float textSize = tv.getTextSize();
		if(rp.contacts.size() > 0 ){
			tv.setText(Html.fromHtml("<u>"+rp.contacts.get(0).phone+"</u>"));
			tv.setTag(rp.contacts.get(0));
			tv.setOnClickListener(pressToPhoneCall);
		}else
			tv.setVisibility(View.GONE);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.llContacts);
		
		for (int i = 1; i < rp.contacts.size(); i++){
			Contact c = rp.contacts.get(i);
			tv = new TextView(this);
			tv.setTextColor(getResources().getColor(R.color.lgrey));
			tv.setText(Html.fromHtml(getString(R.string.contacts, c.name, c.phone)));
			tv.setTextSize(textSize); //getResources().getDimension(R.dimen.normalTextSz));
			tv.setTag(c);
			tv.setOnClickListener(pressToPhoneCall);
			layout.addView(tv);
		}
			
		tv = (TextView) findViewById(R.id.tvRemark);
		tv.setVisibility(View.GONE);
		
		ListView list = (ListView) findViewById(R.id.list);
		adapter = new Adapter();
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (DispositionActivity.isNeedDisposition(RoutePointView.this, rii.getData().id, null)) {
					DispositionActivity.open(RoutePointView.this, rii.getData().id);
					return;
				}
				
				if(allowWork() && !doc.isInWork())
					startWork();
				
				if(!doc.openDoc(view.getContext(), position)) {
					ItemDef idef = (ItemDef) adapter.getItem(position);
					if(idef != null && idef.type.equals(DShipmentDoc.instance().getObjectName())) {
						DFakeShipmentImpl fakeDoc = new DFakeShipmentImpl();
						DispatchItem item = new DispatchItem();
						item.date = new Date();
						item.itemid = rii.getData().itemid;
						item.number = idef.number;
						item.type = idef.type;
						fakeDoc.init(RoutePointView.this, doc, item, GPSUtilNew.getLastKnownLocation());
						fakeDoc.open(RoutePointView.this);
					}
				}
			}});

		String number = getIntent().getStringExtra(NUMBER);

		if(number != null && number.length() > 0){
			for(int i = 0; i < adapter.getCount(); i++){
				ItemDef item = (ItemDef) adapter.getItem(i);

				if (item.type.equals(DShipmentDoc.instance().getObjectName()) && item.number.equals(number))
					list.performItemClick(list.getAdapter().getView(i,null, null),i,0);
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(POINTID, rii.getData().itemid);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		doc.read();
		doc.close();
		refreshData();
		
		vis = new DVisitImpl();
		DVisit dvisit = vis.getData();
		dvisit.created = doc.getData().visit;
		if(vis.read()) {
			iadapter = new ImagesItemsAdapter(this, dvisit.items, 150, 150, 5, false);
			HorizontalListView g = (HorizontalListView)findViewById(R.id.gvItems);
			if(g != null) {
				g.setAdapter(iadapter);
			}
		}
		vis.close();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == R.id.document_dlg_result)
			adapter.notifyDataSetChanged();
	}
	
	public void refreshData() {
		if(adapter != null)
			adapter.notifyDataSetChanged(); 
	}
	
	private class Adapter extends BaseAdapter{
		@Override public int getCount() {	return rii.getData().docs.size(); }
		@Override public Object getItem(int position) { return rii.getData().docs.get(position); }
		@Override public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null){
				view = View.inflate(RoutePointView.this, R.layout.docrow, null);
				((TextView)view).setBackgroundResource(R.drawable.pending_doc_selector);
			}
			
			TextView tv = (TextView)view;
			tv.setBackgroundResource(R.drawable.pending_doc_selector);
			
			ItemDef ri = (ItemDef)getItem(position);

			int state = doc.isItemFinished(view.getContext(), position);

			if(state == DispatchItem.DOC_COMPLETE)
				tv.setBackgroundResource(R.drawable.done_doc_selector);
			else if (state == DispatchItem.DOC_INITED)
				tv.setBackgroundResource(R.drawable.turquoise_selector);
			
			tv.setText(ri.title.trim());
			
			return view;
		}
	}
	
	OnClickListener pressToPhoneCall = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Contact c = (Contact) v.getTag();
			if(c != null)
				Main.phoneCall(RoutePointView.this, c.phone);
		}
	};
	
	public boolean isAllowClose() {	return true; }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.point_option_menu, menu);
		return true;
//		return super.onCreateOptionsMenu(menu);
	}
	
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		menu.clear();
//		if (allowWork())
//			inflater.inflate(R.menu.point_option_menu, menu);
//		super.onCreateOptionsMenu(menu, inflater);
//	}
	
	protected boolean allowWork(){
		return doc.getRowid() == ExtrasConst.INVALID_ROWID || doc.isEditable(); 
	}
	
	protected void makeVisit() {
		if(allowWork() && !doc.isInWork())
			startWork();
		DVisitImpl vis = (DVisitImpl) DVisitDoc.instance().create();
		if( vis.readOrCreate(this, doc)) {
			DocType.setCurDoc(DVisitDoc.instance());
			vis.open(this);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itVisit){
			makeVisit();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(!doc.isExported() && doc.isInWork() && doc.isDocFinished(this) == DispatchItem.DOC_COMPLETE) {
			finishWork();
		}
	}
	
	protected void finishWork() {
		DispatchTime t = doc.getData().times.get(doc.getData().times.size() - 1);
		t.finish = Util.getDateTime();
		doc.finish();
	}

	private void startWork() {
		if (doc.getRowid() == ExtrasConst.INVALID_ROWID)
			doc.init(this, rii.getData(), GPSUtilNew.getLastKnownLocation());
		else{
			DispatchTime t = new DispatchTime();
			t.start = Util.getDateTime();
			doc.getData().times.add(t);
			doc.write();
			doc.close();
		}
		
		invalidateOptionsMenu();
	}
}
