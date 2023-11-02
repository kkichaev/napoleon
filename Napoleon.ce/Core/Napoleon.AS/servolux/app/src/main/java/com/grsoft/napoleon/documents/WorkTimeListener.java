package com.grsoft.napoleon.documents;

import java.util.Date;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.WorkTime;
import com.grsoft.dataobjects.impl.WorkTimeImpl;
import com.grsoft.napoleon.NapoleonApp;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class WorkTimeListener implements OnClickListener {
	protected NapoleonApp app;
	ImageButton btnStart;
	View btnNewDoc;
	String id;
	
	public WorkTimeListener(NapoleonApp app, String id, ImageButton btnStart, View newDoc) {
		this.btnStart = btnStart;
		this.app = app;
		this.id = id;

		btnStart.setOnClickListener(this);
		
		boolean inWork = isInWork();
		btnStart.setImageResource(inWork ? R.drawable.stop_tt : R.drawable.start_tt);
		this.btnNewDoc = newDoc;
		if( newDoc != null )
			newDoc.setEnabled(!inWork);
	}
	
	public boolean isInWork() { return app.getInWork().length() > 0; }

	@Override
	public void onClick(View v) {
		String inWork = app.getInWork();
		Date now = Util.getDateTime(); 
		
		WorkTimeImpl wti = new WorkTimeImpl();
		DbWriter.checkDBTable(wti.getData().getClass());

		if(inWork.length() > 0){
			inWork = "";
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
		
		app.putInWork(inWork);

		btnStart.setImageResource(inWork.length() > 0 ? R.drawable.stop_tt 
				: R.drawable.start_tt);
	}}
