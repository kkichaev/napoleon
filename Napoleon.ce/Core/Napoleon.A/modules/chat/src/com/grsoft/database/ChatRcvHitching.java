package com.grsoft.database;

import com.grsoft.dataobjects.ChatData;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;


public class ChatRcvHitching extends Hitching{
	public static String OBJECT_NAME = "ChatQuery"; 
		
	public ChatRcvHitching() {
		super(ChatData.class, OBJECT_NAME);
	}
	
	public void onRead(RawObject rawObject) throws RuntimeException {
		ChatData dobj = (ChatData) rawObject.createDataObject(dataObject);
		dobj.params = ParamState.ofExported;
		dbProxy.insertRecord(dobj);
	};

}
