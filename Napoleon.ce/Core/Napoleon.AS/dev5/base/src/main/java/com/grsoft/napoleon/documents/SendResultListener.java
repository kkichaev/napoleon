/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   27/04/2011   creating
 */
package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

/***
 * Окончание отправки данных на сервер
 * @author kki
 *
 */
public interface SendResultListener {
	void postSendExecute(boolean result);
}
