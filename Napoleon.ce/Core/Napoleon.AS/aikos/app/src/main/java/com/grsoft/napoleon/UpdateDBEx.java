package com.grsoft.napoleon;

import android.widget.CheckBox;

import com.grsoft.database.DivisionStockHitching;
import com.grsoft.dataobjects.Brand;
import com.grsoft.database.DbReader;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.PriceCostHitching;
import com.grsoft.database.PriceTypeHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.StoreHitching;
import com.grsoft.database.StoreQtyHitching;
import com.grsoft.dataobjects.Supplier;
import com.grsoft.dataobjects.Balance;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.RemnantsEx;
import com.grsoft.napoleon.documents.StockDocEx;
import com.grsoft.network.exception.RuntimeException;

import java.util.List;

public class UpdateDBEx extends UpdateDB {

    boolean needRestoreStock() {
        boolean clearing = ((CheckBox)findViewById(R.id.cbRecreateStory)).isChecked();
        if(clearing)
            return true;

        DbReader r = new DbReader();
        RemnantsEx doc = new RemnantsEx();
        boolean haveDocs = r.select(doc, doc.getTableName(), "");
        return !haveDocs;
    }

    @Override
    protected List<Hitching> getGenDataHitchings() throws RuntimeException {
        List<Hitching> ret = super.getGenDataHitchings();

        if( needRestoreStock() ) {
            ret.add(new DocumentRestore(StockDocEx.instance(), StockDocEx.instance().getObjectName(), 4));
        }
        ret.add(new StoreHitching());
        ret.add(new PriceTypeHitching());
        ret.add(new PriceCostHitching());
        ret.add(new StoreQtyHitching());
        ret.add(new RcvNewHitching(Firm.class));
        ret.add(new RcvNewHitching(Supplier.class));
        ret.add(new RcvNewHitching(Brand.class));
        ret.add(new DivisionStockHitching());

        return ret;
    }

    @Override
    protected List<Hitching> getDebetHitching() {
        List<Hitching> ret =  super.getDebetHitching();
        ret.add(new RcvNewHitching(Balance.class));
        return ret;
    }

    @Override
    protected void postSync(Boolean result) {
        super.postSync(result);
        if(result)
            CostStrategyEx.resetCache();
    }
}
