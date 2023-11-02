package com.ashberrysoft.leadertask.service;

import android.app.Service;
import android.os.Looper;

import com.v2soft.AndLib.dataproviders.AbstractServiceTaskHandler;

/**
 * Хендлер сервисов.
 * 
 * @author Eugen Oleynik jinoleynik@gmail.com
 * 
 */
public class ServiceHandler extends AbstractServiceTaskHandler {  

	public ServiceHandler(Looper looper, Service context, String handledAction,
			String errorAction) {
		super(looper, context, handledAction, errorAction);	
	}

	@Override
	protected boolean shouldStop() {
		return true;
	}
}
