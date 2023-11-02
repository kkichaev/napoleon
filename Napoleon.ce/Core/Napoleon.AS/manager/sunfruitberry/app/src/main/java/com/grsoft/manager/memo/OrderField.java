package com.grsoft.manager.memo;

public class OrderField {
    public static final int ORDER_UP = 1;
    public static final int ORDER_DN = 2;

    public enum Type {
        Topic,
        Status,
        Created,
        Org
    }

    public Type type;
    public int direction;

    public OrderField(Type type, int direction) {
        this.type = type;
        this.direction = direction;
    }
}
