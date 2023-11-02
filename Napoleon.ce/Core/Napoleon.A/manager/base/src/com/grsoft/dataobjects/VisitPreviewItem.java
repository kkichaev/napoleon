package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.BlobSource;
import com.grsoft.types.FieldOrder;

public class VisitPreviewItem extends DataObject {
	@FieldOrder(order=0)
	public String name = "";
	
	@FieldOrder(order=1)
	public String smallName = "";
	
	@FieldOrder(order=2)
	public String smallSize = "";
	
	@FieldOrder(order=3)
    public int rating = 0;
	
	@FieldOrder(order=4)
    public Date date;
    
	@BlobSource
	@FieldOrder(order=0)
    public byte[] smallPhoto;
}
