package com.grsoft.ads.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="pause", keyFields="pause")
public class Pause extends DataObject {
	public Date pause = new Date(0);
	@Scale(value=Consts.GPS_SCALE)
	public int plat;
	@Scale(value=Consts.GPS_SCALE)
	public int plong;
	public Date resume = new Date(0);
	@Scale(value=Consts.GPS_SCALE)
	public int rlat;
	@Scale(value=Consts.GPS_SCALE)
	public int rlong;
	public int params;
}
