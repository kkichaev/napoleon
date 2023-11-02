package com.serviko.dataobjects;

public class BasketItem {
    public Price item = null;
    public int qty = 0; // всегда в штуках
    public boolean packMode = false;

    public float cost = 0;
    public float discount = 0;

    public float sum() {
        return qty * (cost - discount);
    }
}
