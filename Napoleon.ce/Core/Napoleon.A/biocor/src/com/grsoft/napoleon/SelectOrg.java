package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.dialogs.SelectDialog;


public class SelectOrg extends SelectDialog {
	public static final String SELECT_ORG_ACTION = "com.grsoft.napoleon.SelectOrg.SELECT_ORG_ACTION";
	public static final String SELECTED = "SELECTED";
	private ListView listView;
	private List<OrgInfo> data = new ArrayList<OrgInfo>();
	
	@Override
	public void onOKButtonPressed(View result) {
		List<String> sel = new ArrayList<String>();
		
		for (OrgInfo i : data) {
			if (i.selected && i.org != null)
				sel.add(i.org.id);
		}
		
		String[] ids = new String[sel.size()];
		ids = sel.toArray(ids);
		Intent i = new Intent(SELECT_ORG_ACTION);
		i.putExtra(SELECTED, ids);
		getActivity().sendBroadcast(i);
	}

	@Override
	public int getViewId() { return R.layout.select_org; }

	public void setOrgList(List<Org> list, List<String> sel){
		if(list != null && sel != null){
			for(Org o : list){
				OrgInfo i = new OrgInfo();
				i.selected = sel.contains(o.id);
				i.org = o;
				
				data.add(i);
			}
			
			Collections.sort(data, new Comparator<OrgInfo>() { @Override public int compare(OrgInfo lhs, OrgInfo rhs) { return lhs.org.name.compareTo(rhs.org.name); }});
		}
	}
	
	@Override
	public void prepareView(View view) {
		listView = (ListView) view.findViewById(R.id.list);
		listView.setDividerHeight(0);
		listView.setAdapter(new OrgListAdapter());
	}

	@Override
	public int getTitle() {
		return R.string.select_orgs;
	}

	class OrgListAdapter extends BaseAdapter {
		@Override
		public int getCount() { return data.size();	}

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(getContext(), R.layout.select_org_row, null);
			
			OrgInfo inf = (OrgInfo) getItem(position);
			Org org = inf.org;
			
			if(org != null){
				TextView tv = (TextView) view.findViewById(R.id.tvName);
				tv.setText(org.name);
				tv = (TextView) view.findViewById(R.id.tvAddress);
				tv.setText(org.address);
				CheckBox cb = (CheckBox) view.findViewById(R.id.cbSelected);
				cb.setTag(position);
				
				cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
					@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { data.get((Integer) buttonView.getTag()).selected = isChecked; }
				});
				
				cb.setChecked(inf.selected);
			}
			
			view.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
			
			return view;
		}
	}
	
	private static class OrgInfo{
		public boolean selected;
		public Org org;
	}
}
