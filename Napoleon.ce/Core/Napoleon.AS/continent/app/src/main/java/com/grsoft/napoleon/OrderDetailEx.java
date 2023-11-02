package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.grsoft.database.DocHandleResultHitching;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.util.MessageBox;

public class OrderDetailEx extends OrderDetail{
    @Override
    protected void setContentView() {
        setContentView(R.layout.orderdetailex);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        findViewById(R.id.btnSendOnline).setOnClickListener(v -> sendOnline());
//    }

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

        new DocumentSender(OrderDetailEx.this, btnSend,
                "OrderOnline", doc,
                doc.getRowid(), this).execute((Void[])null);
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
