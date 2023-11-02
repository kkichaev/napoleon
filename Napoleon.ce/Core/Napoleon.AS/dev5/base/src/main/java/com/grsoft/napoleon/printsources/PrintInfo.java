package com.grsoft.napoleon.printsources;
import com.grsoft.aceteam.R;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface PrintInfo{
	String name() default "";
}

