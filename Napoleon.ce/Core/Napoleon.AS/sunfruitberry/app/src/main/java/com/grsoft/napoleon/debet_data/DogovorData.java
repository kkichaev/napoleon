package com.grsoft.napoleon.debet_data;

import android.graphics.Color;

import com.grsoft.dataobjects.OrgBalance;
import com.grsoft.napoleon.BalanceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DogovorData  implements Comparable<DogovorData> {
    public String id;
    public String name;
    public int dueDays;
    public int overdueDays;
    public long sum;
    public long overdueSum;
    public long endSum;
    public Date unlockDate;


    public List<DocData> documents = new ArrayList<DocData>();

    void sort() { Collections.sort(documents); }

    public DogovorData(OrgBalance data) {
        id = data.idDog;
        name = data.name;
        sum = 0;
        overdueDays = 0;
        overdueSum = 0;
        dueDays = data.dueDays;
        endSum = data.balance;
        if(data.unlockDate.getTime() > OrgBalance.CHECK_DATE)
            unlockDate = data.unlockDate;
    }

    public void add(DocData doc) {
        sum += doc.sum;
        if(doc.overdueDays > 0) {
            overdueSum += doc.sum;
            if(overdueDays < doc.overdueDays)
                overdueDays = doc.overdueDays;
        }

        documents.add(doc);
    }

    @Override
    public int compareTo(DogovorData another) {
        return name.compareTo(another.name);
    }

    public int getColor() {
        return sum <= 0 ? Color.BLACK : BalanceHelper.getColorFromDueDays(overdueDays);
    }
}
