package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.BankItem;

public class IncassImplEx extends IncassDebDistrImpl {
    @Override
    public boolean delete() {
        if(BankImpl.contains(data))
            return true;
        return super.delete();
    }

    public boolean forceDelete(){
        return super.delete();
    }
}
