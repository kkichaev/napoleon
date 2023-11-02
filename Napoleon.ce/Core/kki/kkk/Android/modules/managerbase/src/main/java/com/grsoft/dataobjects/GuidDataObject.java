package com.grsoft.dataobjects;

import java.util.UUID;

public class GuidDataObject extends DataObject {
	public String id = "";
	public String name = "";
	
	public void init(){
		id = UUID.randomUUID().toString();
	}
}
