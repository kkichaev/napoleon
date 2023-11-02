package com.grsoft.ads;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskTimeHelper {
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm");
	
	public String timeToString(Date start, Date finish) {
		return String.format("%s - %s", sdf.format(start), sdf.format(finish));
	}
}
