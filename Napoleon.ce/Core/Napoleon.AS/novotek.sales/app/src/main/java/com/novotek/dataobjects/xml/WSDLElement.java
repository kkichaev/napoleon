package com.novotek.dataobjects.xml;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value= RetentionPolicy.RUNTIME)
@Inherited
public @interface WSDLElement {

    /**
     * @return ��� ��������
     */
    String name() default "";

    /**
     * @return ������� ����� ����� ������� (xs:sequnce)
     */
    String memberOrder() default "";
}

