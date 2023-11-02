package com.grsoft.napoleon.price;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.Itemsable;

public class Model extends ViewModel {
    public OrderImplBase<?> doc;
    public PriceEx editItem;

    MutableLiveData<OrderItem> orderItem = new MutableLiveData<>();
    public LiveData<OrderItem> getOrderItem() { return orderItem; }

    public  void setOrderItem(OrderItem src) { orderItem.postValue(src);}
}
