package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

/***
 * /***
 * Базовый класс для всех 
 * DataObjects которые создают документ на КПК
 * @author kki
 *
 */
public class CreateDocDataObject extends DocDataObject {
	/**
	 * Дата создания
	 */
	public Date created;
	
	/**
	 * примечание из обработки документа
	 */
	public String podRemark = "";

	/**
	 * Служебный флаг описывающий
	 * текущее состояния
	 */
	@Scale(value=1)
	public int params;
	
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

	/**
	 * Текстовая заметка
	 */
	public String remark = "";
	
}
