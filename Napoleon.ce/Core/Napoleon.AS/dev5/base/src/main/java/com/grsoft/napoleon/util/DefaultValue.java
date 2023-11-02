package com.grsoft.napoleon.util;
import com.grsoft.aceteam.R;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(value=RetentionPolicy.RUNTIME)
@Inherited	
public @interface DefaultValue {
	String value() default "";
}
