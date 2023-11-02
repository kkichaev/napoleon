/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   26/04/2011   creating
 */
package com.grsoft.database;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * Поле хранит не данныe а путь к ним
 * для бинарного поля
 * @author kki
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface BlobSource {
}
