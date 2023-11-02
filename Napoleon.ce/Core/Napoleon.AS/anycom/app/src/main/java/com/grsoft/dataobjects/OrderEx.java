package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrderEx extends Order{
    public String deliveryType = "";
//    public String card = "";

    public List<OrderCard> cards = new ArrayList<>();
}
