package com.grsoft.dataobjects;

import com.grsoft.util.Util;

public class IncassEx extends Incass {
    public String number = "";
    public String dogId = "";

    public void init(PaymentEx src) {
        id = src.id;
        dogId = src.dogId;
        number = src.number;
        sum = (int) src.sum;

        date = Util.getDate();
        created = Util.getDateTime();

        params = 0;
    }
}
