package com.grsoft.aceteam.grass;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Order;
    import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napmobile.R;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

import java.util.Date;
import java.util.List;

public class Model extends ViewModel {
    static public boolean TEST = true;

    OrderImpl order = null;
    String noItems = "";

    int changeIndex = 0;

    public void onScanBC(String bc) {
        Thread t = new Thread(() -> {
            Config cfg = (CfgNpl) ConfigManager.getConfig();
            ServerHelper.getGoods( bc, (result, error) -> {
                if (result != null) {
                    if(result.size() > 0) {
                        curItem.postValue(result.get(0));
                    } else {
                        scanError.postValue(noItems);
                    }
                } else if(error != null) {
                    scanError.postValue(error);
                }
            });
        });
        t.start();
    }

    public void onItemSelected(int position) {
        Order doc = order.getData();
        if(doc.items.size() > position) {
            loadPriceData(doc.items.get(position).id);
        }
    }

    MutableLiveData<String> scanError = new MutableLiveData<>();
    MutableLiveData<PriceEx> curItem = new MutableLiveData<>();
    MutableLiveData<Integer> orderChanged = new MutableLiveData<>();
    MutableLiveData<String> exchangeError = new MutableLiveData<>();
    String orderNumber = null;

    LiveData<String> getExchangeError() { return exchangeError;}

    LiveData<String> pmScanError() { return scanError; }
    public LiveData<PriceEx> currentItem() { return curItem; }
    public LiveData<Integer> onOrderChanged() { return orderChanged;}

    void loadPriceData(String id) {
        String filter = String.format("id='%s'", id);
        for(PriceEx p : DbReader.fetch(PriceEx.class)) {
            curItem.postValue(p);
        }
    }

    public void clearItem() {
        curItem.postValue(null);
    }

    public void onCreate(Main main) {
        noItems = main.getString(R.string.no_items_found);
    }

    public Order prepareOrder() {
        if(order == null) {
            OrderImpl co = null;
            Date curDate = Util.getDate();
            String where = String.format("created > %d and params=0", curDate.getTime());
            for (Document<?> d : OrderDoc.instance().docList(null, "created desc", where)) {
                co = (OrderImpl) d;
                break;
            }
            if (co == null) {
                co = new OrderImpl();
                co.initSilent("", new GpsCoord(0, 0, curDate.getTime()));
            }

            order = co;
            orderChanged.postValue(changeIndex++);
        }
        return order.getData();
    }

    public OrderItemEx getItem(PriceEx item) {
        OrderItemEx oi = (OrderItemEx) order.findItem(item.id);
        if(oi == null) {
            oi = new OrderItemEx();
            oi.id = item.id;
            oi.qty = Consts.QTY_SCALE;
            oi.qtyPack = Consts.QTY_SCALE;

            if(item.units.size() > 0) {
                PriceUnit pu = item.units.get(0);
                oi.unit = pu.id;
                oi.qty = pu.inpack;
            }
            if(item.cost.size() > 0) {
                oi.cost = item.cost.get(0).cost;
                oi.costItem = oi.cost;
            }
        }

        return oi;
    }

    public void updateItem(OrderItemEx orderItem) {
        Order doc = order.getData();
        if(orderItem.qty == 0) {
            doc.items.remove(orderItem);
        } else if(!doc.items.contains(orderItem)) {
            doc.items.add(orderItem);
        }
        order.write();
        orderChanged.postValue(changeIndex++);
    }

    public void setOrder(OrderImpl doc) {
        order = doc;
        orderChanged.postValue(changeIndex++);
    }

    public void removeIem(OrderItemEx oie) {
        order.getData().items.remove(oie);
        order.write();
        orderChanged.postValue(changeIndex++);
    }

    public void insetItem(OrderItemEx oie, int pos) {
        order.getData().items.add(pos, oie);
        order.write();
        orderChanged.postValue(changeIndex++);
    }

    public void setOrderNumber(String number) {
        orderNumber = number;
    }

    public String getOrderNumber() {
        String res = orderNumber;
        orderNumber = null;
        return res;
    }

    public void setExchangeError(String error) {
        exchangeError.postValue(error);
    }

    public interface OrderNumberHandler {
        void complete();
    }
    public void sendOrder(OrderNumberHandler handler) {
        ServerHelper.requestNumber((number, error) -> {
            if(number == null) {
                if (error != null) {
                    setExchangeError(error);
                }
                return;
            }
            order.getData().number = number;
            order.write();

            ServerHelper.sendOrder(order, (sent, e) -> {
                if(sent) {
                    order.setExported(true);
                    orderNumber = number;
                    handler.complete();
                } else if(e != null) {
                    setExchangeError(e);
                }
            });

        });
    }
}
