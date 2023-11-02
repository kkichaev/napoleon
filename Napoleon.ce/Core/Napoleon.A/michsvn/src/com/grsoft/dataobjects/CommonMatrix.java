package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="CommonMatrix")
@TableInfo(name="commonmatrix", keyFields = "name")
public class CommonMatrix extends Matrix {

}
