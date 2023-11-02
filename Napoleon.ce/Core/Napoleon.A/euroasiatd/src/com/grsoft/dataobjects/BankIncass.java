package com.grsoft.dataobjects;

import com.grsoft.database.BlobSource;
import com.grsoft.database.TableInfo;

@TableInfo(name="BankIncass", keyFields="created")
public class BankIncass extends Incass {
	@BlobSource
	public byte[] photo = new byte[] {};	
}
