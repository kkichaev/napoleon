package com.grsoft.manager.memo;

import java.util.ArrayList;
import java.util.List;

public class Ordering {

    public List<OrderField> fields = new ArrayList<>();

    public Ordering() {
        fields.add(new OrderField(OrderField.Type.Created, OrderField.ORDER_DN));
    }

    public Ordering(Ordering src) {
        fields.addAll(src.fields);
    }

    public void update(OrderField.Type type) {
        for(OrderField f : fields) {
            if(f.type == type) {
                if(f.direction == OrderField.ORDER_UP) {
                    f.direction = OrderField.ORDER_DN;
                } else {
                    fields.remove(f);
                }
                return;
            }
        }
        OrderField f = new OrderField(type, OrderField.ORDER_UP);
        fields.add(f);
    }
}
