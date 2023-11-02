package com.serviko.sales.main_views.order_filter;

import com.serviko.dataobjects.Order;
import com.serviko.dataobjects.OrderItem;
import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.Price;
import com.serviko.dataobjects.priceTree.PriceTree;
import com.serviko.sales.main_views.Filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;

public class OrderFilter extends Filter {
    public boolean payed = false;
    public boolean unpayed = false;
    public boolean active = false;
    public boolean done = false;

    public List<String> contracts = new ArrayList<>();
    Map<String, String> priceToContract = new HashMap<>();

    public boolean inSet(Order order) {
        boolean checkPay = payed ^ unpayed;
        boolean checkActive = active ^ done;

        if(checkPay && ((payed & order.isUnpayed()) || (unpayed && !order.isUnpayed())))
            return false;

        if(checkActive && ((active & order.state != Order.ORDER_STATE_ACTIVE) || (done && order.state == Order.ORDER_STATE_ACTIVE)))
            return false;

        if(contracts.size() != 0) {
            return checkContract(order);
        }
        return true;
    }

    boolean checkContract(Order order) {
        boolean haveContracts = false;

        PriceTree pt = PartnerList.getCurrent().getPrice();
        for(OrderItem oi : order.items) {
            String ctrId = priceToContract.get(oi.id);
            if(ctrId == null) {
                Price p = pt.find(oi.id);
                if(p != null) {
                    ctrId = p.contract;
                } else {
                    ctrId = "";
                }
                priceToContract.put(oi.id, ctrId);
            }
            if(contracts.contains(ctrId)) {
                haveContracts = true;
                break;
            }
        }

        return haveContracts;
    }
}
