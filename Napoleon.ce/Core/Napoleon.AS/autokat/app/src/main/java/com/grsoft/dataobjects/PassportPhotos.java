package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name="passportphotos", keyFields = "number")
@ServerInfo(name="PassportPhotos")
public class PassportPhotos extends DataObject{
    public String number = "";
}
