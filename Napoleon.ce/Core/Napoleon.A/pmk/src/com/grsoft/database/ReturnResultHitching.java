package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.grsoft.dataobjects.DocHandleStatus;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnResult;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class ReturnResultHitching extends Hitching {
	SimpleDateFormat parser;
	static String errorMessage = "";
	ReturnImplEx ret = new ReturnImplEx();

	public ReturnResultHitching() {
		super(ReturnResult.class, "ReturnResult");
		parser = new SimpleDateFormat("yyyyMMddHHmmss");
	}

	@Override
	public void onStart() {
		errorMessage = "";
	}
	
	public static String getErrorMessage() { 
		String ret = errorMessage;
		errorMessage = "";
		return ret;
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		ret.close();
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		try {
		
			ReturnResult rr = (ReturnResult)rawObject.createDataObject(dataObject);
			int status = DocHandleStatus.getStatus(rr.status);
			if( status == DocHandleStatus.FAIL ) {
				errorMessage = rr.message;
				return;
			}
			if( rr.created.length() == 0 || rr.created.toUpperCase(Locale.getDefault()).equals("NONE"))
				return;
			
			ReturnEx rdoc = (ReturnEx) ret.getData();
			rdoc.created = parser.parse(rr.created);
			if( ret.read()) {
				rdoc.retNumber = rr.number;
				ret.setProceeded();
				ret.setExported(true);
				ret.write();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
