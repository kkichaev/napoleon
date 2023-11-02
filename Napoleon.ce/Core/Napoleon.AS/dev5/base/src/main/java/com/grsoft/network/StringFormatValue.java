/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   03/08/2011   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;

/***
 * Интерфейс подклассов для MemberFormat, которые 
 * могут форматировать свое значение в строку для 
 * передачи на сервер
 * 
 * @author kki
 *
 */
public interface StringFormatValue {
	String valueToFormatString(Object value);
}
