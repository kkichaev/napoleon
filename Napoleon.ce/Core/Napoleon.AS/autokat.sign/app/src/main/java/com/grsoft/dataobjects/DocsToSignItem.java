package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class DocsToSignItem extends DataObject{
    @FieldOrder(order = 0)
    public String name = "";

    @FieldOrder(order = 1)
    public byte[] document = null;

    @FieldOrder(order = 2)
    public String file = "";
}
