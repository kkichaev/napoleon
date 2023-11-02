package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.types.FieldOrder;

public class OrgDogovor extends DataObject {
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	public String name;
	
	@FieldOrder(order=2)
	public List<MatrixItem> matrix;
}
