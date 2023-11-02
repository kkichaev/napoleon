package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="typeorgmatrix", keyFields="id")
public class TypeOrgMatrix extends DataObject {
	public String id = "";
	public List<MatrixItem> items = new ArrayList<MatrixItem>();
}
