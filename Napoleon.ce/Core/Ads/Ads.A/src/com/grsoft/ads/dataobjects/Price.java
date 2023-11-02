package com.grsoft.ads.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;

@TableInfo(name="warehouse", keyFields = "id")
public class Price extends DataObject {
	public String id = "";
    public String folder = "";
    public String name = "";
    @Scale(value=100)
    public int cost = 0;

}
