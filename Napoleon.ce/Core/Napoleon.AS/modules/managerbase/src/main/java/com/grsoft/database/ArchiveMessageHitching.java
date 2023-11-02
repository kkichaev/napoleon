package com.grsoft.database;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

import com.grsoft.dataobjects.ArchiveMessage;

public class ArchiveMessageHitching extends HitchOnSelect {

	@SuppressLint("SimpleDateFormat")
	public ArchiveMessageHitching(String userid) {
		super(ArchiveMessage.class, "MessageArchive");
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		Date begin = calendar.getTime();
		
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		setCondition(String.format(" \"userid\" = '%s' and \"date\" >= ToDate('%s 00:00:00')",
				userid, simpleDateFormat.format(begin)));

	}

}
