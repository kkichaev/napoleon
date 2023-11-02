package com.grsoft.ads.dataobjects;

import com.grsoft.database.BlobSource;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="picstore", keyFields="id")
@ServerInfo(name="PicStore")
public class PicStore extends DataObject {
	public String id = "";
	public int params = 0;
	public int readytosend = 0;
	
	@BlobSource
	public byte[] picture; 
}
