package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.EquQty;
import com.grsoft.dataobjects.Equipment;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.OrderDetail;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.EquipmentDoc;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;

public class EquipmentImpl extends OrderImplBase<Equipment> {
    @Override
    public void open(Context context) {
        OrderDetail.open(context, this);
    }

    public boolean deleteItem(Price item) {
        boolean result = false;
        DataObject ditem = findItem(item.id);

        if(ditem != null){
            data.items.remove(ditem);
            write();
            result = true;
        }

        return result;
    }

    @Override
    protected boolean checkPriceQty() {
        return false;
    }

    @Override
    public void editItem(final long itemRowid, final Context context) {
        InputNumberDlg.open(context, new InputNumber() {

            @Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
            @Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }

            @Override
            public void applayInput(int value, Object... params) {

                if (isExported())
                    return;

                PriceImpl priceImpl = new PriceImpl();
                priceImpl.read(itemRowid);

                boolean refresh = false;
                if( value == 0 && editValue.length() == 0) {
                    refresh = deleteItem(priceImpl.getData());
                } else

                refresh = updateQty(priceImpl, value, 0, false);
                if (refresh && context instanceof DataSetNotify)
                    ((DataSetNotify)context).notifyDataSetChanged();

                priceImpl.close();

                EquipmentDoc.instance().refreshDocSum(data.id);
            }

            @Override
            public int getValue() {
                PriceImpl priceImpl = new PriceImpl();
                priceImpl.read(itemRowid);
                priceImpl.close();
                OrderItem ri = (OrderItem) findItem(priceImpl.data.id);
                int qty = ri == null ? 0 : ri.qty;
                return qty;
            }
        });}

    @Override
    public void editProperties(Context ctx, boolean isOldOrder) {
        Warehouse.open(ctx, this, false);
    }

    @Override
    protected void updatePrice(PriceImpl price, int qty) {

    }

    @Override
    public CreatableDocument<Equipment> createInstance() {
        return new EquipmentImpl();
    }

    @Override
    public int getItemValue(Price item) {
        EquQtyImpl impl = new EquQtyImpl();
        impl.getData().id = getId();
        impl.getData().idItem = item.id;
        impl.read();
        impl.close();

        return impl.getData().qty;
    }

    @Override
    public long getItemSum(Price item) {
        return 0;
    }
}
