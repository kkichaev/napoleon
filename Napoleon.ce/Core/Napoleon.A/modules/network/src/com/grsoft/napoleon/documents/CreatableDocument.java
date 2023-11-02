package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.TimeZone;

import android.content.Context;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.R;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;


/**
 * ¬се документы которые могут быть созданы порождены от этого класса
 * @author 1111
 *
 * @param <T>
 */
public abstract class CreatableDocument<T extends CreateDocDataObject> 
	extends Document<T> {
	
	public void setExported(boolean value){
		DocumentUtils.setExported(this, data.params, value);
	}
	
//	public boolean isWasSended() {
//		return (data.params & ParamState.ofSended) != 0;
//	}
	
	public boolean isExported(){
		return DocumentUtils.isExported(data.params); 
	}
	
	public boolean isEditable() { return !isExported(); }
	
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
	 * »нициализаци€ документа значени€ми по умолчанию (может выводить диалог например OrderDetail)
	 * @return true - после init можно вызвать open, false - документ сам вызвал диалог дл€ пользовател€
	 */
	public boolean init(Context context, String orgId, GpsCoord gpsCoord){
		data.date = Util.getDate();
		data.created = Util.getDateTime();
		
		data.id = orgId;
		data.latitude = gpsCoord.latitude;
		data.longitude = gpsCoord.longitude;
		data.stltime = gpsCoord.time;
		data.params = 0;
		
		TimeZone tz = TimeZone.getDefault();
		Date now = new Date();
		data.timeZone = -tz.getOffset(now.getTime()) / (60*1000);
		
		postInit();
		
		return (write() != ExtrasConst.INVALID_ID);
	}
	
	public void postInit() {}

	/**
	 *  опирует текущий документ (и сохран€ет его) 
	 * @return null если метод нельз€ скопировать
	 */
	public CreatableDocument<T> copy() {return null;}
	
	/***
	 * –азмер документа в байтах
	 * @return
	 */
	public long size(){
		final long DEFAULT_SIZE_DOC = 200L;
		return DEFAULT_SIZE_DOC;
	}
	
	public String getPodRemark(){ return data.podRemark; }
	
	/**
	 * ќпредел€ем можно ли удалить документ
	 * @return
	 */
	public boolean isEmpty() { return false; }
}
