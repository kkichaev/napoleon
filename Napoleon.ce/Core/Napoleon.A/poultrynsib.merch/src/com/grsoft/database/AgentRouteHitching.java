package com.grsoft.database;

import com.grsoft.dataobjects.AgentRoute;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.AgentRouteImpl;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.network.ObjectExportListener;

public class AgentRouteHitching extends Hitching implements
		ObjectExportListener {
	private AgentRoute data = null;
	private AgentRouteImpl impl = new AgentRouteImpl();

	public AgentRouteHitching() {
		super(AgentRoute.class, "AgentRoute");

		impl = new AgentRouteImpl();
		
		if (impl.read() && !DocumentUtils.isExported(impl.getData().params))
			data = impl.getData();
		
		impl.close();
	}

	@Override
	public int size() {
		return data != null ? 1 : 0;
	}

	@Override
	public DataObject get(int i) { return data; }

	@Override
	public void onEnd() { 
		DocumentUtils.setExported(impl, impl.getData().params, true);
		impl.write();
		impl.close();
	}

}
