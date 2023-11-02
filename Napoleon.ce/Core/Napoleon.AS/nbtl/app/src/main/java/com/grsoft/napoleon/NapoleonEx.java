package com.grsoft.napoleon;

import java.util.Date;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.RouteScriptItem;
import com.grsoft.dataobjects.Target;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.ExtrasConst;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


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
		
		if (!new ScriptExitControl().allowExit()) {
			long rowid = getUncopleteRowid(); 
			if(rowid != ExtrasConst.INVALID_ROWID){
				ScriptImpl s = new ScriptImpl();
				s.read(rowid, false);
				s.open(this);
			}
		}
	}
	
	private long getUncopleteRowid(){
		long result = ExtrasConst.INVALID_ROWID;

		
		DocExportListener e =  ScriptDoc.instance().getDirtyDocuments();
		
		for(Document<?> d : e.getDocuments()){
			ScriptImpl s = (ScriptImpl)d;
			boolean complete = s.isComplete();
			
			if(!complete || (complete && scriptMissedTask(s.getData().created))){
				result = d.getRowid();
				break;
			}
		}
		
		return result;
	}
	
	private boolean scriptMissedTask(Date created) {
		DbWriter.checkDBTable(Target.class);
		SQLiteDatabase db = DataBaseManager.getDataBase();
		db.delete(DataObjectInfo.getInstance().getTableName(Target.class), "remark = '' or remark = null", null);
		
		List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Target.class), 
				String.format("scriptCreated = %d", created.getTime()), null);
		return ids.size() == 0;
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
