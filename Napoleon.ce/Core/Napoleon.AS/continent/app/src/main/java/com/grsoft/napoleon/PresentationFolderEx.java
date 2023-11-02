package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;

public class PresentationFolderEx extends PresentationFolder{
    public static long PriceFocus = ExtrasConst.INVALID_ROWID;

    @Override
    protected int getLayoutId() {
        return R.layout.presentationfolderex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText edFind = findViewById(R.id.edFind);
        PriceFocus = ExtrasConst.INVALID_ROWID;

        edFind.addTextChangedListener(new FindTextWatcher(edFind, adapter));
        FindOnClickListener fnd  = new FindOnClickListener(edFind, adapter, findViewById(R.id.llFind));
        findViewById(R.id.btnFind).setOnClickListener(fnd);
        findViewById(R.id.btnDelFind).setOnClickListener(view -> edFind.setText(""));
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.present_cell;
    }

    protected int getFolderItemLayoutId() {
        return R.layout.presentation_item;
    }

    @Override
    public View getFolderView(FolderTreeNode node, View convertView) {
        convertView = null;
        return super.getFolderView(node, convertView);
    }

    @Override
    public View getPriceView(PriceTreeNode node, View convertView) {
        convertView = null;
        View v = super.getPriceView(node, convertView);
        TextView tv = (TextView) v.findViewById(R.id.tvInfo);

        int cost = (int)CostStrategy.getInstance(
                (Class<? extends Document<?>>) doc.getClass()).getCostInt(
                        price.getData(), (Document<?>) doc, WarehouseEx.costype);
        int qty = getWhQty((Itemsable)doc, price.getData());
        long sum = (doc instanceof Itemsable) ? ((Itemsable)doc).getItemSum(price.getData()) : 0;
        long packCost = FPOperation.itemMul(cost, price.getData().qtyInPack,Consts.QTY_SCALE);

        tv.setText(Html.fromHtml(String.format(
                "ќстаток: <b>%s</b><br/>÷ена за кг\\шт: <b>%s</b><br/>÷ена за упак: <b>%s</b><br/>”пак: <b>%s</b><br/>—умма: <b>%s</b>",
                Util.IntToScaleStr(qty, Consts.QTY_SCALE),
                Util.IntToScaleStr(cost, Consts.SUM_SCALE),
                Util.IntToScaleStr(packCost, Consts.SUM_SCALE),
                Util.IntToScaleStr(price.getData().qtyInPack, Consts.QTY_SCALE, Util.DEC_DELIM, true),
                Util.IntToScaleStr(sum, Consts.SUM_SCALE)
        )));

        tv = (TextView)v.findViewById(R.id.tvItem);
        tv.setBackgroundColor(Color.WHITE);

        return v;
    }

    @Override
    public void editItem(long rowid) {
        PriceCount.PriceMover = new PresentationMover();
        if (doc instanceof Itemsable)
            ((Itemsable)doc).editItem(rowid, this);
        else
            super.editItem(rowid);
    }

    @Override
    public void applySearchFilter(String value) {
        if(buildingProcess)
            return;

        if(value.length() == 0) {
            adapter.resetFilter();
            return;
        }

        PriceTextFilter filter = (PriceTextFilter) adapter.getFilter(PriceTextFilter.NAME);

        if (filter == null) {
            filter = new PriceTextFilter();
            adapter.putFilter(filter);
        }

        filter.srchFieldName = PriceTextFilter.SRCH_NAME_FLD;
        adapter.setExpanded(true);
        filter.build(adapter, value, false);
        adapter.buildSet(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PriceFocus != ExtrasConst.INVALID_ROWID) {
            PriceImpl p = new PriceImpl();
            p.read(PriceFocus);
            p.close();
            adapter.setFolder(p.getData().folderID);
        }
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);

        if (PriceFocus != ExtrasConst.INVALID_ROWID) {
            int pos = 0;
            boolean found = false;

            for (;pos< adapter.getCount(); pos++)
            {
                Object node = adapter.getItem(pos);

                if (node instanceof PriceTreeNode){
                    if (((PriceTreeNode) node).getRowid() == PriceFocus) {
                        found = true;
                        break;
                    }
                }
            }

            if (found) {
                final int to = pos;
                gvPresentation.post(()->gvPresentation.smoothScrollToPosition(to));
            }

            PriceFocus = ExtrasConst.INVALID_ROWID;
        }
    }
}
