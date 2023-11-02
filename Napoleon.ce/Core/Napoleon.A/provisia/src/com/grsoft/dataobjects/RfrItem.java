package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class RfrItem extends DataObject {
	
	static final int IS_CHECKED = 1;
	
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=0)
	public String name;

	@FieldOrder(order=0)
	public String text;

	@FieldOrder(order=0)
	public int flags;
	
	public boolean isChecked() { return (flags & IS_CHECKED) != 0; }
	
	public void setChecked(boolean checked) {
		if( checked ) flags |= IS_CHECKED;
		else flags &= ~IS_CHECKED;
	}
}
