package com.grsoft.dataobjects;

import com.grsoft.database.BlobSource;
import com.grsoft.types.FieldOrder;

public class ClientDocsItem extends DataObject{
    @FieldOrder(order=0)
    public String type = "";
    @BlobSource
    @FieldOrder(order=1)
    public byte[] file;
}
