package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="mapgis", keyFields="date,userid")
@ServerInfo(name="Mapgis")
public class Mapgis extends DataObject {
	public String html = "";
	public String title = "";
	public Date date;
	public String userid = "";
}
