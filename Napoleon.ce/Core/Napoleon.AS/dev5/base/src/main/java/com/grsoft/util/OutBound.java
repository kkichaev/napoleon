/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Интерфейс для объекта обработчика
 * событий отправки объект на сервер
 *
 * kki   03/02/2011   creating
 */
package com.grsoft.util;
import com.grsoft.aceteam.R;

import com.grsoft.napoleon.documents.CreatableDocument;

public interface OutBound
{
	void onSending(CreatableDocument<?> dataObject);
	void onSended(CreatableDocument<?> dataObject);
}
