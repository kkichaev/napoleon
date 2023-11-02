package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import android.view.View;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DailyRoute;
import com.grsoft.dataobjects.DailyRouteItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.OrgFoldersImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.OrgFoldersCmp;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.util.Util;

public class NapoleonEx extends Napoleon {
	OrgFoldersTreeEx treeEx; 
	
	@Override protected OrgFoldersAdapter getOrgFoldersAdapter() { return new OrgFoldersAdapterEx(); }
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		if( listViewMode == ListViewMode.ROUTE_LIST ) {
			int resid = (treeEx.isOrgInRoute(oi.getData())) ? R.drawable.list_grey_selector : R.drawable.list_selector;
			view.setBackgroundResource(resid);
		}
	}
	
	class OrgFoldersAdapterEx extends OrgFoldersAdapter {
		@Override protected OrgFoldersTree createOrgFoldersTree() {
			treeEx = new OrgFoldersTreeEx(); 
			return  treeEx;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String inWork = ((NapoleonApp)getApplication()).getInWork();
		
		if(inWork.length() > 0){
			Org o = new Org();
			o.id = inWork;
			Documents.open(this, o);
		}
	}
}

class OrgFoldersTreeEx extends OrgFoldersTree {
	Hashtable<WeekDay, DailyRoute> route;
	
	public boolean isOrgInRoute(Org o) {
		boolean ret = false;
		if( currentOrgFolder != null ) {
			WeekDay wd = WeekDay.getWeekDay(currentOrgFolder.name);
			if( wd != null && route != null) {
				DailyRoute dr = route.get(wd);
				if( dr != null )
					ret = dr.containsOrg(o);
			}
		}
		
		return ret;
	}
	
	@Override
	protected void loadData(Date date) {
		super.loadData(date);
		loadRoutes();
	}
	
	@Override
	public String getFirstColumnText(int pos) {
		Object dataObject = getItem(pos);		
		if (dataObject instanceof OrgFoldersImpl) {
			OrgFoldersImpl of = (OrgFoldersImpl)dataObject; 
			String text = of.getData().name;
			WeekDay wd = WeekDay.getWeekDay(of.getData().name);
			if( wd != null ) {
				DailyRoute dr = route.get(wd);
				if( dr != null ) {
					text += " / " + Util.simpleDateFormat.format(dr.date);
				}
			}
			return text;
		}
		return super.getFirstColumnText(pos);
	}
	
	private OrgFolders getFolder(WeekDay day) {
		OrgFolders ret = null;
		for(OrgFolders of : orgFolders) {
			WeekDay wd = WeekDay.getWeekDay(of.name);
			if(wd != null && wd.equals(day)) {
				ret = of;
				break;
			}
		}
		
		return ret;
	}
	
	OrgFolders createFolder(WeekDay wd, DailyRoute dr) {
		OrgFolders of = new OrgFolders();
		of.name = wd.getCaption();
		
		of.items = new ArrayList<OrgFolderItem>();
		for(DailyRouteItem dri : dr.items) {
			OrgFolderItem oi = new OrgFolderItem();
			oi.name = dri.id;
			of.items.add(oi);
		}
		
		orgFolders.add(of);
		return of;
	}
	
	void addOrg(OrgFolders of, DailyRoute dr) {
		List<OrgFolderItem> items = of.items;
		
		for(DailyRouteItem dri : dr.items) {
			boolean found = false;
			for(OrgFolderItem i : items) {
				if( i.name.equals(dri.id)) {
					found = true;
					break;
				}
			}
			
			if( !found ) {
				OrgFolderItem oi = new OrgFolderItem();
				oi.name = dri.id;
				items.add(oi);				
			}
		}
	}
	

	public void loadRoutes() {
		Calendar from = Calendar.getInstance();
		
		from.setTime(Util.getDate());
		Calendar till = Calendar.getInstance();
		till.setTime(from.getTime());
		till.add(Calendar.DAY_OF_MONTH, 6);
		
		route = new Hashtable<WeekDay, DailyRoute>();
		
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(DailyRoute.class);
		String where = "date >= " + from.getTime().getTime() + " and date <= " + till.getTime().getTime();
		DailyRoute dr = new DailyRoute();
		boolean bdo = r.select(dr, table, where);
		while(bdo) {
			from.setTime(dr.date);
			WeekDay wd = WeekDay.getDayBySystemId(from.get(Calendar.DAY_OF_WEEK));
			OrgFolders of = getFolder(wd);
			if( of == null )
				of = createFolder(wd, dr);
			else
				addOrg(of, dr);
			route.put(wd, dr);
			
			dr = new DailyRoute();
			bdo = r.selectNext(dr);
		}
		r.close();
		
		Collections.sort(orgFolders, new OrgFoldersCmp());
	}
}