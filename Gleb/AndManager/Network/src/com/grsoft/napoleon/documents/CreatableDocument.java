package com.grsoft.napoleon.documents;

import android.content.Context;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.R;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;


/**
 * Все документы которые могут быть созданы порождены от этого класса
 * @author 1111
 *
 * @param <T>
 */
public abstract class CreatableDocument<T extends CreateDocDataObject> 
	extends Document<T> {
	
	public void setExported(boolean value){
		if (value)
			data.params |= ParamState.ofExported;
		else
			data.params &= ~ParamState.ofExported;
		
		write();
	}	
	
	public boolean isExported(){
		return (data.params & ParamState.ofExported) == ParamState.ofExported; 
	}
	
	public void setProceeded(){
		data.params |= ParamState.ofProceeded;
		write();
	}
	
	public void unsetProceeded(){
		data.params &= ~ParamState.ofProceeded;
		data.podRemark = "";
		write();
	}
	
	public boolean isProceeded(){
		return (data.params & ParamState.ofProceeded) == ParamState.ofProceeded; 
	}
	
	@Override
	public String getDescription(Context context) {
		return (data.podRemark.length() > 0) ? data.podRemark : 
				(isProceeded()) ?  context.getString(R.string.in_processeng) : 
				(isExported()) ? context.getString(R.string.sent) : 
				""; 
	}

	/**
	 * Инициализация документа значениями по умолчанию (может выводить диалог например OrderDetail)
	 * @return true - после init можно вызвать open, false - документ сам вызвал диалог для пользователя
	 */
	public boolean init(Context context, String orgId, GpsCoord gpsCoord){
		data.date = Util.getDate();
		data.created = Util.getDateTime();
		
		data.id = orgId;
		data.latitude = gpsCoord.latitude;
		data.longitude = gpsCoord.longitude;
		data.params = 0;
		
		return (write() != ExtrasConst.INVALID_ID);
	}
	
	/**
	 * Копирует текущий документ (и сохраняет его) 
	 * @return null если метод нельзя скопировать
	 */
	public CreatableDocument<T> copy() {return null;}
	
	/***
	 * Размер документа в байтах
	 * @return
	 */
	public long size(){
		final long DEFAULT_SIZE_DOC = 200L;
		return DEFAULT_SIZE_DOC;
	}
}
