package com.grsoft.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.MOrderImplBase;
import com.grsoft.manager.documents.MContractDoc;

public class ContractDetail extends OrderDetail{
    public static void open(Context context, MOrderImplBase<? extends Order> doc) {
        Intent intent = new Intent(context, ContractDetail.class);

        intent.putExtra(DocDetailDecorator.DOCTYPE, doc.getClass());
        intent.putExtra(DocDetailDecorator.ROWID, doc.getRowid());

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for(int id : new int[]{R.id.tvCost, R.id.tvSum})
            findViewById(id).setVisibility(View.GONE);
    }

    @Override
    protected String priceName(String id) {
        String result = String.format("товар с кодом<%s>", id);
        final String ID_STR = "id";

        if(price.read(ID_STR, id ))
            result = price.getData().name + " " + ((PriceEx)price.getData()).group;

        price.close();

        return result;
    }

    @Override
    public String getTitle(CreateDocDataObject exdata) {
        return getString(MContractDoc.instance().getDocTitle());
    }

    @Override
    protected View getItemView(View view, OrderItem item) {
        View res = super.getItemView(view, item);
        res.findViewById(R.id.tvCost).setVisibility(View.GONE);

        return res;
    }
}
