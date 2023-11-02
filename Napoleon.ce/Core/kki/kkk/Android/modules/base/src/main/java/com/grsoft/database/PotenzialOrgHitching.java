package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.network.ObjectExportListener;

public class PotenzialOrgHitching extends Hitching implements ObjectExportListener{
	private List<Long> list;
	OrgImpl impl = new OrgImpl();
	
	public PotenzialOrgHitching() {
		this("PotenzialOrg");
	}
	
	public PotenzialOrgHitching(String objectName) {
		super(Org.class, objectName);
		
		list = new ArrayList<Long>();
		String where = "([flags] & (" + Org.FL_EXPORTED + "|" + Org.FL_USER_CREATED + ")) = " + Org.FL_USER_CREATED;
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Org.class), where, "");
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
