package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

public class DataObjectRestore extends HitchOnSelect{
	public DataObjectRestore(Class<? extends DataObject> dataObject,
			String objectName, String timeField) {
		super(dataObject, objectName);
		makeDocReceiveCondition(timeField, 
				((CfgNplW)ConfigManager.getConfig()).monthsToRecreate,
				((CfgNplW)ConfigManager.getConfig()).daysToRecreate);
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Log.d(Consts.D_TAG, "DataObjectRestore.onRead");
		DataObject dobj = rawObject.createDataObject(dataObject);
		beforeWrite(dobj);
		dbProxy.insertRecord(dobj);
	}
	
	protected void beforeWrite(DataObject dobj) {}
	
	/**
	 * Создает условие выборки документов за период для текущего пользователя
	 * @param months - число месяцев для выборки
	 */
	protected void makeDocReceiveCondition(String timeField, int months, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -months);
		calendar.add(Calendar.DATE, -days);
		Date begin = calendar.getTime();
		
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		setCondition(String.format(" \"userid\" = '$CURRENT_USERID' and \"%s\" >= ToDate('%s 00:00:00')",
				timeField, simpleDateFormat.format(begin)));
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
	}
}
