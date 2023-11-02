package com.grsoft.napoleon.main;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.views.RoundedDialog;
import com.grsoft.util.ExtrasConst;

public class FilterMsgDlg extends RoundedDialog {
    public final static String KEY = "filtermsgdlg";
    public final static String FILTER = "filter";
    private RadioGroup radioGroup;

    @Override
    protected int getLayoutId() { return R.layout.notify_filter_dialog; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        v.findViewById(R.id.ok).setOnClickListener(w->doFilter());
        radioGroup = v.findViewById(R.id.radioGroup);

        return v;
    }

    private void doFilter() {
        Bundle res = new Bundle();
        res.putInt(FILTER, radioGroup.getCheckedRadioButtonId());
        getParentFragmentManager().setFragmentResult(KEY, res);
        dismiss();
    }
}
