package com.grsoft.napoleon.util;

import java.util.Date;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.WorkTime;
import com.grsoft.dataobjects.impl.WorkTimeImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;

public class WorkTimeListener implements OnClickListener {
	private static final String ID_ORG_IN_WORK = "id_org_in_work";
	private static final String PREF_NAME = "";
	private Context context;
	private ImageButton btnStart;
	private View btnNewDoc;
	private String id;
	
	ClickHandler handler;
	
	public interface ClickHandler {
		void onClick(WorkTimeListener owner, boolean inWork);
	}
	
	public WorkTimeListener(Context context, String id, ImageButton btnStart, View newDoc) {
		this.btnStart = btnStart;
		this.context = context;
		this.id = id;
		this.handler = null;

		boolean inWork = isInWork();
		
		if(btnStart != null){
			btnStart.setVisibility(View.VISIBLE);
			btnStart.setOnClickListener(this);
			btnStart.setImageResource(inWork ? R.drawable.stop_tt : R.drawable.start_tt);
		}
		
		this.btnNewDoc = newDoc;
		
		if( newDoc != null )
			newDoc.setEnabled(!inWork);
	}
	
	public void senOnClickHandler(ClickHandler handler) { this.handler = handler; }
	
	public boolean isInWork() { return getInWork().length() > 0; }

	@Override
	public void onClick(View v) {
		String inWork = getInWork();
		Date now = Util.getDateTime(); 
		
		WorkTimeImpl wti = new WorkTimeImpl();
		DbWriter.checkDBTable(wti.getData().getClass());

		if(inWork.length() > 0){
			inWork = "";
			DbWriter.checkDBTable(WorkTime.class);
			String table = DataObjectInfo.getInstance().getTableName(WorkTime.class);
			DbReader r = new DbReader();
			if( r.select(wti.getData(), table, "rowid = (select max(rowid) from " + table +" )") ) {
				WorkTime wt = wti.getData();
				wt.params = 0;
				wt.stop = now;
			}
		}else{
			inWork = id;
			WorkTime wt = wti.getData();
			wt.params = 0;
			wt.id = inWork;
			wt.start = now;
			wt.stop = now;
		}

		if( btnNewDoc != null )
			btnNewDoc.setEnabled(inWork.length() > 0);
		
		wti.write();
		wti.close();
		
		putInWork(inWork);

		btnStart.setImageResource(inWork.length() > 0 ? R.drawable.stop_tt 
				: R.drawable.start_tt);
		
		if(handler != null)
			handler.onClick(this, isInWork());
	}
	
	public String getInWork(){
		return getWorkOrg(context);
	}
	
	public static String getWorkOrg(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return pref.getString(ID_ORG_IN_WORK, "");
	}
	
	public void putInWork(String inWork) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = pref.edit();
		ed.putString(ID_ORG_IN_WORK, inWork);
		ed.commit();
	}
}
