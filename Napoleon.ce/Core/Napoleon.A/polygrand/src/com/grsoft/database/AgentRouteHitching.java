package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.AgentRoute;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.AgentRouteImpl;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.network.ObjectExportListener;

public class AgentRouteHitching extends Hitching implements ObjectExportListener {
	private AgentRouteImpl impl = new AgentRouteImpl();
	private List<Long> list = new ArrayList<Long>();

	public AgentRouteHitching() {
		super(AgentRoute.class, "AgentRoute");

		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(AgentRoute.class), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		impl.read(list.get(i));
		return impl.getData();
	}

	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			impl.read(list.get(i));
			DocumentUtils.setExported(impl, impl.getData().params, true);
			impl.write();
		}
		
		impl.close();
	}
}
