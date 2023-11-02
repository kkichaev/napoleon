package com.grsoft.napoleon.main;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.grsoft.dataobjects.impl.OrgScheduleImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.views.RoundedDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SelectTime extends RoundedDialog {
    public final static String KEY = "key";
    private static final int STEP = 15;
    private EditText edHour;
    private EditText edMin;
    private TextInputLayout comment;
    private String key = null;
    private SwitchMaterial alarm;
    private AutoCompleteTextView alarmBefore;


    interface TimeSelectedListener{
        void onTimeSelected(String k, int h, int m, String c, boolean a, int t);
    }

    private TimeSelectedListener timeSelectedListener;

    @Override
    protected int getLayoutId() { return R.layout.select_time_dialog; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        alarm = v.findViewById(R.id.alarm);
        key = getArguments().getString(KEY);
        Calendar c = Calendar.getInstance();

        String cmt  = "";
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        if (key != null){
            OrgScheduleImpl impl = new OrgScheduleImpl();
            impl.read("key", key);
            hour = impl.getData().date.getHours();
            min = impl.getData().date.getMinutes();
            cmt = impl.getData().remark;
        }


        edHour = v.findViewById(R.id.edHour);
        edHour.setText(addLeadingZero(Integer.toString(hour)));
        edHour.setFilters(new InputFilter[]{new LimitFilter(23)});
        edMin = v.findViewById(R.id.edMin);
        edMin.setText(addLeadingZero(Integer.toString(min)));
        edMin.setFilters(new InputFilter[]{new LimitFilter(59)});
        comment = v.findViewById(R.id.comment);
        comment.getEditText().setText(cmt);

        v.findViewById(R.id.ok).setOnClickListener((w)->doSelect());

        alarmBefore = ((AutoCompleteTextView)((TextInputLayout)v.findViewById(R.id.alarm_before)).getEditText());
        alarmBefore.setAdapter(
                new ArrayAdapter<>(getContext(), R.layout.setting_list_item, getResources().getStringArray(R.array.alert_time))
        );
        alarmBefore.setText(getResources().getStringArray(R.array.alert_time)[0], false);
        return v;
    }

    private String addLeadingZero(String text) {
        if (text.length()<2)
            text = '0' + text;

        return text;
    }

    private void doSelect() {
        fireTimeSelect();
        dismiss();
    }

    private void fireTimeSelect() {
        if (timeSelectedListener != null){
            int h = tryParseFormEditText(edHour);
            int m = tryParseFormEditText(edMin);
            String c = comment.getEditText().getText().toString().trim();

            timeSelectedListener.onTimeSelected(key, h, m, c, alarm.isChecked(), getMinutes());
        }
    }

    private int getMinutes(){
        List<String> alarm = Arrays.asList(getResources().getStringArray(R.array.alert_time));
        int p = alarm.indexOf(alarmBefore.getText().toString().trim());

        return (p + 1) * STEP;
    }

    int tryParseFormEditText(EditText ed){
        int res = 0;

        try{
            res = Integer.parseInt(ed.getText().toString().trim());
        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    public void setTimeSelectedListener(TimeSelectedListener listener){
        timeSelectedListener = listener;
    }

    static class LimitFilter implements InputFilter{
        private int lim = 0;

        LimitFilter(int lim){
            this.lim = lim;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (dstart < dend) {
                return "";
            }

            StringBuilder sb = new StringBuilder(dest);
            sb = sb.insert(dstart, source);

            try {
                String s = sb.toString().trim();
                int val = 0;

                if (s.length() > 0) {
                    val = Integer.parseInt(sb.toString());

                    if (val <= lim)
                        return null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return "";
        }
    }
}
