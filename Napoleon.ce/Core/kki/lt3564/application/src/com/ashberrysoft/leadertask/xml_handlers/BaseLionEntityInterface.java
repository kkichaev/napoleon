package com.ashberrysoft.leadertask.xml_handlers;

import java.util.UUID;

import android.content.ContentValues;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public interface BaseLionEntityInterface {

    public static final String UNSUPPORTED_OPERATION_EXCEPTION = "Use static <getLionEntity> function";

    void fillKeyValue(String key, String value);

    void getLionEntity(StringBuilder sb);

    String getServerClass();

    UUID getId();

    int getIdTask();

    ContentValues getContentValues(ContentValues cv);
}