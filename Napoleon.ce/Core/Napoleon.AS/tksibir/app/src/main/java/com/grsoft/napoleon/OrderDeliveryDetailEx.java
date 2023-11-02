package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.grsoft.database.DocHandleResultHitching;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderAnswer;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderAnswerInpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MessageBox;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
    OrderAnswer answer = null;

    static public void open(Context context, OrderImplBase<? extends Order> order) {
        Intent i = new Intent(context, OrderDeliveryDetailEx.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void loadItems() {
        OrderAnswerInpl oai = new OrderAnswerInpl();
        if(oai.read("created", doc.getData().created)) {
            answer = oai.getData();
            for(OrderItem oi : oai.getData().items) {
                DeliveryItem di = new DeliveryItem();
                di.id = oi.id;
                di.qty = oi.qty;
                di.sum = (int)((long)oi.qty * oi.cost / Consts.SUM_SCALE);

                items.add(di);
            }
        } else {
            super.loadItems();
        }
//        btnSend.setVisibility(View.GONE);
    }

    @Override
    protected String getOrgText(Org o) {
        String ret =  super.getOrgText(o);
        if(doc.getData().podRemark.length() > 0) {
            ret += "<br/>" + doc.getData().podRemark;
        }
        return ret;
    }

    @Override
    public void postSendExecute(boolean result) {
        if(DocHandleResultHitching.Result.isFail()) {
            MessageBox.show(this, "Ошибка при передаче", DocHandleResultHitching.Result.message);
        }  else {
            loadItems();
            MessageBox.show(this, "Отправка заказа", answer.remark);
        }
    }
}
