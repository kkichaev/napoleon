package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.FBTransfer;
import com.grsoft.dataobjects.FBTransferCommit;
import com.grsoft.dataobjects.FBTransferReject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.napoleon.FBTransferDetail;
import com.grsoft.napoleon.FBTransferProperties;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FBTransferImpl extends OrderImplBase<FBTransfer> {
    List<DeliveryItem> refItems = new ArrayList<>();

    FBTransferCommit ref = null;
    FBTransferReject reject = null;
    Date checkDate = null;
    boolean allowEdit = false;

    Integer skladIndex = null;
    @Override
    public int getItemValue(Price item) {
        PriceEx pe = (PriceEx) item;
        int skl = getSkladIdx();
        if(skl > 0 && skl <= pe.whQty.size()) {
            return pe.whQty.get(skl - 1).qty;
        }
        return super.getItemValue(item);
    }

    public int getSkladIdx() {
        if(skladIndex == null) {
            String whId = data.direction == FBTransfer.DIRECTION_FROM_ME?  Sklad.mySklad().id:
                    data.whId;

            int idx = 0;
            for (Sklad s : DbReader.fetch(Sklad.class)) {
                if(s.id.equals(whId)) {
                    break;
                }
                idx++;
            }
            skladIndex = idx;
        }
        return skladIndex;
    }

    @Override
    public void open(Context context) {
        FBTransferDetail.open(context, this);
    }

    @Override
    public void editProperties(Context ctx, boolean isOldOrder) {
        FBTransferProperties.open(ctx, this, !isOldOrder);
    }

    @Override
    public CreatableDocument<FBTransfer> createInstance() {
        return new FBTransferImpl();
    }

    public void setRefItems(List<DeliveryItem> ref, boolean allowEdit) {
        this.allowEdit = allowEdit;
        this.refItems = ref;
    }

    @Override
    public void editItem(long itemRowid, Context context) {
        if(!(allowEdit || isEditable()))
            return;

        final PriceImpl pi = new PriceImpl();
        final Price p = pi.getData();
        pi.read(itemRowid);
        pi.close();

        OrderItem oi = (OrderItem) findItem(p.id);
        final boolean useRefItem = !data.isMyRequest();
        final DeliveryItem di = findRefItem(p.id);
        final int qty = useRefItem ? ((di == null) ? 0 : di.qty):
                oi == null ? 0 : oi.qty;

        InputNumberDlg.open(context, new InputNumber() {
            @Override
            public void applayInput(int value, Object... params) {
                if(useRefItem) {
                    if(oi != null) {
                        if (value > oi.qty)
                            value = oi.qty;
                        if(di == null) {
                            DeliveryItem tdi = new DeliveryItem();
                            tdi.id = oi.id;
                            tdi.qty = value;
                            refItems.add(tdi);
                        } else {
                            di.qty = value;
                        }
                    }
                } else {
                    updateQty(pi, value, 0, false);
                    pi.close();
                }
                if (context instanceof DataSetNotify)
                    ((DataSetNotify)context).notifyDataSetChanged();
            }
            @Override
            public long getValue() {  return qty; }
        });
    }

    private DeliveryItem findRefItem(String id) {
        DeliveryItem ret = null;
        for(DeliveryItem di : refItems) {
            if(di.id.equals(id))
                return di;
        }
        return null;
    }

    private void loadReferences() {
        if(checkDate == null || !checkDate.equals(data.created)) {
            checkDate = data.created;

            ref = null;
            String where = "created = " + Long.toString(data.created.getTime());
            List<FBTransferCommit> answ = DbReader.fetch(FBTransferCommit.class, where);

            for (FBTransferCommit d : answ) {
                ref = d;
                break;
            }

            reject = null;
            List<FBTransferReject> rjl = DbReader.fetch(FBTransferReject.class, where);
            for (FBTransferReject ri : rjl) {
                reject = ri;
                break;
            }
        }
    }

    @Override
    public String getDescription(Context context) {
        loadReferences();

        String text = data.stateText(ref, reject);
        if(text.length() > 0)
            return text;
        return super.getDescription(context);
    }
}
