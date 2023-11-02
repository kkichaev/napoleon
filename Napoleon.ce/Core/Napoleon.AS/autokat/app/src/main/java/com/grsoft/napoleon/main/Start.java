package com.grsoft.napoleon.main;

import static android.content.Context.LOCATION_SERVICE;

import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.IncompleteOrgDataDlg;
import com.grsoft.napoleon.IncompleteScriptDlg;
import com.grsoft.napoleon.IncompleteScriptsDlg2;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.NapoleonApp;
import com.grsoft.napoleon.NapoleonAppBase;
import com.grsoft.napoleon.PromptToGPS;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.org_adapter.BaseOrgAdapter;
import com.grsoft.napoleon.org_adapter.OrgAdapter;
import com.grsoft.napoleon.org_adapter.ScheduleAdapter;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.NapoleonServiceW;
import com.grsoft.util.gps.GPSUtilNew;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Start extends BaseFragment {

    static final String TAG = Start.class.toString();
    BaseOrgAdapter adapter;

    View v;

    @Override
    protected int getLayoutID() {
        return R.layout.start_view;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public int getOptionMenu() {
        return R.menu.start;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            ((MainActivity) getActivity()).openSettings();
            return true;
        }
        if (id == R.id.refresh) {
            doRefresh();
            return true;
        }
        if (id == R.id.about) {
            ((MainActivity) getActivity()).showAbout();
            return true;
        }
        if (id == R.id.notify) {
            ((MainActivity) getActivity()).showNotify();
            return true;
        }
        if (id == R.id.exit) {
            ((NapoleonAppBase)getActivity().getApplication()).exit();
            getActivity().finishAndRemoveTask();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ScriptImpl getIncompleteScript(){
       return ((MainActivity)getActivity()).getIncompleteScript();
    }

    private void doRefresh() {

        ScriptImpl us = getIncompleteScript();
        if (us != null) {
            IncompleteScriptDlg dlg = new IncompleteScriptDlg(us.getRowid());
            dlg.show(getParentFragmentManager(), "");
            return;
        }

        model.refresh(getContext(), (CfgNpl) ConfigManager.getConfig());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Start", "onCreateView");
        v = super.onCreateView(inflater, container, savedInstanceState);

        model.getWorkingDate().observe(getViewLifecycleOwner(), this::onDateChanged);

        model.getRouteMode().observe(getViewLifecycleOwner(), mode -> {
            int clr1 = getContext().getColor(R.color.primary);
            int clr2 = Color.WHITE;

            v.findViewById(R.id.guide1).setBackgroundColor(mode ? clr1 : clr2);
            v.findViewById(R.id.guide2).setBackgroundColor(mode ? clr2 : clr1);
        });

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
                adapter.refresh();
                getActivity().invalidateOptionsMenu();
            }
        });

        model.getSyncProgress().observe(getViewLifecycleOwner(), progress -> {
            if (progress.first != null && progress.first.length() > 0) {
                ((TextView) v.findViewById(R.id.progress_text)).setText(progress.first);
            }
            ((ProgressBar) v.findViewById(R.id.progress)).setProgress(progress.second);
        });

        model.checkGPS.observe(getViewLifecycleOwner(), on->{
            if (NapoleonServiceW.isTracking()){
                GPSUtilNew.stop(getContext());
                GPSUtilNew.start(getContext());
            }else
                GPSUtilNew.stop(getContext());
        });

        SearchView sv = v.findViewById(R.id.search);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) adapter.filter(query);
                model.searchText = query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    if (adapter != null) adapter.filter("");
                    model.searchText = "";
                }
                return false;
            }
        });

        v.findViewById(R.id.show_route).setOnClickListener(v -> model.setRouteMode(true));
        v.findViewById(R.id.showo_clients).setOnClickListener(v -> model.setRouteMode(false));

        model.getRouteMode().observe(getViewLifecycleOwner(), routeMode -> {
            RecyclerView rv = v.findViewById(R.id.items);

            MainActivity activity = (MainActivity) getActivity();
            adapter = routeMode ? new ScheduleAdapter(activity) : new OrgAdapter(activity);
            rv.setAdapter(adapter);

            rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            v.findViewById(R.id.set_date).setVisibility(routeMode ? View.VISIBLE : View.GONE);

            if (model.getCurrentOrg().getValue() != null)
                rv.scrollToPosition(adapter.getItemPosition(model.getCurrentOrg().getValue()));
        });

        v.findViewById(R.id.set_date).setOnClickListener(view -> selectDate());

        v.findViewById(R.id.start_visit).setOnClickListener(view -> startVisit());
        v.findViewById(R.id.add_new_client).setOnClickListener(view -> ((MainActivity) getActivity()).editClient(false));
        v.findViewById(R.id.edit_client).setOnClickListener(view -> {
            if (model.currentOrg.getValue() != null)
                ((MainActivity) getActivity()).editClient(true);
        });
        v.findViewById(R.id.show_map).setOnClickListener(view -> ((MainActivity) getActivity()).showMap());
        v.findViewById(R.id.show_documents).setOnClickListener(view -> ((MainActivity) getActivity()).showDocuments());

        getParentFragmentManager().setFragmentResultListener(IncompleteScriptDlg.KEY,
                getViewLifecycleOwner(), (requestKey, result) -> {
                    long rowid = result.getLong(ExtrasConst.DOC_ROW_ID_STR);

                    ScriptImplEx script = new ScriptImplEx();
                    script.read(rowid);
                    script.close();

                    if (result.getInt(IncompleteScriptDlg.ACTION) == IncompleteScriptDlg.DELETE_ACTION) {
                        script.delete();
                        script.close();

                        if (result.getBoolean(IncompleteScriptDlg.SYN_AFTER_DEL))
                            doRefresh();
                    } else {
                        OrgImpl org = new OrgImpl();
                        org.read("id", script.getId());

                        model.setCurrentOrg((OrgEx) org.getData());
                        model.currentScript = script;
                        int step = script.isSigned() ? 2 : 0;
                        ((MainActivity) getActivity()).openFragment(new ScriptWizard(step), true);
                    }
                });

        if (getArguments() != null){
            Bundle b = getArguments();
            if (b.getBoolean(MainActivity.OPEN_SCHEDULE)) {
                b.putBoolean(MainActivity.OPEN_SCHEDULE, false);
                model.setRouteMode(true);
            }
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(model.searchText.length() > 0) {
            SearchView sv = v.findViewById(R.id.search);
            sv.setQuery(model.searchText, true);
        }

        OrgEx oe = model.getCurrentOrg().getValue();
        if(oe != null && adapter != null) {
            int idx = adapter.indexOf(oe);
            if(idx >= 0)
                adapter.notifyItemChanged(idx, oe);
        }

        if (((NapoleonApp)getActivity().getApplication()).need_sync) {
            ((NapoleonApp)getActivity().getApplication()).need_sync = false;
            doRefresh();
        }
    }

    void startVisit() {
        if (NapoleonServiceW.isTracking() && ! ((MainActivity) getActivity()).isGPSTurnOn()){
            PromptToGPS dlg = new PromptToGPS();
            dlg.show(getParentFragmentManager(), "");
            return;
        }

        ScriptImpl us = getIncompleteScript();
        if (us != null) {
            IncompleteScriptDlg dlg = new IncompleteScriptsDlg2(us.getRowid());
            dlg.show(getParentFragmentManager(), "");
            return;
        }

        OrgEx o = (OrgEx) model.getCurrentOrg().getValue();
        if (o == null) {
            Toast.makeText(getContext(), R.string.need_select_org, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!o.name.equals(getString(R.string.private_client)) && (o.orgFormat.length() == 0 || o.orgType.length() == 0)) {
            IncompleteOrgDataDlg dlg = new IncompleteOrgDataDlg();
            dlg.show(getParentFragmentManager(), "");
            return;
        }

        ((MainActivity) getActivity()).startVisit(o);
    }

    void selectDate() {
        MaterialDatePicker<Long> dp = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText(R.string.select_date)
                .setSelection(model.getWorkingDate().getValue().getTime())
                .build();

        dp.addOnPositiveButtonClickListener(selection -> {
            model.setWorkingDate(new Date(selection));
        });
        dp.show(getParentFragmentManager(), "");
    }

    void onDateChanged(Date date) {
        Button b = v.findViewById(R.id.set_date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        b.setText(sdf.format(date));

        if (b.isShown()) {
            RecyclerView rv = v.findViewById(R.id.items);
            MainActivity activity = (MainActivity) getActivity();
            adapter = new ScheduleAdapter(activity);
            rv.setAdapter(adapter);
        }
    }
}
