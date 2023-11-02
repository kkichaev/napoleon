package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Cities;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.Retails;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.FilterAdapter;

public class NapoleonEx extends Napoleon {
	RetailsAdapter retails;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setGoUpVisibility(true);		
		findViewById(R.id.ivGoUp).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if( listViewMode == ListViewMode.ORG_LIST && retails != null ){
					if(retails.filtred == null || retails.filtred.size() == 0)
						retails.moveUp();
					else 
						retails.resetFilter();
				}
			}
		});
	}
	
	@Override
	protected BaseAdapter getMainOrgAdapter()
			throws IllegalAccessException, InstantiationException {
		if( retails == null )
			retails = new RetailsAdapter();
		return retails;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		View view = ((AdapterContextMenuInfo)menuInfo).targetView;
		Object tag =  view.getTag();

		if (tag instanceof OrgImpl && ((OrgImpl)tag).getData().isPotencial()){
			menu.add(R.string.edit);
			menu.add(R.string.visit);
		}
	}
	
	@Override protected OnItemClickListener getItemOnClickListner() { return new ItemClicked(); }
	
	class ItemClicked extends OrglListOnClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Object o = arg1.getTag();
			if( o instanceof Retails ) 
				retails.select((Retails)o);
			else if( o instanceof Cities ) 
				retails.select((Cities)o);
			else if (o instanceof OrgFolders)
				openOrgFolder(arg0, o);	
			else if (o instanceof Long){
				if( isPotencialOrg((Long)o) )
					Documents.open(NapoleonEx.this, (Long)o, true);
				else
					openOrgDocs(arg1, o);
			}else if(o instanceof OrgImpl){
				if(((OrgImpl)o).getData().isPotencial())
					Documents.open(NapoleonEx.this, (OrgImpl)o, true);
				else
					openOrg((OrgImpl)o);
			}
		}
	}

	class RetailsAdapter extends BaseAdapter implements FilterAdapter {

		int level;
		HashMap<String, Retails> retails;
		HashMap<String, Cities> cities;
		List<OrgImpl> orgs = new ArrayList<OrgImpl>();
		
		List<Retails> curRetails = new ArrayList<Retails>();
 		List<Cities> curCities = new ArrayList<Cities>();
 		List<OrgImpl> curOrgs = new ArrayList<OrgImpl>();
 		List<OrgImpl> filtred = null;
		
 		Retails curRetail = null;
 		Cities curCity = null;
 		
		public RetailsAdapter() {
			refresh();
			level = 0;
		}
		
		public void select(Cities city) {
			level = 2;
			
			curCity = city;
			curOrgs.clear();
			
			for(OrgImpl oi : orgs) {
				OrgEx oe = (OrgEx)oi.getData();
				String rc = oe.retail;
				String cc = oe.city;
				if( rc.equals(curRetail.id) && cc.equals(curCity.id) )
					curOrgs.add(oi);
			}
			
			Collections.sort(curOrgs, new CmpOrgs());
			setFirstColumnCaption(curCity.name);
			notifyDataSetChanged();
		}
		
		public void moveUp() {
			if(level > 0 ) {
				level--;
				if( level == 1) {
					select(curRetail);
					return;
				}
				
				if( level == 0 )
					setFirstColumnCaption("Название");
				notifyDataSetChanged();
			}
		}
		
		public void select(Retails retail) {
			level = 1;
			
			curRetail = retail;
			curCities.clear();
			for(OrgImpl oi : orgs) {
				OrgEx oe = (OrgEx)oi.getData();
				String rc = oe.retail;
				if( rc.equals(retail.id) ) {
					String cc = oe.city;
					Cities c = cities.get(cc);
					if( c != null && curCities.contains(c) == false )
						curCities.add(c);
				}
			}
			Collections.sort(curCities);
			setFirstColumnCaption(curRetail.name);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return level == 0 ? curRetails.size() : 
				level == 1 ? curCities.size() :
				filtred == null ? curOrgs.size() : filtred.size();
		}

		public void refresh() {
			retails = Retails.getRetails();
			cities = Cities.getCities();
			orgs.clear();
			curRetails.clear();
			
			String table = DataObjectInfo.getInstance().getTableName(Org.class);
			List<Long> ids = DbReader.readIds(table, null, null);
			for(Long rid : ids) {
				OrgImpl oi = new OrgImpl();
				oi.read(rid);
				oi.close();
				
				orgs.add(oi);
				
				OrgEx oe = (OrgEx)oi.getData();
				String rc = oe.retail;
				Retails r = retails.get(rc);
				if( r != null && curRetails.contains(r) == false )
					curRetails.add(r);
			}
			Collections.sort(curRetails);
		}

		@Override
		public Object getItem(int arg0) {
			return level == 0 ? curRetails.get(arg0) :
				level == 1 ? curCities.get(arg0) :
				filtred == null ? curOrgs.get(arg0) : filtred.get(arg0);
		}

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(NapoleonEx.this, getRowResourceID(), null);
			
			ImageView ivFolder = (ImageView) view.findViewById(R.id.ivFolder);
			ivFolder.setImageResource(R.drawable.folder);
			int ivview = View.VISIBLE;

			Object data = getItem(pos);
			view.setTag(data);
			
			view.findViewById(R.id.tvOrgSum).setVisibility(View.GONE);
			TextView name = (TextView)view.findViewById(R.id.tvOrgName);
			name.setTextColor(getResources().getColor(R.color.black));
			linesController.prepareTextView(name);
			if(level == 0) {
				Retails r = (Retails)data;
				name.setText(r.name);
			} else if(level == 1) {
				Cities c = (Cities)data;
				name.setText(c.name);
			} else {
				OrgImpl org = (OrgImpl)data;
				setOrgBackground(pos, org, view);
				drawOrg(org, view);
				
				ivview = View.GONE;
			}
			ivFolder.setVisibility(ivview);
			return view;
		}

		public void applyFilter(String value) {
			if( value.length() == 0 ) {
				resetFilter();
				return;
			}
			
			level = 2;
			filtred = new ArrayList<OrgImpl>();
			
			String upVal = value.toUpperCase(Locale.getDefault());
			for(OrgImpl oi : orgs) {
				if( oi.getData().srchName.contains(upVal) )
					filtred.add(oi);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public void resetFilter() {
			level = 0;
			filtred = null;
			notifyDataSetChanged();
		}
	}
}

class CmpOrgs implements Comparator<OrgImpl> {

	@Override
	public int compare(OrgImpl o1, OrgImpl o2) {
		return o1.getData().name.compareTo(o1.getData().name);
	}
	
}

