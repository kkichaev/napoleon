package com.novotek.dataobjects.priceTree;

import com.novotek.dataobjects.Catalog;
import com.novotek.dataobjects.NameObj;
import com.novotek.dataobjects.Price;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubFolder extends FolderBase {

    public List<Price> items = new ArrayList<>();
    public Map<NameObj, List<Price>> filters = new HashMap<>();

    public SubFolder(Catalog src) {
        super(src);
    }

    public void add(Price p) {
        items.add(p);

        List<Price> fp = filters.get(p.filterType);
        if(fp == null) {
            fp = new ArrayList<>();
            filters.put(p.filterType, fp);
        }
    }
}
