package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@TableInfo(name="GoodsAnalogs", keyFields = "id")
@ServerInfo(name = "GoodsAnalogs")
public class GoodsAnalogs extends DataObject {
    public String id = "";
    public int base = 0;

    static public List<Set<String>> analogs() {
        Map<Integer, Set<String>> data = new HashMap<>();

        for(GoodsAnalogs ga : DbReader.fetch(GoodsAnalogs.class)) {
            Set<String> set = data.get(ga.base);
            if(set == null) {
                set = new HashSet<>();
                data.put(ga.base, set);
            }
            set.add(ga.id);
        }

        return new ArrayList<>(data.values());
    }
}
