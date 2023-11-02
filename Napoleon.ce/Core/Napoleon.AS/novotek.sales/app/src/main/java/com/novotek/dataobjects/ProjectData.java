package com.novotek.dataobjects;


import com.novotek.dataobjects.priceTree.PriceTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectData {
    static List<Partner> partners = new ArrayList<>();
    static Partner current = null;

    public static Map<NameObj, Brand> brands = new HashMap<>();
    public static CommonInfo commonInfo = new CommonInfo();

    public static List<Partner> partners() { return partners;}

    public static void setPartners(List<Partner> newPartners) {
        current = null;
        partners = newPartners;
    }

    public static Price findBarcode(String bc, Partner current) {
        if(current != null) {
            PriceTree pt = current.getPrice();
            return pt.findBarcode(bc);
        }
        return null;
    }

    public static Partner getCurrent() { return current; }
    public static void setCurrent(Partner p) {current = p;}
}
