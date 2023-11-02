package com.grsoft.napoleon;

import java.util.List;

import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;
import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ISReturn;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgHelper;
import com.grsoft.dataobjects.OrgStop;
import com.grsoft.dataobjects.SalesBan;
import com.grsoft.dataobjects.StockQty;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DivisionHitching;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.WSAddOrderDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDBPrint {
	
	@Override
	protected List<DocExportListener> getExportedDocs(boolean docs, boolean visit) {
		List<DocExportListener> result = DocType.getDocuments(docs, visit);
		
		if (!((CheckBox)findViewById(R.id.cbAddWSOrder)).isChecked()) {
			for (DocExportListener d : result) {
				if (d instanceof DocSendListner && ((DocSendListner)d).getObjectName().equals(WSAddOrderDoc.instance().getObjectName())) {
					result.remove(d);
					break;
				}
			}
		}
			
		return result;
	}
	
	@Override
	protected int getContentView() {
		return R.layout.updatedbex;
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		try {
			DebtDoc.postUpdateProcess();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.onFinishUpdate(task);
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		
		((CheckBox)findViewById(R.id.cbVisit)).setChecked(true);
		((CheckBox)findViewById(R.id.cbGenData)).setChecked(false);
		findViewById(R.id.cbDebt).setVisibility(View.GONE);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new OrgStopHitching());
		ret.add(new RcvNewHitching(OrgDogovor.class, "OrgDog"));
		OrgHelper.refresh();

		ret.add(new RcvNewHitching(StockQty.class, "StockQty"));
		
		List<Hitching> debetDocs = getDebetHitching();
		ret.addAll(debetDocs);

		ret.add(new DivisionHitching());
		ret.add(new RcvNewHitching(SalesBan.class));
		
		return ret;
	}
	
	@Override
	protected List<Hitching> getDebetHitching() {
		List<Hitching> ret = super.getDebetHitching();
		ret.add(new RcvNewHitching(ISReturn.class, "ISReturns"));
		return ret;
	}
}

class OrgStopHitching extends Hitching {
	SQLiteStatement stmt;
	
	public OrgStopHitching() {
		super(OrgStop.class, "BlockedOrg");
	}
	
	@Override
	public void onStart() { 
		String tableName = DataObjectInfo.getInstance().getTableName(Org.class);
		try {
			stmt = DataBaseManager.getDataBase().compileStatement("DELETE FROM '" + tableName + "' WHERE id=?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if( stmt != null ) {
			try {
				OrgStop dobj = (OrgStop) rawObject.createDataObject(dataObject);
				stmt.clearBindings();
				stmt.bindString(1, dobj.id);
				
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onEnd() { 
		if( stmt != null )
			stmt.close();
	}
}