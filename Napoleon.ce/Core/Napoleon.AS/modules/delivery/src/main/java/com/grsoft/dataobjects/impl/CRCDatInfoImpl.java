package com.grsoft.dataobjects.impl;

import com.grsoft.database.StoreUtils;
import com.grsoft.dataobjects.CRCDatInfo;

public class CRCDatInfoImpl extends DbObject<CRCDatInfo> {
	public void writeCRC(long crc){
		data.crc = crc;
		data.name = StoreUtils.CRC_DAT;
		
		write();
		close();
	}
	
	public long readCRC(){
		long result = -1;
		data.name = StoreUtils.CRC_DAT;
		
		if(read())
			result = data.crc;
		
		close();
		
		return result;
	}
}
