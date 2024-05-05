package com.grsoft.napoleon;

import android.os.Bundle;

import com.grsoft.database.DocHandleResultHitching;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderAnswer;
import com.grsoft.dataobjects.impl.OrderAnswerInpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.util.MessageBox;

public class OrderDetailEx extends OrderDetail{

    @Override
    public void send() {
        new DocumentSender(this, btnSend,
                docType.getObjectName(), doc,
                doc.getRowid(), this).execute((Void[])null);
    }

    @Override
    public void postSendExecute(boolean result) {
////        if(!result) {
        if(DocHandleResultHitching.Result.isFail()) {
            MessageBox.show(this, "Ошибка при передаче", DocHandleResultHitching.Result.message);
        }  else {
            OrderAnswerInpl oai = new OrderAnswerInpl();
            if(oai.read("created", doc.getData().created)) {
                MessageBox.show(this, "Отправка заказа", oai.getData().remark, dialog -> {
                    OrderDeliveryDetailEx.open(this, doc);
                    finish();
                }, false);
            }
        }

    }
}
