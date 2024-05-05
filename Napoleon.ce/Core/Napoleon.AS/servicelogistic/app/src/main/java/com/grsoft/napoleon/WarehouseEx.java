package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.OrgAsmMatrix;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixItemsAdapter;
import com.grsoft.util.WarehouseAdapter;

import java.util.ArrayList;
import java.util.List;

public class WarehouseEx extends Warehouse {

    Boolean needCheckItems = null;
    List<MatrixItem> assortment = new ArrayList<>();
    private boolean writeTime;
    private long startTime = -1;
    private boolean notStopTimer = false;

    @Override
    protected int getLayoutId() {
        return R.layout.warehouseex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            FoldersAdapter.resetCache();
            needCheckItems = false;

            if (writeTime){
                writeDocTime();
                writeTime = false;
            }

            WarehouseAdapter a = (WarehouseAdapter) super.createListAdapter();
            v.setVisibility(View.GONE);
            applyAdapter(a, adapter.isExpanded(), false);
        });
    }

    @Override
    protected void onMatrixSelected(String name) {
        if(needCheckItems != null && !needCheckItems)
            super.onMatrixSelected(name);
        else
            showCheckAlert();
    }

    void showCheckAlert() {
        Toast.makeText(this, "Необходимо отметить все товары", Toast.LENGTH_LONG).show();
    }

    @Override
    public void setColor(TextView textView, Price price) {
        super.setColor(textView, price);
        if(needCheckItems != null && needCheckItems) {
            OrderImplEx o = (OrderImplEx) document;
            if(o.findItem(price.id) == null && o.itemChecked(price.id)) {
                textView.setTextColor(getResources().getColor(((Itemsable) document).getItemColor()));
            }
        }
    }

    @Override
    protected BaseAdapter createListAdapter() {
        OrgImpl org = new OrgImpl();
        org.read("id", document.getId());
        assortment = new ArrayList<>();

        PriceImpl pi = new PriceImpl();
        for(MatrixItem mi : ((OrgEx)org.getData()).matrix) {
            pi.getData().id = mi.id;
            if(pi.read()) {
                assortment.add(mi);
            }
        }
//        assortment = ((OrgEx)org.getData()).matrix;

        if(needCheckItems == null) {
            needCheckItems = false;
            if(document instanceof OrderImplEx && OrgAsmMatrix.needCheckAssortment(document.getId())) {
                needCheckItems = !((OrderImplEx) document).isComplete(assortment);
            }
        }

        if(needCheckItems) {
            writeTime = true;
            FoldersAdapter.resetCache();
            return new MatrixItemsAdapter(this, assortment);
        }

        if (writeTime)
            writeDocTime();

        writeTime = false;
        return super.createListAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needCheckItems != null && needCheckItems) {
            if(((OrderImplEx)document).isComplete(assortment)) {
                findViewById(R.id.btnNext).setVisibility(View.VISIBLE);
            }
        }

        if (startTime == -1 && writeTime && !notStopTimer){
            startTime = System.currentTimeMillis();
        }

        notStopTimer = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(writeTime && !notStopTimer){
            writeDocTime();
        }
    }

    @Override
    public void editItem(long rowid) {
        notStopTimer = true;
        super.editItem(rowid);
    }

    private void writeDocTime() {
        long finishTime = System.currentTimeMillis();

        if (assortment != null && assortment.size() > 0){
            int cnt = assortment.size();
            long time = (finishTime - startTime) / 1000;

            long avg  = time / cnt;

            ((OrderEx) document.getData()).avgTime += (int) avg;
            document.write();
            document.close();
        }
    }
}
