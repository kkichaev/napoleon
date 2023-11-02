package com.grsoft.napoleon.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.SignEditor;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Settings extends BaseFragment {
    static final String TAG = Settings.class.toString();

    @Override
    protected int getLayoutID() {
        return R.layout.settings_view;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    Pair<Integer, String>[] items = new Pair[] {
            new Pair(R.id.ip, "address"),
            new Pair(R.id.port, "port"),
            new Pair(R.id.login, "login"),
            new Pair(R.id.password, "passw"),
    };

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();

        for (Pair<Integer, String> kv : items) {
            View til = v.findViewById(kv.first);
            if (til instanceof TextInputLayout) {
                EditText ed = ((TextInputLayout) til).getEditText();
                ed.setText(cfg.getValue(kv.second));
            }
        }


        v.findViewById(R.id.ok).setOnClickListener(view -> {
            saveConfig(cfg, v);
            getParentFragmentManager().popBackStack();
        });

        return v;
    }

    boolean hasMainSign(){
        boolean res = false;
        AgentPrefix ap = AgentPrefix.get();

        if (ap != null) {
            PicStoreImpl pci = new PicStoreImpl();
            res = pci.read("id", ap.id);
        }

        return res;
    }

    private void saveConfig(CfgNpl cfg, View v) {
        List<Pair<String, String>> values = new ArrayList<>();

        for (Pair<Integer, String> kv : items) {
            View til = v.findViewById(kv.first);
            if(til instanceof TextInputLayout) {
                EditText ed = ((TextInputLayout)til).getEditText();
                values.add(new Pair<>(kv.second, ed.getText().toString()));
            }
        }

        cfg.setFrom(values);

        ConfigManager.save();
    }
}
