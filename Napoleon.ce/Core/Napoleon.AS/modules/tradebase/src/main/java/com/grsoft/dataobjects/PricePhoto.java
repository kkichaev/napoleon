/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   15/07/2011   creating
 */
package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.BlobSource;

public class PricePhoto extends DataObject {
	
	/***
	 * Список id пунктов Price
	 */
	public List<PricePhotoItem> items = new ArrayList<PricePhotoItem>();
	
	/***
	 * Фотография
	 */
	public byte[] photo;
}

