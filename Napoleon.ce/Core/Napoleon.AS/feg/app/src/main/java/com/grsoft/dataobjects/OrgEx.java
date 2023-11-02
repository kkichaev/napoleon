package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx  extends  Org {
    public String ido = "";
    public int useTax = 0;
    public List<OrgItemDiscount> itemCost = new ArrayList<>();
    public List<OrgDiscount> discounts = new ArrayList<>();
}
