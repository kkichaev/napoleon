package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.napoleon.documents.Itemsable;

public class PricePresentationFolderEx extends  PricePresentationFolder{
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (document != null && document instanceof Itemsable){
            PresentationData data =  list.get(pager.getCurrentItem());
            ((Itemsable)document).editItem(data.rowid, this);
        }
    }

    @Override
    public void onItemClick(View v) {
        super.onItemClick(v);
        finish();
    }
}
