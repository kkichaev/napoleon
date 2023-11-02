package com.grsoft.napoleon.main;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.AnswerDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PurchaseDoc;
import com.grsoft.napoleon.documents.SellingDoc;
import com.grsoft.napoleon.views.RoundedDialog;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FilterDlg extends RoundedDialog {
    private Date start = new Date();
    private Date finish = new Date();
    private TextView tvStart;
    private TextView tvFinish;
    private RadioGroup radioGroup;
    private Map<Integer, DocType> docTypes = new HashMap<>();

    public interface IFilterDialog{
        void doFilter(DocType type, DatePeriod dp);
    }

    private IFilterDialog filterListener;

    public void setOnFilterListener(IFilterDialog listener){
        filterListener = listener;
    }

    @Override
    protected int getLayoutId() { return R.layout.filter_dialog; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        tvStart = v.findViewById(R.id.start);
        tvStart.setOnClickListener(w->{selectDate(true);});
        tvStart.setText(Util.simpleDateFormat.format(start));

        tvFinish = v.findViewById(R.id.finish);
        tvFinish.setOnClickListener(w->{selectDate(false);});
        tvFinish.setText(Util.simpleDateFormat.format(finish));

        v.findViewById(R.id.ok).setOnClickListener(w->doFilter());
        radioGroup = v.findViewById(R.id.radioGroup);

        docTypes.put(R.id.purchase, PurchaseDoc.instance());
        docTypes.put(R.id.sales, SellingDoc.instance());
        docTypes.put(R.id.quest, AnswerDoc.instance());

        return v;
    }

    private void doFilter() {
        if (filterListener != null) {
            DocType type = getSelectedType();
            start = Util.resetTime(start);

            Calendar c = Calendar.getInstance();
            c.setTime(Util.resetTime(finish));
            c.add(Calendar.DATE, 1);
            finish = c.getTime();

            DatePeriod dp = new DatePeriod(start, finish);
            filterListener.doFilter(type, dp);

            dismiss();
        }
    }

    private DocType getSelectedType() {
        int id = radioGroup.getCheckedRadioButtonId();

        if (docTypes.containsKey(id))
            return docTypes.get(id);
        else
            return null;
    }

    void selectDate(boolean s) {
        MaterialDatePicker<Long> dp = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText(R.string.select_date)
                .setSelection(s ? start.getTime() : finish.getTime())
                .build();

        dp.addOnPositiveButtonClickListener(selection -> {
            if (s) {
                start = new Date(selection);
                tvStart.setText(Util.simpleDateFormat.format(start));
            }else{
                finish = new Date(selection);
                tvFinish.setText(Util.simpleDateFormat.format(finish));
            }
        });

        dp.show(getParentFragmentManager(), "");
    }
}
