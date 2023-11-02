package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="RivalFolder", keyFields = "id", indexes="fid")
@ServerInfo(name="RivalFolder")
public class RivalFolder extends Folder {

}
