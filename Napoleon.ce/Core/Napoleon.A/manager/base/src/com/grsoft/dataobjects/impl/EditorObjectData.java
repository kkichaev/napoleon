package com.grsoft.dataobjects.impl;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface EditorObjectData {
	int titleid() default 0;
	Class<?> activity();
}
