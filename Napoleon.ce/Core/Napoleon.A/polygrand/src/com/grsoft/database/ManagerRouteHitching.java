package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;
import com.grsoft.dataobjects.ManagerRoute;


public class ManagerRouteHitching extends HitchOnSelect{

	@SuppressLint("SimpleDateFormat")
	public ManagerRouteHitching() {
		super(ManagerRoute.class, "ManagerRoute");
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		setCondition(String.format(" \"userid\" = '$CURRENT_USERID' and \"date\" >= ToDate('%s 00:00:00')",
				simpleDateFormat.format(Calendar.getInstance().getTime())));
	}

}
