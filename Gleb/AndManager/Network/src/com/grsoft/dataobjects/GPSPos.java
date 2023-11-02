/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   18/04/2010   creating
 */
package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

/***
 * Маршрут 
 * @author kki
 *
 */

@TableInfo(name="GPSPos", keyFields="date")
public class GPSPos extends DataObject {

	/***
	 * Дата создания записи
	 */
	public Date date;
	
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
	 * Скорость
	 */
	@Scale(value=100)
	public int speed;
	
	/**
	 * Служебный флаг описывающий
	 * текущее состояния
	 */
	@Scale(value=1)
	public int params;
	
	/**
	 * Если координаты сняты GSM провайдером то 1 
	 */
	@Scale(value=1)
	public int isGSM;

	/**
	 * Количество спутников
	 */
	@Scale(value=1)
	public int satellite;
	
	/**
	 * Точность в метрах
	 */
	@Scale(value=1)
	public int accuracy;
}

