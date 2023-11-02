package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;


import java.util.ArrayList;

import com.grsoft.network.UploadContext;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.exception.UploadException;
import com.grsoft.network.util.UnicodUtils;
import com.grsoft.util.Util;

public class DataObjectPool {

	UploadContext context = new UploadContext();

	private ArrayList<SendedObjectData> cash = new ArrayList<SendedObjectData>();
//	private Map<String, FormatObjectList> cash = new HashMap<String, FormatObjectList>();
	
	public void add(DataObject dataObject, String key) throws RuntimeException, UploadException {
		if(cash.size() > 0 && cash.get(cash.size()-1).objectName.equals(key)) {
			cash.get(cash.size()-1).objects.appendObject(dataObject, context);
			return;
		}
		cash.add(new SendedObjectData(key, dataObject, context));
	}

	public void closeUpload() {
		context.close();
	}
	
	public int size() { return cash.size(); }
	
	public void add(DataObject dataObject) throws RuntimeException, UploadException {
		String key = getObjectName(dataObject);		
		add(dataObject, key);
	}
	
	public void add(String name, FormatObjectList fol) {
		cash.add(new SendedObjectData(name, fol));
	}

	private String getObjectName(DataObject dataObject) throws RuntimeException {
		try {
			return  DataObjectInfo.getInstance().getTableName(dataObject.getClass());
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	public byte[] toStreamData() throws RuntimeException {
		ArrayList<byte[]> result = new ArrayList<byte[]>();
		
		for(SendedObjectData sod : cash) {
			result.add(UnicodUtils.toBytes(sod.objectName));
			result.add(sod.objects.getData());			
		}
				
		return Util.ArrayListToBytes(result);
	}
	
	public boolean isEmpty()
	{
		return cash.size() == 0;
	}
}

class SendedObjectData {
	public SendedObjectData(String name, DataObject dataObject, UploadContext context) throws UploadException {
		objectName = name;
		try {
			objects = new FormatObjectList(dataObject, context);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	public SendedObjectData(String name, FormatObjectList fol) {
		objectName = name;
		objects = fol;
	}
	public String objectName;
	public FormatObjectList objects;
}
