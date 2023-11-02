package com.ashberrysoft.leadertask.interfaces;

import java.io.Serializable;

import android.content.ContentValues;

public interface LionEntity<DATA extends LionEntity<DATA>> extends IdentifierEntity, CursorFiller, Serializable {

    String getUid();

    int getUsnEntity();

    String getLionName();

    void getLionEntity(StringBuilder sb);

    void fillKeyValue(String key, String value);

    ContentValues getDifference(DATA entity);
}