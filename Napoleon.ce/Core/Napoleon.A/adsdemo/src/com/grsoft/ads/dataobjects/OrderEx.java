package com.grsoft.ads.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrderEx extends Order {
	/**
	 * Отказана
	 */
	public static final int MISSED = 0x200000;
	
	/**
	 * Фотографии
	 */
	public List<OrderPhotoItem> photos = new ArrayList<OrderPhotoItem>();
	
	/**
	 * Часы доставки
	 */
	public String delivhour = "";
	
	/**
	 * Порядковый номер
	 */
	public int priority = 0;
}
