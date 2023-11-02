package com.serviko.dataobjects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceCategoryFolder extends PriceCategory implements Comparable<PriceCategoryFolder> {
    public int level = 0;
    Pattern pattern = Pattern.compile("(Категория( )*)(\\d+)");

    public PriceCategoryFolder(PriceCategory pc) {
        Matcher m = pattern.matcher(pc.code);
        if(m.matches() && m.groupCount() >= 2) {
            String lvl = m.group(m.groupCount());
            level = Integer.parseInt(lvl);
            code = pc.name + "/" + lvl;
            name = pc.name;
        }
    }

    public boolean isEmpty() { return name.isEmpty(); }

    public Price toPrice() {
        Price ret = new Price();
        ret.name = name;
        ret.code = code;

        return ret;
    }

    @Override
    public int compareTo(PriceCategoryFolder item1) {
        int r = level - item1.level;
        if(r == 0)
            r = code.compareTo(item1.code);
        return r;
    }
}
