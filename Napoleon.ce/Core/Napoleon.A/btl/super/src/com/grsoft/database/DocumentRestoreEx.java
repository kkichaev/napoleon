package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.grsoft.napoleon.documents.DocType;

public class DocumentRestoreEx extends DocumentRestore {
	
	public DocumentRestoreEx(DocType docType, String objName, Date from, Date till) {
		super(docType, objName);
		initCondition(from, till);
	}
	
	public DocumentRestoreEx(DocType docType, Date from, Date till) {
		super(docType);
		initCondition(from, till);
	}

	public void initCondition(Date from, Date till) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(till);
		calendar.add(Calendar.DATE, 1);
		
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		setCondition(String.format(" userid = '$CURRENT_USERID' and created >= ToDate('%s 00:00:00') " +
				"and created < ToDate('%s 00:00:00')",
				simpleDateFormat.format(from), simpleDateFormat.format(calendar.getTime())));
	}
}
