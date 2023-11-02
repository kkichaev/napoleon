package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;

import java.util.ArrayList;
import java.util.List;

public class PresentationFolderEx extends PresentationFolder{
    List<String> kupecAction = new ArrayList<>();
    @Override
    protected int getItemLayoutId() {
        return R.layout.presentation_itemex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kupecAction = KupecActionHelper.getItemsByOrg(doc.getId());
    }

    @Override
    public View getPriceView(PriceTreeNode node, View convertView) {
        View view = super.getPriceView(node, convertView);
        view.findViewById(R.id.ivKupec).setVisibility(kupecAction.contains(node.getId()) ? View.VISIBLE : View.GONE);
        return view;
    }

    @Override
    public View getFolderView(FolderTreeNode node, View convertView) {
        View view = super.getFolderView(node, convertView);
        view.findViewById(R.id.ivKupec).setVisibility(View.GONE);
        return view;
    }
}
