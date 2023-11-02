/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 *
 *
 * kki   14/10/2010   creating
 */
package com.grsoft.network;

import java.util.List;

import android.util.Log;

import com.grsoft.database.Hitching;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

public class StreamReader
{
	private List<Hitching> hitchings;
	private UpdateProcessListener updateProcessListener;
	
	public StreamReader(List<Hitching> hitchings)
	{
		this.hitchings = hitchings;
	}
	
	public void read(ByteStream stream) throws RuntimeException
	{
		while(!stream.isEOS())
		{
			Format format = Format.createFormat(stream);
			dataHandle(format, stream);
		}
	}
	
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
		
		while(rawObject.read(stream))
		{
			if (handler != null)
				handler.onRead(rawObject);
			
			fireUpdate(stream.getPosition());
		}
		
		if(handler != null)
			handler.onEnd();
	}
	
	private Hitching getHitching(String name) throws RuntimeException
	{
		Log.d(Consts.D_TAG, "Hitching recieved: " + name);
		for(Hitching hitching : hitchings)
		{
			if (hitching.getObjectName().equals(name))
				return hitching;
		}
		
		return null;
	}
}
