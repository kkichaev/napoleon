package com.grsoft.database;

import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.ProgramSettings;
import com.grsoft.dataobjects.impl.ProgramSettingsImpl;
import com.grsoft.network.ObjectExportListener;

public class PrrogramSettingsHitching extends Hitching implements ObjectExportListener{
	List<Long> list;
	
	ProgramSettingsImpl psi = new ProgramSettingsImpl();
	
	public PrrogramSettingsHitching() {
		super(ProgramSettings.class, "PDASettings");
		
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(ProgramSettings.class), where, "");
	}

	@Override
	public void onEnd() {
		super.onEnd();
		for( int i=0; i<list.size(); i++ ) {
			psi.read(list.get(i));
			psi.getData().params |= ParamState.ofExported;
			psi.write();
		}		
		psi.close();
	}
	
	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public DataObject get(int i) {
		psi.read(list.get(i));
		return psi.getData();
	}

}
