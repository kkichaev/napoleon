package com.grsoft.napoleon.util;

public class CfgMgrEx extends CfgMgr{
	@Override
	public void resetToDefault() {
		super.resetToDefault();
		
		address = "192.168.104.194";
		port = 7777;
	}
}
