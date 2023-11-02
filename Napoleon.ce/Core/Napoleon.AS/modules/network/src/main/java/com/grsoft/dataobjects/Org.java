/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Организации
 *
 * kki   09/11/2010   creating
 */
package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.Features;
import com.grsoft.types.Feature;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="Org", keyFields="id")
@ServerInfo(name="Org")
public class Org extends DataObject
{
	/***
	 * Организая в стоп-листе
	 */
	public static final int FL_STOP_BLOCK = 1;
	public static final int FL_STOP_LIST = 2;
	public static final int FL_EXPORTED = 16;
	public static final int FL_USER_CREATED = 32;
	public static final int FL_CUSTOM_FIZ = 64; // пока используется только в liada
	
	// флаг 8 занят в Alianta see OrgEx
	
	public String id = "";
	public String name = "";
	public String srchName = "";
	public String address = "";
	
	@Scale(value=1)
	public int color;
	
	public List<Contact> contacts = new ArrayList<Contact>();
	
	@Scale(value=Consts.GPS_SCALE)
	public int longitude;
	
	@Scale(value=Consts.GPS_SCALE)
	public int latitude;
	
	@Scale(value=0)
	public int type;
	
	public String userid = "";
	
	@Scale(value=1)
	public int flags;

	/***
	 * дата создания
	 */
	public Date created;
	
	/**
	 * Тип цены
	 */
	public int costype;
	
	/**
	 * тип цены для v5
	 */
	public String prcType="";
	
	/**
	 * адреса доставки
	 */
	@Feature(feature="DELIVERY_ADDRESS")
	public List<OrgAddress> orgAddress;
	
	public int hidden = 0;

	// from OrgPrint
	public String inn = "";
	public String phone = "";
	public String bank = "";
	public String fullName = "";
	public String legalAddress = "";
	public String okpo = "";
	// from OrgPrint

	public List<OrgFolderDiscount> fldDsc = new ArrayList<>();
	public List<OrgPriceCost> prcCost = new ArrayList<>();


	public boolean isPotencial() { return (flags & FL_USER_CREATED) != 0; }
	public void setPotencial() { flags |= FL_USER_CREATED; }

	public boolean isStopList() {
		if( Features.ORG_STOP_TABLE ) {
			loadStopped();
			return stopped.contains(id);
		}
		return (flags & Org.FL_STOP_LIST) != 0 || isBlocked(); 
	}
	
	public boolean isBlocked(){
		return (flags & Org.FL_STOP_BLOCK) != 0;
	}
	
	protected static HashSet<String> stopped;
	public static void clearCache() {
		stopped = null;
	}
	
	void loadStopped() {
		if( stopped == null ) {
			stopped = new HashSet<String>();
			String table = DataObjectInfo.getInstance().getTableName(OrgStop.class);
			DbWriter.checkDBTable(OrgStop.class);
			OrgStop data = new OrgStop();
			DbReader r = new DbReader();
			boolean bdo = r.select(data, table, null);
			while(bdo) {
				stopped.add(data.id);
				bdo = r.selectNext(data);
			}
			r.close();
		}
	}
	
	@Override public String toString() { return name; }
}
