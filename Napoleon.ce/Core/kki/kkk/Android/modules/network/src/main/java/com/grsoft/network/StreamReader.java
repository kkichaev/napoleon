/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 *
 *
 * kki   14/10/2010   creating
 */
package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.ServerDemoDataSendHitching;
import com.grsoft.database.ServerLicenseDataSendHitching;
import com.grsoft.database.ServerLicenseTypeSendHitchiing;
import com.grsoft.database.ServerReqDataHitching;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

import android.util.Log;

public class StreamReader
{
	private static final Object STREAM_CONTINUE_OBJECT = "StreamContinue";
	private List<Hitching> hitchings;
	private UpdateProcessListener updateProcessListener;
	
	ServerDemoDataSendHitching demoData;
	ServerLicenseDataSendHitching licenseData;
	ServerLicenseTypeSendHitchiing licenseType;
	ServerReqDataHitching reqDataHitching;
	
	public boolean isContinue = false;
	
	public StreamReader(List<Hitching> hitchings) {
		this.hitchings = new ArrayList<Hitching>(hitchings);
		
		demoData = new ServerDemoDataSendHitching();
		licenseData = new ServerLicenseDataSendHitching();
		licenseType = new ServerLicenseTypeSendHitchiing();
		
		reqDataHitching = new ServerReqDataHitching(demoData, licenseData, licenseType);
	}
	
	public List<Hitching> getHitchings() { return hitchings; }
	
	public void addHitching(List<Hitching> hitchings) {
		this.hitchings.addAll(hitchings);
	}

	public void addHitching(Hitching hitching) {
		this.hitchings.add(hitching);
	}
	
	public void read(ByteStream stream) throws RuntimeException {
		isContinue = false;
		
		while(!stream.isEOS()) {
			Format format = Format.createFormat(stream);
			
			if(format.getName().equals(STREAM_CONTINUE_OBJECT))
				isContinue = true;
			
			Log.d("StreamReader", format.getName());
			dataHandle(format, stream);
		}
	}
	
	public boolean haveServerData() { return licenseType.size() > 0; }
	
	public void setUpdateProcessListener(UpdateProcessListener listener)
	{
		updateProcessListener = listener;
	}
	
	private void fireUpdate(int val)
	{
		if (updateProcessListener != null)
			updateProcessListener.onUpdate(UpdateStatus.STEP, val);
	}
	
	private void dataHandle(Format format, ByteStream stream) throws RuntimeException
	{
			ObjectListener handler = getHitching(format.getName());
			
			if (handler != null)
				readObjects(stream, format, handler);
			else
				readObjects(stream, format, null);
	}
	
	private void readObjects(ByteStream stream, Format format, ObjectListener handler) 
		throws RuntimeException
	{
		RawObject rawObject = new RawObject(format);
		
		if(handler != null)
			handler.onStart();
		
		while(rawObject.read(stream)) {
			if (handler != null)
				handler.onRead(rawObject);
			
			fireUpdate(stream.getPosition());
		}
		
		if(handler != null)
			handler.onEnd();
	}
	
	private ObjectListener getHitching(String name) throws RuntimeException {
		Log.d(Consts.D_TAG, "Hitching recieved: " + name);
		if(reqDataHitching.getObjectName().equals(name))
			return reqDataHitching;
		
		for(Hitching hitching : hitchings) {
			if (hitching.getObjectName().equals(name)) {
				return hitching;
			}
		}
		
		return null;
	}

	public List<ObjectExportListener> getServerObjects() {
		List<ObjectExportListener> ret = new ArrayList<ObjectExportListener>();
		ret.add(demoData);
		ret.add(licenseData);
		ret.add(licenseType);
		return ret;
	}
}
