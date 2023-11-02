package com.grsoft.napoleon;

import android.view.View;
import android.widget.BaseAdapter;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Distrib;
import com.grsoft.dataobjects.DistribItem;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.GoodsHelper;
import com.grsoft.dataobjects.impl.GoodsMatrixImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BarcodePrice extends WarehouseEx {

    @Override
    protected BaseAdapter createListAdapter() {
        FoldersAdapter res = new FoldersAdapter(this);

        OrgImpl org = new OrgImpl();
        org.read("id", getIntent().getStringExtra(ExtrasConst.ORG_ID_STR));
        String omtx = ((OrgEx)org.getData()).goodsMatrix;
        GoodsMatrixImpl matrix = new GoodsMatrixImpl();

        PriceImpl pi = new PriceImpl();
        PriceEx pe = (PriceEx)pi.getData();

        List<String> items = new ArrayList<>();

        if(omtx.trim().length() > 0 && matrix.read("name", omtx)){
            for(MatrixItem mi : matrix.getData().items) {
                pe.id = mi.id;
                if(pi.read() && pe.my > 0)
                    items.add(pe.id);
            }

            res.putFilter(new Filter("BM") {
                @Override
                public boolean inset(long priceRowID, String id) {
                    return items.contains(id);
                }
            });
        }else
            res.putFilter(new Filter("MY") {
                @Override
                public String getWhereStr() {
                    return "my=1 and fid in (select id from GroupGoods)";
                }
            });
        return res;
    }

    @Override
    public void editItem(long rowid) {
        price.read(rowid, false);
        BarcodeView.open(this, ((PriceEx)price.getData()).barcode);
    }

    @Override
    protected void postAdapterInit() {
        adapter.buildSet(true);
    }

    @Override
    public View getPriceView(PriceTreeNode node, View convertView) {
        View res = super.getPriceView(node, convertView);
        res.findViewById(R.id.tvClmn2).setVisibility(View.GONE);
        return res;
    }

    protected int getItemLayoutId() {
        return R.layout.priceitemrow;
    }
}
