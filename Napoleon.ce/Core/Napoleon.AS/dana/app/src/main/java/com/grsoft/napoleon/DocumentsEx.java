package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

public class DocumentsEx extends Documents {
    @Override
    protected String orgInfo(Org o) {
        String text = super.orgInfo(o);;
        String license = ((OrgEx)o).license;
        if(license.length() > 0)
            text += "<br/>лицензия: " + license;
        return text;
    }
}
