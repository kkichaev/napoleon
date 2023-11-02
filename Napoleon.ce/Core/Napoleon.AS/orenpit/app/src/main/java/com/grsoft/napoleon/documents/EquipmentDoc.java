package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.EquipmentImpl;
import com.grsoft.napoleon.R;

public class EquipmentDoc extends OrderDoc{
    public static String OBJ_NAME = "Equipment";
    public static EquipmentDoc instance;

    static public DocType instance() {
        if(instance == null)
            instance = new EquipmentDoc();
        return instance;
    }

    public EquipmentDoc(){
        super(OBJ_NAME, OBJ_NAME, EquipmentImpl.class);
    }

    @Override
    public int getDocTitle() {
        return R.string.equip_doc_title;
    }
}
