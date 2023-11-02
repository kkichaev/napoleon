package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="requestChek", keyFields="created")
@ServerInfo(name="RequestChek")
public class RequestChek extends ChekBase {
	public boolean canSend() {
		// sum < 100000 ð
		return sum > 0 && sum < 10000000 && (handleStatus == ChekBase.CHEK_ERROR || handleStatus == ChekBase.CHEK_IS_NEW);
	}
}
