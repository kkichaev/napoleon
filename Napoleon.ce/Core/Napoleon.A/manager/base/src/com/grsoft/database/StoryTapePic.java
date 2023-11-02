package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;

public class StoryTapePic extends DataObject {
	@BlobSource
	@FieldOrder(order=0)
	public byte[] pic;
	@FieldOrder(order=1)
	public String name;
}
