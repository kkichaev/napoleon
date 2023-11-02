package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderItemEx extends OrderItem {
    public String uid = UUID.randomUUID().toString().replace("-", "");

    public List<OrderQtyItem> qtys = new ArrayList<>();
}
