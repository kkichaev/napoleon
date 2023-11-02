package com.grsoft.dataobjects;

import com.grsoft.database.BlobSource;
import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.Date;

@TableInfo(name="Banner")
@ServerInfo(name = "Banner")
public class Banner extends DataObject {
    public static final int PLACE_START = 1;
    public static final int PLACE_ORDER = 2;

    public Date date = new Date();

    @BlobSource
    public byte[] pic = null;

    public  int duration = 0;
    public int place = 0;
    public int pos = 0;
}
