package com.serviko.sales.main_views;

import java.lang.reflect.Field;

public class Filter {
    public boolean getValue(String fieldName) {
        try {
            Field f = getClass().getField(fieldName);
            return (boolean) f.get(this);
        } catch (Exception e) {

        }
        return false;
    }

    public void setValue(String fieldName, boolean value) {
        try {
            Field f = getClass().getField(fieldName);
            f.set(this, value);
        } catch (Exception e) {

        }
    }
}
