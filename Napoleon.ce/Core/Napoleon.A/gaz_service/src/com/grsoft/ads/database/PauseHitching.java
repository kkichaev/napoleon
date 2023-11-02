package com.grsoft.ads.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.ads.dataobjects.Pause;
import com.grsoft.ads.dataobjects.impl.PauseImpl;
import com.grsoft.database.DbReader;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.ObjectExportListener;

public class PauseHitching extends Hitching implements ObjectExportListener {
	List<Long> list;
	
	public PauseHitching() {
		super(Pause.class, "Pause");
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Pause.class), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		PauseImpl impl = new PauseImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}

	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			PauseImpl impl = new PauseImpl();
			impl.read(list.get(i));
			impl.getData().params |=  ParamState.ofExported;
			impl.write();
			impl.close();
		}
	}
}
