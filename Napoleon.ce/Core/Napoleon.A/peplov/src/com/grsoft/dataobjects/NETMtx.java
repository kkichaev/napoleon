package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="netmtx", keyFields="id")
@ServerInfo(name="NETMtx")
public class NETMtx extends DataObject {
	public String id = "";
	public List<NetMtxItem> items = new ArrayList<NetMtxItem>();
}
