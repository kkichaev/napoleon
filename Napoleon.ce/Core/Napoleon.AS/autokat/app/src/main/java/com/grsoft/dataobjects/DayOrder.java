package com.grsoft.dataobjects;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value= RetentionPolicy.RUNTIME)
public @interface DayOrder {
    int order();
}
