/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Обработчик событий от приема/передачи
 * для объект Message
 *
 * kki   06/04/2011   creating
 */
package com.grsoft.database;

import com.grsoft.dataobjects.Message;
import com.grsoft.napoleon.UpdateMessageBox;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.MessageStock;

public class MessageHitching  extends Hitching {

	private static final String OBJECT_NAME = "Message";
	
	public MessageHitching() {super(Message.class, OBJECT_NAME);}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Message message = (Message) rawObject.createDataObject(dataObject);
		MessageStock.add(message);
	}

}
