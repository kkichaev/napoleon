package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.dataobjects.ContractMatrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.impl.ContractMatrixImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixItemsAdapter;

import org.w3c.dom.Text;

import java.util.List;

public class WarehouseEx extends Warehouse {
    static String lastMatrix = "";
    static OrgEx org = null;

    @Override protected int getLayoutId() { return R.layout.warehouseex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnMatrix).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                MatrixItemsAdapter mi = nextMatrix();
                if(mi != null) {
                    FoldersAdapter.resetCache();
                    applayAdapter(mi);
                }
            }
        });
    }

    @Override
    protected BaseAdapter createListAdapter() {
        if(document != null && DocType.getCurDoc() == MerchDoc.instance()) {
            if(org == null || !org.id.equals(document.getId())) {
                OrgImpl oi = new OrgImpl();
                org = (OrgEx) oi.getData();
                org.id = document.getId();
                oi.read();
                oi.close();
                lastMatrix = org.matrix.size() > 0 ? org.matrix.get(0).name :  "";
            }
            MatrixItemsAdapter adapter = loadMatrix(lastMatrix);
            if(adapter != null) {
                FoldersAdapter.resetCache();
                return adapter;
            }
        }
        return super.createListAdapter();
    }

    MatrixItemsAdapter loadMatrix(String matrixName) {
        lastMatrix = matrixName;
        List<MatrixItem> items = ContractMatrix.read(matrixName);
        if(items != null) {
            findViewById(R.id.llMatrix).setVisibility(View.VISIBLE);
            TextView tv = findViewById(R.id.tvMatrix);
            tv.setText(matrixName);
            return new MatrixItemsAdapter(this, items);
        }

        return null;
    }

    MatrixItemsAdapter nextMatrix() {
        if(org != null && org.matrix.size() > 0) {
            String curMatrix = org.matrix.get(0).name;
            for(OrgMatrix cm : org.matrix) {
                if(cm.name.equals(lastMatrix)) {
                    int idx = org.matrix.indexOf(cm);
                    if(idx < org.matrix.size() - 1) {
                        curMatrix = org.matrix.get(idx+1).name;
                    }
                }
            }
            return loadMatrix(curMatrix);
        }
        return null;
    }
}
