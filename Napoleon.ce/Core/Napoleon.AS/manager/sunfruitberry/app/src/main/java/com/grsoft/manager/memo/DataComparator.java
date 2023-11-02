package com.grsoft.manager.memo;

import com.grsoft.dataobjects.AgentManagerMemo;
import com.grsoft.manager.AgentMemo;

import java.util.Comparator;

public class DataComparator implements Comparator<AgentManagerMemo> {

    Ordering ordering;

    public DataComparator(Ordering ordering) {
        this.ordering = ordering;
    }


    int reverse(int res, boolean reverse) {
        if(!reverse || res == 0)
            return res;

        return res > 0 ? -1 : 1;
    }
    @Override
    public int compare(AgentManagerMemo a1, AgentManagerMemo a2) {
        int cmp = 0;
        for(OrderField of : ordering.fields) {
            switch (of.type) {
                case Org:
                    cmp = a1.orgName.compareTo(a2.orgName);
                    break;
                case Topic:
                    cmp = AgentMemo.getTopic(a1.topic).compareTo(AgentMemo.getTopic(a2.topic));
                    break;
                case Status:
                    cmp = a1.state() - a2.state();
                    break;
                case Created:
                    cmp = a1.created.compareTo(a2.created);
                    break;
            }

            cmp = reverse(cmp, of.direction == OrderField.ORDER_DN);
            if(cmp != 0)
                break;
        }
        return cmp;
    }
}