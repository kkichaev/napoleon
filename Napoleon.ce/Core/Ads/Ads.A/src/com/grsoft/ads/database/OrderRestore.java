/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   12/05/2010   creating
 */

package com.grsoft.ads.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.grsoft.ads.dataobjects.Order;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

/***
 * Восстановление заявок
 * @author kki
 *
 */
public class OrderRestore extends HitchOnSelect {
	public OrderRestore() {
		super(Order.class, "Order");
		Calendar calendar = Calendar.getInstance();
		final int DAYS_TO_RESTORE = 1;
		calendar.add(Calendar.DAY_OF_MONTH, -DAYS_TO_RESTORE);
		Date begin = calendar.getTime();
		
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		setCondition(String.format(" userid = '$CURRENT_USERID' and planbegin >= ToDate('%s 00:00:00')",
				simpleDateFormat.format(begin)));
		
		try{
			Log.d(Consts.D_TAG, getParams());
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Log.d(Consts.D_TAG, "OrderRestore.onRead");
		DataObject dobj = rawObject.createDataObject(Order.class);
		((Order)dobj).params |= ParamState.ofExported;
		dbProxy.insertRecord(dobj);
	}
}
