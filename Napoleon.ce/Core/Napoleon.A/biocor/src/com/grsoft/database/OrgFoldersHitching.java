package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgFoldersEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgFoldersImpl;
import com.grsoft.network.ObjectExportListener;


public class OrgFoldersHitching extends Hitching implements ObjectExportListener{
	List<Long> list;
	public OrgFoldersHitching() {
		super(DbObject.getDataType(OrgFolders.class), "OrgFolder");
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
			OrgFoldersImpl impl = new OrgFoldersImpl();
			impl.read(list.get(i));
			((OrgFoldersEx)impl.getData()).params |=  ParamState.ofExported;
			impl.write();
			impl.close();
		}
	}

	@Override
	public DataObject get(int i) {
		OrgFoldersImpl impl = new OrgFoldersImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}
}
