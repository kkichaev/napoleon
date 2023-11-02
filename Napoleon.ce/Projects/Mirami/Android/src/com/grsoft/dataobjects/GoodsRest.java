package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="rests", keyFields = "created")
public class GoodsRest extends DataObject {
	/**
	 * Дата создания
	 */
	public Date created;
	
	/**
	 * Дата создания
	 */
	public Date date;
	
	/**
	 * Код организации
	 */
	public String id = "";
	
	/**
	 * Служебный флаг описывающий
	 * текущее состояния
	 */
	public int params;
	
	/**
	 * Текстовая заметка
	 */
	public String remark = "";

	public List<GoodsRestItem> items;

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
