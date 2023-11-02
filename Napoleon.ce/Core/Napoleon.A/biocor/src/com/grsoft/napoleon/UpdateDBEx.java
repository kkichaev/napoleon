package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.CheckBox;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgFoldersHitching;
import com.grsoft.database.OrgHitchingW;
import com.grsoft.database.PotenzialOrgHitching;
import com.grsoft.database.PriceHitching;
import com.grsoft.database.TaskSendHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
//		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) {
		return new PriceHitching(){
			@Override
			public void prepareReading() {
				DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
				DbWriter.checkDBTable(dataObject);
			}
		};
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		
		//Организации в конец, что бы удаление потенциальных организаций не удаляло организацию
		for(Hitching h : ret)
			if (h.getObjectName().equals("Org")){
				ret.remove(h);
				ret.add(h);
				break;
			}
		
		return ret;
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> result = new ArrayList<ObjectListener>();
		
		PotenzialOrgHitching poh = new PotenzialOrgHitching();
		if( poh.size() > 0 )
			result.add(poh);
			
		ObjectExportListener ol = new TaskSendHitching();
		if( ol.size() > 0 )
			result.add(ol);
		
		ol = new OrgFoldersHitching();
		if(ol.size() > 0)
			result.add(ol);
		
		return result;
	}
	
	@Override protected Hitching getOrgFoldersHitching() { return null; }
	
	@Override
	protected Hitching getOrgHitching() {
		return new OrgHitchingW(){
			@Override
			public void prepareReading() {
				DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
				DbWriter.checkDBTable(dataObject);
			}
		};
	}
}