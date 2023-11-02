package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

import android.widget.Toast;

public class DocumentsEx extends Documents {
	public static final String LAST_SYNC = "LastSyncPref";

	boolean isSyncValid(int d, int h, int curD, int curH) {
		int diff = curD - d;
		if(curH < 9) {
				return diff == 0 ? true : 
					diff == 1 ? h >= 14 && h < 15 : 
					false;
		}
		if(curH < 15) {
			return diff == 0 ? h >= 5 : false;
		}
		
		return diff == 0 ? h >= 14 : false;
	}
	
	@Override
	boolean canCreateDoc() {
		Calendar tc = Calendar.getInstance();
		Date genDate = SyncInfo.getLastSync(SyncInfo.GEN_DATA);
		Date dbtDate = SyncInfo.getLastSync(SyncInfo.DEBT);

		tc.setTime(genDate);
		int genDay = tc.get(Calendar.DAY_OF_YEAR);
		int genHour = tc.get(Calendar.HOUR_OF_DAY);
		
		tc.setTime(dbtDate);
		int dbtDay = tc.get(Calendar.DAY_OF_YEAR);
		int dbtHour = tc.get(Calendar.HOUR_OF_DAY);
		
		tc.setTime(new Date());
		int curDay = tc.get(Calendar.DAY_OF_YEAR);
		int curHour = tc.get(Calendar.HOUR_OF_DAY);
		
		boolean genValid = isSyncValid(genDay, genHour, curDay, curHour);
		boolean dbtValid = isSyncValid(dbtDay, dbtHour, curDay, curHour);
		
		
		if( !genValid || !dbtValid ) {
			String text = "";
			if(!genValid) {
				text += "Основные данные не актуальны.";
			}
			if(!dbtValid) {
				if(text.length() != 0)
					text += "\n";
				text += "Долги не актуальные";
			}
			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
			UpdateDB.open(this);
			return false;
		}
		
		return super.canCreateDoc();
	}
	
	@Override
	protected void doCreate() {
		Calendar tc = Calendar.getInstance();
		Date genDate = SyncInfo.getLastSync(SyncInfo.GEN_DATA);
		Date dbtDate = SyncInfo.getLastSync(SyncInfo.DEBT);

		boolean genValid = genDate != null;
		boolean dbtValid = dbtDate != null;
		
		if(dbtDate != null && genDate != null) {
			tc.setTime(genDate);
			int genDay = tc.get(Calendar.DAY_OF_YEAR);
			int genHour = tc.get(Calendar.HOUR_OF_DAY);
			
			tc.setTime(dbtDate);
			int dbtDay = tc.get(Calendar.DAY_OF_YEAR);
			int dbtHour = tc.get(Calendar.HOUR_OF_DAY);
			
			tc.setTime(new Date());
			int curDay = tc.get(Calendar.DAY_OF_YEAR);
			int curHour = tc.get(Calendar.HOUR_OF_DAY);
			genValid = isSyncValid(genDay, genHour, curDay, curHour);
			dbtValid = isSyncValid(dbtDay, dbtHour, curDay, curHour);
		}
		
		if( !genValid || !dbtValid ) {
			String text = "";
			if(!genValid) {
				text += "Основные данные не актуальны.";
			}
			if(!dbtValid) {
				if(text.length() != 0)
					text += "\n";
				text += "Долги не актуальные";
			}
			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
			UpdateDB.open(this);
		} else {
			super.doCreate();
		}
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
	
	@Override
	protected String orgInfo(Org o) {
		String result = super.orgInfo(o);
		
		if(((OrgEx)o).info.trim().length() > 0)
			result += "<br>" + ((OrgEx)o).info;
				
		return result;
	}
}
