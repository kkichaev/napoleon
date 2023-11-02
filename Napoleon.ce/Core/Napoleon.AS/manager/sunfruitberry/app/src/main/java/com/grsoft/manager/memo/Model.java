package com.grsoft.manager.memo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Model extends ViewModel {
    MutableLiveData<Ordering> order = new MutableLiveData<>(new Ordering());

    public LiveData<Ordering> getOrdering() { return order;}

    public void updateOrdering(Ordering src) {
        order.postValue(src);
    }
}
