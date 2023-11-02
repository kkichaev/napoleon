/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   03/08/2011   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;
import com.grsoft.network.exception.UploadException;

/***
 * Интерфейс подклассов для MemberFormat, которые 
 * могут форматировать свое значение в массив байт 
 * передачи на сервер
 * 
 * @author kki
 *
 */
public interface BinaryFormatValue {
	byte[] valueToBinary(Object value) throws UploadException;
}
