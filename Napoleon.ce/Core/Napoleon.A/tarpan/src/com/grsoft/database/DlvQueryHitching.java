package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DlvQuery;
import com.grsoft.dataobjects.impl.DlvQueryImpl;
import com.grsoft.network.ObjectExportListener;

public class DlvQueryHitching extends Hitching
implements ObjectExportListener{
	List<Long> list;
	public DlvQueryHitching() {
		super(DlvQuery.class, "DlvQuery");
		list = new ArrayList<Long>();
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(DlvQuery.class), "", "");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		DlvQueryImpl impl = new DlvQueryImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}
	
	@Override
	public void onEnd() {
		new DlvQueryImpl().deleteAll();
	}
}
