package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SelectOrgHelper extends SelectHelper implements UpdateCtrl{
	protected List<Org> orgs = new ArrayList<Org>();
	private OrgSelectedListener orgSelectedListener;
	private Map<String, Org> data = new HashMap<String, Org>();
	private Adapter adapter;
	private String userid = "";
	private List<String> filter;

	public void setFilter(List<String> ids) {
		filter = ids;
	}

	interface OrgSelectedListener{
		void onOrgSelected(Org org);
	}
	
	public void setOrgSelectedListner(OrgSelectedListener listener){
		this.orgSelectedListener = listener;
	}
	
	public void init() {
		collectOrgs();
	}

	protected void collectOrgs() {
		orgs.clear();
		data.clear();
		
		DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>(true) {

			@Override
			public boolean travel(DataTraveler<Org> item) {
				if (filter == null || filter.contains(item.data.id))
					orgs.add(item.data);

				return true;
			}
		}, null);
		
		Collections.sort(orgs, new Comparator<Org>(){

			@Override
			public int compare(Org lhs, Org rhs) {
				return lhs.name.compareTo(rhs.name);
			}});

		Org allOrg = new Org();
		allOrg.name = "<Все>";

		orgs.add(0, allOrg);

		for(Org o : orgs)
			if(!data.containsKey(o.id))
				data.put(o.id, o);
	}
	
	public String getOrgName(int index){
		String result = "";
		
		if(index >= 0 && index < orgs.size())
			result = orgs.get(index).name;
		
		return result;
	}
	
	private static class Adapter extends BaseAdapter implements FilterAdapter{
		private SelectOrgHelper helper;
		private Context context;
		private List<Org> data = new ArrayList<Org>();
		
		public Adapter(Context context, SelectOrgHelper helper) {
			this.helper = helper;
			this.context = context;
			data.addAll(helper.orgs);
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(context, R.layout.selectorg_row, null);
			
			Org o = (Org) getItem(position);
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(o.name);
			
			tv = (TextView) view.findViewById(R.id.tvAddress);
			tv.setText(o.address);
			
			return view;
		}

		@Override
		public void applyFilter(String value) {
			data.clear();
			
			for(Org o : helper.orgs) {
				String str = o.name + o.address;
				if (str.toUpperCase().contains(value.toUpperCase()))
					data.add(o);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public void resetFilter() {
			data.clear();
			data.addAll(helper.orgs);
			notifyDataSetChanged();
		}
	}
	public Dialog createDialog(final Context context, boolean hideSync) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.selectorg, null);
		EditText ed = (EditText) view.findViewById(R.id.edFind);
		ListView list = (ListView) view.findViewById(R.id.list);
		View btnClear = view.findViewById(R.id.btnClear);
		FindTextWatcher fw = new FindTextWatcher(ed, list);
		View btnSync = view.findViewById(R.id.btnSync);

		ed.addTextChangedListener(fw);

		adapter = new Adapter(context, this);
		list.setAdapter(adapter);
		builder.setTitle(R.string.orgs);
		builder.setView(view);

		final Dialog dlg = builder.create();

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Org o = (Org) parent.getItemAtPosition(position);
				fireOrgSelected(o);
				dlg.dismiss();
			}
		});

		btnClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText ed = (EditText) dlg.findViewById(R.id.edFind);
				ed.setText("");
			}
		});

		if(hideSync) {
			btnSync.setVisibility(View.GONE);
		} else {
			btnSync.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					List<Hitching> ret = new ArrayList<Hitching>();
					Hitching h = userid == null || userid.length() == 0 ? new RcvNewHitching(Org.class, "CommonOrgs") : new AgentOrgsHitching(userid);
					ret.add(h);
					UpdateProcess upd = new UpdateProcess((Activity) context, SelectOrgHelper.this, ret);
					upd.execute((Void[]) null);
				}
			});
		}

		return dlg;
	}

	public Dialog createDialog(final Context context){
		return createDialog(context, false);
	}


	public void prepareDialog(Dialog dialog){
		ListView list = (ListView) dialog.findViewById(R.id.list);
		Adapter a = (Adapter) list.getAdapter();
		
		if (a instanceof FilterAdapter)
			((FilterAdapter)a).resetFilter();
		
		EditText ed = (EditText) dialog.findViewById(R.id.edFind);
		ed.setText("");
	}

	
	private void fireOrgSelected(Org org){
		if (orgSelectedListener != null)
			orgSelectedListener.onOrgSelected(org);
	}
	
	public Org findOrg(String id){
		if (data.containsKey(id))
			return data.get(id);
		else 
			return null;
	}

	@Override
	public void updateCtrl(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish(boolean success) {
		if (success && adapter != null) {
			collectOrgs();
			adapter.resetFilter();
		}
	}
	
	public void setUserID(String val) {
		userid = val;
	}

	public void clearCache() {
		orgs.clear();
		data.clear();
	}
}
