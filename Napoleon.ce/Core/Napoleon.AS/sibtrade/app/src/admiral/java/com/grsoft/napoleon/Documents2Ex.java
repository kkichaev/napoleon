package com.grsoft.napoleon;

import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org2Ex;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;

import java.util.List;

public class Documents2Ex extends DocumentsEx{
    @Override
    protected void doCreate() {
        if (DocType.getCurDoc() == ReturnDoc.instance()){
            int type = ((Org2Ex)org.getData()).rtype;

            switch (type){
                case 0:
                    Toast.makeText(this, "Нельзя создать документ возврат для этого типа организаций", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    DbReader reader = new DbReader();
                    List<Order> ord = (List<Order>) reader.fetch(Order.class, String.format("params & 1 == 1 and id='%s'", org.getData().id), "created desc");

                    if (ord.size() == 0){
                        Toast.makeText(this, "Нет отправленного документа заявки, что бы создать возврат", Toast.LENGTH_LONG).show();
                        break;
                    }

                    Order order = ord.get(0);
                    List<Return> ret = (List<Return>) reader.fetch(Return.class, String.format("ordcrt=%d", order.created.getTime()));

                    if (ret.size() > 0){
                        Toast.makeText(this, "Вы уже создали возврат", Toast.LENGTH_LONG).show();
                        break;
                    }

                    reader.close();
                    ReturnImplEx.order = order;
                    super.doCreate();
                    break;
                default:
                    super.doCreate();
            }

        }else
            super.doCreate();
    }
}
