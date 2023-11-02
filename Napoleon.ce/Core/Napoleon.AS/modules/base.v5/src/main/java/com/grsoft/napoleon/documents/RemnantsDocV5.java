package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RemnantsImpl;

public class RemnantsDocV5 extends RemnantsDoc {
    public static void init() {
        instance = new RemnantsDocV5();
    }

    private RemnantsDocV5() {
        super(DOC_NAME, "OrgStock", RemnantsImpl.class);
    }
}
