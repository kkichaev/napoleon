package com.grsoft.dataobjects.impl;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Purchase;
import com.grsoft.dataobjects.PurchaseItem;
import com.grsoft.dataobjects.PurchaseTemplate;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.main.PurchasePreview;
import com.grsoft.napoleon.script_wizard.BaseFragment;
import com.grsoft.napoleon.script_wizard.Scriptable;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;

import java.util.List;

public class PurchaseImpl extends OrderImplBase<Purchase> implements Scriptable {
    @Override
    public void open(Context context) {

    }

    @Override
    public void editProperties(Context ctx, boolean isOldOrder) {

    }

    @Override
    public CreatableDocument<Purchase> createInstance() {
        return new PurchaseImpl();
    }

    @Override
    public void editItem(long itemRowid, Context context) {

    }

    public int weight() {
        int tq = 0;
        for(OrderItem oi : data.items)
            tq += ((PurchaseItem)oi).weight;
        return tq;
    }

    @Override
    public void initDoc(Context context, GpsCoord gpsCoord, ScriptImpl owner, ScriptDefItem item) {
        initData(context, owner.getId(), gpsCoord);

        List<PurchaseTemplate> ti = DbReader.fetch(PurchaseTemplate.class, "", "pos");
        for(PurchaseTemplate src : ti) {
            PurchaseItem oi = new PurchaseItem();
            oi.id = src.id;
            data.items.add(oi);
        }
    }

    @Override
    public BaseFragment getView() {
        return new com.grsoft.napoleon.script_wizard.Purchase();
    }

    @Override
    public Fragment getPreview() {
        return new PurchasePreview();
    }
}
