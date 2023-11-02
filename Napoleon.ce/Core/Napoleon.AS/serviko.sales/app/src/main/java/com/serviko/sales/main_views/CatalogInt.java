package com.serviko.sales.main_views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.priceTree.Folder;
import com.serviko.sales.R;

public class CatalogInt extends Catalog {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        tvName.setVisibility(View.GONE);

        v.findViewById(R.id.llTitle).setVisibility(View.VISIBLE);
        if(model.currentFolder != null)
            ((TextView)v.findViewById(R.id.tvTitle)).setText(model.currentFolder.item.name);

        v.findViewById(R.id.back).setOnClickListener(view -> onBackPressed());

        return v;
    }

    @Override
    protected void initFolderList(Partner partner) {
        adapter = new Adapter(model.currentFolder);
        lvItems.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override public void handleOnBackPressed() { onBackPressed(); }
        };

        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        super.onAttach(context);
    }

    void onBackPressed() {
        model.popFolder();
        getParentFragmentManager().popBackStack();
    }
}
