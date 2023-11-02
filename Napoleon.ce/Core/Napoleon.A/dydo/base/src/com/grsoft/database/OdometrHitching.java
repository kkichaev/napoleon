package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Odometr;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OdometrImpl;
import com.grsoft.network.ObjectExportListener;


public class OdometrHitching extends Hitching implements ObjectExportListener {
	private List<Long> list;
	OdometrImpl impl = new OdometrImpl();
	
	public OdometrHitching() {
		super(Odometr.class, "Odometr");
		
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(dataObject), where, "");
	}

	@Override
	public int size() { return list.size(); }

	@Override
	public DataObject get(int i) {
		impl.read(list.get(i), false);
		return impl.getData();
	}
	
	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			impl.read(list.get(i));
			impl.getData().params |= Org.FL_EXPORTED;
			impl.write();
		}
		impl.close();
		
		super.onEnd();
	}

}
