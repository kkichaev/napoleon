package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.CMonitoring;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.MonitoringDetail;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CMonitoringDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;

public class MonitoringImpl extends CreatableDocument<CMonitoring>
        implements Itemsable {

    @Override
    public void open(Context context) {
        MonitoringDetail.open(context, this);
    }

    @Override
    public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
        super.init(context, orgId, gpsCoord);
        Warehouse.open(context, this, false);
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

                if( value == 0 && editValue.length() == 0)
                    refresh = deleteItem(priceImpl.getData());
                else
                    refresh = updateQty(priceImpl, 0, value, false);

                if (refresh && context instanceof DataSetNotify)
                    ((DataSetNotify)context).notifyDataSetChanged();

                priceImpl.close();

                CMonitoringDoc.instance().refreshDocSum(data.id);
            }

            @Override
            public int getValue() {
                PriceImpl priceImpl = new PriceImpl();
                priceImpl.read(itemRowid);
                priceImpl.close();
                OrderItem i = (OrderItem) findItem(priceImpl.data.id);
                int cost = i == null ? 0 : i.cost;

                return cost;
            }
        }, Consts.SUM_SCALE, true, context.getString(R.string.input_value), false);
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
    public DataObject findItem(String itemId) {
        for(OrderItem i : data.items)
            if (i.id.equals(itemId))
                return i;
        return null;
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
        return item.qty;
    }

    @Override
    public long getItemSum(Price item) {
        return 0;
    }

    @Override
    public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
        Price price = priceImpl.getData();
        OrderItem item = (OrderItem) findItem(price.id);

        boolean needUpdate = true;
        if( item == null ) // new item
        {
            if( cost >= 0 )
            {
                Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");

                try {
                    item = (OrderItem) itemClass.newInstance();

                    item.id = price.id;
                    item.cost = cost;
                    data.items.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
                needUpdate = false;
        } else
        {
            if( item.cost != cost )
                item.cost = cost;
            else
                needUpdate = false;
        }

        if( needUpdate )
            write();

        return needUpdate;
    }

    @Override
    public boolean isEmpty() {
        return data.items.size() == 0;
    }
}
