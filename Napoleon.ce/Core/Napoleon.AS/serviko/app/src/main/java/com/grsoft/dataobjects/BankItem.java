package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

public class BankItem extends DataObject{
    public Date incass;

    @Scale(value = Consts.SUM_SCALE)
    public int sum;
}
