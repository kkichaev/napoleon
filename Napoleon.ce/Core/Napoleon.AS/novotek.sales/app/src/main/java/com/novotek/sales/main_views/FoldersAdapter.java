package com.novotek.sales.main_views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.novotek.dataobjects.priceTree.FolderBase;
import com.novotek.dataobjects.priceTree.FolderSrc;
import com.novotek.sales.R;
import com.novotek.utils.ImageGetController;

import java.util.List;

public class FoldersAdapter extends BaseAdapter {
    List<? extends FolderBase> folders;
    ImageGetController images;
    Context context;

    public FoldersAdapter(Context context, List<? extends FolderBase> src, ImageGetController images) {
        this.images = images;
        folders = src;
        this.context = context;
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public Object getItem(int i) {
        return folders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = View.inflate(context, R.layout.categoires_main_row, null);

        FolderBase f = (FolderBase) getItem(i);
        TextView tv = view.findViewById(R.id.tvName);
        tv.setText(f.name.toString());

        ImageView iv = view.findViewById(R.id.image);
        images.setImage(f.url, iv);
        return view;
    }
}