package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.OrgImpl;

public class NapoleonEx extends Napoleon {

	boolean drawInOrgFolders = false;
	
	public static long orgShedule;
	
	@Override protected int getRowResourceID() { return R.layout.main_row_ex; }
	
	@Override protected OnItemClickListener getItemOnClickListner() { return new OrgClickHandler(); }

	@Override protected OrgFoldersAdapter getOrgFoldersAdapter() { return new OrgFoldersEx(); }
	
	void setShedule(String orgId) { orgShedule = ((OrgFoldersEx)orgFoldersAdapter).getOrgShedule(orgId); }
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		TextView tv = (TextView)view.findViewById(R.id.tvTime);
		int vsbl = View.GONE;
		if(drawInOrgFolders) {
			vsbl = View.VISIBLE;
			tv.setText(((OrgFoldersEx)orgFoldersAdapter).getOrgTime(oi.getData().id));
		}
		tv.setVisibility(vsbl);
	}
	
	class OrgFoldersEx extends OrgFoldersAdapter {
				
		class OrgSorter implements Comparator<OrgFolderItem> {
			
			@Override
			public int compare(OrgFolderItem object1, OrgFolderItem object2) {
				return toTime(((OrgFolderItemEx)object1).time) - toTime(((OrgFolderItemEx)object2).time);
			}

		}
		
		int toTime(String str) {
			int ret = 0;
			
			try {
				String[] val = str.split(":");
				if( val.length >= 1 )
					ret += Integer.parseInt(val[0]) * 3600;
				if( val.length >= 2 )
					ret += Integer.parseInt(val[1]) * 60;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return ret;
		}
		
		public long getOrgShedule(String orgId) {
			if( tree.currentOrgFolder != null ) {
				for( OrgFolderItem oi : tree.currentOrgFolder.items) {
					if( oi.name.equals(orgId) ) {
						// дату считаем текущую, но это косяк надобы дату определять из currentOrgFolder
						Date d = new Date();
						Calendar c = Calendar.getInstance();
						c.set(d.getYear(), d.getMonth(), d.getDay());
						
						int time = toTime(((OrgFolderItemEx)oi).time);
						return c.getTime().getTime() + time;
					}
				}
			}
			return 0;
		}

		public String getOrgTime(String orgId) {

			if( tree.currentOrgFolder != null ) {
				for( OrgFolderItem oi : tree.currentOrgFolder.items) {
					if( oi.name.equals(orgId) )
						return ((OrgFolderItemEx)oi).time;
				}
			}
			
			return "";
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			drawInOrgFolders = true;
			View ret = super.getView(position, convertView, parent);
			drawInOrgFolders = false;
			
			return ret;
		}
		
		@Override
		public void itemsMode(OrgFolders currentOrgFolders) {
			super.itemsMode(currentOrgFolders);
			Collections.sort(currentOrgFolders.items, new OrgSorter());
		}
	}
	
	class OrgClickHandler extends OrglListOnClickListener {
		@Override
		protected void openOrg(OrgImpl oi) {
			super.openOrg(oi);
		}
	}
}
