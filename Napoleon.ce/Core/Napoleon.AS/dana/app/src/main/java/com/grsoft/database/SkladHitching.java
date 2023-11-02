package com.grsoft.database;

import com.grsoft.dataobjects.Sklad;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SkladHitching extends RcvNewHitching {
    List<Sklad> sklads = new ArrayList<>();

    public SkladHitching() {
        super(Sklad.class);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        Sklad s = (Sklad) rawObject.createDataObject(dataObject);
        sklads.add(s);
        //super.onRead(rawObject);
    }

    @Override
    public void onEnd() {
        Collections.sort(sklads, (sklad1, sklad2) -> {
            int val = sklad1.index - sklad2.index;
            if(val == 0)
                val = sklad1.name.compareTo(sklad2.name);
            return val;
        });

        int ctr = 0;
        for(Sklad s : sklads) {
            s.index = ctr++;
            dbProxy.insertRecord(s);
        }
        super.onEnd();
    }
}
