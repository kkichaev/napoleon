package com.grsoft.napoleon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.RouteScriptItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.VisitDoc;


public class NapoleonEx extends Napoleon {
	public static final String CUR_ROTE = "CurrentRoute";
	public static final String AVAIL_SCRIPTS = "AvailScripts";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.tvMainDocValColTitle).setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		tvTotalSum.setVisibility(View.GONE);
	}
	
	@Override
	protected void setDefaultDocType() { DocType.setCurDoc(VisitDoc.instance()); }


	@Override protected OnItemClickListener getItemOnClickListner() { return new OrgClick(); }
	
	class OrgClick extends OrglListOnClickListener {

		int position;
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			position = arg2;
			super.onItemClick(arg0, arg1, arg2, arg3);
		}
		
		@Override
		protected void openOrg(OrgImpl oi) {
			SharedPreferences.Editor e = getSharedPreferences(CUR_ROTE, MODE_PRIVATE).edit();
			String scripts = "";
			if( listViewMode == ListViewMode.ROUTE_LIST ) {
				Object ofi = orgFoldersAdapter.getItem(position);
				if( ofi instanceof OrgFolderItemEx ) {
					for(RouteScriptItem i : ((OrgFolderItemEx)ofi).scripts) {
						if( scripts.length() > 0)
							scripts += ",";
						scripts += Integer.toString(i.id);
					}
				}
			}
			e.putString(AVAIL_SCRIPTS, scripts);
			e.commit();
			super.openOrg(oi);
		}
	}
	
}
