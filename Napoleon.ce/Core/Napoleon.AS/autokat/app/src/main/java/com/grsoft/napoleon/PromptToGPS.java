package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.views.RoundedDialog;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;

public class PromptToGPS extends RoundedDialog {
    @Override
    protected int getLayoutId(){
        return R.layout.prompt_gps_dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.btnOK).setOnClickListener((e)->{
            getActivity().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            dismiss();
        });

        return v;
    }
}
