package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="bank", keyFields = "created")
public class Bank extends CreateDocDataObject{
    public static int TERMINAL_MODE = 0;
    public static int CASH_MODE = 1;

    public String dogovor = "";
    public List<BankItem> items = new ArrayList<>();
    public int mode = CASH_MODE;

    @Scale(value= Consts.SUM_SCALE)
    public int terminal = 0;
    public String photo;

    public boolean contains(Incass doc) {
        for(BankItem bi : items) {
            if(bi.incass.equals(doc.created))
                return true;
        }

        return false;
    }
}
