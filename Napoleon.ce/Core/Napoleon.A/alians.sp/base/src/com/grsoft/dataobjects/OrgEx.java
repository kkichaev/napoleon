package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="Org", keyFields="id", indexes="sortOrder")
public class OrgEx extends Org {
	public int sortOrder;
	public List<MatrixItemEx> matrix = new ArrayList<MatrixItemEx>();
	
	@Override
	public String toString() {
		return name;
	}
}
