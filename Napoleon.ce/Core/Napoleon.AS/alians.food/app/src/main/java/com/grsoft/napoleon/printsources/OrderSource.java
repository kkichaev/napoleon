package com.grsoft.napoleon.printsources;

import android.content.Context;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.modules.print.DataSource;

public class OrderSource extends DataSource {
    protected OrderPrint data;

    public OrderSource(Order order){
        data = new OrderPrint(order);
    }

    @Override
    public void startPage() { data.init(); }

    @Override
    public void init(Context context, int res) {
        data.initSource(context, res);
    }

    @Override
    public boolean getValue(StringBuilder value, String name, String format) {
        if (value != null && name != null){
            value.setLength(0);
            ConfigImpl config = new ConfigImpl();

            return config.getValue(value, name) ||
                    data.getSupplSorce().getValue(value, name, format) ||
                    data.getValue(value, name, format);
        }else
            return false;
    }

    @Override
    public DataSource getObject(String name) {
        return data.getItems();
    }
}
