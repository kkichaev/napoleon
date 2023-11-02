package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.documents.DocType;

public class DocumentRestoreEx extends DocumentRestore {

	String where = "";

	public DocumentRestoreEx(DocType docType) {
		super(docType);
	}

	@Override
	protected void makeDocReceiveCondition(String timeField, int months, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -months);
		calendar.add(Calendar.DATE, -days);
		Date begin = calendar.getTime();
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		
		where = "\"id\" in (";
		DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>() {

			@Override
			public boolean travel(DataTraveler<Org> item) {
				where += "'" + item.data.id + "',";
				return true;
			}
		}, "");
		
		where = where.substring(0, where.length()-1);
		where += ")";
		where += String.format(" and \"%s\" >= ToDate('%s 00:00:00')",timeField, simpleDateFormat.format(begin));
		
		setCondition(where);
	}
}
