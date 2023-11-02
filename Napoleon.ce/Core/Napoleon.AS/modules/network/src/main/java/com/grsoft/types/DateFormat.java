package com.grsoft.types;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface DateFormat {
	public String format() default "";
}
