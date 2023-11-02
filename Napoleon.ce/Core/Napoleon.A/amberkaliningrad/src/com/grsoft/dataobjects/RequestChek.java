package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="requestChek", keyFields="created")
@ServerInfo(name="RequestChek")
public class RequestChek extends ChekBase {
	public int docSended = 0 ; 
	
	public boolean canSend() {
		return sum > 0 && (handleStatus == ChekBase.CHEK_ERROR || handleStatus == ChekBase.CHEK_IS_NEW);
	}
}
