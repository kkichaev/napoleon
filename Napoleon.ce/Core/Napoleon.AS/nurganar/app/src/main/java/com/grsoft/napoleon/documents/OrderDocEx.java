package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;

public class OrderDocEx extends  OrderDoc implements CreateByScriptDef {
    @Override
    public Document<?> create(ScriptDef def, ScriptDefItem item) {
        OrderImpl doc = (OrderImpl) create();

        if (doc != null){
            OrderEx order = (OrderEx) doc.getData();

            order.filter = ((ScriptDefEx)def).filter;

            if (order.filter == 1)
                order.tareType = ((ScriptDefEx)def).tareType;
            else
                order.tareType = 1;
        }

        return doc;
    }
}
