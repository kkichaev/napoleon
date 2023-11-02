/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   16/07/2011   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;

import java.util.List;

import com.grsoft.database.Hitching;


/**
 * Выдает Read/WriteService
 * @author kki
 *
 */
public class RWServiceFactory {
	public static RWServiceFactory instance = new RWServiceFactory();
	
	public ReadServiceBase createReadService(List<Hitching> hitchings) {
		return new ReadServiceBase(hitchings);
	}

	public WriteServiceBase createWriteService(List<? extends ObjectListener> objectsToSend) {
		return createWriteService(objectsToSend, false);
	}

	public WriteServiceBase createWriteService(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		return new WriteServiceBase(objectsToSend, rcvRemnants);
	}
}
