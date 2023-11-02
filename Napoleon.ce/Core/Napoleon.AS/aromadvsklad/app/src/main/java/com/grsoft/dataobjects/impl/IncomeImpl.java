package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.CheckInvoice;
import com.grsoft.dataobjects.CheckItem;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.IncomeItem;
import com.grsoft.napoleon.BarcodeData;
import com.grsoft.napoleon.IncomeEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.gps.GPSUtilNew;

public class IncomeImpl extends CreatableDocument<Income> {
    @Override
    public void open(Context context) {
        IncomeEdit.open(context, this);
    }

    String incomeCode(String bc, boolean isBox) {
        int idx = bc.indexOf("01");
        if(idx < 0)
            return "";
        String code = bc.substring(2, 16);
        if(isBox) {
            idx = bc.indexOf("240");
            if(idx < 0)
                return "";
            return code + bc.substring(idx + 3, idx + 11);
        }
        idx = bc.indexOf("8005");
        if(idx < 0)
            return "";

        return code +  bc.substring(idx + 4, idx + 10);
    }

    public boolean addBarcode(String barcode) {
        BarcodeData bd = new BarcodeData(barcode);
        if(bd.isItemCode) {
            for(IncomeItem ii : data.items) {
                if(ii.code.equals(barcode)) {
                    ii.have = 1;
                    write();
                    return true;
                }
            }
        } else {
            String checkbc = incomeCode(barcode, bd.isBox);
            for(IncomeItem ii : data.items) {
                if(incomeCode(ii.code, bd.isBox).equals(checkbc)) {
                    ii.have = 1;
                    ii.incomeCode = barcode;
                    write();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGood() {
        for(IncomeItem ii : data.items) {
            if (ii.have == 0)
                return false;
        }
        return true;
    }

    public int haveCount() {
        int count = 0;
        for(IncomeItem ii : data.items) {
            if (ii.have != 0)
                count++;
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        for(IncomeItem ii : data.items) {
            if (ii.have != 0)
                return false;
        }
        return true;
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    public void initFrom(Context context, CheckInvoice src) {
        super.initSilent(context, "", GPSUtilNew.getLastKnownLocation());

        for(CheckItem ci : src.items) {
            IncomeItem ii = new IncomeItem();
            ii.code = ci.code;
            data.items.add(ii);
        }
        data.number = src.number;
        data.date = src.date;
        write();
    }
}
