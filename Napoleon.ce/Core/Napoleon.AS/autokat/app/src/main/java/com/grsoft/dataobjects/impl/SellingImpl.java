package com.grsoft.dataobjects.impl;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Selling;
import com.grsoft.dataobjects.StoreData;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.main.SellingPreview;
import com.grsoft.napoleon.script_wizard.BaseFragment;
import com.grsoft.napoleon.script_wizard.Scriptable;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;

public class SellingImpl extends OrderImplBase<Selling> implements Scriptable {
    @Override
    public void editProperties(Context ctx, boolean isOldOrder) {

    }

    public boolean validPayType() { return data.payType.length() > 0;}

    @Override
    public int getItemValue(Price item) {
        if(item != null) {
            for (StoreData sd : ((PriceEx) item).stores) {
                if (sd.bmark == data.bmark) {
                    return sd.qty;
                }
            }
        }
        return 0;
    }

    @Override
    public CreatableDocument<Selling> createInstance() {
        return new SellingImpl();
    }

    @Override
    public void editItem(long itemRowid, Context context) {

    }

    @Override
    public void open(Context context) {

    }

    @Override
    public void initDoc(Context context, GpsCoord gpsCoord, ScriptImpl owner, ScriptDefItem item) {
        initData(context, owner.getId(), gpsCoord);
    }

    @Override
    public BaseFragment getView() {
        return new com.grsoft.napoleon.script_wizard.Selling();
    }

    @Override
    public Fragment getPreview() {
        return new SellingPreview();
    }

    PriceImpl price = new PriceImpl();

    public void updateDoc(OrderItem src){
        if (src == null)
            return;

        OrderItem oi = (OrderItem) findItem(src.id);
        int qty = 0;
        if(oi == null) {
            oi = new OrderItem();
            oi.id = src.id;
            oi.qty = src.qty;
            oi.cost = src.cost;

            data.items.add(oi);
        } else if(oi != src) {
            qty = oi.qty;
            oi.qty = src.qty;
            oi.cost = src.cost;

        }

        qty -= oi.qty;

        price.read("id", oi.id);
        updatePrice(price, qty);
        write();
        close();
    }

    public void removeItem(OrderItem item) {
        data.items.remove(item);
        price.read("id", item.id);
        updatePrice(price, item.qty);
    }
}
