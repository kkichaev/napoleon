package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.grsoft.dataobjects.Visit;

public class VisitSelectHitching extends HitchOnSelect {
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public VisitSelectHitching(Date created) {
		super(Visit.class, "Visit");
		setCondition(String.format(" \"created\"=ToDate('%s')", sdf.format(created)));
	}

}
