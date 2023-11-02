package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.ActionFolder;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ActionImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.PriceTextFilter;

import java.util.ArrayList;
import java.util.List;

public class ActionView extends Warehouse{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static int ACTION_VIEW_REQUEST = 100;
    public static String ACTION_ID = "action_id";
    ActionImpl action = new ActionImpl();

    public static void openActionView(Activity context){
        Intent intent = new Intent(context, ActionView.class);
        context.startActivityForResult(intent, ACTION_VIEW_REQUEST);
    }

    protected FoldersAdapter createAdapterInstance() {
        FoldersAdapter.resetCache();
        return new FoldersAdapter(this){
            @Override
            protected String getPriceTableName() {
                return DataObjectInfo.getInstance().getTableName(Action.class);
            }

            @Override
            protected String getFolderTableName() {
                return DataObjectInfo.getInstance().getTableName(ActionFolder.class);
            }
        };
    }

    @Override
    protected String getItemName(Price p) {
        return action.getData().name;
    }

    void readPriceNode(long rowid) {
        action.read(rowid, false);
    }

    @Override
    public void editItem(long rowid) {
        action.read(rowid, false);
        Intent i = new Intent();
        i.putExtra(ACTION_ID, action.getData().id);
        setResult(RESULT_OK, i);
        finish();
    }

    protected PriceTextFilter createPriceTextFilter() {
        return new PriceTextFilter(){
            @Override
            public String getPriceTable() {
                return "action";
            }
        };
    }

    @Override
    public View getPriceView(PriceTreeNode node, View convertView) {
        View res = super.getPriceView(node, convertView);
        res.findViewById(R.id.llQuant).setVisibility(View.GONE);
        return res;
    }
}
