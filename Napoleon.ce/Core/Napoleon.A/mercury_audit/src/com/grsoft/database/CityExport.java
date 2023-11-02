package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.City;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.Citylmpl;
import com.grsoft.network.ObjectExportListener;

public class CityExport extends Hitching implements ObjectExportListener {
	private List<Long> list;
	private Citylmpl impl = new Citylmpl();
	
	public CityExport() {
		super(City.class);
		
		list = new ArrayList<Long>();
		String where = "([flags] & (" + Org.FL_EXPORTED + "|" + Org.FL_USER_CREATED + ")) = " + Org.FL_USER_CREATED;
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(City.class), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			impl.read(list.get(i));
			impl.getData().flags |= Org.FL_EXPORTED;
			impl.write();
		}
		
		impl.close();
	}

	@Override
	public DataObject get(int i) {
		impl.read(list.get(i));
		return impl.getData();
	}
}
