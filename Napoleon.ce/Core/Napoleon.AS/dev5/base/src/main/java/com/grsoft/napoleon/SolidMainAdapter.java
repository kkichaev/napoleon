package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Main.MainAdapter;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.FilterAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

class SolidMainAdapter extends BaseMainAdapter implements FilterAdapter, MainAdapter {
	protected List<Org> data = new ArrayList<Org>();
	protected Main main;
	
	public SolidMainAdapter(Main main){
		this.main = main;
		load(null);
	}
	
	protected String getWhereStr() {
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		return cfg.onlyNewstItems == 1 ? "(hidden = 0 or hidden is null)" : "";
	}

	protected void loadAdapter(String where) {
		
	}
	
	protected void load(String filter) {
		data.clear();
		
		String where = getWhereStr();
		
		if (filter != null && filter.trim().length() > 0){
			if(where.trim().length() > 0)
				where += " and ";

			if(Features.MULTI_WORD_SEARCH) {
				StringBuilder sbWhere = new StringBuilder();
				String[] parts = filter.split("\\s+");
				sbWhere.append("(");
				boolean starting = false;
				for (String p : parts) {
					if (starting) {
						sbWhere.append(" AND ");
					} else {
						starting = true;
					}
					sbWhere.append(" srchName").append(" LIKE '%").append(p.toUpperCase()).append("%' ");
				}
				where += sbWhere.toString() + " )";
			} else {
				where += " srchName LIKE '%" + filter.toUpperCase() + "%'";
			}
		}
		
		final Class<? extends DataObject> type = DbObject.getDataType(Org.class);
		DataTraveler.travel(type, new DataTraveler.Travel<Org>() {
			@Override
			public boolean travel(DataTraveler<Org> item) {
				boolean result = true;
				
				if(!skipItem(item.data)){
					data.add(item.data);
					
					try{
						item.data = (Org) type.newInstance();
					}catch(Exception e){
						result = false;
					}
				}
				
				return result;
				
			}}, where);
		
		Collections.sort(data, new Comparator<Org>() {
			@Override public int compare(Org lhs, Org rhs) { return lhs.name.compareTo(rhs.name); }});
	}
	
	protected boolean skipItem(Org o) { return false; }

	@Override
	public int getCount() {	return data.size(); }

	@Override public Object getItem(int pos) { return data.get(pos); }

	@Override
	public void applyFilter(String value) {
		load(value);
		super.notifyDataSetChanged();
	}

	@Override
	public void resetFilter() {
		load(null);
		super.notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Org org = (Org) getItem(position);
		return main.getSolidMainView(org, position, convertView);
	}

	@Override
	public void adjustView() {
		View v = main.findViewById(R.id.ivGoUp);
		if(v != null)
			v.setVisibility(View.GONE);
		
		v = main.findViewById(R.id.btnMode);
		
		if(v != null)
			((ImageView)v).setImageResource(R.drawable.clients);
		
		DocType.getCurDoc().viewOpened(main);

		main.onAdapterViewAdjusted();
	}

	@Override
	public void click(int position) {
		Org org = (Org) getItem(position);
		
		if(org != null)
			main.openOrg(org, position);
	}

	@Override
	void reload() { load(null); }

	@Override
	public Org getOrg(int pos) { return (Org) getItem(pos);	}

	@Override
	public int getPos(String id) {
		int result = -1;
		
		for (int i = 0; i < getCount(); i++) {
			Org o = getOrg(i);
			
			if (o.id.equals(id)) {
				result = i;
				break;
			}
		}
			
		return result;
	}
}
