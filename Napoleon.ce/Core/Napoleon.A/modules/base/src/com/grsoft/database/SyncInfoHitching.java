package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.SyncInfoImpl;
import com.grsoft.network.ObjectExportListener;


public class SyncInfoHitching extends Hitching  implements ObjectExportListener{

	List<Long> list;
	public SyncInfoHitching() {
		super(SyncInfo.class);
		
		DbWriter.checkDBTable(dataObject);
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(dataObject), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			SyncInfoImpl impl = new SyncInfoImpl();
			impl.read(list.get(i));
			impl.getData().params |=  ParamState.ofExported;
			impl.write();
			impl.close();
		}
	}

	@Override
	public DataObject get(int i) {
		SyncInfoImpl impl = new SyncInfoImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}

}
