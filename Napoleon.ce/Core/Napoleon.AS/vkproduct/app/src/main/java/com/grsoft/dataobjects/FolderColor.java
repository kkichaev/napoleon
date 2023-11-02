package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(keyFields="id", name="FolderColor")
@ServerInfo(name="FolderColor")
public class FolderColor extends DataObject {
	public String id;
	public int color;
}
