package com.serviko.dataobjects.xml;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value= RetentionPolicy.RUNTIME)
@Inherited
public @interface WSDLElement {

    /**
     * @return Имя элемента
     */
    String name() default "";

    /**
     * @return порядок полей через запятую (xs:sequnce)
     */
    String memberOrder() default "";
}

