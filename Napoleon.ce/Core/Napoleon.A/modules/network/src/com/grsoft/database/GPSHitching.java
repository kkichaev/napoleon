package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.GPSPos;
import com.grsoft.dataobjects.GPSPosImpl;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.ObjectExportListener;

public class GPSHitching extends Hitching  implements ObjectExportListener{

	List<Long> list;
	public GPSHitching() {
		super(GPSPos.class, "GPSPos");
		DbWriter.checkDBTable(DbObject.getDataType(GPSPos.class));
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(GPSPos.class), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			GPSPosImpl impl = new GPSPosImpl();
			impl.read(list.get(i));
			impl.getData().params |=  ParamState.ofExported;
			impl.write();
			impl.close();
		}
	}

	@Override
	public DataObject get(int i) {
		GPSPosImpl impl = new GPSPosImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}

}
