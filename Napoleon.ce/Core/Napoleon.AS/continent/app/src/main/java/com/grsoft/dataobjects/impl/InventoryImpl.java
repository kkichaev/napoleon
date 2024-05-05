package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Inventory;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.InvPriceCount;
import com.grsoft.napoleon.InventoryDetail;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InventoryDoc;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;

public class InventoryImpl extends OrderImplBase<Inventory> implements Itemsable {

    @Override
    public void open(Context context) {
        InventoryDetail.open(context, this);
    }

    @Override
    public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
        super.init(context, orgId, gpsCoord);

        DocType.setCurDoc(InventoryDoc.instance());
        Warehouse.open(context, this, false);

        return false;
    }

    @Override
    public void editItem(final long itemRowid, final Context context) {
        InvPriceCount.open(context, itemRowid, (DbObject<Inventory>) this);
    }

    @Override
    public DataObject findItem(String itemId) {

        if (data.items != null)
            for (OrderItem ri : data.items) {
                if (ri.id.compareTo(itemId) == 0)
                    return ri;
            }

        return null;
    }

    @Override
    public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack) {
        Price price = priceImpl.getData();
        OrderItem item = (OrderItem) findItem(price.id);

        boolean needUpdate = true;
        if (item == null) // new item
        {
            if (qty >= 0) {
                Class<? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");

                try {
                    item = (OrderItem) itemClass.newInstance();

                    item.id = price.id;
                    item.qty = qty;
                    data.items.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(updateQtyHandler != null)
                    updateQtyHandler.itemUpdated(item, data, true);
            } else
                needUpdate = false;
        } else {
//			if( qty == 0 )
//				data.items.remove(item);
//			else {
            if (item.qty != qty)
                item.qty = qty;
            else
                needUpdate = false;
//			}

            if(updateQtyHandler != null) {
                updateQtyHandler.itemUpdated(item, data, false);
                needUpdate = true;
            }
        }

        if (needUpdate)
            write();

        return needUpdate;
    }

    public boolean deleteItem(Price item) {
        boolean result = false;
        DataObject ditem = findItem(item.id);

        if (ditem != null) {
            data.items.remove(ditem);
            write();
            result = true;
        }

        return result;
    }

    @Override
    public int getItemColor() {
        return R.color.magneta;
    }

    @Override
    public int getItemValue(Price item) {
        return item.qty;
    }

    @Override
    public int getItemQty(Price item) {
        OrderItem ri = (OrderItem) findItem(item.id);
        return ri == null ? 0 : ri.qty;
    }

    @Override
    public long getItemSum(Price item) {
        return 0;
    }

    @Override
    public int qty() {
        int count = 0;
        for (OrderItem oi : data.items)
            count += oi.qty;
        return count / Consts.QTY_SCALE;
    }

    @Override
    public void editProperties(Context ctx, boolean isOldOrder) {
        // TODO Auto-generated method stub

    }

    @Override
    public CreatableDocument<Inventory> createInstance() {
        return new InventoryImpl();
    }

    @Override
    public String getDescription(Context context) {
        // TODO Auto-generated method stub
        return super.getDescription(context);
    }
}
