package com.grsoft.napoleon.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DocsToSign;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.PasswordDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

import java.util.List;

public class StartView extends BaseFragment {
    @Override protected int getLayoutID() {return R.layout.start_view;}
    @Override public String TAG() {return "StartView";}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.start).setOnClickListener(v1 -> {
            model.refresh(getContext(), (CfgNpl) ConfigManager.getConfig());
        });

        v.findViewById(R.id.settings).setOnClickListener(v1 -> {
            if(PasswordDlg.getPassword().length() > 0) {
                PasswordDlg dlg = new PasswordDlg();
                dlg.show(getParentFragmentManager(), "");
            } else {
                ((MainActivity)getActivity()).openFragment(new Settings(), true);
            }
        });

        getParentFragmentManager().setFragmentResultListener(PasswordDlg.RESULT,
                getViewLifecycleOwner(), (requestKey, result) -> {
            if(result.getBoolean(PasswordDlg.RESULT, false)) {
                ((MainActivity)getActivity()).openFragment(new Settings(), true);
            }
        });

        model.setRefreshHandler(data -> {
            getActivity().runOnUiThread(() -> {
                v.findViewById(R.id.wait).setVisibility(data.refreshing ? View.VISIBLE : View.GONE);

                if(data.error != null && data.error.length() > 0) {
                    SyncErrorDialog dlg = new SyncErrorDialog(data.error);
                    dlg.show(getParentFragmentManager(), "");
                    v.findViewById(R.id.wait).setVisibility(View.GONE);
                } else if(data.traffic > 0) {
                    List<DocsToSign> docs = DbReader.fetch(DocsToSign.class);
                    if(docs.size() > 0) {
                        ((MainActivity) getActivity()).openFragment(new ViewDocs(), true);
                    } else
                        Toast.makeText(getContext(), R.string.no_documents_to_sign, Toast.LENGTH_SHORT).show();
                    v.findViewById(R.id.wait).setVisibility(View.GONE);
                }
            });
        });

//        model.getRefreshing().observe(getViewLifecycleOwner(), data -> {
//            v.findViewById(R.id.wait).setVisibility(data.refreshing ? View.VISIBLE : View.GONE);
//
//            if(data.error != null && data.error.length() > 0) {
//                SyncErrorDialog dlg = new SyncErrorDialog(data.error);
//                dlg.show(getParentFragmentManager(), "");
//            } else if(data.traffic > 0) {
//                List<DocsToSign> docs = DbReader.fetch(DocsToSign.class);
//                if(docs.size() > 0) {
//                    ((MainActivity) getActivity()).openFragment(new ViewDocs(), true);
//                } else
//                    Toast.makeText(getContext(), R.string.no_documents_to_sign, Toast.LENGTH_SHORT).show();
//            }
//
//            if(!data.refreshing) {
//                model.clearRefreshing();
//            }
//        });
        return v;
    }
}
