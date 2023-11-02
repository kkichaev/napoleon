package com.grsoft.napoleon;

import java.util.HashSet;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

import android.database.Cursor;
import android.view.View;
import android.widget.BaseAdapter;

public class MainEx extends Main {
	
	HashSet<String> stopped = new HashSet<String>();
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		((SolidMainAdapterEx)solidMainAdapter).setDebtFilter(docType == DebtDoc.instance());
		super.adjustViewForDocType(docType);
	}
	
	@Override
	protected void setOrgBackground(int pos, Org org, View v) {
		if(stopped.contains(org.id)) {
			v.setBackgroundResource(R.drawable.red_selector);
			return;
		}
		super.setOrgBackground(pos, org, v);
	}
	
	@Override
	protected void onResume() {
		
		String sql = "select id, count(*) as ctr from delivery where sumd > 0 group by id having ctr >= 2";
		Cursor c = null;
		try {
			c = DataBaseManager.getDataBase().rawQuery(sql, null);
			while(c.moveToNext()) {
				stopped.add(c.getString(0));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		if( c != null)
			c.close();
		super.onResume();
	}

	@Override
	protected BaseAdapter createSolidMainAdapter() {
		return new SolidMainAdapterEx(this);
	}
	
	class SolidMainAdapterEx extends SolidMainAdapter {
		boolean debetFilter = false;
		
		public SolidMainAdapterEx(Main main) {
			super(main);
		}
		
		public void setDebtFilter(boolean df) { 
			debetFilter = df;
			load(null);
		}

		@Override
		protected String getWhereStr() {
			String f = super.getWhereStr(); 
			if(debetFilter) {
				if(f.length() > 0) f += " and ";
				f += "id in (select id from " + new OrgSum().getTableName() + " where type='" + DebtDoc.instance().getName() + "' and sum > 0)";
			}
			return f;
		}
	}
}
