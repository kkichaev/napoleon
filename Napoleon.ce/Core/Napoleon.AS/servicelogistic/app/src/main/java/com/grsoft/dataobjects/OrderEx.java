package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrderEx extends Order{
    public int retdoc = 0;
    public int writeoff = 0;
    public int avgTime = 0;

    public List<OrderItem> used = new ArrayList<>();
}
