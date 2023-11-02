/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Обработчик для обработки объекта
 * при чтении записи из потока
 *
 * kki   14/10/2010   creating
 */
package com.grsoft.network;

import com.grsoft.network.exception.RuntimeException;

public interface ObjectListener
{
	/**
	 * Вызывается перед началом чтения объектов из потока
	 */
	void onStart();
	void onRead(RawObject rawObject) throws RuntimeException;
	void onSave();
	void onEnd();
	
	/**
	 * Имя объекта на сервере
	 * @return
	 */
	String getObjectName();
}
