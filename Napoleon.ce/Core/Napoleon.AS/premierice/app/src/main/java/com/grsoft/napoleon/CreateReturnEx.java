package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;

public class CreateReturnEx extends CreateReturn {
    @Override
    protected void init(Return r, Org data) {
        super.init(r, data);
        ((ReturnEx)r).route = ((OrgEx)data).route;
    }
}
