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
    public static String TAB_IDX = "tab_idx";
    @Override
    protected int getLayoutID() {
        return R.layout.settings_view;
    }

    @Override
    public String TAG() {
        return TAG;
    }

//    @Override
//    public int getOptionMenu() {
//        return R.menu.settings;
//    }

    Pair<Integer, Integer>[] tabs = new Pair[] {
            new Pair(R.id.network, R.id.network_view),
            new Pair(R.id.gps, R.id.gps_view),
            new Pair(R.id.common, R.id.common_view),
    };

    Pair<Integer, String>[] items = new Pair[] {
            new Pair(R.id.ip, "address"),
            new Pair(R.id.ip2, "address2"),
            new Pair(R.id.port, "port"),
            new Pair(R.id.login, "login"),
            new Pair(R.id.password, "passw"),
            new Pair(R.id.gps_polling_interval, "gpsFrequience"), // /= Consts.ONE_SECOND
            new Pair(R.id.gps_chg_distance, "gpsDistance"),
    };

    Pair<Integer, String>[] comboBoxes = new Pair[] {
            new Pair(R.id.gps_transmit_interval, "gpsSendInterval:1,5,10,20,30,40,50,60,70,80,90,100,110,120"),
            new Pair(R.id.gps_waiting, "waitGpsCoordOnRequest:1,5,10,20,30,40,50,60,70,80,90,100,110,120"),
            new Pair(R.id.gps_keep_location, "gps_valid_in_org:1,5,10,15,20,25,30"), // / (Consts.SEC_PER_MIN * Consts.ONE_SECOND)
            new Pair(R.id.remove_photo_after, "day_to_del_visit:1,2,3,4,5,6,7,8,9,10,11,12"),
    };
    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        for(Pair<Integer, Integer> ti : tabs) {
            v.findViewById(ti.first).setOnClickListener(view -> changeTab(ti));
        }

        CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
        cfg.gpsFrequience /= Consts.ONE_SECOND;
        cfg.gps_valid_in_org /= Consts.SEC_PER_MIN * Consts.ONE_SECOND;

        for (Pair<Integer, String> kv : items) {
            View til = v.findViewById(kv.first);
            if(til instanceof TextInputLayout) {
                EditText ed = ((TextInputLayout)til).getEditText();
                ed.setText(cfg.getValue(kv.second));
            }
        }

        for(Pair<Integer, String> kv : comboBoxes) {
            setCombo(v.findViewById(kv.first), kv.second, cfg);
        }

        cfg.gpsFrequience *= Consts.ONE_SECOND;
        cfg.gps_valid_in_org *= Consts.SEC_PER_MIN * Consts.ONE_SECOND;

        v.findViewById(R.id.restore_db).setOnClickListener(view ->
                restoreDB());

        v.findViewById(R.id.sync_acrh).setOnClickListener(view ->
                syncArch());

        v.findViewById(R.id.export_base).setOnClickListener(view ->
                exportBase());

        ((SwitchMaterial)v.findViewById(R.id.send_in_background)).setChecked(cfg.dataSendInBackground);

        v.findViewById(R.id.btnOK).setOnClickListener(view -> {
            saveConfig(cfg, v);
            getParentFragmentManager().popBackStack();
        });
        changeTab(tabs[0]);

        model.getRefreshing().observe(getViewLifecycleOwner(), data -> {
            v.findViewById(R.id.waiting).setVisibility(data.refreshing ? View.VISIBLE : View.GONE);
            if (data.error != null && data.error.length() > 0) {
                model.clearRefrheshing();
                SyncErrorDialog dlg = new SyncErrorDialog(data.error);
                dlg.show(getParentFragmentManager(), "");
            } else if (data.traffic > 0) {
                model.clearRefrheshing();
                SuccessExchange dlg = new SuccessExchange();
                dlg.show(getParentFragmentManager(), "");
                getParentFragmentManager().popBackStack();
            }
        });

        model.getSyncProgress().observe(getViewLifecycleOwner(), progress -> {
            if (progress.first != null && progress.first.length() > 0) {
                ((TextView) v.findViewById(R.id.progress_text)).setText(progress.first);
            }
            ((ProgressBar) v.findViewById(R.id.progress)).setProgress(progress.second);
        });

        File sf = new File(SignHelper.getMainSignPath());

        ImageView sign = v.findViewById(R.id.sign);
        sign.setOnClickListener(view->{
            if (AgentPrefix.get() == null) {
                Toast.makeText(getContext(), R.string.sync_needed, Toast.LENGTH_SHORT).show();
                return;
            }

            saveConfig(cfg, v);
            ((MainActivity)getActivity()).signEditor(SignHelper.getMainSignPath(), hasMainSign());
        });

        if (sf.exists() && hasMainSign()) {
            Bitmap bitmap = BitmapFactory.decodeFile(sf.getAbsolutePath());
            sign.setImageBitmap(bitmap);
        }

        getParentFragmentManager().setFragmentResultListener(SignEditor.KEY,
                getViewLifecycleOwner(), (requestKey, result) -> {
                    String path = result.getString(SignEditor.FILE_NAME);

                    if (path.length() > 0) {
                        PicStoreImpl pc = new PicStoreImpl();
                        pc.getData().id = AgentPrefix.get().id;
                        pc.getData().created = SignHelper.minePicStore;
                        pc.getData().picture = path.getBytes();
                        pc.write();
                        pc.close();

                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        sign.setImageBitmap(bitmap);
                    }
                });



        if (getArguments() != null) {
            int idx = getArguments().getInt(TAB_IDX);
            changeTab(tabs[idx]);
        }
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

    private void restoreDB() {
        model.restoreDB(getContext(), (CfgNpl) ConfigManager.getConfig());
    }

    private void syncArch() {
        model.refresh(getContext(), (CfgNpl) ConfigManager.getConfig(), true);
    }

    private void exportBase(){
        model.exportBase(getContext());
    }

    private void saveConfig(CfgNpl cfg, View v) {
        final String login = cfg.login;
        final String pwd = cfg.passw;

        List<Pair<String, String>> values = new ArrayList<>();

        cfg.gpsFrequience /= Consts.ONE_SECOND;
        cfg.gps_valid_in_org /= Consts.SEC_PER_MIN * Consts.ONE_SECOND;

        for (Pair<Integer, String> kv : items) {
            View til = v.findViewById(kv.first);
            if(til instanceof TextInputLayout) {
                EditText ed = ((TextInputLayout)til).getEditText();
                values.add(new Pair<>(kv.second, ed.getText().toString()));
            }
        }

        for(Pair<Integer, String> kv : comboBoxes) {
            View til = v.findViewById(kv.first);
            if(til instanceof TextInputLayout) {
                AutoCompleteTextView av =(AutoCompleteTextView) ((TextInputLayout)til).getEditText();
                String val= av.getText().toString();
                String fn = kv.second.split(":")[0];
                values.add(new Pair<>(fn, val));
            }
        }

        cfg.dataSendInBackground = ((SwitchMaterial)v.findViewById(R.id.send_in_background)).isChecked();

        cfg.setFrom(values);

        cfg.gpsFrequience *= Consts.ONE_SECOND;
        cfg.gps_valid_in_org *= Consts.SEC_PER_MIN * Consts.ONE_SECOND;

        cfg.cameraWidth = 1024;
        cfg.cameraHeight = 1024;

        ConfigManager.save();

        if (!login.equals(cfg.login) || !pwd.equals(cfg.passw)){
            PicStoreImpl p = new PicStoreImpl();
            AgentPrefix ap = AgentPrefix.get();

            if (ap != null) {
                p.getData().id = ap.id;

                if (p.read()) {
                    p.delete();
                    new File(SignHelper.getMainSignPath()).delete();
                }

                p.close();
            }
        }
    }

    private void setCombo(View v, String fieldDef, CfgNpl cfg) {
        if(v instanceof TextInputLayout) {
            String[] fd = fieldDef.split(":");

            String[] items = fd[1].split(",");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.setting_list_item, items);
            AutoCompleteTextView av =(AutoCompleteTextView) ((TextInputLayout)v).getEditText();
            av.setAdapter(adapter);
            av.setInputType(InputType.TYPE_NULL);

            String selected = items[0];
            String value = cfg.getValue(fd[0]);
            if(value.length() > 0) {
                int iv = Integer.parseInt(value);
                for(String s : items) {
                    if(iv < Integer.parseInt(s))
                        break;
                    selected = s;
                }
            }

            av.setText(selected, false);
        }
    }

    void changeTab(Pair<Integer, Integer> curT) {
        for(Pair<Integer, Integer> id : tabs) {
            TextView tv = v.findViewById(id.first);
            tv.setBackgroundResource(id == curT ? R.color.primary : R.color.settings_secondary_back);
            v.findViewById(id.second).setVisibility(id == curT ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
