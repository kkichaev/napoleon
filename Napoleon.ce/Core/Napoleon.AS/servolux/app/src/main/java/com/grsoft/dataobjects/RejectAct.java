package com.grsoft.dataobjects;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="RejectAct", keyFields="created")
@ServerInfo(name="RejectAct")
public class RejectAct extends Order {
	public Date visitDoc = new Date(0);

	public Date getStartExpiredDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, -1);
		c.set(Calendar.DAY_OF_MONTH, 20);
		return c.getTime();
	}

	public Date getEndExpiredDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}
}
