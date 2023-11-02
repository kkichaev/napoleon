package com.grsoft.napoleon;

import java.util.List;
import android.annotation.SuppressLint;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitchingW;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentsName;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.IncomeImpl;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> res =  super.getGenDataHitchings();
		res.add(new RcvNewHitching(Income.class, IncomeImpl.OBJECT_NAME));
		res.add(new RcvNewHitching(AgentsName.class, "AgentsName"));
		return res;
	}
	
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> ret = super.getRestoreHitching();
		ret.add(new DocumentRestore(MonitoringDoc.instance()));		
		return ret;
	}
	
	@Override
	protected void postSync(Boolean result) {
		CostStrategyEx.resetCash();
	}
	
	@Override
	protected Hitching getOrgHitching() {
		return new OrgHitchingW() {
			@Override
			public void prepareReading() {
				DbWriter.checkDBTable(dataObject);
				final String sql = "update org set hidden = 1";
				DataBaseManager.getDataBase().execSQL(sql);
			}
			
			@SuppressLint("DefaultLocale")
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				Org dobj = (Org)rawObject.createDataObject(dataObject);
				dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.address.toUpperCase();
				dobj.hidden = 0;
				dbProxy.insertRecord(dobj);
			}
		};
	}
}
