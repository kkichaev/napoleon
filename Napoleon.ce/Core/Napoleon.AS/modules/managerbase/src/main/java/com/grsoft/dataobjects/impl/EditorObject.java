package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.GuidDataObject;

public abstract class EditorObject<T extends GuidDataObject> extends DbObject<T> {
	
	public void init(){
		getData().init();
	}
	
	public String getTitle(){
		return getData().name;
	}
}
