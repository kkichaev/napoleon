package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Bank;
import com.grsoft.dataobjects.BankItem;
import com.grsoft.dataobjects.Incass;
import com.grsoft.napoleon.BankEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import java.util.List;

public class BankImpl extends CreatableDocument<Bank> {
    private static final int CAMERA_ACTIVITY = 0x181212; //1;

    public static boolean contains(Incass incass) {
        String filter = String.format("created >= %d", incass.created.getTime());
        List<Bank> docs = DbReader.fetch(Bank.class, filter);
        for(Bank b : docs) {
            if(b.contains(incass))
                return true;
        }

        return false;
    }

    @Override
    public void open(Context context) {
        BankEdit.open(context, getRowid());
    }

    @Override
    public boolean isEmpty() {
        if(data.items.size() == 0)
            return true;
        if (data.mode == Bank.TERMINAL_MODE)
            return data.terminal == 0 || data.photo.length() == 0;
        return false;
    }

    @Override
    public long sum() {
//        if (data.mode == 0)
//            return data.terminal;
//        else {
            int sum = 0;

            for(BankItem i : data.items)
                sum += i.sum;

            return sum;
//        }
    }

    @Override
    public boolean delete() {
        if (data.photo.length() > 0){
            PicStoreImpl p = new PicStoreImpl();
            p.read("id", data.photo);
            p.delete();
        }

        return super.delete();
    }

    public void preview(Context context) {
        BankEdit.preview(context, getRowid());
    }
}
