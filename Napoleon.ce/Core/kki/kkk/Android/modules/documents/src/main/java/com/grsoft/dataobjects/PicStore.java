package com.grsoft.dataobjects;


import com.grsoft.database.BlobSource;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="picstore", keyFields="id")
@ServerInfo(name="PicStore")
public class PicStore extends CreateDocDataObject{
	@BlobSource
	public byte[] picture; 
}
