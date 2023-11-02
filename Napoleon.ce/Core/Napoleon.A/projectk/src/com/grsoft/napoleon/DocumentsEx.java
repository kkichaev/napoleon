package com.grsoft.napoleon;

public class DocumentsEx extends Documents {
	public String getOrgId(){
		return org == null ? "" : org.getData().id;
	}
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
}
