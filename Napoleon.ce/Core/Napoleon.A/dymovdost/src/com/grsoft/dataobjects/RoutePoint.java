package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="routepoint",keyFields="id")
@ServerInfo(name="RoutePoint")
public class RoutePoint extends DataObject{
	public int pos = 0;
	public String id = "";
	public String name = "";
	public String address = "";
	public String terminal = "";
	public List<PointContact> contacts = new ArrayList<PointContact>();
	public String remark = "";
	public List<PointDoc> docs = new ArrayList<PointDoc>();
	
	/**
	 * Широта
	 */
	@Scale(value=Consts.GPS_SCALE)
	public int latitude;
	
	/**
	 * Долгота
	 */
	@Scale(value=Consts.GPS_SCALE)
	public int longitude;
}
