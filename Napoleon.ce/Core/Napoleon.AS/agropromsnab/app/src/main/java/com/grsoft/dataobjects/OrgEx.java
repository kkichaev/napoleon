package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
    public String info = "";

    public List<OrgDiscountItem> folderDsc = new ArrayList<OrgDiscountItem>();
    public List<OrgDiscountItem> priceDsc = new ArrayList<OrgDiscountItem>();
}
