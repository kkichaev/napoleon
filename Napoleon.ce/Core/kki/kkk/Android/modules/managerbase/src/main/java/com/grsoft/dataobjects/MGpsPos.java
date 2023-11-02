package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="mgpspos", keyFields="userid,stltime")
@ServerInfo(name="GPSPos")
public class MGpsPos extends GPSPos {
	public String userid = "";
}
