package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="wsorder", keyFields="created")
public class WSOrder extends Order{
    public int delivery = 0;
}
