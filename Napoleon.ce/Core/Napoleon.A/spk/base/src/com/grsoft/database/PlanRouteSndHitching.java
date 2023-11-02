package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PlanRoute;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.PlanRouteImpl;
import com.grsoft.network.ObjectExportListener;

public class PlanRouteSndHitching extends Hitching implements
		ObjectExportListener {
	public static final String OBJECT_NAME = "PlanRoute";
	private List<Long> list;
	private PlanRouteImpl impl = new PlanRouteImpl();

	public PlanRouteSndHitching() {
		super(DbObject.getDataType(PlanRoute.class), OBJECT_NAME);
		DbWriter.checkDBTable(DbObject.getDataType(PlanRoute.class));
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(
				DataObjectInfo.getInstance().getTableName(
						DbObject.getDataType(PlanRoute.class)), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		impl.read(list.get(i));
		impl.close();

		return impl.getData();
	}

	@Override
	public void onEnd() {
		for (int i = 0; i < list.size(); i++) {
			impl.read(list.get(i));
			impl.getData().params |= ParamState.ofExported;
			impl.write();
		}
		impl.close();
	}

}
