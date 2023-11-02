package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.grsoft.dataobjects.Visit;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class VisitSelectHitching extends HitchOnSelect {
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public VisitSelectHitching(Date created) {
		super(Visit.class, "Visit");
		setCondition(String.format(" \"created\"=ToDate('%s')", sdf.format(created)));
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		// TODO Auto-generated method stub
		super.onRead(rawObject);
	}

}
