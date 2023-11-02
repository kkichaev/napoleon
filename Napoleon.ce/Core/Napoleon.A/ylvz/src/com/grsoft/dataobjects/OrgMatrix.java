package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgMatrix", keyFields = "name")
public class OrgMatrix extends DataObject {
	
	/***
	 * Имя
	 */
	public String name = "";
	
	/**
	 * Содержание
	 */
	public List<OrgMatrixItem> items = new ArrayList<OrgMatrixItem>();
}
