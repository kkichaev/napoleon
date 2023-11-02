package com.grsoft.network;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.Config;


public class ConnectionManagerEx extends ConnectionManager {
	@Override
	protected void postUpdatePool(Config cfg) {
		CfgNplEx c = (CfgNplEx)cfg;
		
		connPool.add(new SocketConnection(c.address3, c.port3));
	}
}
