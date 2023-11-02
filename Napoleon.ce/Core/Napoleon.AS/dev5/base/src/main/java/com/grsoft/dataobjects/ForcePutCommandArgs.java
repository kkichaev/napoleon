package com.grsoft.dataobjects;

import com.grsoft.network.exception.RuntimeException;

public class ForcePutCommandArgs implements CommandArgs {
	String impersonate;
	
	public ForcePutCommandArgs(String impersonate){
		this.impersonate = impersonate;
	}
	
	@Override public String getCommand() { return impersonate.length() == 0 ? "FORCE PUT" : "FORCE PUT AS '" + impersonate + "'"; }
	@Override public String getParams() throws RuntimeException { return new String(); }	
}

