package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.DispatchTime;
import com.grsoft.dataobjects.ItemDef;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.impl.DVisitImpl;
import com.grsoft.dataobjects.impl.DispatchImpl;
import com.grsoft.dataobjects.impl.RouteItemImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.documents.DVisitDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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


public class PointFragment extends Fragment {
	public static final String POINTID = "pointid";
	private RoutePointImpl rpi = new RoutePointImpl();
	private DispatchImpl doc = new DispatchImpl();
	private Adapter adapter;
	private RouteItemImpl rii = new RouteItemImpl();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		rii.read("itemid", getArguments().getString(POINTID));
		rpi.read("id", rii.getData().id);
		doc.readFromId(rii.getData().itemid);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.point, container, false);
		
		RoutePoint rp = rpi.getData();
		
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(rp.name);
		
		tv = (TextView) view.findViewById(R.id.tvAddress);
		tv.setText(rp.address);
		
		tv = (TextView) view.findViewById(R.id.tvPhone);
		
		if(rp.contacts.size() > 0 ){
			tv.setText(Html.fromHtml("<u>"+rp.contacts.get(0).phone+"</u>"));
			tv.setTag(rp.contacts.get(0));
			tv.setOnClickListener(pressToPhoneCall);
		}else
			tv.setVisibility(View.GONE);
		
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.llContacts);
		
		for (int i = 1; i < rp.contacts.size(); i++){
			Contact c = rp.contacts.get(i);
			tv = new TextView(getActivity());
			tv.setTextColor(getResources().getColor(R.color.lgrey));
			tv.setText(Html.fromHtml(getString(R.string.contacts, c.name, c.phone)));
			tv.setTextSize(getResources().getDimension(R.dimen.normalTextSz));
			tv.setTag(c);
			tv.setOnClickListener(pressToPhoneCall);
			layout.addView(tv);
		}
			
		tv = (TextView) view.findViewById(R.id.tvRemark);
		tv.setVisibility(View.GONE);
		
		ListView list = (ListView) view.findViewById(R.id.list);
		adapter = new Adapter();
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(allowWork() && !doc.isInWork())
					startWork();
				doc.openDoc(view.getContext(), position);
			}});
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		doc.read();
		doc.close();
		refreshData();
		
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
				view = View.inflate(getActivity(), R.layout.docrow, null);
				((TextView)view).setBackgroundResource(R.drawable.pending_doc_selector);
			}
			
			TextView tv = (TextView)view;
			tv.setBackgroundResource(R.drawable.pending_doc_selector);
			
			ItemDef ri = (ItemDef)getItem(position);
			
			DispatchItem d = (DispatchItem) doc.findItem(ri.id);
			
			if(d != null && d.state == DispatchItem.DOC_INITED)
				tv.setBackgroundResource(R.drawable.done_doc_selector);
			
			tv.setText(ri.title.trim());
			
			return view;
		}
	}
	
	OnClickListener pressToPhoneCall = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Contact c = (Contact) v.getTag();
			if(c != null)
				Main.phoneCall(getActivity(), c.phone);
		}
	};
	
	public boolean isAllowClose() {	return !doc.isInWork(); }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		if (allowWork())
			inflater.inflate(R.menu.point_option_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	private boolean allowWork(){
		return doc.getRowid() == ExtrasConst.INVALID_ROWID || doc.isEditable(); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itStartFinish){
			if(!doc.isInWork()){
				startWork();
				item.setEnabled(false);
				return true;
			}else {
				finishWork();
				item.setEnabled(false);
				((Main)getActivity()).saveActivePoint("");
				((Main)getActivity()).openRouteFragment();
				return true;
			}
		}else if(item.getItemId() == R.id.itVisit){
			DVisitImpl vis = (DVisitImpl) DVisitDoc.instance().create();
			
			if(allowWork() && !doc.isInWork())
				startWork();
			
			if( vis.readOrCreate(getActivity(), doc))
				vis.open(getActivity());
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void finishWork() {
		DispatchTime t = doc.getData().times.get(doc.getData().times.size() - 1);
		t.finish = Util.getDateTime();
		doc.finish();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if(allowWork())
			menu.findItem(R.id.itStartFinish).setIcon(getResources().getDrawable(doc.isInWork() ? R.drawable.ic_media_stop : R.drawable.ic_media_play));
	}
	
	private void startWork() {
		if (doc.getRowid() == ExtrasConst.INVALID_ROWID)
			doc.init(getActivity(), rii.getData(), GPSUtilNew.getLastKnownLocation());
		else{
			DispatchTime t = new DispatchTime();
			t.start = Util.getDateTime();
			doc.getData().times.add(t);
			doc.write();
			doc.close();
		}
		
		getActivity().invalidateOptionsMenu();
	}
}
