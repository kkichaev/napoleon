package com.serviko.sales;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.actionTree.ActionClause;
import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.view.treeview.InMemoryTreeNode;
import com.serviko.view.treeview.TreeAdapter;
import com.serviko.view.treeview.TreeView;

import java.util.List;

public class ActionRules extends BaseActivityOld implements PictureHolder.Handler {
    static final String ACTION_DEF_TAG = "selAction";

    ActionDef actionDef;
    TreeAdapter adapter;

    public static void open(Context context, String actionId) {
        Intent i = new Intent(context, ActionRules.class);
        i.putExtra(ACTION_DEF_TAG, actionId);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        String action = b.getString(ACTION_DEF_TAG);
        actionDef = PartnerList.getCurrent().getAction(action);

        if(actionDef != null) {
            InMemoryTreeNode root = InMemoryTreeNode.createRoot();
            List<ActionClause> clauses = actionDef.getClauses();
            root.addAll(clauses);

            TreeView tv = findViewById(R.id.tvItems);
            adapter = new TreeAdapter(this, root);
            tv.setAdapter(adapter);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTION_DEF_TAG, actionDef.getId());
    }

    @Override protected int getLayoutID() { return R.layout.action_rules; }
    @Override protected int getBottomMenuID() { return R.id.itActions; }

    @Override protected void selectCurrentPartner() { }

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

    @Override public void onReceive(String id, Bitmap img) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
