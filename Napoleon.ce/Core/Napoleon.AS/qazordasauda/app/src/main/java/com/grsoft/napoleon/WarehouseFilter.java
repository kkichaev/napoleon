package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Price;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WarehouseFilter extends Warehouse {

    public static String ITEM_TAG = "items";
    public static String FOLDER_TAG = "folders";

    List<String> items = new ArrayList<>();
    List<Integer> folders = new ArrayList<>();

    static void open(Activity context, List<String> items, List<Integer> folders, int reqCode) {
        Intent i = new Intent(context, WarehouseFilter.class);

        String[] data = items.toArray(new String[]{});
        i.putExtra(ITEM_TAG, data);

        int ctr = 0;
        int[] fdata = new int[folders.size()];
        for(int f : folders) {
            fdata[ctr++] = f;
        }
        i.putExtra(FOLDER_TAG, fdata);

        context.startActivityForResult(i, reqCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();

        int[] fa = b.getIntArray(FOLDER_TAG);
        for(int f : fa) folders.add(f);

        String[] sa = b.getStringArray(ITEM_TAG);
        items.addAll(Arrays.asList(sa));

        linesController.setVariable();

        findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(ITEM_TAG, items.toArray(new String[]{}));
                int ctr = 0;
                int[] fa = new int[folders.size()];
                for(int f: folders) fa[ctr++] = f;
                i.putExtra(FOLDER_TAG, fa);
                setResult(RESULT_OK, i);
                finish();
            }
        });


        findViewById(R.id.btnCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderTreeNode ft = adapter.getFolderTop();
                for(TreeNode tn : ft.getChilds()) {
                    if(tn.isFolderNode()) {
                        int id = ((FolderTreeNode)tn).id;
                        if(!folders.contains(id)) {
                            folders.add(id);
                        }
                    } else {
                        String id = ((PriceTreeNode)tn).getId();
                        if(!items.contains(id))
                            items.add(id);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btnUnCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderTreeNode ft = adapter.getFolderTop();
                for(TreeNode tn : ft.getChilds()) {
                    if(tn.isFolderNode()) {
                        Integer id = ((FolderTreeNode)tn).id;
                        folders.remove(id);
                    } else {
                        String id = ((PriceTreeNode)tn).getId();
                        items.remove(id);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override protected int getLayoutId() { return R.layout.warehouse_filter; }
    @Override protected int getFolderLayoutId() { return R.layout.folder_filter_row;}
    @Override protected int getItemLayoutId() { return R.layout.price_filter_row; }

    @Override
    public View getPriceView(PriceTreeNode node, View convertView) {
        readPriceNode(node.getRowid());
        Price p = price.getData();

        View view;
        int id = getItemLayoutId();
        if (convertView != null && convertView.getTag(id) != null)
            view = convertView;
        else {
            view = View.inflate(this, id, null);
            view.setTag(id, true);
        }

        setName(view, p, 1, node);

        CheckBox cb = view.findViewById(R.id.cbCheck);
        cb.setTag(p.id);
        cb.setChecked(items.contains(p.id));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String id = (String) buttonView.getTag();
                if(isChecked)  {
                    if(!items.contains(id))
                        items.add(id);
                } else {
                    items.remove(id);
                }
            }
        });
        return view;
    }

    @Override protected boolean isShowDailySales() { return false; }

    @Override
    public View getFolderView(FolderTreeNode node, View convertView) {
        View v = super.getFolderView(node, convertView);
        CheckBox cb = v.findViewById(R.id.cbCheck);
        cb.setTag(node.id);
        cb.setChecked(folders.contains(node.id));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = (int) buttonView.getTag();
                if(isChecked) {
                    if(!folders.contains(id))
                        folders.add(id);
                } else {
                    folders.remove((Integer)id);
                }
            }
        });
        return v;
    }

    @Override
    public void editItem(long rowid) {
    }

    @Override
    protected void updateTotalSum() {
    }

    @Override
    public void updateTotalSum(long sum, int weight) {
    }

    @Override
    public void updateTotalSum(long sum, int weight, int count) {
    }
}
