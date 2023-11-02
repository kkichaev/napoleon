package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.grsoft.database.DocHandleResultHitching;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderAnswer;
import com.grsoft.dataobjects.OrderAnswerItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderAnswerInpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocumentSender;
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

    void loadAnswer() {
        if(answer == null) {
            OrderAnswerInpl oai = new OrderAnswerInpl();
            if (oai.read("created", doc.getData().created)) {
                answer = oai.getData();
            }
        }
    }

    @Override
    protected void loadItems() {
        loadAnswer();
        if(answer != null) {
            for(OrderAnswerItem oi : answer.items) {
                DeliveryItem di = new DeliveryItem();
                di.id = oi.id;
                di.qty = oi.qty;
                di.sum = oi.sum;

                items.add(di);
            }
        } else {
            super.loadItems();
        }
        btnSend.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem mi = menu.findItem(OptionsMenuHelper.MNU_SEND_ID);
        mi.setTitle(R.string.send_online);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == OptionsMenuHelper.MNU_SEND_ID) {
            sendOnline();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendOnline() {
        ((OrderImplEx)doc).markSendOnline();

        new DocumentSender(OrderDeliveryDetailEx.this, btnSend,
                "OrderOnline", doc,
                doc.getRowid(), this).execute((Void[])null);
    }

    @Override
    protected String getOrgText(Org o) {
        loadAnswer();
        String txt = super.getOrgText(o);
        if(answer != null && answer.remark.length() > 0) {
            txt += "<br/>" + answer.remark;
        }
        return txt;
    }

    @Override
    public void postSendExecute(boolean result) {
        if(!result) {
            if(DocHandleResultHitching.Result.isFail()) {
                MessageBox.show(this, "Ошибка при передаче", DocHandleResultHitching.Result.message);
            }
        } else {
            OrderDeliveryDetailEx.open(this, doc);
            finish();
        }
    }
}
