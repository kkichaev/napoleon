package com.grsoft.napoleon;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends Warehouse{

    @Override
    protected BaseAdapter createListAdapter() {
        FoldersAdapter fa = (FoldersAdapter) super.createListAdapter();
        if(document instanceof OrderImpl) {
            boolean action = false;
            FirmImpl fi = new FirmImpl();
            FirmEx fe = (FirmEx) fi.getData();
            fe.id = ((Order)document.getData()).firmCode;
            fi.read();
            fi.close();
            action = fe.action != 0;

            fa.putFilter(new ActionFilter(action));
        }
        return fa;
    }

    static class ActionFilter extends Filter {
        boolean action;
        public ActionFilter(boolean action) {
            super("ActionFilter" + (action? "1": "0"));
            this.action = action;
        }

        @Override
        public String getWhereStr() {
            return "(action=" + (action ? "1" : "0") + " or action=2)";
        }
    }
}
