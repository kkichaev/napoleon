package com.serviko.dataobjects.priceTree;

import com.serviko.dataobjects.Order;
import com.serviko.dataobjects.OrderItem;
import com.serviko.dataobjects.Price;
import com.serviko.dataobjects.PriceCategory;
import com.serviko.dataobjects.PriceCategoryFolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceTree {
    Folder root = new Folder();

    public Price find(String id) {
        return root.findItem(id);
    }

    public Folder findFolder(String id) {
        return root.findFolder(id);
    }

    public static PriceTree make(List<Price> data, Set<String> manufacturer) {
        PriceTree ret = new PriceTree();
        Map<String, Folder> folders = new HashMap<>();
        folders.put("", ret.root);

        for(Price p : data) {
            if(p.isFolder) continue;

            manufacturer.add(p.suplyer);

            Map<Integer, Folder> cf = new TreeMap<>();

            List<PriceCategoryFolder> pcf = new ArrayList<>();
            for (PriceCategory pc : p.categories) {
                PriceCategoryFolder ce = new PriceCategoryFolder(pc);
                if (ce.isEmpty()) {
                    ce.name = "<>";
                    ce.code = ce.name;
//                    continue;
                }
                pcf.add(ce);
            }
            while(pcf.size() < 2) {
                PriceCategoryFolder ce = new PriceCategoryFolder(new PriceCategory());
                ce.name = "<?>";
                ce.level = pcf.size() + 1;
                ce.code = ce.name + "/" + Integer.toString(ce.level);
                pcf.add(ce);
            }

            Collections.sort(pcf);
            String folderCode = "";
            for(PriceCategoryFolder ce : pcf) {
                ce.code += '\t' + Integer.toString(ce.level);
                if(folderCode.length() > 0)
                    ce.code = folderCode + '\t' + ce.code;
                Folder f = folders.get(ce.code);
                if(f == null) {
                    f = new Folder(ce);
                    folders.put(ce.code, f);
                }
                cf.put(ce.level, f);
                folderCode = ce.code;
            }
            Folder curf = ret.root;
            for(Map.Entry<Integer,Folder> kv : cf.entrySet()) {
                curf = curf.add(kv.getValue());
            }
            curf.add(p);
        }

        return ret;
    }

    public static PriceTree make1(List<Price> data) {
        PriceTree ret = new PriceTree();
        Map<String, Folder> folders = new HashMap<>();
        folders.put("", ret.root);

        Map<String, Price> mapPrice = new HashMap<>();

        for(Price i : data) {
            mapPrice.put(i.id, i);

            Folder parent = folders.get(i.parent);
            if(parent == null) {
                parent = new Folder();
                folders.put(i.parent, parent);
            }

            if(i.isFolder) {
                Folder dest = folders.get(i.id);
                if(dest == null) {
                    dest = new Folder(i);
                    folders.put(i.id, dest);
                } else {
                    dest.setSrc(i);
                }
                parent.add(dest);
            } else {
                parent.add(i);
            }
        }
        for(Folder f : folders.values()) {
            f.sort();
        }
        return ret;
    }

    public PriceTree() {}

    public int size() { return root.size(); }
    public Folder root() { return root; }
}
