/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Обработчик для обработки объекта
 * при чтении записи из потока
 * расширение для объектов владеющих 
 * курсором
 *
 * kki   14/10/2010   creating
 */
package com.grsoft.network;

import com.grsoft.napoleon.documents.DocList;

public interface DocExportListener extends ObjectListener
{
	DocList getDocuments();
}
