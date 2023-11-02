package com.grsoft.database;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * Связывает объекты данных с объектами на сервере
 * @author kkichaev
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface ServerInfo {
	/***
	 * 
	 * @return имя объекта на сервере
	 */
	String name() default "";
}
