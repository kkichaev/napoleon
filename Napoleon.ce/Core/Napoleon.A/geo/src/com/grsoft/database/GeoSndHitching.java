package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.network.ObjectExportListener;

public class GeoSndHitching extends Hitching implements ObjectExportListener {
	private List<Long> list;

	public GeoSndHitching() {
		super(DbObject.getDataType(Org.class), "GOUT");
		DbWriter.checkDBTable(DbObject.getDataType(Org.class));
		
		list = new ArrayList<Long>();
		String where = "(([flags] & " + 
				Org.FL_USER_CREATED + " ) == "+ Org.FL_USER_CREATED + ")" +
				" and latitude != 0 and longitude != 0 and geocommit = 0";
		list = DbReader.readIds(DataObjectInfo.getInstance()
				.getTableName(DbObject.getDataType(Org.class)), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		OrgImpl impl = new OrgImpl();
		impl.read(list.get(i));
		impl.close();
		
		if (impl.getData().id.length() > 0)
			impl.getData().id = impl.getData()
				.id.substring(0, impl.getData().id.length()-1);
		
		return impl.getData();
	}
	
	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			OrgImpl impl = new OrgImpl();
			impl.read(list.get(i));
			((OrgEx)impl.getData()).geocommit = 1;
			impl.write();
			impl.close();
		}
	}
}
