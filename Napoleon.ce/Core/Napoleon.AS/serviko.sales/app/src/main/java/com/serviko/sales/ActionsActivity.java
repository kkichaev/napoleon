package com.serviko.sales;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.view.treeview.InMemoryTreeNode;
import com.serviko.view.treeview.TreeAdapter;
import com.serviko.view.treeview.TreeView;

import java.util.List;

public class ActionsActivity extends BaseActivityOld implements PictureHolder.Handler {

    static final String ACTION_DEF_TAG = "selAction";
    TreeAdapter adapter;
    String selected = null;

    public static void open(Context context) {
        Intent i = new Intent(context, ActionsActivity.class);
        context.startActivity(i);
    }

    public static void open(Context context, ActionDef selected) {
        Intent i = new Intent(context, ActionsActivity.class);
        i.putExtra(ACTION_DEF_TAG, selected.getId());
        context.startActivity(i);
    }

    @Override protected int getLayoutID() { return R.layout.actions; }
    @Override protected int getBottomMenuID() { return R.id.itActions; }

    @Override
    protected void onPartnerSelect(Partner newPartner) {
        super.onPartnerSelect(newPartner);
        InMemoryTreeNode root = InMemoryTreeNode.createRoot();
        List<ActionDef> actionDefs = newPartner.getActions();
        root.addAll(actionDefs);

        ActionDef sel = null;
        for(ActionDef ad : actionDefs) {
            boolean expand = ad.getId().equals(selected);
            ad.expand(expand);
            if(expand)
                sel = ad;
        }
        selected = null;

        TreeView tv = findViewById(R.id.tvItems);
        adapter = new TreeAdapter(this, root);
        tv.setAdapter(adapter);
        if(sel != null) {
            tv.setSelection(actionDefs.indexOf(sel));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selected = getIntent().getStringExtra(ACTION_DEF_TAG);

        MaterialToolbar mb = findViewById(R.id.topAppBar);
        mb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PictureHolder.addHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PictureHolder.removeHandler(this);
    }

    @Override
    public void onReceive(String id, Bitmap img) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
