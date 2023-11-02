package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.TextView;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
    protected int getContentViewId() { return R.layout.pricecount_new_ex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(DocType.getCurDoc() == OrderDoc.instance()){
            TextView tv = findViewById(R.id.tvQty2);
            tv.setText(Util.IntToScaleStr(((OrderImplEx)document).getItem2Value(price.getData()), Consts.QTY_SCALE));
        }
    }
}
