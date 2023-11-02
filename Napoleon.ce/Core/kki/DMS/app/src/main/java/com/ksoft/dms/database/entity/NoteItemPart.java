package com.ksoft.dms.database.entity;

import java.io.Serializable;

public class NoteItemPart implements Serializable {
    public static final int TEXT = 0;
    public static final int FILE = 1;

    public String id;
    public String note_item_id;
    public String text;
    public int pos;
    public int type;


}
